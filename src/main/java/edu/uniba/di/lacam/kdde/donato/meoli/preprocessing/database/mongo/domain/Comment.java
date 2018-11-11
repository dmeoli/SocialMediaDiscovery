package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain;

import edu.stanford.nlp.ling.WordLemmaTag;

import java.util.List;

public class Comment extends Post {

    private List<Comment> replies;

    public Comment(String id, String author, long utc, List<WordLemmaTag> bodyPosTags, List<String> mentions,
                   List<Comment> replies) {
        super(id, author, utc, bodyPosTags, mentions);
        this.replies = replies;
    }

    public List<Comment> getReplies() {
        return replies;
    }
}
