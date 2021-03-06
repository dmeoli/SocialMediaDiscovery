package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeSemanticSimilarityLink.CUMULATIVE_SEMANTIC_SIMILARITY_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_SEMANTIC_SIMILARITY_LINK_LABEL)
public class CumulativeSemanticSimilarityLink extends CumulativeLink {

    public static final String CUMULATIVE_SEMANTIC_SIMILARITY_LINK_LABEL = "CUMULATIVE_SEMANTIC_SIMILAR_TO";

    public CumulativeSemanticSimilarityLink() { }

    public CumulativeSemanticSimilarityLink(User userFrom, User userTo, SparseArray cumulativeTemporalSubGraphsCounter) {
        super(userFrom, userTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().writesCumulativeSemanticallySimilarTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeSemanticSimilarityLink
                && ((CumulativeSemanticSimilarityLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeSemanticSimilarityLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}
