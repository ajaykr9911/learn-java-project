package com.demo.util;

/**
 * Fuzzy matching utilities: Levenshtein distance + Soundex phonetic encoding.
 *
 * Used by SearchService to match typos like "mibole" → "mobile".
 */
public class FuzzyUtils {

    // ── Levenshtein Distance ────────────────────────────────────────────────

    /**
     * Standard iterative Levenshtein distance (space-optimised to O(min(m,n))).
     */
    public static int distance(String a, String b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        if (a.equals(b)) return 0;

        int m = a.length(), n = b.length();
        if (m == 0) return n;
        if (n == 0) return m;

        // Keep only two rows
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) prev[j] = j;

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(curr[j - 1] + 1, prev[j] + 1),
                        prev[j - 1] + cost
                );
            }
            int[] tmp = prev; prev = curr; curr = tmp;
        }
        return prev[n];
    }

    /**
     * Dynamic threshold: short words need exact/near-exact match;
     * longer words allow more edits.
     *
     * Length  Max-edits
     * 1–3     0
     * 4–5     1
     * 6–8     2
     * 9+      3
     */
    public static int maxEditDistance(String word) {
        int len = word.length();
        if (len <= 3) return 0;
        if (len <= 5) return 1;
        if (len <= 8) return 2;
        return 3;
    }

    /** Returns true when {@code candidate} is "close enough" to {@code query}. */
    public static boolean isFuzzyMatch(String query, String candidate) {
        int threshold = maxEditDistance(query);
        return threshold > 0 && distance(query, candidate) <= threshold;
    }

    // ── Soundex ────────────────────────────────────────────────────────────

    private static final char[] SOUNDEX_TABLE = {
            //A  B   C   D   E   F   G   H   I   J   K   L   M
            '0','1','2','3','0','1','2','0','0','2','2','4','5',
            //N  O   P   Q   R   S   T   U   V   W   X   Y   Z
            '5','0','1','2','6','2','3','0','1','0','2','0','2'
    };

    /**
     * Generates a 4-character Soundex code for {@code word}.
     * "mobile" and "mibole" both encode to M140.
     */
    public static String soundex(String word) {
        if (word == null || word.isEmpty()) return "0000";

        word = word.toUpperCase().replaceAll("[^A-Z]", "");
        if (word.isEmpty()) return "0000";

        StringBuilder sb = new StringBuilder();
        sb.append(word.charAt(0));
        char lastCode = digitFor(word.charAt(0));

        for (int i = 1; i < word.length() && sb.length() < 4; i++) {
            char c = word.charAt(i);
            char code = digitFor(c);
            if (code != '0' && code != lastCode) {
                sb.append(code);
            }
            lastCode = code;
        }

        while (sb.length() < 4) sb.append('0');
        return sb.toString();
    }

    private static char digitFor(char c) {
        c = Character.toUpperCase(c);
        if (c < 'A' || c > 'Z') return '0';
        return SOUNDEX_TABLE[c - 'A'];
    }

    /** True when both words share the same Soundex code. */
    public static boolean soundexMatch(String a, String b) {
        return soundex(a).equals(soundex(b));
    }
}