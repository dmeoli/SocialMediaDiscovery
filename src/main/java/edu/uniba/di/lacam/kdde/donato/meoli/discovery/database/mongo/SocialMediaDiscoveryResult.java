package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo;

import com.google.gson.GsonBuilder;
import edu.uniba.di.lacam.kdde.donato.meoli.database.mongo.MongoDAO;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo.domain.Result;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.bson.Document;

public class SocialMediaDiscoveryResult extends MongoDAO {

    private static final String RESULTS_COLLECTION = "results";

    public void insertResult(Result result, int cumulativeTemporalGraphMinutes, int temporalSubGraphsMinutes, float minSupport) {
        StringBuilder sb = new StringBuilder();
        sb.append(RESULTS_COLLECTION).append(SEPARATOR);
        if (SocialMediaDiscoveryConfiguration.getInstance().useLexicalSimilarityLinks())
            sb.append("l").append(SEPARATOR);
        if (SocialMediaDiscoveryConfiguration.getInstance().useSemanticSimilarityLinks())
            sb.append("s").append(SEPARATOR);
        if (SocialMediaDiscoveryConfiguration.getInstance().useEmotionalLinks())
            sb.append("e").append(SEPARATOR);
        sb.append(cumulativeTemporalGraphMinutes).append(SEPARATOR)
                .append(temporalSubGraphsMinutes).append(SEPARATOR)
                .append(minSupport);
        getCollection(sb.toString()).insertOne(Document.parse(
                new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(result)));
    }
}
