package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain;

import edu.stanford.nlp.ling.WordLemmaTag;

import java.util.List;

public abstract class Post {

    private String id;
    private String author;
    private long utc;
    private List<WordLemmaTag> bodyPOSTags;
    private List<String> mentions;

    public Post(String id, String author, long utc, List<WordLemmaTag> bodyPosTags, List<String> mentions) {
        this.id = id;
        this.author = author;
        this.utc = utc;
        this.bodyPOSTags = bodyPosTags;
        this.mentions = mentions;
    }

    public String getID() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public long getUTC() {
        return utc;
    }

    public List<WordLemmaTag> getBodyPOSTags() {
        return bodyPOSTags;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public static String getRegexMentionPrefix(String mentionPrefix) {
        return String.format("%s\\w*", mentionPrefix);
    }
}
