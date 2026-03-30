package com.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Redis-backed inverted index with:
 *  - TF-IDF style scoring (term frequency per document + global doc-frequency)
 *  - Soundex phonetic index for fuzzy phonetic lookup
 *  - Prefix set for fast autocomplete
 *
 * Redis key layout:
 *   idx:term:{word}          → Hash  { docId → termFreq }
 *   idx:df:{word}            → String (document frequency count)
 *   idx:soundex:{code}       → Set   of words that map to this Soundex code
 *   idx:words                → Set   of all indexed words (for fuzzy scan)
 *   idx:prefix:{prefix}      → Set   of words starting with that prefix
 *   idx:doclen:{docId}       → String (total tokens in document, for TF normalisation)
 *   meta:doccount            → String (total number of documents indexed)
 */
@Component
@RequiredArgsConstructor
public class RedisInvertedIndex {

    private final StringRedisTemplate redis;

    // ── Indexing ────────────────────────────────────────────────────────────

    public void indexDocument(String docId, String cleanedContent) {

        String[] tokens = cleanedContent.split(" ");
        Map<String, Integer> termFreq = new HashMap<>();
        for (String token : tokens) {
            if (!token.isBlank()) termFreq.merge(token, 1, Integer::sum);
        }

        // Store term-frequency per document
        for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
            String word = entry.getKey();
            int tf = entry.getValue();

            // Inverted index: word → { docId: tf }
            redis.opsForHash().put("idx:term:" + word, docId, String.valueOf(tf));

            // Document frequency (number of docs containing this word)
            redis.opsForValue().increment("idx:df:" + word);

            // Master word set
            redis.opsForSet().add("idx:words", word);

            // Soundex index
            String code = FuzzyUtils.soundex(word);
            redis.opsForSet().add("idx:soundex:" + code, word);

            // Prefix index (store every prefix of the word)
            for (int i = 1; i <= word.length(); i++) {
                redis.opsForSet().add("idx:prefix:" + word.substring(0, i), word);
            }
        }

        // Store document length for TF normalisation
        redis.opsForValue().set("idx:doclen:" + docId, String.valueOf(tokens.length));

        // Increment total document count
        redis.opsForValue().increment("meta:doccount");
    }

    // ── Search (returns docId → raw TF score; caller applies IDF weighting) ─

    /**
     * Returns { docId → termFrequency } for a given word.
     */
    public Map<String, Integer> search(String[] words) {
        Map<String, Integer> scores = new HashMap<>();
        for (String word : words) {
            Map<Object, Object> entries = redis.opsForHash().entries("idx:term:" + word);
            for (Map.Entry<Object, Object> e : entries.entrySet()) {
                scores.merge((String) e.getKey(), Integer.parseInt((String) e.getValue()), Integer::sum);
            }
        }
        return scores;
    }

    /**
     * IDF = log((N + 1) / (df + 1))  — smoothed to avoid division by zero.
     */
    public double idf(String word) {
        String dfStr = redis.opsForValue().get("idx:df:" + word);
        String nStr  = redis.opsForValue().get("meta:doccount");
        double df = dfStr != null ? Double.parseDouble(dfStr) : 0;
        double n  = nStr  != null ? Double.parseDouble(nStr)  : 1;
        return Math.log((n + 1) / (df + 1)) + 1;
    }

    /**
     * Document length for TF normalisation.
     */
    public int docLength(String docId) {
        String len = redis.opsForValue().get("idx:doclen:" + docId);
        return len != null ? Integer.parseInt(len) : 1;
    }

    // ── Phonetic lookup ────────────────────────────────────────────────────

    /**
     * Returns all indexed words that share the same Soundex code as {@code word}.
     */
    public Set<String> soundexWords(String word) {
        String code = FuzzyUtils.soundex(word);
        Set<String> members = redis.opsForSet().members("idx:soundex:" + code);
        return members != null ? members : Collections.emptySet();
    }

    // ── Autocomplete ───────────────────────────────────────────────────────

    /**
     * Returns up to {@code limit} words that start with {@code prefix}.
     * Uses the pre-built prefix index — O(1) Redis lookup.
     */
    public Set<String> autocomplete(String prefix, int limit) {
        Set<String> all = redis.opsForSet().members("idx:prefix:" + prefix);
        if (all == null) return Collections.emptySet();
        if (all.size() <= limit) return all;
        // Return a stable subset
        Set<String> result = new LinkedHashSet<>();
        for (String w : all) {
            result.add(w);
            if (result.size() == limit) break;
        }
        return result;
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    public Set<String> getAllWords() {
        Set<String> all = redis.opsForSet().members("idx:words");
        return all != null ? all : Collections.emptySet();
    }
}