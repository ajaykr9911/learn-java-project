package com.demo.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InvertedIndex {

    // word -> (docId -> frequency)
    private final Map<String, Map<String, Integer>> index = new ConcurrentHashMap<>();

    public void indexDocument(String docId, String content) {

        String[] words = content.split(" ");

        for (String word : words) {
            if (word.isBlank()) continue;

            index
                    .computeIfAbsent(word, k -> new ConcurrentHashMap<>())
                    .merge(docId, 1, Integer::sum);
        }
    }

    public Map<String, Integer> search(String[] words) {

        Map<String, Integer> scores = new HashMap<>();

        for (String word : words) {

            Map<String, Integer> docs = index.get(word);

            if (docs == null) continue;

            for (Map.Entry<String, Integer> entry : docs.entrySet()) {
                scores.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return scores;
    }

    // 🔥 NEW: prefix search (autocomplete base)
    public Set<String> prefixSearch(String prefix) {

        Set<String> resultDocs = new HashSet<>();

        for (String key : index.keySet()) {
            if (key.startsWith(prefix)) {
                resultDocs.addAll(index.get(key).keySet());
            }
        }

        return resultDocs;
    }
}