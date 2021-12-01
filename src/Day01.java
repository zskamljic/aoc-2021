import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.List;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var numbers = Files.readAllLines(Paths.get("input01.txt"))
            .stream()
            .map(Integer::parseInt)
            .toList();

        part01(numbers);
        part02(numbers);
    }

    private static void part01(List<Integer> numbers) {
        var increases = 0;
        var previous = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            var current = numbers.get(i);
            if (current > previous) {
                increases++;
            }
            previous = current;
        }
        System.out.println(increases);
    }

    private static void part02(List<Integer> numbers) {
        var queue = new ArrayDeque<>(numbers);
        int a = queue.poll();
        int b = queue.poll();
        int c = queue.poll();
        int increases = 0;
        while (!queue.isEmpty()) {
            var currentNum = queue.poll();
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
