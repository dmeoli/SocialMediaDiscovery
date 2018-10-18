package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature;

public abstract class ContentBasedLink {

    private String authorFrom;
    private String authorTo;
    private long utc;

    public ContentBasedLink(String authorFrom, String authorTo, long utc) {
        this.authorFrom = authorFrom;
        this.authorTo = authorTo;
        this.utc = utc;
    }

    public String getAuthorFrom() {
        return authorFrom;
    }

    public String getAuthorTo() {
        return authorTo;
    }

    public long getUTC() {
        return utc;
    }
}
