package com.demo.service.search;

import com.demo.model.SearchDocument;
import com.demo.repo.DocumentRepository;
import com.demo.util.InvertedIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DocumentRepository repository;
    private final InvertedIndex index;

    // Stop words (basic)
    private static final Set<String> STOP_WORDS = Set.of(
            "is", "the", "a", "an", "of", "to", "in", "on", "and"
    );

    public void addDocument(String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        // clean text
        String cleaned = cleanText(content);

        SearchDocument doc = new SearchDocument();
        doc.setContent(content);

        repository.save(doc);

        index.indexDocument(doc.getId(), cleaned);
    }

    public List<SearchDocument> search(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String[] words = Arrays.stream(cleanText(query).split(" "))
                .filter(word -> !STOP_WORDS.contains(word))
                .distinct()
                .toArray(String[]::new);

        if (words.length == 0) return Collections.emptyList();

        // ranking scores
        Map<String, Integer> scores = index.search(words);

        if (scores.isEmpty()) return Collections.emptyList();

        // sort by score desc
        List<String> sortedDocIds = scores.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // fetch documents
        List<SearchDocument> docs = repository.findAllById(sortedDocIds);

        // map for ordering
        Map<String, SearchDocument> docMap = docs.stream()
                .collect(Collectors.toMap(SearchDocument::getId, d -> d));

        return sortedDocIds.stream()
                .map(docMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    // 🔥 Text cleaning method
    private String cleanText(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}