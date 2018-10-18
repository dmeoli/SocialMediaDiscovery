package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeReplyLink.CUMULATIVE_REPLY_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_REPLY_LINK_LABEL)
public class CumulativeReplyLink extends CumulativeLink {

    public static final String CUMULATIVE_REPLY_LINK_LABEL = "CUMULATIVE_REPLY_TO";

    public CumulativeReplyLink() { }

    public CumulativeReplyLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                               int[] cumulativeTemporalSubGraphsCounter) {
        super(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().repliesCumulativeTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeReplyLink
                && ((CumulativeReplyLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeReplyLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}
