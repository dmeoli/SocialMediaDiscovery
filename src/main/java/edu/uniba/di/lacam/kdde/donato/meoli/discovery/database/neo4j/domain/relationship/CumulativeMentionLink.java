package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeMentionLink.CUMULATIVE_MENTION_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_MENTION_LINK_LABEL)
public class CumulativeMentionLink extends CumulativeLink {

    public static final String CUMULATIVE_MENTION_LINK_LABEL = "CUMULATIVE_MENTION_TO";

    public CumulativeMentionLink() { }

    public CumulativeMentionLink(User userFrom, User userTo, SparseArray cumulativeTemporalSubGraphsCounter) {
        super(userFrom, userTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().mentionsCumulativeTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeMentionLink
                && ((CumulativeMentionLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeMentionLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}
