package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink.REPLY_LINK_LABEL;

@RelationshipEntity(type = REPLY_LINK_LABEL)
public class ReplyLink extends Link {

    public static final String REPLY_LINK_LABEL = "REPLY_TO";

    ReplyLink() { }

    public ReplyLink(User userFrom, User userTo, long utc) {
        super(userFrom, userTo, utc);
    }

    @Override
    protected void addLink() {
        getUserFrom().repliesTo(this);
    }

    @Override
    public String getLinkLabel() {
        return REPLY_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReplyLink && ((ReplyLink) obj).getUTC().equals(getUTC())
                && ((ReplyLink) obj).getUserFrom().equals(getUserFrom())
                && ((ReplyLink) obj).getUserTo().equals(getUserTo());
    }
}

