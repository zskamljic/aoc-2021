import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day08 {
    private static final String FULL_7_SEGMENT = "abcdefg";

    public static void main(String[] args) throws IOException {
        var input = Arrays.stream("""
                be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
                edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
                fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
                fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
                aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
                fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
                dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
                bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
                egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
                gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
                """.split("\n"))
            .toList();
        var input2 = Files.readAllLines(Paths.get("input08.txt"));

        part01(input);
        decode("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf");
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

    private static void decode(String line) {
        var numbers = Arrays.stream(line.replace("| ", "").split("\s"))
            .map(String::toCharArray)
            .peek(chars -> Arrays.sort(chars, 0, chars.length))
            .map(String::new)
            .collect(Collectors.toCollection(ArrayList::new));

        var number1 = filterLength(numbers, 2);
        var number4 = filterLength(numbers, 4);
        var number7 = filterLength(numbers, 3);
        var number8 = filterLength(numbers, 7);

        var number6 = numbers.stream()
            .filter(s -> s.length() == 6)
            .filter(s -> !s.contains(number1))
            .findFirst()
            .orElseThrow();
        numbers.remove(number6);

        var number9 = numbers.stream()
            .filter(s -> s.length() == 6)
            .filter(s -> s.contains(number4))
            .findFirst()
            .orElseThrow();
        numbers.remove(number9);

        System.out.println(number9);
    }

    private static String filterLength(List<String> numbers, int length) {
        var value = numbers.stream()
            .filter(s -> s.length() == length)
            .findFirst()
            .orElseThrow();
        numbers.remove(value);
        return value;
    }
}
