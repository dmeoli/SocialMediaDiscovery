package edu.uniba.di.lacam.kdde.donato.meoli.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.bson.Document;

import java.io.IOException;
import java.util.Properties;

public abstract class MongoDAO {

    protected static final int MONTH = SocialMediaDiscoveryConfiguration.getInstance().getMonth();
    protected static final int YEAR = SocialMediaDiscoveryConfiguration.getInstance().getYear();

    private static final String MONGO_PROPERTIES_FILE_NAME = "application.properties";
    private static final String URI_PROPERTY = "spring.data.mongodb.uri";

    protected static final String SEPARATOR = ".";

    private Properties mongoProps;
    private MongoDatabase mongoDatabase;

    public MongoDAO() {
        try {
            loadMongoProps();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoProps.getProperty(URI_PROPERTY)));
        mongoDatabase = mongoClient.getDatabase(SocialMediaDiscoveryConfiguration.getInstance().getDataset().getName());
    }

    synchronized private void loadMongoProps() throws IOException {
        mongoProps = new Properties();
        mongoProps.load(getClass().getClassLoader().getResourceAsStream(MONGO_PROPERTIES_FILE_NAME));
    }

    protected MongoCollection<Document> getCollection(String collection) {
        return mongoDatabase.getCollection(collection + SEPARATOR + YEAR + SEPARATOR + MONTH);
    }
}
