package edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class SparseArray {

    private int size;
    private Map<Integer, Integer> indexToValue;

    public SparseArray(int size) {
        this.size = size;
        indexToValue = new ConcurrentHashMap<>();
    }

    public SparseArray(int size, int index, int value) {
       this(size);
       this.add(index, value);
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
        return ((double) values / gaussSum());
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
