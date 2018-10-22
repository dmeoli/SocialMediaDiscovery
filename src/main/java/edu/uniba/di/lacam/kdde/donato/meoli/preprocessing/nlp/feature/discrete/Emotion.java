package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete;

import edu.stanford.nlp.ling.WordLemmaTag;
import edu.uniba.di.lacam.kdde.WNAffect;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.POSTag;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.util.WNAffectConfiguration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        Set<String> emotionsPostX = new HashSet<>();
        for (WordLemmaTag wordLemmaTagX : postX.getBodyPOSTags()) {
            String parent;
            try {
                parent = wnAffect.getParent(wnAffect.getEmotion(wordLemmaTagX.lemma(), Objects.requireNonNull(
                        POSTag.getPOS(wordLemmaTagX.tag()))), EMOTION_PARENT_LEVEL);
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (Objects.nonNull(parent)) emotionsPostX.add(parent);
        }
        Set<String> emotionsPostY = new HashSet<>();
        for (WordLemmaTag wordLemmaTagY : postY.getBodyPOSTags()) {
            String parent;
            try {
                parent = wnAffect.getParent(wnAffect.getEmotion(wordLemmaTagY.lemma(), Objects.requireNonNull(
                        POSTag.getPOS(wordLemmaTagY.tag()))), EMOTION_PARENT_LEVEL);
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (Objects.nonNull(parent)) emotionsPostY.add(parent);
        }
        emotionsPostX.retainAll(emotionsPostY);
        return emotionsPostX;
    }
}
