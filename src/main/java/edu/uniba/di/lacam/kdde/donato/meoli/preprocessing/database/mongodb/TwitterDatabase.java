package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb;

import com.mongodb.Block;
import com.mongodb.client.model.*;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Comment;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

@Component
public class TwitterDatabase extends SocialMediaDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterDatabase.class);

    private static final int MINIMUM_ACCEPTED_LENGTH = 35;
    private static final int MAXIMUM_ACCEPTED_MENTION = 5;
    private static final int MINIMUM_ACCEPTED_TWEET_IN_TOPIC = 2;

    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

    private static final String RETWEETED_STATUS_FIELD_NAME = "retweeted_status";
    private static final String INVALID_JSON_FIELD_NAME = "limit";

    private static final String AGGREGATE_ID_FIELD_NAME = "_id";
    private static final String COUNT_TWEET_FIELD_NAME = "count";

    private static final String TWEET_COLLECTION = "tweets";
    private static final String FILTERED_TWEET_COLLECTION = "filteredtweet";
    private static final String DISTINCT_TOPIC_COLLECTION = "topics";

    private static final String TWEET_ID_FIELD_NAME = "id";
    private static final String TWEET_ID_STR_FIELD_NAME = "id_str";

    private static final String TWEET_TEXT_FIELD_NAME = "text";

    private static final String IN_REPLY_TO_STATUS_FIELD_NAME = "in_reply_to_status_id_str";

    private static final String TWEET_TIME_STAMP_UTC = "created_at";
    private static final String TWEET_TIME_STAMP_UTC_SECOND = "created_at_second";

    private static final String HASHTAGS_LIST_TEXT_FIELD_NAME = "hashtags";
    private static final String USER_MENTIONS_LIST_NAME_FIELD_NAME = "user_mentions";

    private static final String TWEET_USER_NAME_FIELD_NAME = "user_name";
    private static final String TWEET_USER_ID_FIELD_NAME = "user_id";
    private static final String TWEET_USER_ID_STR_FIELD_NAME = "user_id_str";

    private static final String TWEET_ENTITIES_OBJECT = "entities";

    private static final String TWEET_USER_OBJECT = "user";
    private static final String TWEET_USER_OBJECT_NAME = "name";
    private static final String TWEET_USER_OBJECT_ID = "id";
    private static final String TWEET_USER_OBJECT_ID_STR = "id_str";

    private static final String TWEET_HASHTAG_OBJECT = "hashtags";
    private static final String TWEET_HASHTAG_OBJECT_TEXT = "text";

    private static final String TWEET_USER_MENTIONS_OBJECT = "user_mentions";
    private static final String TWEET_USER_MENTIONS_OBJECT_NAME = "name";
    private static final String TWEET_USER_MENTIONS_OBJECT_INDEX = "indices";

    private static final String TWEET_URLS_OBJECT = "urls";
    private static final String TWEET_URLS_OBJECT_INDEX = "indices";

    private static final String TWEET_MEDIA_OBJECT = "media";
    private static final String TWEET_MEDIA_OBJECT_INDEX = "indices";

    public TwitterDatabase() { }

    @Autowired
    public TwitterDatabase(GridFsTemplate socialMediaCollection) {
        super(socialMediaCollection);
    }

    @Override
    public void createSocialMediaDatabase() {
        cleanDataset();
        remapDataset();
        dropRawDatasetCollection();
        createIndexes();
        Set<String> distinctTopic = getDistinctTopic();
        LOGGER.info("Set dim... " + distinctTopic.size());
        distinctTopic.parallelStream().forEach(topic -> {
            LOGGER.info("Start extraction discussion from topics... ");
            addDiscussion(getTweet(topic));
            LOGGER.info("The discussion of the topic {} have been added to the Twitter dataset", topic);
        });
    }

    private void cleanDataset() {
        getCollection(TWEET_COLLECTION).deleteMany(or(exists(INVALID_JSON_FIELD_NAME), exists(RETWEETED_STATUS_FIELD_NAME)));
    }

    private void remapDataset() {
        getCollection(TWEET_COLLECTION).find().forEach((Consumer<? super Document>) doc -> {
            if (checkDocument(doc)) {
                Document mapDoc = getMappedDocument(doc);
                if (mapDoc.getString(TWEET_TEXT_FIELD_NAME).length() >= MINIMUM_ACCEPTED_LENGTH)
                    getCollection(FILTERED_TWEET_COLLECTION).insertOne(mapDoc);
            }
        });
    }

    private void dropRawDatasetCollection() {
        getCollection(TWEET_COLLECTION).drop();
    }

    private void createIndexes() {
        getCollection(FILTERED_TWEET_COLLECTION).createIndexes(Arrays.asList(
                new IndexModel(new Document(HASHTAGS_LIST_TEXT_FIELD_NAME, 1), new IndexOptions().unique(false)),
                new IndexModel(new Document(IN_REPLY_TO_STATUS_FIELD_NAME, 1), new IndexOptions().unique(false))));
    }

    private Set<String> getDistinctTopic() {
        Set<String> topics = new HashSet<>();
        getCollection(FILTERED_TWEET_COLLECTION)
                .aggregate(Arrays.asList(unwind("$" + HASHTAGS_LIST_TEXT_FIELD_NAME),
                        group("$" + HASHTAGS_LIST_TEXT_FIELD_NAME, sum(COUNT_TWEET_FIELD_NAME, 1)),
                        sort(Sorts.orderBy(Sorts.descending(COUNT_TWEET_FIELD_NAME))),
                        match(gte(COUNT_TWEET_FIELD_NAME, MINIMUM_ACCEPTED_TWEET_IN_TOPIC)),
                        out(DISTINCT_TOPIC_COLLECTION))).allowDiskUse(true)
                .forEach((Block<? super Document>) sub -> topics.add(sub.getString(AGGREGATE_ID_FIELD_NAME)));
        return topics;
    }

    private Discussion getTweet(String topic) {
        List<Document> tweetsAndComments = retrieveFilteredTweetsAndComments(topic);
        return new Discussion(tweetsAndComments.get(0).getString(TWEET_ID_STR_FIELD_NAME),
                tweetsAndComments.get(0).getString(TWEET_USER_NAME_FIELD_NAME),
                tweetsAndComments.get(0).getLong(TWEET_TIME_STAMP_UTC_SECOND).longValue(),
                stanfordNLPAnalyzer.extractPOSTags(tweetsAndComments.get(0).getString(TWEET_TEXT_FIELD_NAME)),
                tweetsAndComments.get(0).get(USER_MENTIONS_LIST_NAME_FIELD_NAME, List.class),
                getComments(tweetsAndComments.subList(1, tweetsAndComments.size())));
    }

    private List<Comment> getComments(List<Document> comments) {
        return comments.parallelStream().map(comment ->
                new Comment(
                        comment.getString(TWEET_ID_STR_FIELD_NAME), comment.getString(TWEET_USER_NAME_FIELD_NAME),
                        comment.getLong(TWEET_TIME_STAMP_UTC_SECOND).longValue(),
                        stanfordNLPAnalyzer.extractPOSTags(comment.getString(TWEET_TEXT_FIELD_NAME)),
                        comment.get(USER_MENTIONS_LIST_NAME_FIELD_NAME, List.class),
                        getReplies(comment.getString(TWEET_ID_STR_FIELD_NAME))))
                .collect(Collectors.toList());
    }

    private List<Comment> getReplies(String id) {
        return retrieveReplies(id).parallelStream().map(comment ->
                new Comment(comment.getString(TWEET_ID_STR_FIELD_NAME),
                        comment.getString(TWEET_USER_NAME_FIELD_NAME),
                        comment.getLong(TWEET_TIME_STAMP_UTC_SECOND).longValue(),
                        stanfordNLPAnalyzer.extractPOSTags(comment.getString(TWEET_TEXT_FIELD_NAME)),
                        comment.get(USER_MENTIONS_LIST_NAME_FIELD_NAME, List.class),
                        getReplies(comment.getString(TWEET_ID_STR_FIELD_NAME))))
                .collect(Collectors.toList());
    }

    private List<Document> retrieveFilteredTweetsAndComments(String topic) {
        List<Document> topicTweet = new ArrayList<>();
        Comparator<Document> sortByDate = Comparator.comparing(p -> p.getLong(TWEET_TIME_STAMP_UTC_SECOND));
        Comparator<Document> sortById = Comparator.comparing(p -> p.getLong(TWEET_ID_FIELD_NAME));
        StreamSupport.stream(getCollection(FILTERED_TWEET_COLLECTION).find(
                eq(HASHTAGS_LIST_TEXT_FIELD_NAME, topic)).spliterator(), true).forEach((topicTweet::add));
        return topicTweet.parallelStream().sorted(sortByDate.thenComparing(sortById)).collect(Collectors.toList());
    }

    private List<Document> retrieveReplies(String id) {
        List<Document> replies = new ArrayList<>();
        StreamSupport.stream(getCollection(FILTERED_TWEET_COLLECTION).find(
                eq(IN_REPLY_TO_STATUS_FIELD_NAME, id)).spliterator(), true).forEach(replies::add);
        return replies;
    }

    private static Document getMappedDocument(Document doc) {
        Document mappedDocument = new Document();
        String created_at = doc.getString(TWEET_TIME_STAMP_UTC);
        mappedDocument.append(TWEET_ID_FIELD_NAME, doc.getLong(TWEET_ID_FIELD_NAME).longValue());
        mappedDocument.append(TWEET_ID_STR_FIELD_NAME, doc.getString(TWEET_ID_STR_FIELD_NAME));
        mappedDocument.append(IN_REPLY_TO_STATUS_FIELD_NAME, doc.getString(IN_REPLY_TO_STATUS_FIELD_NAME));
        mappedDocument.append(TWEET_TIME_STAMP_UTC, created_at);
        mappedDocument.append(TWEET_TIME_STAMP_UTC_SECOND, getDateEpochSecond(created_at));
        mappedDocument.append(TWEET_USER_NAME_FIELD_NAME,
                doc.get(TWEET_USER_OBJECT, Document.class).getString(TWEET_USER_OBJECT_NAME));
        mappedDocument.append(TWEET_USER_ID_FIELD_NAME,
                doc.get(TWEET_USER_OBJECT, Document.class).getInteger(TWEET_USER_OBJECT_ID).intValue());
        mappedDocument.append(TWEET_USER_ID_STR_FIELD_NAME,
                doc.get(TWEET_USER_OBJECT, Document.class).getString(TWEET_USER_OBJECT_ID_STR));
        String originalText = doc.getString(TWEET_TEXT_FIELD_NAME);
        String[] textArray = originalText.split("");
        List<String> hashtags_list = new ArrayList<>();
        List<String> userMentions = new ArrayList<>();
        Document entities = doc.get(TWEET_ENTITIES_OBJECT, Document.class);
        if (entities != null) {
            List<Document> hashtags = entities.get(TWEET_HASHTAG_OBJECT, List.class);
            if ((hashtags != null) && (!hashtags.isEmpty()))
                hashtags.forEach(tag -> hashtags_list.add(tag.getString(TWEET_HASHTAG_OBJECT_TEXT)));
            List<Document> user_mentions = entities.get(TWEET_USER_MENTIONS_OBJECT, List.class);
            if ((user_mentions != null) && (!user_mentions.isEmpty())) {
                user_mentions.forEach(mention -> {
                    userMentions.add(mention.getString(TWEET_USER_MENTIONS_OBJECT_NAME));
                    List<Integer> indexes = mention.get(TWEET_USER_MENTIONS_OBJECT_INDEX, List.class);
                    Arrays.fill(textArray, indexes.get(0).intValue(), indexes.get(1).intValue(), "");
                });
            }
            List<Document> urls = entities.get(TWEET_URLS_OBJECT, List.class);
            if ((urls != null) && (!urls.isEmpty())) {
                urls.forEach(url -> {
                    List<Integer> indexes = url.get(TWEET_URLS_OBJECT_INDEX, List.class);
                    Arrays.fill(textArray, indexes.get(0).intValue(), indexes.get(1).intValue(), "");
                });
            }
            List<Document> medias = entities.get(TWEET_MEDIA_OBJECT, List.class);
            if ((medias != null) && (!medias.isEmpty())) {
                medias.forEach(media -> {
                    List<Integer> indexes = media.get(TWEET_MEDIA_OBJECT_INDEX, List.class);
                    Arrays.fill(textArray, indexes.get(0).intValue(), indexes.get(1).intValue(), "");
                });
            }
        }
        List<String> list = Arrays.asList(textArray);
        String cleanedText = String.join("", list).trim();
        String clean = cleanStringText(cleanedText);
        mappedDocument.append(TWEET_TEXT_FIELD_NAME, clean);
        mappedDocument.append(HASHTAGS_LIST_TEXT_FIELD_NAME, hashtags_list);
        mappedDocument.append(USER_MENTIONS_LIST_NAME_FIELD_NAME, userMentions);
        return mappedDocument;
    }

    private static String cleanStringText(String text) {
        text = text.replaceAll("[^\\x00-\\x7F]", "");
        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");
        text = text.replaceAll("#", "").trim();
        return text;
    }

    private static long getDateEpochSecond(String date) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        Instant test2 = Instant.from(df.parse(date));
        return test2.getEpochSecond();
    }

    private static boolean checkDocument(Document doc) {
        boolean valid = false;
        Document entities = doc.get(TWEET_ENTITIES_OBJECT, Document.class);
        if ((entities != null) && (doc.getString(TWEET_TEXT_FIELD_NAME).length() >= MINIMUM_ACCEPTED_LENGTH)) {
            List<Document> user_mentions = entities.get(TWEET_USER_MENTIONS_OBJECT, List.class);
            if ((user_mentions != null) && (user_mentions.size() <= MAXIMUM_ACCEPTED_MENTION))
                valid = true;
        }
        return valid;
    }
}
