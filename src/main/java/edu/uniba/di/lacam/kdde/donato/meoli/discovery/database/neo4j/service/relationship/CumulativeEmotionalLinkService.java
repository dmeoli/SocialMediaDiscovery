package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeEmotionalLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeEmotionalLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeEmotionalLinkService extends CumulativeLinkService<CumulativeEmotionalLink> {

    public CumulativeEmotionalLinkService() { }

    @Autowired
    public CumulativeEmotionalLinkService(ICumulativeEmotionalLinkRepository cumulativeEmotionalSimilarityLinkRepo) {
        super(cumulativeEmotionalSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.parallelStream().forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeEmotionalLink> cumulativeEmotionalSimilarityLink =
                    cumulativeLinkRepo.findByCumulativeUserFromAndCumulativeUserTo(cumulativeUserFrom, cumulativeUserTo);
            if (cumulativeEmotionalSimilarityLink.isPresent()) {
                cumulativeEmotionalSimilarityLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeEmotionalSimilarityLink.get());
            } else {
                int[] cumulativeTemporalSubGraphsCounter = new int[getCumulativeGraphCounterArraySize()];
                cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber] = 1;
                cumulativeLinkRepo.save(
                        new CumulativeEmotionalLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
