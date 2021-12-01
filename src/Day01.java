import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var numbers = Files.readAllLines(Paths.get("input01.txt"))
            .stream()
            .mapToInt(Integer::parseInt)
            .toArray();

        part01(numbers);
        part02(numbers);
    }

    private static void part01(int[] numbers) {
        var increases = 0;
        var previous = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            var current = numbers[i];
            if (current > previous) {
                increases++;
            }
            previous = current;
        }
        System.out.println(increases);
    }

    private static void part02(int[] numbers) {
        int i = 0;
        int a = numbers[i++];
        int b = numbers[i++];
        int c = numbers[i++];
        int increases = 0;
        while (i < numbers.length) {
            var currentNum = numbers[i++];
            var lastSum = a + b + c;
            var currentSum = b + c + currentNum;

            if (currentSum > lastSum) {
                increases++;
            }
            a = b;
            b = c;
            c = currentNum;
        }
        System.out.println(increases);
    }
}
