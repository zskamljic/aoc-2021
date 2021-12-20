import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day20 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input20.txt"));

        var parts = input.split("\n\n");
        var enhancementAlgorithm = parts[0].toCharArray();
        var image = parseImage(parts[1]);

        part1(image, enhancementAlgorithm);
        part2(image, enhancementAlgorithm);
    }

    private static void part1(List<String> image, char[] enhancementAlgorithm) {
        enhanceTimes(image, 2, enhancementAlgorithm);
    }

    private static void part2(List<String> image, char[] enhancementAlgorithm) {
        enhanceTimes(image, 50, enhancementAlgorithm);
    }

    private static void enhanceTimes(List<String> image, int times, char[] enhancementAlgorithm) {
        var darkPixel = '.';
        for (int i = 0; i < times; i++) {
            image = enhance(image, enhancementAlgorithm, darkPixel);
            darkPixel = enhancementAlgorithm[darkPixel == '.' ? 0 : enhancementAlgorithm.length - 1];
        }
        var lit = image.stream()
            .map(s -> s.replaceAll("[^#]", ""))
            .mapToInt(String::length)
            .sum();
        System.out.println(lit);
    }

    private static List<String> enhance(List<String> image, char[] enhancementAlgorithm, char darkPixel) {
        var result = new ArrayList<String>();
        for (int y = -1; y <= image.size(); y++) {
            var lineBuilder = new StringBuilder();
            for (int x = -1; x <= image.get(0).length(); x++) {
                var point = new Point(x, y);
                var value = point.index(image, darkPixel);
                if (enhancementAlgorithm[value] == '#') {
                    lineBuilder.append("#");
                } else {
                    lineBuilder.append(".");
                }
            }
            result.add(lineBuilder.toString());
        }
        return result;
    }

    private static List<String> parseImage(String image) {
        return Arrays.asList(image.split("\n"));
    }

    record Point(int x, int y) {
        public int index(List<String> image, char darkPixel) {
            var value = 0;
            for (var y = -1; y <= 1; y++) {
                for (var x = -1; x <= 1; x++) {
                    value <<= 1;
                    var point = new Point(this.x + x, this.y + y);
                    var outside = point.x < 0 || point.y < 0 || point.y >= image.size() || point.x >= image.get(0).length();
                    char pixel;
                    if (outside) {
                        pixel = darkPixel;
                    } else {
                        pixel = image.get(point.y).charAt(point.x);
                    }
                    if (pixel == '#') {
                        value |= 1;
                    }
                }
            }
            return value;
        }
    }
}
