package org.lara.weavergen2.integration.fixtures;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public record InvariantSnapshot(Map<String, Long> json, Map<String, Long> java) {

    public InvariantSnapshot {
        json = immutableSortedCopy(json);
        java = immutableSortedCopy(java);
    }

    private static Map<String, Long> immutableSortedCopy(Map<String, Long> source) {
        TreeMap<String, Long> sorted = new TreeMap<>(source);
        return Collections.unmodifiableMap(sorted);
    }
}
