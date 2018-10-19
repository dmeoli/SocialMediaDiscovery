package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete;

import edu.uniba.di.lacam.kdde.WNAffect;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.POSTag;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.util.WNAffectConfiguration;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Emotion extends DiscreteContentBasedFeatureExtraction {

    private static final int EMOTION_PARENT_LEVEL = 5;

    private static WNAffect wnAffect;

    static {
        WNAffectConfiguration.getInstance().setCache(true);
        WNAffectConfiguration.getInstance().setMemoryDB(true);
        wnAffect = new WNAffect(((MITWordNet) db).getDictionary());
    }

    @Override
    public Set<String> extractFeature(Post postX, Post postY) {
        Set<String> emotionsPostX = postX.getBodyPOSTags().parallelStream().filter(wordLemmaTagX ->
                Objects.nonNull(POSTag.getPOS(wordLemmaTagX.tag()))).map(wordLemmaTagX ->
                wnAffect.getEmotion(wordLemmaTagX.lemma(), Objects.requireNonNull(POSTag.getPOS(wordLemmaTagX.tag()))))
                .map(emotion -> wnAffect.getParent(emotion, EMOTION_PARENT_LEVEL)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> emotionsPostY = postY.getBodyPOSTags().parallelStream().filter(wordLemmaTagY ->
                Objects.nonNull(POSTag.getPOS(wordLemmaTagY.tag()))).map(wordLemmaTagY ->
                wnAffect.getEmotion(wordLemmaTagY.lemma(), Objects.requireNonNull(POSTag.getPOS(wordLemmaTagY.tag()))))
                .map(emotion -> wnAffect.getParent(emotion, EMOTION_PARENT_LEVEL)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        emotionsPostX.retainAll(emotionsPostY);
        return emotionsPostX;
    }
}
