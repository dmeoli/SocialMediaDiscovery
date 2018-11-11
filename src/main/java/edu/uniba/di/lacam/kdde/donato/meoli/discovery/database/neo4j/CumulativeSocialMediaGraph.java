package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.node.ICumulativeUserRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship.*;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining.GraphMining;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class CumulativeSocialMediaGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMining.class);

    private LocalDateTime startUtc;
    private LocalDateTime endUtc;
    private ICumulativeUserRepository cumulativeUserRepo;
    private List<CumulativeLinkService<? extends CumulativeLink>> cumulativeLinkServices;

    @Autowired
    public CumulativeSocialMediaGraph(ICumulativeUserRepository cumulativeUserRepo,
                                      CumulativeCommentLinkService cumulativeCommentLinkService,
                                      CumulativeReplyLinkService cumulativeReplyLinkService,
                                      CumulativeMentionLinkService cumulativeMentionLinkService,
                                      CumulativeLexicalSimilarityLinkService cumulativeLexicalSimilarityLinkService,
                                      CumulativeSemanticSimilarityLinkService cumulativeSemanticSimilarityLinkService,
                                      CumulativeEmotionalLinkService cumulativeEmotionalLinkService) {
        this.cumulativeUserRepo = cumulativeUserRepo;
        cumulativeLinkServices = new ArrayList<>(
                Arrays.asList(cumulativeCommentLinkService, cumulativeReplyLinkService, cumulativeMentionLinkService));
        if (SocialMediaDiscoveryConfiguration.getInstance().useLexicalSimilarityLinks())
            cumulativeLinkServices.add(cumulativeLexicalSimilarityLinkService);
        if (SocialMediaDiscoveryConfiguration.getInstance().useSemanticSimilarityLinks())
            cumulativeLinkServices.add(cumulativeSemanticSimilarityLinkService);
        if (SocialMediaDiscoveryConfiguration.getInstance().useEmotionalLinks())
            cumulativeLinkServices.add(cumulativeEmotionalLinkService);
    }

    public LocalDateTime getStartUtc() {
        return startUtc;
    }

    public void setStartUtc(LocalDateTime startUtc) {
        this.startUtc = startUtc;
    }

    public LocalDateTime getEndUtc() {
        return endUtc;
    }

    public void setEndUtc(LocalDateTime endUtc) {
        this.endUtc = endUtc;
    }

    public void cumulateTemporalSubGraphs(Collection<Link> temporalSubGraph) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkService ->
                cumulativeLinkService.cumulateLinks(temporalSubGraph));
    }

    public void normalizeCumulativeSocialMediaGraph() {
        cumulativeLinkServices.parallelStream().forEach(CumulativeLinkService::normalizeCumulativeLinks);
    }

    public void deleteCumulativeSocialMediaGraph() {
        cumulativeUserRepo.deleteAll();
    }

    public void setTemporalSubGraphsMinutes(int temporalSubGraphsMinutes) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkService ->
                cumulativeLinkService.setTemporalSubGraphsMinutes(temporalSubGraphsMinutes));
    }

    public void setTemporalSubGraphNumber(int temporalSubGraphNumber) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkService ->
                cumulativeLinkService.setTemporalSubGraphNumber(temporalSubGraphNumber));
    }

    public void setCumulativeTemporalGraphNumber(int cumulativeTemporalGraphNumber) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkService ->
                cumulativeLinkService.setCumulativeTemporalGraphNumber(cumulativeTemporalGraphNumber));
    }

    public void computeCentralities() {
        long t = System.currentTimeMillis();
        LOGGER.info("Starting the computation of the In-Degrees on the Cumulative Temporal Graph from {} to {}",
                startUtc, endUtc);
        cumulativeUserRepo.computeInDegree();
        LOGGER.info("The computation of the In-Degrees on the Cumulative Temporal Graph from {} to {} has been " +
                "finished in {} msec.", startUtc, endUtc, System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        LOGGER.info("Starting the computation of the Out-Degrees on the Cumulative Temporal Graph from {} to {}",
                startUtc, endUtc);
        cumulativeUserRepo.computeOutDegree();
        LOGGER.info("The computation of the Out-Degrees on the Cumulative Temporal Graph from {} to {} has been " +
                "finished in {} msec.", startUtc, endUtc, System.currentTimeMillis() - t);
        LOGGER.info("Starting the computation of the Page Rank on the Cumulative Temporal Graph from {} to {}",
                startUtc, endUtc);
        t = cumulativeUserRepo.computePageRank();
        LOGGER.info("The computation of the Page Rank on the Cumulative Temporal Graph from {} to {} has been " +
                "finished in {} sec.", startUtc, endUtc, t);
        LOGGER.info("Starting the computation of the Betweenness Centrality on the Cumulative Temporal Graph from {} " +
                "to {}", startUtc, endUtc);
        t = cumulativeUserRepo.computeBetweennessCentrality();
        LOGGER.info("The computation of the Betweenness Centrality on the Cumulative Temporal Graph from {} to {} has " +
                "been finished in {} sec.", startUtc, endUtc, t);
    }

    public void computeCommunityDetection() {
        LOGGER.info("Starting the computation of the Louvain Community Detection on the Cumulative Temporal Graph from " +
                "{} to {}", startUtc, endUtc);
        long t = cumulativeUserRepo.computeLouvain();
        LOGGER.info("The computation of the Louvain Community Detection on the Cumulative Temporal Graph from {} to {} " +
                "has been finished in {} sec.", startUtc, endUtc, t);
    }

    public Map<String, CumulativeUser> getFilteredCumulativeUsers(int nodeIndicatorsThreshold) {
        return cumulativeUserRepo.getFilteredCumulativeUsers(nodeIndicatorsThreshold)
                .parallelStream().collect(toMap(CumulativeUser::getName, Function.identity()));
    }
}
