package com.demo.service.search;

import com.demo.model.SearchDocument;
import com.demo.model.dto.SearchResponse;
import com.demo.repo.DocumentRepository;
import com.demo.util.FuzzyUtils;
import com.demo.util.RedisInvertedIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DocumentRepository repository;
    private final RedisInvertedIndex index;


    public void addDocument(String content) {
        SearchDocument doc = new SearchDocument();
        doc.setContent(content);
        repository.save(doc);
        index.indexDocument(doc.getId(), normalize(content));
    }



    public SearchResponse search(String query, int page, int size) {

        String[] queryTokens = normalize(query).split(" ");

        Map<String, Double> scores = new HashMap<>();

        for (String token : queryTokens) {
            Set<String> candidates = expandToken(token);

            for (String candidate : candidates) {
                accumulateScores(scores, candidate, 1.0);
            }

            boolean anyExactHit = candidates.stream()
                    .anyMatch(c -> !index.search(new String[]{c}).isEmpty());

            if (!anyExactHit) {
                for (String indexedWord : index.getAllWords()) {
                    if (FuzzyUtils.isFuzzyMatch(token, indexedWord)) {
                        double penalty = 1.0 - (double) FuzzyUtils.distance(token, indexedWord)
                                / Math.max(token.length(), indexedWord.length());
                        accumulateScores(scores, indexedWord, penalty);
                    }
                }
            }

            if (scores.isEmpty()) {
                for (String soundexWord : index.soundexWords(token)) {
                    accumulateScores(scores, soundexWord, 0.7);
                }
            }
        }

        // Sort by descending TF-IDF score
        List<String> sortedIds = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        long total = sortedIds.size();

        int from = page * size;
        int to   = Math.min(from + size, sortedIds.size());

        if (from >= sortedIds.size()) {
            return SearchResponse.builder()
                    .results(Collections.emptyList())
                    .total(total)
                    .build();
        }

        List<String> pageIds = sortedIds.subList(from, to);
        List<SearchDocument> docs = repository.findAllById(pageIds);

        // Preserve sort order from scores
        Map<String, SearchDocument> docMap = docs.stream()
                .collect(Collectors.toMap(SearchDocument::getId, d -> d));

        List<SearchDocument> orderedDocs = pageIds.stream()
                .map(docMap::get)
                .filter(Objects::nonNull)
                .peek(d -> d.setContent(highlight(d.getContent(), queryTokens)))
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .results(orderedDocs)
                .total(total)
                .build();
    }

    // ── Autocomplete ────────────────────────────────────────────────────────

    public List<String> autocomplete(String prefix) {
        String cleanPrefix = normalize(prefix);
        // Use the O(1) Redis prefix-set
        return new ArrayList<>(index.autocomplete(cleanPrefix, 10));
    }

    private void accumulateScores(Map<String, Double> scores, String word, double boostFactor) {
        Map<String, Integer> hits = index.search(new String[]{word});
        if (hits.isEmpty()) return;

        double idf = index.idf(word);

        for (Map.Entry<String, Integer> e : hits.entrySet()) {
            String docId = e.getKey();
            int    tf    = e.getValue();
            int    docLen = index.docLength(docId);

            // TF-IDF with length normalisation
            double tfNorm = (double) tf / Math.max(docLen, 1);
            double score  = tfNorm * idf * boostFactor;
            scores.merge(docId, score, Double::sum);
        }
    }

    private Set<String> expandToken(String token) {
        Set<String> variants = new LinkedHashSet<>();
        variants.add(token);

        // If token is a pure number, also try with common suffixes
        if (token.matches("\\d+")) {
            variants.add(token + "gb");
            variants.add(token + "mb");
            variants.add(token + "tb");
        }

        // If token ends with a unit, also add the number alone
        String stripped = token.replaceAll("(gb|mb|tb|inch|mp|mah)$", "");
        if (!stripped.equals(token) && !stripped.isEmpty()) {
            variants.add(stripped);
        }

        return variants;
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String highlight(String content, String[] tokens) {
        if (content == null) return "";
        for (String token : tokens) {
            if (token.isBlank()) continue;
            content = Pattern
                    .compile("(?i)" + Pattern.quote(token))
                    .matcher(content)
                    .replaceAll(m -> "<b>" + m.group() + "</b>");
        }
        return content;
    }
}