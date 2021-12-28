import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Day24 {
    private static final List<Integer> ALPHABET = List.of(9, 8, 7, 6, 5, 4, 3, 2, 1);

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input24.txt"));

        var xModifiers = new int[14];
        var yModifiers = new int[14];
        for (var i = 0; i < 14; i++) {
            xModifiers[i] = Integer.parseInt(input.get(i * 18 + 5).split(" ")[2]);
            yModifiers[i] = Integer.parseInt(input.get(i * 18 + 15).split(" ")[2]);
        }

        var stack = new Stack<Entry>();
        var maxDigits = new int[14];
        var minDigits = new int[14];
        for (int i = 0; i < 14; i++) {
            if (xModifiers[i] >= 10) {
                stack.push(new Entry(i, yModifiers[i]));
            } else {
                var entry = stack.pop();
                var value = entry.lastValue + xModifiers[i];
                var max = ALPHABET.stream()
                    .filter(candidate -> ALPHABET.contains(candidate + value))
                    .findFirst()
                    .orElseThrow();
                var min = ALPHABET.stream()
                    .sorted()
                    .filter(candidate -> ALPHABET.contains(candidate + value))
                    .findFirst()
                    .orElseThrow();
                maxDigits[entry.index] = max;
                maxDigits[i] = max + value;
                minDigits[entry.index] = min;
                minDigits[i] = min + value;
            }
        }
        var maxResult = 0L;
        var minResult = 0L;
        for (int i=0;i<14;i++) {
            maxResult *= 10;
            maxResult += maxDigits[i];
            minResult *= 10;
            minResult += minDigits[i];
        }
        System.out.println(maxResult);
        System.out.println(minResult);
    }

    record Entry(int index, int lastValue) {
    }
}
