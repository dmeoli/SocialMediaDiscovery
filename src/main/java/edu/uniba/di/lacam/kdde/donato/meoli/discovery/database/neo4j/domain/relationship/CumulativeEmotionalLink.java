package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeEmotionalLink.CUMULATIVE_EMOTION_LINK_LABEL;

@RelationshipEntity(type = CUMULATIVE_EMOTION_LINK_LABEL)
public class CumulativeEmotionalLink extends CumulativeLink {

    public static final String CUMULATIVE_EMOTION_LINK_LABEL = "CUMULATIVE_EMOTION_TO";

    public CumulativeEmotionalLink() { }

    public CumulativeEmotionalLink(User userFrom, User userTo, SparseArray cumulativeTemporalSubGraphsCounter) {
        super(userFrom, userTo, cumulativeTemporalSubGraphsCounter);
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