import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day08 {
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
        var numbers = line.replace("| ", "")
            .split("\s");


    }
}
