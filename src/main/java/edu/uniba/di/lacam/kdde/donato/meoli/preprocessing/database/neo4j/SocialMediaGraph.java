package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j;

import com.mongodb.Block;
import com.mongodb.client.MongoIterable;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.SocialMediaDatabase;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.ICommentLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.ILinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.IMentionLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.IReplyLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.IEmotionalLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.ILexicalSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.ISemanticSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.CommentLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.LinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.MentionLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.ReplyLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based.EmotionalLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based.LexicalSimilarityLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based.SemanticSimilarityLinkService;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SocialMediaGraph {

    private static final Logger LOGGER = LogManager.getLogger(SocialMediaGraph.class);

    private static final String DATASET = Character.toUpperCase(
            SocialMediaDiscoveryConfiguration.getInstance().getDataset().getName().charAt(0)) +
            SocialMediaDiscoveryConfiguration.getInstance().getDataset().getName().substring(1);
    private static final int MONTH = SocialMediaDiscoveryConfiguration.getInstance().getMonth();
    private static final int YEAR = SocialMediaDiscoveryConfiguration.getInstance().getYear();

    private List<LinkService<? extends Link>> linkServices;
    private List<ILinkRepository<? extends Link>> linkRepos;

    @Autowired
    public SocialMediaGraph(ICommentLinkRepository commentLinkRepo, CommentLinkService commentLinkService,
                            IReplyLinkRepository replyLinkRepo, ReplyLinkService replyLinkService,
                            IMentionLinkRepository mentionLinkRepo, MentionLinkService mentionLinkService,
                            ILexicalSimilarityLinkRepository lexicalSimilarityLinkRepo,
                            LexicalSimilarityLinkService lexicalSimilarityLinkService,
                            ISemanticSimilarityLinkRepository semanticSimilarityLinkRepo,
                            SemanticSimilarityLinkService semanticSimilarityLinkService,
                            IEmotionalLinkRepository emotionLinkRepo,
                            EmotionalLinkService emotionalLinkService) {
        linkRepos = Arrays.asList(commentLinkRepo, replyLinkRepo, mentionLinkRepo, lexicalSimilarityLinkRepo,
                semanticSimilarityLinkRepo, emotionLinkRepo);
        linkServices = Arrays.asList(commentLinkService, replyLinkService, mentionLinkService,
                lexicalSimilarityLinkService, semanticSimilarityLinkService, emotionalLinkService);
    }

    public void createSocialMediaGraph(SocialMediaDatabase socialMediaDB) {
        LOGGER.info("Please wait until creating {} graph of {}/{} is finished", DATASET, MONTH, YEAR);
        MongoIterable<String> discussionIDs = socialMediaDB.getDiscussionIDs();
        discussionIDs.forEach((Block<? super String>) discussionID -> {
            Discussion discussion = socialMediaDB.getDiscussion(discussionID);
            linkServices.forEach(linkService -> linkService.addLinks(discussion));
            LOGGER.info("Discussion {} added in the {} graph of {}/{}", discussionID, DATASET, MONTH, YEAR);
        });
        LOGGER.info("The creation of the {} graph of {}/{} is finished", DATASET, MONTH, YEAR);
    }

    public List<Link> getTemporalSubGraphs(LocalDateTime startUtc, LocalDateTime endUtc) {
        return linkRepos.parallelStream().flatMap(linkRepo -> {
            try {
                return linkRepo.findByUtcGreaterThanEqualAndUtcLessThan(startUtc, endUtc).get().stream();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return Stream.empty();
            }
        }).collect(Collectors.toList());
    }

    public LocalDateTime getFirstUtc() {
        return linkRepos.parallelStream().map(ILinkRepository::getFirstUtc).min(LocalDateTime::compareTo).orElse(null);
    }

    public LocalDateTime getLastUtc() {
        return linkRepos.parallelStream().map(ILinkRepository::getLastUtc).max(LocalDateTime::compareTo).orElse(null);
    }
}
