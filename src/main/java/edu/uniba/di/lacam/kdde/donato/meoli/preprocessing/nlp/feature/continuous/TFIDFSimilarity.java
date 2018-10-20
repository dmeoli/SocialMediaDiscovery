package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous;

import com.google.common.util.concurrent.AtomicDouble;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TFIDFSimilarity {

    private List<Post> posts;
    private Map<String, Integer> DFs;
    private Map<String, Map<String, Double>> TFs;

    public TFIDFSimilarity(List<Post> posts) {
        this.posts = posts;
        DFs = new ConcurrentHashMap<>();
        TFs = new ConcurrentHashMap<>();
        posts.parallelStream().forEach(post -> post.getBodyPOSTags().forEach(wordLemmaTag -> {
            if (!DFs.containsKey(wordLemmaTag.lemma()))
                DFs.put(wordLemmaTag.lemma(), calculateDF(wordLemmaTag.lemma()));
            if (TFs.containsKey(post.getID())) {
                if (!TFs.get(post.getID()).containsKey(wordLemmaTag.lemma()))
                    TFs.get(post.getID()).put(wordLemmaTag.lemma(), calculateTF(post.getBodyPOSTags(), wordLemmaTag.lemma()));
            } else TFs.put(post.getID(), new ConcurrentHashMap<>(
                    Collections.singletonMap(wordLemmaTag.lemma(), calculateTF(post.getBodyPOSTags(), wordLemmaTag.lemma()))));
        }));
    }

    private double calculateTF(List<WordLemmaTag> wordLemmaTags, String word) {
        AtomicDouble result = new AtomicDouble();
        wordLemmaTags.parallelStream().filter(wordLemmaTag ->
                word.equals(wordLemmaTag.lemma())).forEach(wordLemmaTag ->
                result.getAndSet(result.get() + 1));
        return result.get() / wordLemmaTags.size();
    }

    private int calculateDF(String word) {
        AtomicInteger df = new AtomicInteger();
        posts.parallelStream().filter(post ->
                post.getBodyPOSTags().parallelStream().anyMatch(wordLemmaTag ->
                        word.equals(wordLemmaTag.lemma()))).forEach(post ->
                df.getAndIncrement());
        return df.get();
    }

    double getTF(String id, String word) {
        return TFs.get(id).get(word);
    }

    double getDF(String word) {
        return DFs.get(word);
    }

    private double getIDF(String word) {
        return Math.log(posts.size() / getDF(word));
    }

    double getTFIDF(String id, String word) {
        return getTF(id, word) * getIDF(word);
    }
}