package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeCommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeCommentLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CumulativeCommentLinkService extends CumulativeLinkService<CumulativeCommentLink> {

    public CumulativeCommentLinkService() { }

    @Autowired
    public CumulativeCommentLinkService(ICumulativeCommentLinkRepository cumulativeCommentLinkRepo) {
        super(cumulativeCommentLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream()
                .filter(link -> link.getClass().equals(CommentLink.class))
                .collect(Collectors.groupingByConcurrent(link ->
                        Pair.of(link.getUserFrom(), link.getUserTo())))
                .entrySet().parallelStream()
                .map(entry -> cumulativeLinkRepo.findByCumulativeUserFromNameAndCumulativeUserToName(
                        entry.getKey().getLeft().getName(), entry.getKey().getRight().getName())
                        .map(cumulativeCommentLink -> {
                            cumulativeCommentLink.updateCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber,
                                    entry.getValue().size());
                            return cumulativeCommentLink;
                        }).orElseGet(() -> new CumulativeCommentLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize(),
                                        temporalSubGraphNumber, entry.getValue().size()))))
                .collect(Collectors.toList()));
    }
}
