import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day08 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input08.txt"));

        part01(input);
        part02(input);
    }

    private static void part01(List<String> input) {
        var simpleCount = 0;
        for (var line : input) {
            var parts = line.split(" \\| ");

            var outputDigits = parts[1].split("\s");

            for (var digit : outputDigits) {
                if (digit.length() == 1 || digit.length() == 2 || digit.length() == 3 || digit.length() == 4 || digit.length() == 7) {
                    simpleCount++;
                }
            }
        }
        System.out.println(simpleCount);
    }

    private static void part02(List<String> input) {
        var sum = input.stream()
            .mapToInt(Day08::decode)
            .sum();
        System.out.println(sum);
    }

    private static int decode(String line) {
        var numbers = Arrays.stream(line.replace("| ", "").split("\s"))
            .map(Day08::stringToSet)
            .collect(Collectors.toCollection(HashSet::new));

        // Unambiguous
        var number1 = filterLength(numbers, 2).get(0);
        var number4 = filterLength(numbers, 4).get(0);
        var number7 = filterLength(numbers, 3).get(0);
        var number8 = filterLength(numbers, 7).get(0);

        var numbers690 = filterLength(numbers, 6);
        // Only one that doesn't have the vertical line
        var number6 = fetchAndRemove(numbers690, s -> !s.containsAll(number1));

        // 4 has the bar in the middle, that zero doesn't
        var number9 = fetchAndRemove(numbers690, s -> s.containsAll(number4));
        var number0 = numbers690.get(0);

        // Only one remaining that has vertical line
        var number3 = fetchAndRemove(numbers, s -> s.containsAll(number1));
        // 5 fits into 6
        var number5 = fetchAndRemove(numbers, number6::containsAll);
        // Last one left
        var number2 = fetchAndRemove(numbers, s -> true);

        var solutions = Map.of(
            number0, 0,
            number1, 1,
            number2, 2,
            number3, 3,
            number4, 4,
            number5, 5,
            number6, 6,
            number7, 7,
            number8, 8,
            number9, 9
        );

        var solutionNumbers = line.split(" \\| ")[1];
        var solution = Arrays.stream(solutionNumbers.split("\\s"))
            .map(Day08::stringToSet)
            .mapToInt(solutions::get)
            .toArray();
        var value = 0;
        for (var item : solution) {
            value *= 10;
            value += item;
        }
        return value;
    }

    private static Set<Character> stringToSet(String s) {
        return s.chars().mapToObj(i -> (char) i).collect(Collectors.toSet());
    }

    private static List<Set<Character>> filterLength(Collection<Set<Character>> numbers, int length) {
        var value = numbers.stream()
            .filter(s -> s.size() == length)
            .collect(Collectors.toCollection(ArrayList::new));
        numbers.removeAll(value);
        return value;
    }

    private static Set<Character> fetchAndRemove(Collection<Set<Character>> numbers, Predicate<Set<Character>> predicate) {
        var item = numbers.stream()
            .filter(predicate)
            .findFirst()
            .orElseThrow();
        numbers.remove(item);
        return item;
    }
}
