package edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedFeatureExtraction.MAX_SCORE;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedFeatureExtraction.MIN_SCORE;

public class SparseArray {

    private int size;
    private Map<Integer, Integer> indexToValue;

    public SparseArray(int size) {
        this.size = size;
        indexToValue = new HashMap<>();
    }

    public void add(int index, int value) {
        indexToValue.put(index, value);
    }

    public int get(int index) {
        return indexToValue.getOrDefault(index, 0);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getWeightedAvg() {
        int values = indexToValue.entrySet().stream().mapToInt(entry -> entry.getKey() * entry.getValue()).sum();
        double weightedAvg = ((double) values / gaussSum());
        checkArgument(MIN_SCORE <= weightedAvg && weightedAvg <= MAX_SCORE);
        return weightedAvg;
    }

    private int gaussSum() {
        return size * (size + 1) / 2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        IntStream.rangeClosed(1, size).forEach(i -> {
            sb.append(get(i));
            if (i < size) sb.append(", ");
        });
        sb.append("]");
        return sb.toString();
    }
}
