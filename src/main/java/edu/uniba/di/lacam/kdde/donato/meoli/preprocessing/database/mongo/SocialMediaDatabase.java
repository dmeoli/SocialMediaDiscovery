package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import edu.uniba.di.lacam.kdde.donato.meoli.database.mongo.MongoDAO;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.StanfordNLPAnalyzer;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.ContentBasedFeatureExtraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Component
public abstract class SocialMediaDatabase extends MongoDAO {

    static final StanfordNLPAnalyzer stanfordNLPAnalyzer = StanfordNLPAnalyzer.getInstance();

    private GridFsTemplate socialMediaCollection;

    public SocialMediaDatabase() { }

    @Autowired
    SocialMediaDatabase(GridFsTemplate socialMediaCollection) {
        this.socialMediaCollection = socialMediaCollection;
    }

    public MongoIterable<String> getDiscussionIDs() {
        return socialMediaCollection.find(new Query()).map(GridFSFile::getFilename);
    }

    void addDiscussion(Discussion discussion) {
        if (Objects.isNull(socialMediaCollection.findOne(Query.query(GridFsCriteria.whereFilename().is(discussion.getID()))))) {
            ContentBasedFeatureExtraction.extractContentBasedFeatures(discussion);
            socialMediaCollection.store(
                    new ByteArrayInputStream(
                            new GsonBuilder().disableHtmlEscaping().create().toJson(discussion, Discussion.class)
                                    .getBytes()),
                    discussion.getID());
        }
    }

    public Discussion getDiscussion(String discussionID) {
        try {
            return new GsonBuilder().disableHtmlEscaping().create().fromJson(
                    new JsonParser().parse(
                            new InputStreamReader(
                                    socialMediaCollection.getResource(discussionID).getInputStream())),
                    Discussion.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void createSocialMediaDatabase();
}
