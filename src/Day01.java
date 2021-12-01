import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input01.txt"))
            .stream()
            .map(Integer::parseInt)
            .toList();

        var increases = 0;
        var previous = lines.get(0);
        for(int i = 1; i< lines.size();i++) {
            var current = lines.get(i);
            if (current > previous) {
                increases++;
            }
            previous = current;
        }
        System.out.println(increases);
    }
}
