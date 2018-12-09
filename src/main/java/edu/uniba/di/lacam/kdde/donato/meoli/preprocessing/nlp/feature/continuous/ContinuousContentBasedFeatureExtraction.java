package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.ContentBasedFeatureExtraction;

public abstract class ContinuousContentBasedFeatureExtraction extends ContentBasedFeatureExtraction {

    public static final double MIN_SCORE = 0.0D;
    static final double MAX_SCORE = 1.0D;

    TFIDFSimilarity tfidfCalculator;

    ContinuousContentBasedFeatureExtraction(TFIDFSimilarity tfidfCalculator) {
        this.tfidfCalculator = tfidfCalculator;
    }

    public abstract double extractFeature(Post postX, Post postY);
}
