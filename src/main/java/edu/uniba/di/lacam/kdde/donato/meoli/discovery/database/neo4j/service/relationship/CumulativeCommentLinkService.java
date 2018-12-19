package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeCommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeCommentLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
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
        cumulativeLinkRepo.saveAll(temporalSubGraph.parallelStream().filter(link -> link.getClass().equals(CommentLink.class))
                .collect(Collectors.groupingByConcurrent(link ->
                        Pair.of(new CumulativeUser(link.getUserFrom()), new CumulativeUser(link.getUserTo()))))
                .entrySet().parallelStream().map(entry -> {
                    Optional<CumulativeCommentLink> optCumulativeCommentLink;
                    CumulativeCommentLink cumulativeCommentLink = (optCumulativeCommentLink = cumulativeLinkRepo.
                            findByCumulativeUserFromNameAndCumulativeUserToName(entry.getKey().getLeft().getName(),
                                    entry.getKey().getRight().getName())).isPresent() ? optCumulativeCommentLink.get() :
                            new CumulativeCommentLink(entry.getKey().getLeft(), entry.getKey().getRight(),
                                    new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize()));
                    entry.getValue().forEach(link ->
                            cumulativeCommentLink.incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber));
                    return cumulativeCommentLink;
                }).collect(Collectors.toList()));
    }
}
