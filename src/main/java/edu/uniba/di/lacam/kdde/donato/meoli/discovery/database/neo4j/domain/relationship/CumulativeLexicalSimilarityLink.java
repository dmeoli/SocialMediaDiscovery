package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLexicalSimilarityLink.CUMULATIVE_LEXICAL_SIMILARITY_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_LEXICAL_SIMILARITY_LINK_LABEL)
public class CumulativeLexicalSimilarityLink extends CumulativeLink {

    public static final String CUMULATIVE_LEXICAL_SIMILARITY_LINK_LABEL = "CUMULATIVE_LEXICAL_SIMILAR_TO";

    public CumulativeLexicalSimilarityLink() { }

    public CumulativeLexicalSimilarityLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                                           SparseArray cumulativeTemporalSubGraphsCounter) {
        super(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().writesCumulativeLexicallySimilarTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeLexicalSimilarityLink
                && ((CumulativeLexicalSimilarityLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeLexicalSimilarityLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}
