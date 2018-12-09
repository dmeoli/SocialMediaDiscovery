package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLexicalSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLexicalSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink;
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
        temporalSubGraph.stream().filter(link -> link.getClass().equals(LexicalSimilarityLink.class)).forEach(link -> {
            CumulativeUser cumulativeUserFrom = new CumulativeUser(link.getUserFrom());
            CumulativeUser cumulativeUserTo = new CumulativeUser(link.getUserTo());
            Optional<CumulativeLexicalSimilarityLink> cumulativeLexicalSimilarityLink =
                    cumulativeLinkRepo.findByCumulativeUserFromNameAndCumulativeUserToName(cumulativeUserFrom.getName(),
                            cumulativeUserTo.getName());
            if (cumulativeLexicalSimilarityLink.isPresent()) {
                cumulativeLexicalSimilarityLink.get().incrementCumulativeTemporalSubGraphsCounter(temporalSubGraphNumber);
                cumulativeLinkRepo.save(cumulativeLexicalSimilarityLink.get());
            } else {
                SparseArray cumulativeTemporalSubGraphsCounter = new SparseArray(getCumulativeTemporalSubGraphsCounterArraySize());
                cumulativeTemporalSubGraphsCounter.add(temporalSubGraphNumber, 1);
                cumulativeLinkRepo.save(
                        new CumulativeLexicalSimilarityLink(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter));
            }
        });
    }
}
