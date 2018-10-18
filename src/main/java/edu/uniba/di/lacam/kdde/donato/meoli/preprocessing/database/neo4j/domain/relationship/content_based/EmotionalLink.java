package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink.EMOTIONAL_LINK_LABEL;

@RelationshipEntity(type = EMOTIONAL_LINK_LABEL)
public class EmotionalLink extends DiscreteContentBasedLink {

    public static final String EMOTIONAL_LINK_LABEL = "EMOTION_TO";

    EmotionalLink() { }

    public EmotionalLink(User userFrom, User userTo, long utc, String label) {
        super(userFrom, userTo, utc, label);
    }

    @Override
    protected void addLink() {
        getUserFrom().writesEmotionTo(this);
    }

    @Override
    public String getLinkLabel() {
        return EMOTIONAL_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmotionalLink && ((EmotionalLink) obj).getUTC().equals(getUTC())
                && ((EmotionalLink) obj).getUserFrom().equals(getUserFrom())
                && ((EmotionalLink) obj).getUserTo().equals(getUserTo());
    }
}
