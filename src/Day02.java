import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day02 {
    public static void main(String[] args) throws IOException {
        var instructions = Files.readAllLines(Paths.get("input02.txt"))
            .stream()
            .map(Instruction::parse)
            .toList();

        part01(instructions);
        part02(instructions);
    }

    private static void part01(List<Instruction> instructions) {
        int horizontal = 0;
        int depth = 0;
        for (var instruction : instructions) {
            switch (instruction.direction()) {
                case "forward" -> horizontal += instruction.amount();
                case "down" -> depth += instruction.amount();
                case "up" -> depth -= instruction.amount();
            }
        }
        System.out.println(horizontal * depth);
    }

    private static void part02(List<Instruction> instructions) {
        var aim = 0;
        var horizontal = 0;
        var depth = 0;
        for (var instruction : instructions) {
            switch (instruction.direction()) {
                case "down" -> aim += instruction.amount();
                case "up" -> aim -= instruction.amount();
                case "forward" -> {
                    horizontal += instruction.amount();
                    depth += aim * instruction.amount();
                }
            }
        }

        System.out.println(horizontal * depth);
    }

    record Instruction(String direction, int amount) {
        static Instruction parse(String line) {
            var parts = line.split("\s");

            return new Instruction(parts[0], Integer.parseInt(parts[1]));
        }
    }
}
