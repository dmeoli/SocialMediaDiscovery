package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.ContentBasedLink;

public class ContinuousContentBasedLink extends ContentBasedLink {

    private String label;

    public ContinuousContentBasedLink(String authorFrom, String authorTo, long utc, String label) {
        super(authorFrom, authorTo, utc);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
