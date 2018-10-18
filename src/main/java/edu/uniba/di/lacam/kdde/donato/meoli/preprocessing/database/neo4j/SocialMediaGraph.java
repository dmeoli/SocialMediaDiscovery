package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.SocialMediaDatabase;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class SocialMediaGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialMediaGraph.class);

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
        Set<String> discussionIDs = socialMediaDB.getDiscussionIDs();
        discussionIDs.forEach(discussionID -> {
            Discussion discussion = socialMediaDB.getDiscussion(discussionID);
            linkServices.forEach(linkService -> linkService.addLinks(discussion));
            LOGGER.info("Discussion {} added in the {} graph of {}/{}", discussionID, DATASET, MONTH, YEAR);
        });
        LOGGER.info("The creation of the {} graph of {}/{} is finished", DATASET, MONTH, YEAR);
    }

    public Collection<Link> getTemporalSubGraphs(LocalDateTime startUtc, LocalDateTime endUtc) {
        List<Link> temporalSubGraphs = new ArrayList<>();
        linkRepos.parallelStream().map(linkRepo ->
                linkRepo.findByUTCGreaterThanEqualAndUTCLessThan(startUtc, endUtc)).forEach(temporalSubGraphs::addAll);
        return temporalSubGraphs;
    }

    public LocalDateTime getFirstUTC() {
        return linkRepos.parallelStream().map(ILinkRepository::getFirstUTC).min(LocalDateTime::compareTo).orElse(null);
    }

    public LocalDateTime getLastUTC() {
        return linkRepos.parallelStream().map(ILinkRepository::getLastUTC).max(LocalDateTime::compareTo).orElse(null);
    }
}
