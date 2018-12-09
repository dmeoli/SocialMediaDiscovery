package edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining;

import com.google.gson.annotations.Expose;
import org.dyminer.jminer.interfaces.ItemSet;
import org.dyminer.jminer.structures.Pair;
import org.dyminer.model.LabeledEdge;

import java.util.Set;

public class FrequentPattern {

    @Expose
    private float relativeSupport;

    @Expose
    private ItemSet<LabeledEdge, Pair<Set<Integer>>> patterns;

    FrequentPattern(float relativeSupport, ItemSet<LabeledEdge, Pair<Set<Integer>>> patterns) {
        this.relativeSupport = relativeSupport;
        this.patterns = patterns;
    }

    public float getRelativeSupport() {
        return relativeSupport;
    }

    ItemSet<LabeledEdge, Pair<Set<Integer>>> getPatterns() {
        return patterns;
    }

    @Override
    public String toString() {
        return patterns.toString();
    }
}
