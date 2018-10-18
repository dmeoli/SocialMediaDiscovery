package edu.uniba.di.lacam.kdde.donato.meoli.database;

public enum Dataset {

    REDDIT("reddit"),
    TWITTER("twitter");

    private String name;

    Dataset(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
