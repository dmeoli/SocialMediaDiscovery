package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLexicalSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLexicalSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CumulativeLexicalSimilarityLinkService extends CumulativeLinkService<CumulativeLexicalSimilarityLink> {

    public CumulativeLexicalSimilarityLinkService() { }

    @Autowired
    public CumulativeLexicalSimilarityLinkService(ICumulativeLexicalSimilarityLinkRepository cumulativeLexicalSimilarityLinkRepo) {
        super(cumulativeLexicalSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream().filter(link -> link.getClass().equals(LexicalSimilarityLink.class))
                .collect(Collectors.groupingByConcurrent(link ->
                        Pair.of(new CumulativeUser(link.getUserFrom()), new CumulativeUser(link.getUserTo()))))
                .entrySet().parallelStream().map(entry -> {
                    Optional<CumulativeLexicalSimilarityLink> optCumulativeLexicalSimilarityLink;
                    CumulativeLexicalSimilarityLink cumulativeLexicalSimilarityLink = (optCumulativeLexicalSimilarityLink = cumulativeLinkRepo.
                            findByCumulativeUserFromNameAndCumulativeUserToName(entry.getKey().getLeft().getName(),
                                    entry.getKey().getRight().getName())).isPresent() ? optCumulativeLexicalSimilarityLink.get() :
                            new CumulativeLexicalSimilarityLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                    new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize()));
                    entry.getValue().forEach(link ->
                            cumulativeLexicalSimilarityLink.incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber));
                    return cumulativeLexicalSimilarityLink;
                }).collect(Collectors.toList()));
    }
}
