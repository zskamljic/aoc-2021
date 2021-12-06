import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day06 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input06.txt")).strip();

        var ages = Arrays.stream(input.split(","))
            .mapToInt(Integer::parseInt)
            .toArray();

        part01(ages);
        part02(ages);
    }

    private static void part01(int[] ages) {
        solveForDays(ages, 80);
    }

    private static void part02(int[] ages) {
        solveForDays(ages, 256);
    }

    private static void solveForDays(int[] ages, int days) {
        var fishAtAge = new long[9];
        for (int i : ages) {
            fishAtAge[i]++;
        }
        for (var i = 0; i < days; i++) {
            var newFish = fishAtAge[0];
            for (var j = 0; j < fishAtAge.length - 1; j++) {
                fishAtAge[j] = fishAtAge[j + 1];
            }
            fishAtAge[8] = newFish;
            fishAtAge[6] += newFish;
        }

        var totalFish = Arrays.stream(fishAtAge)
            .sum();
        System.out.println(totalFish);
    }
}
