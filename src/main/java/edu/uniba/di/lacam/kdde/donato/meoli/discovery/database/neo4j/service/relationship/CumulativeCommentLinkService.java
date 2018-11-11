package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeCommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeCommentLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeCommentLinkService extends CumulativeLinkService<CumulativeCommentLink> {

    public CumulativeCommentLinkService() { }

    @Autowired
    public CumulativeCommentLinkService(ICumulativeCommentLinkRepository cumulativeCommentLinkRepo) {
        super(cumulativeCommentLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.stream().filter(link -> link.getClass().equals(CommentLink.class)).forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeCommentLink> cumulativeCommentLink =
                    cumulativeLinkRepo.findByCumulativeUserFromAndCumulativeUserTo(cumulativeUserFrom, cumulativeUserTo);
            if (cumulativeCommentLink.isPresent()) {
                cumulativeCommentLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeCommentLink.get());
            } else {
                int[] cumulativeTemporalSubGraphsCounter = new int[getCumulativeGraphCounterArraySize()];
                cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber] = 1;
                cumulativeLinkRepo.save(
                        new CumulativeCommentLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
