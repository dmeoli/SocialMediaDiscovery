package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.ContentBasedFeatureExtraction;

import java.util.Set;

public abstract class DiscreteContentBasedFeatureExtraction extends ContentBasedFeatureExtraction {

    public abstract Set<String> extractFeature(Post postX, Post postY);
}
