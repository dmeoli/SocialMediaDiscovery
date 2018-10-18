package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongodb;

import com.google.gson.GsonBuilder;
import edu.uniba.di.lacam.kdde.donato.meoli.database.mongodb.MongoDAO;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongodb.domain.Result;
import org.bson.Document;

public class SocialMediaDiscoveryResult extends MongoDAO {

    private static final String RESULTS_COLLECTION = "results";

    public void insertResult(Result result, int cumulativeTemporalGraphMinutes, int temporalSubGraphsMinutes, float minSupport) {
        getCollection(RESULTS_COLLECTION + SEPARATOR + cumulativeTemporalGraphMinutes + SEPARATOR +
                temporalSubGraphsMinutes + SEPARATOR + minSupport).insertOne(Document.parse(
                new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(result)));
    }
}
