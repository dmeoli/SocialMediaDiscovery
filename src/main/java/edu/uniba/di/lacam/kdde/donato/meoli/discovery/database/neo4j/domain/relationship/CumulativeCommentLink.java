package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeCommentLink.CUMULATIVE_COMMENT_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_COMMENT_LINK_LABEL)
public class CumulativeCommentLink extends CumulativeLink {

    public static final String CUMULATIVE_COMMENT_LINK_LABEL = "CUMULATIVE_COMMENT_TO";

    public CumulativeCommentLink() { }

    public CumulativeCommentLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                                 SparseArray cumulativeTemporalSubGraphsCounter) {
        super(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().commentsCumulativeTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeCommentLink
                && ((CumulativeCommentLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeCommentLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}
