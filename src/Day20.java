import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day20 {
    public static void main(String[] args) throws IOException {
        var input = """
            ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#
                        
            #..#.
            #....
            ##..#
            ..#..
            ..###
            """;
        input = Files.readString(Paths.get("input20.txt"));

        var parts = input.split("\n\n");
        var enhancementAlgorithm = parts[0].toCharArray();
        var image = parseImage(parts[1]);

        part1(image, enhancementAlgorithm);
        part2(image, enhancementAlgorithm);
    }

    private static void part1(List<Point> image, char[] enhancementAlgorithm) {
        enhance(image, 2, enhancementAlgorithm);
    }

    private static void part2(List<Point> image, char[] enhancementAlgorithm) {
        enhance(image, 50, enhancementAlgorithm);
    }

    private static void enhance(List<Point> image, int times, char[] enhancementAlgorithm) {
        var darkPixel = '.';
        for (int i = 0; i < times; i++) {
            image = enhance(image, enhancementAlgorithm, darkPixel);
            darkPixel = enhancementAlgorithm[darkPixel == '.' ? 0 : enhancementAlgorithm.length - 1];
        }
        System.out.println(image.size());
    }

    private static List<Point> enhance(List<Point> image, char[] enhancementAlgorithm, char darkPixel) {
        var minX = Integer.MAX_VALUE;
        var minY = Integer.MAX_VALUE;
        var maxX = Integer.MIN_VALUE;
        var maxY = Integer.MIN_VALUE;

        for (var point : image) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        var result = new ArrayList<Point>();
        for (int y = minY - 1; y <= maxY + 1; y++) {
            for (int x = minX - 1; x <= maxX + 1; x++) {
                var point = new Point(x, y);
                var value = point.index(image, minX, minY, maxX, maxY, darkPixel);
                if (enhancementAlgorithm[value] == '#') {
                    result.add(point);
                }
            }
        }
        return result;
    }

    private static List<Point> parseImage(String image) {
        var result = new ArrayList<Point>();
        var y = 0;
        for (var line : image.split("\n")) {
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    result.add(new Point(x, y));
                }
            }
            y++;
        }
        return result;
    }

    record Point(int x, int y) {
        public int index(List<Point> image, int minX, int minY, int maxX, int maxY, char darkPixel) {
            var value = 0;
            for (var y = -1; y <= 1; y++) {
                for (var x = -1; x <= 1; x++) {
                    value <<= 1;
                    var point = new Point(this.x + x, this.y + y);
                    if (darkPixel == '#' && (point.x < minX || point.y < minY || point.x > maxX || point.y > maxY) || image.contains(point)) {
                        value |= 1;
                    }
                }
            }
            return value;
        }
    }
}
