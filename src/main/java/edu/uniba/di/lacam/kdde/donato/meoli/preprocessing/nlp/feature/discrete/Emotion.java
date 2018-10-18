package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete;

import edu.uniba.di.lacam.kdde.WNAffect;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.POSTag;
import edu.uniba.di.lacam.kdde.util.WNAffectConfiguration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Emotion extends DiscreteContentBasedFeatureExtraction {

    private static final int EMOTION_PARENT_LEVEL = 5;

    private static WNAffect wnAffect;

    static {
        WNAffectConfiguration.getInstance().setMemoryDB(true);
        wnAffect = WNAffect.getInstance();
    }

    @Override
    public Set<String> extractFeature(Post postX, Post postY) {
        Set<String> emotionsPostX = new HashSet<>();
        Set<String> emotionsPostY = new HashSet<>();
        postX.getBodyPOSTags().parallelStream().filter(wordLemmaTagX ->
                Objects.nonNull(POSTag.getPOS(wordLemmaTagX.tag()))).map(wordLemmaTagX ->
                wnAffect.getEmotion(wordLemmaTagX.lemma(), Objects.requireNonNull(POSTag.getPOS(wordLemmaTagX.tag()))))
                .filter(Objects::nonNull).forEach(emotion ->
                emotionsPostX.add(wnAffect.getParent(emotion, EMOTION_PARENT_LEVEL)));
        postY.getBodyPOSTags().parallelStream().filter(wordLemmaTagY ->
                Objects.nonNull(POSTag.getPOS(wordLemmaTagY.tag()))).map(wordLemmaTagY ->
                wnAffect.getEmotion(wordLemmaTagY.lemma(), Objects.requireNonNull(POSTag.getPOS(wordLemmaTagY.tag()))))
                .filter(Objects::nonNull).forEach(emotion ->
                emotionsPostY.add(wnAffect.getParent(emotion, EMOTION_PARENT_LEVEL)));
        emotionsPostX.removeIf(Objects::isNull);
        emotionsPostY.removeIf(Objects::isNull);
        emotionsPostX.retainAll(emotionsPostY);
        return emotionsPostX;
    }
}
