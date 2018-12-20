package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeEmotionalLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeEmotionalLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CumulativeEmotionalLinkService extends CumulativeLinkService<CumulativeEmotionalLink> {

    public CumulativeEmotionalLinkService() { }

    @Autowired
    public CumulativeEmotionalLinkService(ICumulativeEmotionalLinkRepository cumulativeEmotionalSimilarityLinkRepo) {
        super(cumulativeEmotionalSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream()
                .filter(link -> link.getClass().equals(EmotionalLink.class))
                .collect(Collectors.groupingByConcurrent(link -> Pair.of(link.getUserFrom(), link.getUserTo())))
                .entrySet().parallelStream().map(entry -> cumulativeLinkRepo
                        .findByCumulativeUserFromNameAndCumulativeUserToName(entry.getKey().getLeft().getName(),
                                entry.getKey().getRight().getName())
                        .map(cumulativeEmotionalLink -> {
                            cumulativeEmotionalLink.updateCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber,
                                    entry.getValue().size());
                            return cumulativeEmotionalLink;
                        }).orElseGet(() -> new CumulativeEmotionalLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize(),
                                        temporalSubGraphNumber, entry.getValue().size()))))
                .collect(Collectors.toList()));
    }
}
