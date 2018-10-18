package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeMentionLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeMentionLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeMentionLinkService extends CumulativeLinkService<CumulativeMentionLink> {

    public CumulativeMentionLinkService() { }

    @Autowired
    public CumulativeMentionLinkService(ICumulativeMentionLinkRepository cumulativeMentionLinkRepo) {
        super(cumulativeMentionLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.parallelStream().forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeMentionLink> cumulativeMentionLink =
                    cumulativeLinkRepo.findByCumulativeUserFromAndCumulativeUserTo(cumulativeUserFrom, cumulativeUserTo);
            if (cumulativeMentionLink.isPresent()) {
                cumulativeMentionLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeMentionLink.get());
            } else {
                int[] cumulativeTemporalSubGraphsCounter = new int[getCumulativeGraphCounterArraySize()];
                cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber] = 1;
                cumulativeLinkRepo.save(
                        new CumulativeMentionLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
