package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLexicalSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLexicalSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeLexicalSimilarityLinkService extends CumulativeLinkService<CumulativeLexicalSimilarityLink> {

    public CumulativeLexicalSimilarityLinkService() { }

    @Autowired
    public CumulativeLexicalSimilarityLinkService(ICumulativeLexicalSimilarityLinkRepository cumulativeLexicalSimilarityLinkRepo) {
        super(cumulativeLexicalSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.parallelStream().forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeLexicalSimilarityLink> cumulativeLexicalSimilarityLink =
                    cumulativeLinkRepo.findByCumulativeUserFromAndCumulativeUserTo(cumulativeUserFrom, cumulativeUserTo);
            if (cumulativeLexicalSimilarityLink.isPresent()) {
                cumulativeLexicalSimilarityLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeLexicalSimilarityLink.get());
            } else {
                int[] cumulativeTemporalSubGraphsCounter = new int[getCumulativeGraphCounterArraySize()];
                cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber] = 1;
                cumulativeLinkRepo.save(
                        new CumulativeLexicalSimilarityLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
