package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo;

import com.google.gson.GsonBuilder;
import edu.uniba.di.lacam.kdde.donato.meoli.database.mongo.MongoDAO;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo.domain.Result;
import org.bson.Document;

public class SocialMediaDiscoveryResult extends MongoDAO {

    private static final String RESULTS_COLLECTION = "results";

    public void insertResult(Result result, int cumulativeTemporalGraphMinutes, int temporalSubGraphsMinutes, float minSupport) {
        getCollection(RESULTS_COLLECTION + SEPARATOR + cumulativeTemporalGraphMinutes + SEPARATOR +
                temporalSubGraphsMinutes + SEPARATOR + minSupport).insertOne(Document.parse(
                new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(result)));
    }
}
