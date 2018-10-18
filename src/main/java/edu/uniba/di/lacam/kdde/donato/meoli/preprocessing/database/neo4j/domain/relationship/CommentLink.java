package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink.COMMENT_LINK_LABEL;

@RelationshipEntity(type = COMMENT_LINK_LABEL)
public class CommentLink extends Link {

    public static final String COMMENT_LINK_LABEL = "COMMENT_TO";

    CommentLink() { }

    public CommentLink(User userFrom, User userTo, long utc) {
        super(userFrom, userTo, utc);
    }

    @Override
    protected void addLink() {
        getUserFrom().commentsTo(this);
    }

    @Override
    public String getLinkLabel() {
        return COMMENT_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommentLink && ((CommentLink) obj).getUTC().equals(getUTC())
                && ((CommentLink) obj).getUserFrom().equals(getUserFrom())
                && ((CommentLink) obj).getUserTo().equals(getUserTo());
    }
}
