import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Day10 {
    private static final Map<Character, Character> PAIRS = Map.of(
        '[', ']',
        '(', ')',
        '{', '}',
        '<', '>'
    );

    public static void main(String[] args) throws IOException {
        var input2 = Arrays.asList("""
            [({(<(())[]>[[{[]{<()<>>
            [(()[<>])]({[<{<<[]>>(
            {([(<{}[<>[]}>{[]{[(<()>
            (((({<>}<{<{<>}{[]{[]{}
            [[<[([]))<([[{}[[()]]]
            [{[{({}]{}}([{[{{{}}([]
            {<[[]]>}<{[{[{[]{()[[[]
            [<(<(<(<{}))><([]([]()
            <{([([[(<>()){}]>(<<{{
            <{([{{}}[<[[[<>{}]]]>[]]
            """.split("\n"));
        var input = Files.readAllLines(Paths.get("input10.txt"));
        var list = new ArrayList<>(input);

        part01(list);
        part02(list);
    }

    private static void part01(List<String> input) {
        var sum = 0;
        var iterator = input.iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();
            var error = findError(line);
            sum += error;
            if (error != 0) {
                iterator.remove();
            }
        }
        System.out.println(sum);
    }

    private static void part02(List<String> input) {
        var scores = new ArrayList<Long>();
        for (var line : input) {
            var blocks = new Stack<Character>();
            for (char c : line.toCharArray()) {
                if (PAIRS.containsKey(c)) {
                    blocks.push(c);
                } else if (PAIRS.containsValue(c)) {
                    blocks.pop();
                }
            }
            var score = 0L;
            while (!blocks.empty()) {
                var c = PAIRS.get(blocks.pop());
                score *= 5;
                score += scorePart02(c);
            }
            scores.add(score);
        }
        scores.sort(Long::compareTo);
        System.out.println(scores.get(scores.size() / 2));
    }

    private static int findError(String line) {
        var blocks = new Stack<Character>();
        for (char c : line.toCharArray()) {
            if (PAIRS.containsKey(c)) {
                blocks.push(c);
            } else if (PAIRS.containsValue(c)) {
                var open = blocks.pop();
                if (notValidPair(open, c)) {
                    return scorePart01(c);
                }
            }
        }
        return 0;
    }

    private static boolean notValidPair(char open, char c) {
        return PAIRS.get(open) != c;
    }

    private static int scorePart01(char c) {
        return switch (c) {
            case ')' -> 3;
            case ']' -> 57;
            case '}' -> 1197;
            case '>' -> 25137;
            default -> 0;
        };
    }

    private static int scorePart02(char c) {
        return List.of(')', ']', '}', '>').indexOf(c) + 1;
    }
}
