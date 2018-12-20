package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeSemanticSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeSemanticSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CumulativeSemanticSimilarityLinkService extends CumulativeLinkService<CumulativeSemanticSimilarityLink> {

    public CumulativeSemanticSimilarityLinkService() {
    }

    @Autowired
    public CumulativeSemanticSimilarityLinkService(ICumulativeSemanticSimilarityLinkRepository cumulativeSemanticSimilarityLinkRepo) {
        super(cumulativeSemanticSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream()
                .filter(link -> link.getClass().equals(SemanticSimilarityLink.class))
                .collect(Collectors.groupingByConcurrent(link -> Pair.of(link.getUserFrom(), link.getUserTo())))
                .entrySet().parallelStream()
                .map(entry -> cumulativeLinkRepo.findByCumulativeUserFromNameAndCumulativeUserToName(
                        entry.getKey().getLeft().getName(), entry.getKey().getRight().getName())
                        .map(cumulativeSemanticSimilarityLink -> {
                            cumulativeSemanticSimilarityLink.updateCumulativeTemporalSubGraphsCounter(
                                    temporalSubGraphNumber, entry.getValue().size());
                            return cumulativeSemanticSimilarityLink;
                        }).orElseGet(() -> new CumulativeSemanticSimilarityLink(entry.getKey().getLeft(),
                                entry.getKey().getRight(), new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize(),
                                temporalSubGraphNumber, entry.getValue().size()))))
                .collect(Collectors.toList()));
    }
}