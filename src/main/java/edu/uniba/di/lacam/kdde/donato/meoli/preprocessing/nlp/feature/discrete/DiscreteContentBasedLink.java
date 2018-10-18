package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.ContentBasedLink;

public class DiscreteContentBasedLink extends ContentBasedLink {

    private double score;

    public DiscreteContentBasedLink(String authorFrom, String authorTo, long utc, double score) {
        super(authorFrom, authorTo, utc);
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}
