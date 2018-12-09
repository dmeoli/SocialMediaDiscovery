package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeSemanticSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeSemanticSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CumulativeSemanticSimilarityLinkService extends CumulativeLinkService<CumulativeSemanticSimilarityLink> {

    public CumulativeSemanticSimilarityLinkService() { }

    @Autowired
    public CumulativeSemanticSimilarityLinkService(ICumulativeSemanticSimilarityLinkRepository cumulativeSemanticSimilarityLinkRepo) {
       super(cumulativeSemanticSimilarityLinkRepo);
    }

    @Override
    public void cumulateLinks(Collection<? extends Link> temporalSubGraph) {
        temporalSubGraph.stream().filter(link -> link.getClass().equals(SemanticSimilarityLink.class)).forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeSemanticSimilarityLink> cumulativeSemanticSimilarityLink =
                    cumulativeLinkRepo.findByCumulativeUserFromNameAndCumulativeUserToName(cumulativeUserFrom.getName(),
                            cumulativeUserTo.getName());
            if (cumulativeSemanticSimilarityLink.isPresent()) {
                cumulativeSemanticSimilarityLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeSemanticSimilarityLink.get());
            } else {
                SparseArray cumulativeTemporalSubGraphsCounter = new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize());
                cumulativeTemporalSubGraphsCounter.add(temporalSubGraphNumber, 1);
                cumulativeLinkRepo.save(
                        new CumulativeSemanticSimilarityLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}