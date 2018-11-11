package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeReplyLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeReplyLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeReplyLinkService extends CumulativeLinkService<CumulativeReplyLink> {

    public CumulativeReplyLinkService() { }

    @Autowired
    public CumulativeReplyLinkService(ICumulativeReplyLinkRepository cumulativeReplyLinkRepo) {
        super(cumulativeReplyLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.stream().filter(link -> link.getClass().equals(ReplyLink.class)).forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeReplyLink> cumulativeReplyLink =
                    cumulativeLinkRepo.findByCumulativeUserFromAndCumulativeUserTo(cumulativeUserFrom, cumulativeUserTo);
            if (cumulativeReplyLink.isPresent()) {
                cumulativeReplyLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeReplyLink.get());
            } else {
                int[] cumulativeTemporalSubGraphsCounter = new int[getCumulativeGraphCounterArraySize()];
                cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber] = 1;
                cumulativeLinkRepo.save(
                        new CumulativeReplyLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
