package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeMentionLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeMentionLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.MentionLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CumulativeMentionLinkService extends CumulativeLinkService<CumulativeMentionLink> {

    public CumulativeMentionLinkService() { }

    @Autowired
    public CumulativeMentionLinkService(ICumulativeMentionLinkRepository cumulativeMentionLinkRepo) {
        super(cumulativeMentionLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream().filter(link -> link.getClass().equals(MentionLink.class))
                .collect(Collectors.groupingByConcurrent(link ->
                        Pair.of(new CumulativeUser(link.getUserFrom()), new CumulativeUser(link.getUserTo()))))
                .entrySet().parallelStream().map(entry -> {
                    Optional<CumulativeMentionLink> optCumulativeMentionLink;
                    CumulativeMentionLink cumulativeMentionLink = (optCumulativeMentionLink = cumulativeLinkRepo.
                            findByCumulativeUserFromNameAndCumulativeUserToName(entry.getKey().getLeft().getName(),
                                    entry.getKey().getRight().getName())).isPresent() ? optCumulativeMentionLink.get() :
                            new CumulativeMentionLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                    new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize()));
                    entry.getValue().forEach(link ->
                            cumulativeMentionLink.incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber));
                    return cumulativeMentionLink;
                }).collect(Collectors.toList()));
    }
}
