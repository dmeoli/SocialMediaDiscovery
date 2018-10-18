package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;

public abstract class ContinuousContentBasedLink extends ContentBasedLink {

    private double score;

    ContinuousContentBasedLink() { }

    ContinuousContentBasedLink(User userFrom, User userTo, long utc, double score) {
        super(userFrom, userTo, utc);
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}
