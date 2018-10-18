package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeEmotionalLink.CUMULATIVE_EMOTION_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_EMOTION_LINK_LABEL)
public class CumulativeEmotionalLink extends CumulativeLink {

    public static final String CUMULATIVE_EMOTION_LINK_LABEL = "CUMULATIVE_EMOTION_TO";

    public CumulativeEmotionalLink() { }

    public CumulativeEmotionalLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                                   int[] cumulativeTemporalSubGraphsCounter) {
        super(cumulativeUserFrom, cumulativeUserTo, cumulativeTemporalSubGraphsCounter);
    }

    @Override
    protected void addCumulativeLink() {
        getCumulativeUserFrom().writesCumulativeEmotionTo(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeEmotionalLink
                && ((CumulativeEmotionalLink) obj).getCumulativeUserFrom().equals(getCumulativeUserFrom())
                && ((CumulativeEmotionalLink) obj).getCumulativeUserTo().equals(getCumulativeUserTo());
    }
}