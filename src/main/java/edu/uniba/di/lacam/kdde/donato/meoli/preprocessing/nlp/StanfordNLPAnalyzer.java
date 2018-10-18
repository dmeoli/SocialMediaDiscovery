package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StanfordNLPAnalyzer {

    private static final StanfordNLPAnalyzer stanfordNLPAnalyzer = new StanfordNLPAnalyzer();

    private StanfordCoreNLP pipeline;

    private StanfordNLPAnalyzer() {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        properties.setProperty("tokenize.options", "untokenizable=noneKeep");
        pipeline = new StanfordCoreNLP(properties);
    }

    public static StanfordNLPAnalyzer getInstance() {
        return stanfordNLPAnalyzer;
    }

    public List<WordLemmaTag> extractPOSTags(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        return (document.get(CoreAnnotations.SentencesAnnotation.class).parallelStream().flatMap(sentence ->
                sentence.get(CoreAnnotations.TokensAnnotation.class).parallelStream())).map(token ->
                new WordLemmaTag(token.word(), token.lemma(), token.tag())).collect(Collectors.toList());
    }
}
