package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.MentionLink.MENTION_LINK_LABEL;

@RelationshipEntity(type = MENTION_LINK_LABEL)
public class MentionLink extends Link {

    public static final String MENTION_LINK_LABEL = "MENTION_TO";

    MentionLink() { }

    public MentionLink(User userFrom, User userTo, long utc) {
        super(userFrom, userTo, utc);
    }

    @Override
    protected void addLink() {
        getUserFrom().mentionsTo(this);
    }

    @Override
    public String getLinkLabel() {
        return MENTION_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MentionLink && ((MentionLink) obj).getUTC().equals(getUTC())
                && ((MentionLink) obj).getUserFrom().equals(getUserFrom())
                && ((MentionLink) obj).getUserTo().equals(getUserTo());
    }
}
