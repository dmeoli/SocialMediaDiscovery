package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain;

import edu.stanford.nlp.ling.WordLemmaTag;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.DiscreteContentBasedLink;

import java.util.ArrayList;
import java.util.List;

public class Discussion extends Post {

    private List<Comment> comments;

    private List<DiscreteContentBasedLink> lexicalSimilarities;
    private List<DiscreteContentBasedLink> semanticSimilarities;
    private List<ContinuousContentBasedLink> emotions;

    public Discussion(String id, String author, long utc, List<WordLemmaTag> bodyPosTags, List<String> mentions,
                      List<Comment> comments) {
        super(id, author, utc, bodyPosTags, mentions);
        this.comments = comments;
        lexicalSimilarities = new ArrayList<>();
        semanticSimilarities = new ArrayList<>();
        emotions = new ArrayList<>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<DiscreteContentBasedLink> getLexicalSimilarities() {
        return lexicalSimilarities;
    }

    public List<DiscreteContentBasedLink> getSemanticSimilarities() {
        return semanticSimilarities;
    }

    public List<ContinuousContentBasedLink> getEmotions() {
        return emotions;
    }
}
