import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input14.txt"));

        var parts = input.split("\n\n");

        var template = parts[0];
        var pairInsertions = parseInsertions(parts[1]);

        part01(template, pairInsertions);
        part02(template, pairInsertions);
    }

    private static void part01(String template, Map<String, Character> pairInsertions) {
        solve(template, pairInsertions, 10);
    }

    private static void part02(String template, Map<String, Character> pairInsertions) {
        solve(template, pairInsertions, 40);
    }

    private static void solve(String template, Map<String, Character> pairInsertions, int count) {
        Map<String, Long> pairCounts = new HashMap<>();
        for (int i = 0; i < template.length() - 1; i++) {
            pairCounts.compute(template.substring(i, i + 2), Day14::incrementOrOne);
        }
        for (int i = 0; i < count; i++) {
            pairCounts = nextStep(pairCounts, pairInsertions);
        }
        calculateResult(pairCounts, template);
    }

    private static Map<String, Long> nextStep(Map<String, Long> pairCounts, Map<String, Character> pairInsertions) {
        var newCounts = new HashMap<String, Long>();
        pairCounts.forEach((key, value) -> {
            var inserted = pairInsertions.get(key);
            var left = key.substring(0, 1) + inserted;
            var right = inserted + key.substring(1);
            addOrValue(newCounts, left, value);
            addOrValue(newCounts, right, value);
        });
        return newCounts;
    }

    private static void calculateResult(Map<String, Long> pairCounts, String template) {
        var characterCount = new HashMap<Character, Long>();
        pairCounts.forEach((key, value) -> {
            addOrValue(characterCount, key.charAt(0), value);
            addOrValue(characterCount, key.charAt(1), value);
        });
        addOrValue(characterCount, template.charAt(0), 1);
        addOrValue(characterCount, template.charAt(template.length() - 1), 1);

        var min = Long.MAX_VALUE;
        var max = Long.MIN_VALUE;
        for (var value : characterCount.values()) {
            value /= 2;
            if (value < min) min = value;
            if (value > max) max = value;
        }
        System.out.println(max - min);
    }

    private static <T> long incrementOrOne(T ignored, Long value) {
        if (value == null) return 1;
        return value + 1;
    }

    private static <T> void addOrValue(Map<T, Long> values, T key, long value) {
        values.compute(key, (k, v) -> {
            if (v == null) return value;
            return v + value;
        });
    }

    private static Map<String, Character> parseInsertions(String insertions) {
        var lines = insertions.split("\n");
        return Arrays.stream(lines)
            .map(Insertion::parse)
            .collect(Collectors.toMap(Insertion::template, Insertion::insertion));
    }

    record Insertion(String template, char insertion) {
        static Insertion parse(String line) {
            var parts = line.split(" -> ");

            return new Insertion(parts[0], parts[1].charAt(0));
        }
    }
}
