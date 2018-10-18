package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.node.ICumulativeUserRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship.*;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class CumulativeSocialMediaGraph {

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

    public void cumulateTemporalSubGraphs(Collection<Link> temporalSubGraph) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkRepo ->
                cumulativeLinkRepo.cumulateLinks(temporalSubGraph));
    }

    public void normalizeCumulativeSocialMediaGraph() {
        cumulativeLinkServices.parallelStream().forEach(CumulativeLinkService::normalizeCumulativeLinks);
    }

    public void deleteCumulativeSocialMediaGraph() {
        cumulativeUserRepo.deleteAll();
    }

    public void setTemporalSubGraphNumber(int temporalSubGraphNumber) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkRepo ->
                cumulativeLinkRepo.setTemporalSubGraphNumber(temporalSubGraphNumber));
    }

    public void setTemporalSubGraphsMinutes(int temporalSubGraphsMinutes) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkRepo ->
                cumulativeLinkRepo.setTemporalSubGraphsMinutes(temporalSubGraphsMinutes));
    }

    public void setCumulativeTemporalGraphNumber(int cumulativeTemporalGraphNumber) {
        cumulativeLinkServices.parallelStream().forEach(cumulativeLinkRepo ->
                cumulativeLinkRepo.setCumulativeTemporalGraphNumber(cumulativeTemporalGraphNumber));
    }

    public void computeCentralities() {
        cumulativeUserRepo.computeInDegree();
        cumulativeUserRepo.computeOutDegree();
        cumulativeUserRepo.computePageRank();
        cumulativeUserRepo.computeBetweennessCentrality();
    }

    public void computeCommunityDetection() {
        cumulativeUserRepo.computeLouvain();
    }

    public Map<String, CumulativeUser> getFilteredCumulativeUsers(int nodeIndicatorsThreshold) {
        return cumulativeUserRepo.getFilteredCumulativeUsers(nodeIndicatorsThreshold)
                .parallelStream().collect(toMap(CumulativeUser::getName, Function.identity()));
    }
}
