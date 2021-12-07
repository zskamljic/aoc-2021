import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.IntFunction;

public class Day07 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input07.txt")).trim();

        var positions = Arrays.stream(input.split(","))
            .mapToInt(Integer::parseInt)
            .toArray();
        Arrays.sort(positions);

        part01(positions);
        part02(positions);
    }

    private static void part01(int[] positions) {
        calculateMinFuel(positions, i -> i);
    }

    private static void part02(int[] positions) {
        calculateMinFuel(positions, Day07::calculateCost);
    }

    private static void calculateMinFuel(int[] positions, IntFunction<Integer> costing) {
        var minFuel = Integer.MAX_VALUE;
        for (var i = 0; i < positions[positions.length - 1]; i++) {
            int finalI = i;
            var fuel = Arrays.stream(positions)
                .map(x -> costing.apply(Math.abs(x - finalI)))
                .sum();
            if (fuel < minFuel) {
                minFuel = fuel;
            }
        }
        System.out.println(minFuel);
    }

    private static int calculateCost(int distance) {
        return distance * (distance + 1) / 2;
    }
}
