package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;

public abstract class DiscreteContentBasedLink extends ContentBasedLink {

    private String label;

    DiscreteContentBasedLink() { }

    DiscreteContentBasedLink(User userFrom, User userTo, long utc, String label) {
        super(userFrom, userTo, utc);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
