package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeReplyLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeReplyLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CumulativeReplyLinkService extends CumulativeLinkService<CumulativeReplyLink> {

    public CumulativeReplyLinkService() { }

    @Autowired
    public CumulativeReplyLinkService(ICumulativeReplyLinkRepository cumulativeReplyLinkRepo) {
        super(cumulativeReplyLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream()
                .filter(link -> link.getClass().equals(ReplyLink.class))
                .collect(Collectors.groupingByConcurrent(link -> Pair.of(link.getUserFrom(), link.getUserTo())))
                .entrySet().parallelStream()
                .map(entry -> cumulativeLinkRepo.findByCumulativeUserFromNameAndCumulativeUserToName(
                        entry.getKey().getLeft().getName(), entry.getKey().getRight().getName())
                        .map(cumulativeReplyLink -> {
                            cumulativeReplyLink.updateCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber,
                                    entry.getValue().size());
                            return cumulativeReplyLink;
                        }).orElseGet(() -> new CumulativeReplyLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize(),
                                        temporalSubGraphNumber, entry.getValue().size()))))
                .collect(Collectors.toList()));
    }
}
