package edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class SparseArrayConverter implements AttributeConverter<SparseArray, String> {

    @Override
    public String toGraphProperty(SparseArray sparseArray) {
        return sparseArray.toString();
    }

    @Override
    public SparseArray toEntityAttribute(String s) {
        String[] values = s.replace("[", "").replace("]", "")
                .replaceAll(",", "").split(" ");
        SparseArray sparseArray = new SparseArray(values.length);
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(values).mapToInt(Integer::parseInt).forEach(value -> {
            if (value != 0) sparseArray.add(i.incrementAndGet(), value);
        });
        return sparseArray;
    }
}
