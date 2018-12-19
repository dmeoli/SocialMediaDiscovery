package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeReplyLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeReplyLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
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
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream().filter(link -> link.getClass().equals(ReplyLink.class))
                .collect(Collectors.groupingByConcurrent(link ->
                        Pair.of(new CumulativeUser(link.getUserFrom()), new CumulativeUser(link.getUserTo()))))
                .entrySet().parallelStream().map(entry -> {
                    Optional<CumulativeReplyLink> optCumulativeReplyLink;
                    CumulativeReplyLink cumulativeReplyLink = (optCumulativeReplyLink = cumulativeLinkRepo.
                            findByCumulativeUserFromNameAndCumulativeUserToName(entry.getKey().getLeft().getName(),
                                    entry.getKey().getRight().getName())).isPresent() ? optCumulativeReplyLink.get() :
                            new CumulativeReplyLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                    new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize()));
                    entry.getValue().forEach(link ->
                            cumulativeReplyLink.incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber));
                    return cumulativeReplyLink;
                }).collect(Collectors.toList()));
    }
}
