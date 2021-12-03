import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day03 {
    public static void main(String[] args) throws IOException {
//        var input = Arrays.asList("""
//            00100
//            11110
//            10110
//            10111
//            10101
//            01111
//            00111
//            11100
//            10000
//            11001
//            00010
//            01010
//            """.split("\n"));
        var input = Files.readAllLines(Paths.get("input03.txt"));

        var onesCounts = new int[input.get(0).length()];
        for (var line : input) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '1') {
                    onesCounts[i]++;
                }
            }
        }

        var gamma = 0;
        var epsilon = 0;
        for (var one : onesCounts) {
            gamma <<= 1;
            epsilon <<= 1;
            if (one > input.size() / 2) {
                gamma |= 1;
            } else {
                epsilon |= 1;
            }
        }
        System.out.println(gamma * epsilon);
    }
}
