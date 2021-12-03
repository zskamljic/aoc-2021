import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day03 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input03.txt"));

        var onesCounts = countOnes(input, 0);

        part01(onesCounts, input);
        part02(onesCounts, input);
    }

    private static void part01(int[] onesCounts, List<String> input) {
        var gamma = 0;
        var epsilon = 0;
        for (var one : onesCounts) {
            gamma <<= 1;
            epsilon <<= 1;
            if (one > input.size() / 2) {
                gamma |= 1;
            } else {
                epsilon |= 1;
            }
        }
        System.out.println(gamma * epsilon);
    }

    private static void part02(int[] onesCounts, List<String> input) {
        var oxygenBit = onesCounts[0] > input.size() / 2;
        List<String> oxygenCandidates = input.stream()
            .filter(s -> s.startsWith(oxygenBit ? "1" : "0"))
            .toList();

        List<String> co2candidates = new ArrayList<>(input);
        co2candidates.removeAll(oxygenCandidates);

        var oxygen = filterByBits(oxygenCandidates, true);
        var co2 = filterByBits(co2candidates, false);

        System.out.println(oxygen * co2);
    }

    private static int[] countOnes(List<String> input, int startIndex) {
        var onesCounts = new int[input.get(0).length() - startIndex];
        for (var line : input) {
            for (int i = startIndex; i < line.length(); i++) {
                if (line.charAt(i) == '1') {
                    onesCounts[i - startIndex]++;
                }
            }
        }
        return onesCounts;
    }

    private static int filterByBits(List<String> input, boolean keepOnes) {
        var trueChar = keepOnes ? '1' : '0';
        var falseChar = keepOnes ? '0' : '1';
        int start = 1;
        while (input.size() > 1) {
            var ones = countOnes(input, start);
            var filterChar = ones[0] >= input.size() / 2f ? trueChar : falseChar;
            int finalStart = start;
            input = input.stream()
                .filter(s -> s.charAt(finalStart) == filterChar)
                .toList();
            start++;
        }
        return Integer.parseInt(input.get(0), 2);
    }
}
