package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Comment;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.*;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.DiscreteContentBasedFeatureExtraction;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.Emotion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.*;
import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedFeatureExtraction.MIN_SCORE;

public abstract class ContentBasedFeatureExtraction {

    protected static ILexicalDatabase db;

    static {
        db = new MITWordNet();
    }

    private static List<Post> linearize(Post post) {
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        (post instanceof Discussion ? ((Discussion) post).getComments() : ((Comment) post).getReplies())
                .parallelStream().forEach(comment -> posts.addAll(linearize(comment)));
        return posts;
    }

    public static void extractContentBasedFeatures(Discussion discussion) {
        List<Post> submissionPosts = linearize(discussion);
        TFIDFSimilarity tfidfSimilarity = new TFIDFSimilarity(submissionPosts);
        ContinuousContentBasedFeatureExtraction lexicalSimilarity = new LexicalSimilarity(tfidfSimilarity);
        ContinuousContentBasedFeatureExtraction semanticSimilarity = new SemanticSimilarity(tfidfSimilarity);
        DiscreteContentBasedFeatureExtraction emotionalFeature = new Emotion();
        for (int x = 0; x < submissionPosts.size(); x++) {
            Post postX = submissionPosts.get(x);
            for (int y = x + 1; y < submissionPosts.size(); y++) {
                Post postY = submissionPosts.get(y);
                if (!postX.getAuthor().equals(postY.getAuthor())) {
                    double lexicalScore = lexicalSimilarity.extractFeature(postX, postY);
                    double semanticScore = semanticSimilarity.extractFeature(postX, postY);
                    Set<String> emotions = emotionalFeature.extractFeature(postX, postY);
                    String authorFrom, authorTo;
                    if (postX.getUTC() > postY.getUTC()) {
                        authorFrom = postX.getAuthor();
                        authorTo = postY.getAuthor();
                    } else {
                        authorFrom = postY.getAuthor();
                        authorTo = postX.getAuthor();
                    }
                    if (lexicalScore != MIN_SCORE) discussion.getLexicalSimilarities().add(
                            new DiscreteContentBasedLink(authorFrom, authorTo, postX.getUTC(), lexicalScore));
                    if (semanticScore != MIN_SCORE) discussion.getSemanticSimilarities().add(
                            new DiscreteContentBasedLink(authorFrom, authorTo, postX.getUTC(), semanticScore));
                    discussion.getEmotions().addAll(emotions.parallelStream().map(label ->
                            new ContinuousContentBasedLink(authorFrom, authorTo, postX.getUTC(), label))
                            .collect(Collectors.toList()));
                }
            }
        }
    }
}
