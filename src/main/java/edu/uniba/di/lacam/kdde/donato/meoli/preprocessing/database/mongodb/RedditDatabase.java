package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb;

import com.mongodb.Block;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Comment;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

@Component
public class RedditDatabase extends SocialMediaDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedditDatabase.class);

    private static final String SUBMISSIONS_COLLECTION = "submissions";
    private static final String COMMENTS_COLLECTION = "comments";
    private static final String STATISTICS_COLLECTION = "statistics";
    private static final String AUTHORS_COLLECTION = "authors";
    private static final String SUBREDDITS_COLLECTION = "subreddits";

    private static final String SUBMISSION_SELFTEXT_LENGTH_FIELD_NAME = "selftext_length";
    private static final String COMMENT_BODY_LENGTH_FIELD_NAME = "body_length";
    private static final String NUMBER_OF_COMMENTS_FIELD_NAME = "num_comments";
    private static final String PARENT_ID_FIELD_NAME = "parent_id";
    private static final String SUBREDDIT_ID_FIELD_NAME = "subreddit_id";

    private static final String SUBMISSIONS_SELFTEXTS_LENGTH_FIELD_NAME = "submissions_selftexts_length";
    private static final String COMMENTS_BODIES_LENGTH_FIELD_NAME = "comments_bodies_length";
    private static final String NUMBER_OF_DISTINCT_AUTHORS_FIELD_NAME = "num_distinct_authors";
    private static final String NUMBER_OF_DISTINCT_SUBREDDITS_FIELD_NAME = "num_distinct_subreddits";

    private static final String AGGREGATE_ID_FIELD_NAME = "_id";
    private static final String COUNT_AUTHOR_FIELD_NAME = "total";

    private static final String COMMENT_PREFIX = "t1_";
    private static final String SUBMISSION_PREFIX = "t3_";

    private static final String ID_FIELD_NAME = "id";
    private static final String AUTHOR_FIELD_NAME = "author";
    private static final String UTC_FIELD_NAME = "created_utc";
    private static final String SELFTEXT_FIELD_NAME = "selftext";
    private static final String BODY_FIELD_NAME = "body";

    private static final String REDDIT_MENTION_PREFIX = "/u/";

    public RedditDatabase() { }

    @Autowired
    public RedditDatabase(GridFsTemplate socialMediaCollection) {
        super(socialMediaCollection);
    }

    private void createSourceIndexes() {
        LOGGER.info("Please wait until creating indexes on the source Reddit dataset of {}/{} is finished", MONTH, YEAR);
        getCollection(SUBMISSIONS_COLLECTION).createIndexes(Arrays.asList(
                new IndexModel(new Document(SUBREDDIT_ID_FIELD_NAME, 1), new IndexOptions().unique(false)),
                new IndexModel(new Document(AUTHOR_FIELD_NAME, 1), new IndexOptions().unique(false))));
        getCollection(COMMENTS_COLLECTION).createIndexes(Arrays.asList(
                new IndexModel(new Document(PARENT_ID_FIELD_NAME, 1), new IndexOptions().unique(false)),
                new IndexModel(new Document(AUTHOR_FIELD_NAME, 1), new IndexOptions().unique(false))));
        LOGGER.info("The creation of the indexes on the source Reddit dataset of {}/{} is finished", MONTH, YEAR);
    }

    private void calculateSourceStatistics() {
        LOGGER.info("Please wait until calculating statistics to filter the source Reddit dataset of {}/{} is finished", MONTH, YEAR);
        getSubmissionsSelftextsLength();
        getCommentsBodiesLength();
        getNumberOfDistinctAuthors();
        getDistinctSubredditIDs();
        LOGGER.info("The calculation of the statistics to filter the source Reddit dataset of {}/{} is finished", MONTH, YEAR);
    }

    @Override
    public void createSocialMediaDatabase() {
        createSourceIndexes();
        calculateSourceStatistics();
        LOGGER.info("Please wait until creating Reddit dataset of {}/{} is finished", MONTH, YEAR);
        Set<String> distinctSubreddits = getDistinctSubredditIDs();
        long submissionsCollectionCount = getCollection(SUBMISSIONS_COLLECTION).count();
        distinctSubreddits.parallelStream().filter(subredditID -> !isPresent(subredditID)).filter(subredditID ->
                getFilteredSubmissions(subredditID).size() >= submissionsCollectionCount / distinctSubreddits.size())
                .forEach(subredditID -> {
                    LOGGER.info("Adding Submissions of the Subreddit {} to the Reddit dataset", subredditID);
                    getSubmissions(subredditID).parallelStream().forEach(this::addDiscussion);
                    LOGGER.info("The Submissions of the Subreddit {} have been added to the Reddit dataset", subredditID);
                });
        LOGGER.info("The creation of the Reddit dataset of {}/{} is finished", MONTH, YEAR);
    }

    private List<Discussion> getSubmissions(String subredditID) {
        return getFilteredSubmissions(subredditID).parallelStream().map(submission ->
                new Discussion(submission.getString(ID_FIELD_NAME), submission.getString(AUTHOR_FIELD_NAME),
                        submission.getInteger(UTC_FIELD_NAME),
                        stanfordNLPAnalyzer.extractPOSTags(submission.getString(SELFTEXT_FIELD_NAME)),
                        getMentions(submission.getString(SELFTEXT_FIELD_NAME)),
                        getComments(SUBMISSION_PREFIX + submission.getString(ID_FIELD_NAME))))
                .collect(Collectors.toList());
    }

    private List<Comment> getComments(String parentID) {
        return getFilteredComments(parentID).parallelStream().map(comment ->
                new Comment(comment.getString(ID_FIELD_NAME), comment.getString(AUTHOR_FIELD_NAME),
                        comment.getInteger(UTC_FIELD_NAME),
                        stanfordNLPAnalyzer.extractPOSTags(comment.getString(BODY_FIELD_NAME)),
                        getMentions(comment.getString(BODY_FIELD_NAME)),
                        getComments(COMMENT_PREFIX + comment.getString(ID_FIELD_NAME))))
                .collect(Collectors.toList());
    }

    private List<String> getMentions(String text) {
        List<String> mentions = new ArrayList<>();
        Matcher matcher = Pattern.compile(Post.getRegexMentionPrefix(REDDIT_MENTION_PREFIX)).matcher(text);
        while (matcher.find()) mentions.add(matcher.group().substring(REDDIT_MENTION_PREFIX.length()));
        return mentions;
    }

    private Set<String> getDistinctSubredditIDs() {
        Set<String> distinctSubredditIDs = new HashSet<>();
        Document numberOfDistinctSubreddits = getCollection(STATISTICS_COLLECTION).find(
                exists(NUMBER_OF_DISTINCT_SUBREDDITS_FIELD_NAME)).first();
        if (numberOfDistinctSubreddits != null &&
                numberOfDistinctSubreddits.getInteger(NUMBER_OF_DISTINCT_SUBREDDITS_FIELD_NAME) ==
                        getCollection(SUBREDDITS_COLLECTION).count())
            StreamSupport.stream(getCollection(SUBREDDITS_COLLECTION).find().spliterator(), true)
                    .forEach((Consumer<? super Document>) subreddit ->
                            distinctSubredditIDs.add(subreddit.getString(AGGREGATE_ID_FIELD_NAME)));
        else {
            StreamSupport.stream(getCollection(SUBMISSIONS_COLLECTION).aggregate(Arrays.asList(
                    group("$" + SUBREDDIT_ID_FIELD_NAME),
                    out(SUBREDDITS_COLLECTION + YEAR + SEPARATOR + MONTH))).allowDiskUse(true)
                    .spliterator(), true).forEach((Consumer<? super Document>) subreddit ->
                    distinctSubredditIDs.add(subreddit.getString(AGGREGATE_ID_FIELD_NAME)));
            getCollection(STATISTICS_COLLECTION).insertOne(
                    new Document(NUMBER_OF_DISTINCT_SUBREDDITS_FIELD_NAME, distinctSubredditIDs.size()));
        }
        distinctSubredditIDs.removeIf(Objects::isNull);
        return distinctSubredditIDs;
    }

    private Set<Document> getFilteredSubmissions(String subredditID) {
        Set<Document> filteredSubmissions = new HashSet<>();
        getCollection(SUBMISSIONS_COLLECTION).find(
                and(eq(SUBREDDIT_ID_FIELD_NAME, subredditID),
                        gte(NUMBER_OF_COMMENTS_FIELD_NAME, getCollection(COMMENTS_COLLECTION).count() /
                                getCollection(SUBMISSIONS_COLLECTION).count()),
                        gte(SUBMISSION_SELFTEXT_LENGTH_FIELD_NAME,
                                getSubmissionsSelftextsLength() / getCollection(SUBMISSIONS_COLLECTION).count())))
                .forEach((Block<? super Document>) submission -> {
                    if (Objects.requireNonNull(getCollection(AUTHORS_COLLECTION).find(
                            eq(AUTHOR_FIELD_NAME, submission.getString(AUTHOR_FIELD_NAME)))
                            .first()).getLong(COUNT_AUTHOR_FIELD_NAME) >=
                            (getCollection(SUBMISSIONS_COLLECTION).count() +
                                    getCollection(COMMENTS_COLLECTION).count()) / getNumberOfDistinctAuthors())
                        filteredSubmissions.add(submission);
                });
        return filteredSubmissions;
    }

    private long getSubmissionsSelftextsLength() {
        AtomicLong submissionsSelftextsLength = new AtomicLong(0L);
        Document submissionsBodyLengthDoc = getCollection(STATISTICS_COLLECTION).find(
                exists(SUBMISSIONS_SELFTEXTS_LENGTH_FIELD_NAME)).first();
        if (submissionsBodyLengthDoc == null) {
            StreamSupport.stream(getCollection(SUBMISSIONS_COLLECTION).find().spliterator(), true)
                    .forEach((Consumer<? super Document>) submission -> submissionsSelftextsLength.addAndGet(
                            submission.getDouble(SUBMISSION_SELFTEXT_LENGTH_FIELD_NAME).longValue()));
            getCollection(STATISTICS_COLLECTION).insertOne(
                    new Document(SUBMISSIONS_SELFTEXTS_LENGTH_FIELD_NAME, submissionsSelftextsLength));
        } else
            submissionsSelftextsLength.set(submissionsBodyLengthDoc.getLong(SUBMISSIONS_SELFTEXTS_LENGTH_FIELD_NAME));
        return submissionsSelftextsLength.get();
    }

    private Set<Document> getFilteredComments(String parentID) {
        Set<Document> filteredComments = new HashSet<>();
        getCollection(COMMENTS_COLLECTION).find(
                and(eq(PARENT_ID_FIELD_NAME, parentID),
                        gte(COMMENT_BODY_LENGTH_FIELD_NAME, getCommentsBodiesLength() /
                                getCollection(COMMENTS_COLLECTION).count())))
                .forEach((Block<? super Document>) comment -> {
                    if (Objects.requireNonNull(getCollection(AUTHORS_COLLECTION).find(
                            eq(AUTHOR_FIELD_NAME, comment.getString(AUTHOR_FIELD_NAME)))
                            .first()).getLong(COUNT_AUTHOR_FIELD_NAME) >=
                            (getCollection(SUBMISSIONS_COLLECTION).count() +
                                    getCollection(COMMENTS_COLLECTION).count()) / getNumberOfDistinctAuthors())
                        filteredComments.add(comment);
                });
        return filteredComments;
    }

    private long getCommentsBodiesLength() {
        AtomicLong commentsBodiesLength = new AtomicLong(0L);
        Document commentsBodyLengthDoc = getCollection(STATISTICS_COLLECTION).find(
                exists(COMMENTS_BODIES_LENGTH_FIELD_NAME)).first();
        if (commentsBodyLengthDoc == null) {
            StreamSupport.stream(getCollection(COMMENTS_COLLECTION).find().spliterator(), true)
                    .forEach((Consumer<? super Document>) comment -> commentsBodiesLength.addAndGet(
                            comment.getDouble(COMMENT_BODY_LENGTH_FIELD_NAME).longValue()));
            getCollection(STATISTICS_COLLECTION).insertOne(
                    new Document(COMMENTS_BODIES_LENGTH_FIELD_NAME, commentsBodiesLength));
        } else commentsBodiesLength.set(commentsBodyLengthDoc.getLong(COMMENTS_BODIES_LENGTH_FIELD_NAME));
        return commentsBodiesLength.get();
    }

    private int getNumberOfDistinctAuthors() {
        int numberOfDistinctAuthors;
        Document numberOfDistinctAuthorsDoc = getCollection(STATISTICS_COLLECTION).find(
                exists(NUMBER_OF_DISTINCT_AUTHORS_FIELD_NAME)).first();
        if (numberOfDistinctAuthorsDoc != null &&
                numberOfDistinctAuthorsDoc.getInteger(NUMBER_OF_DISTINCT_AUTHORS_FIELD_NAME) ==
                        getCollection(AUTHORS_COLLECTION).count())
            numberOfDistinctAuthors = numberOfDistinctAuthorsDoc.getInteger(NUMBER_OF_DISTINCT_AUTHORS_FIELD_NAME);
        else {
            Set<String> authors = new HashSet<>();
            StreamSupport.stream(getCollection(SUBMISSIONS_COLLECTION).aggregate(Collections.singletonList(
                    group("$" + AUTHOR_FIELD_NAME))).allowDiskUse(true).spliterator(), true)
                    .forEach((Consumer<? super Document>) authorSubmission ->
                            authors.add(authorSubmission.getString(AGGREGATE_ID_FIELD_NAME)));
            StreamSupport.stream(getCollection(COMMENTS_COLLECTION).aggregate(Collections.singletonList(
                    group("$" + AUTHOR_FIELD_NAME))).allowDiskUse(true).spliterator(), true)
                    .forEach((Consumer<? super Document>) authorComment ->
                            authors.add(authorComment.getString(AGGREGATE_ID_FIELD_NAME)));
            numberOfDistinctAuthors = authors.size();
            getCollection(STATISTICS_COLLECTION).insertOne(
                    new Document(NUMBER_OF_DISTINCT_AUTHORS_FIELD_NAME, numberOfDistinctAuthors));
            authors.parallelStream().forEach(author -> getCollection(AUTHORS_COLLECTION).insertOne(
                    new Document(AUTHOR_FIELD_NAME, author).append(COUNT_AUTHOR_FIELD_NAME,
                            getCollection(SUBMISSION_SELFTEXT_LENGTH_FIELD_NAME).count(eq(AUTHOR_FIELD_NAME, author)) +
                                    getCollection(COMMENTS_COLLECTION).count(eq(AUTHOR_FIELD_NAME, author)))));
            getCollection(AUTHORS_COLLECTION).createIndex(
                    new Document(AUTHOR_FIELD_NAME, 1), new IndexOptions().unique(true));
        }
        return numberOfDistinctAuthors;
    }
}