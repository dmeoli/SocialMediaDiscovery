package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous;

import edu.stanford.nlp.ling.WordLemmaTag;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.POSTag;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static com.google.common.base.Preconditions.checkArgument;

public class LexicalSimilarity extends ContinuousContentBasedFeatureExtraction {

    public LexicalSimilarity(TFIDFSimilarity tfidfCalculator) {
        super(tfidfCalculator);
    }

    @Override
    public double extractFeature(Post postX, Post postY) {
        AtomicReference<Double> similarity = new AtomicReference<>(0.0D);
        List<WordLemmaTag> wordLemmaTagsX = postX.getBodyPOSTags();
        List<WordLemmaTag> wordLemmaTagsY = postY.getBodyPOSTags();
        wordLemmaTagsX.parallelStream().forEach(wordLemmaTagX ->
                wordLemmaTagsY.parallelStream().filter(wordLemmaTagY ->
                        POSTag.isNoun(wordLemmaTagX.tag()) & POSTag.isNoun(wordLemmaTagY.tag()) ||
                                POSTag.isVerb(wordLemmaTagX.tag()) & POSTag.isVerb(wordLemmaTagY.tag()))
                        .<UnaryOperator<Double>>map(wordLemmaTagY -> v ->
                                v + ((tfidfCalculator.getTF(postX.getID(), wordLemmaTagX.lemma()) +
                                        tfidfCalculator.getTF(postY.getID(), wordLemmaTagY.lemma())) /
                                        (tfidfCalculator.getDF(wordLemmaTagX.lemma()) +
                                                tfidfCalculator.getDF(wordLemmaTagY.lemma()))) *
                                        Math.pow(wordLemmaTagsX.size() * wordLemmaTagsY.size(), -1))
                        .forEach(similarity::updateAndGet));
        checkArgument(MIN_SCORE <= similarity.get() && similarity.get() <= MAX_SCORE);
        return similarity.get();
    }
}
