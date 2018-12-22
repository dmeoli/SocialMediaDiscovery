package edu.uniba.di.lacam.kdde.donato.meoli;

import edu.uniba.di.lacam.kdde.donato.meoli.database.Dataset;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining.GraphMining;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.RedditDatabase;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.SocialMediaGraph;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Month;

@SpringBootApplication
@EnableAsync
public class SocialMediaDiscoveryApplication implements CommandLineRunner {

    private static final int YEAR = 2017;
    private static final int MONTH = Month.NOVEMBER.getValue();

    private static final int CUMULATIVE_TEMPORAL_GRAPH_MINUTES = 60 * 24 * 2;
    private static final int TEMPORAL_SUB_GRAPHS_MINUTES = 60 * 2 * 2;

    private static final float MIN_SUPPORT = 0.1F;

    private final RedditDatabase redditDatabase;
    // private final TwitterDatabase twitterDatabase;
    private final SocialMediaGraph socialMediaGraph;
    private final GraphMining graphMining;

    static {
        SocialMediaDiscoveryConfiguration.getInstance().setDataset(Dataset.REDDIT);
        // SocialMediaDiscoveryConfiguration.getInstance().setDataset(Dataset.TWITTER);
        SocialMediaDiscoveryConfiguration.getInstance().setYear(YEAR);
        SocialMediaDiscoveryConfiguration.getInstance().setMonth(MONTH);
        SocialMediaDiscoveryConfiguration.getInstance().setLexicalSimilarityLinks(true);
        SocialMediaDiscoveryConfiguration.getInstance().setSemanticSimilarityLinks(true);
        SocialMediaDiscoveryConfiguration.getInstance().setEmotionalLinks(true);
        SocialMediaDiscoveryConfiguration.getInstance().setCumulativeTemporalGraphMinutes(CUMULATIVE_TEMPORAL_GRAPH_MINUTES);
        SocialMediaDiscoveryConfiguration.getInstance().setTemporalSubGraphsMinutes(TEMPORAL_SUB_GRAPHS_MINUTES);
        SocialMediaDiscoveryConfiguration.getInstance().setFrequentPatternMinSupport(MIN_SUPPORT);
    }

    @Autowired
    public SocialMediaDiscoveryApplication(RedditDatabase redditDatabase, SocialMediaGraph socialMediaGraph,
                                           GraphMining graphMining) {
        this.redditDatabase = redditDatabase;
        this.socialMediaGraph = socialMediaGraph;
        this.graphMining = graphMining;
    }

    /* @Autowired
    public SocialMediaDiscoveryApplication(TwitterDatabase twitterDatabase, SocialMediaGraph socialMediaGraph,
                                           GraphMining graphMining) {
        this.twitterDatabase = twitterDatabase;
        this.socialMediaGraph = socialMediaGraph;
        this.graphMining = graphMining;
    } */

    @Override
    public void run(String... args) {
        // redditDatabase.createSocialMediaDatabase();
        // socialMediaGraph.createSocialMediaGraph(redditDatabase);
        graphMining.executeTemporalSocialMediaAnalysis();
    }

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaDiscoveryApplication.class, args);
    }
}
