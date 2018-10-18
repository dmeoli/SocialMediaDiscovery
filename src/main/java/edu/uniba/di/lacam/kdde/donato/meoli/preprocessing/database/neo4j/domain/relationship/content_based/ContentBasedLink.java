package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;

public abstract class ContentBasedLink extends Link {

    ContentBasedLink() { }

    ContentBasedLink(User userFrom, User userTo, long utc) {
        super(userFrom, userTo, utc);
    }
}
