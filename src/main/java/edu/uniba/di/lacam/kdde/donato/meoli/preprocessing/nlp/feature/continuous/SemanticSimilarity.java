package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous;

import edu.stanford.nlp.ling.WordLemmaTag;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.POSTag;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.RelatednessCalculator;
import edu.uniba.di.lacam.kdde.ws4j.similarity.Lin;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.uniba.di.lacam.kdde.ws4j.util.WordSimilarityCalculator.*;

public class SemanticSimilarity extends ContinuousContentBasedFeatureExtraction {

    private static RelatednessCalculator lin;

    static {
        WS4JConfiguration.getInstance().setMFS(true);
        WS4JConfiguration.getInstance().setMemoryDB(true);
        lin = new Lin(db);
    }

    public SemanticSimilarity(TFIDFSimilarity tfidfCalculator) {
        super(tfidfCalculator);
    }

    @Override
    public double extractFeature(Post postX, Post postY) {
        AtomicReference<Double> weightedSimilarity = new AtomicReference<>(0.0D);
        AtomicReference<Double> weights = new AtomicReference<>(0.0D);
        for (WordLemmaTag wordLemmaTagX : postX.getBodyPOSTags()) {
            for (WordLemmaTag wordLemmaTagY : postY.getBodyPOSTags()) {
                double weight = (tfidfCalculator.getTFIDF(postX.getID(), wordLemmaTagX.lemma()) +
                        tfidfCalculator.getTFIDF(postY.getID(), wordLemmaTagY.lemma())) / 2;
                try {
                    if (POSTag.isNoun(wordLemmaTagX.tag()) && POSTag.isNoun(wordLemmaTagY.tag()))
                        weightedSimilarity.updateAndGet(v -> v + lin.calcRelatednessOfWords(
                                wordLemmaTagX.lemma() + SEPARATOR + POS.NOUN,
                                wordLemmaTagY.lemma() + SEPARATOR + POS.NOUN) * weight);
                    else if (POSTag.isVerb(wordLemmaTagX.tag()) && POSTag.isVerb(wordLemmaTagY.tag()))
                        weightedSimilarity.updateAndGet(v -> (v + lin.calcRelatednessOfWords(
                                wordLemmaTagX.lemma() + SEPARATOR + POS.VERB,
                                wordLemmaTagY.lemma() + SEPARATOR + POS.VERB) * weight));
                } catch (IllegalArgumentException e) {
                    continue;
                }
                weights.updateAndGet(v -> v + weight);
            }
        }
        double similarity = weightedSimilarity.get() / weights.get();
        if (Double.compare(similarity, Double.NaN) == 0) return MIN_SCORE;
        checkArgument(MIN_SCORE <= similarity && similarity <= MAX_SCORE);
        return similarity;
    }
}
