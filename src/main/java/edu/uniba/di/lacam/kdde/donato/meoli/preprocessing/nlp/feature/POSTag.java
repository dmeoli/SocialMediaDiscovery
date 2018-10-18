package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature;

import edu.mit.jwi.item.POS;

public class POSTag {

    private static final String STANFORD_NOUN_PREFIX = "NN";
    private static final String STANFORD_VERB_PREFIX = "VB";
    private static final String STANFORD_ADJECTIVE_PREFIX = "JJ";
    private static final String STANFORD_ADVERB_PREFIX = "RB";

    public static boolean isNoun(String stanfordTag) {
        return stanfordTag.startsWith(STANFORD_NOUN_PREFIX);
    }

    public static boolean isVerb(String stanfordTag) {
        return stanfordTag.startsWith(STANFORD_VERB_PREFIX);
    }

    public static boolean isAdjective(String stanfordTag) {
        return stanfordTag.startsWith(STANFORD_ADJECTIVE_PREFIX);
    }

    public static boolean isAdverb(String stanfordTag) {
        return stanfordTag.startsWith(STANFORD_ADVERB_PREFIX);
    }

    public static POS getPOS(String stanfordTag) {
        if (isNoun(stanfordTag)) return POS.NOUN;
        else if (isVerb(stanfordTag)) return POS.NOUN;
        else if (isAdjective(stanfordTag)) return POS.ADJECTIVE;
        else if (isAdverb(stanfordTag)) return POS.ADVERB;
        else return null;
    }
}
