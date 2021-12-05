import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Day05 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input05.txt")).stream()
            .map(Line::parse)
            .toList();

        part01(lines);
        part02(lines);
    }

    private static void part01(List<Line> input) {
        var straightLines = input.stream()
            .filter(Line::isStraight)
            .toList();

        iterateAndPrint(straightLines);
    }

    private static void part02(List<Line> lines) {
        iterateAndPrint(lines);
    }

    private static void iterateAndPrint(List<Line> lines) {
        var pointMap = new HashMap<Point, Integer>();
        for (var line : lines) {
            line.iterate(point -> pointMap.compute(point, (key, value) -> value == null ? 1 : value + 1));
        }

        var overlaps = pointMap.values().stream()
            .filter(i -> i > 1)
            .count();
        System.out.println(overlaps);
    }

    record Line(Point start, Point end) {
        static Line parse(String line) {
            var points = line.split(" -> ");

            return new Line(Point.parse(points[0]), Point.parse(points[1]));
        }

        boolean isStraight() {
            return start.x == end.x || start.y == end.y;
        }

        void iterate(Consumer<Point> consumer) {
            int xStep = (int) Math.signum(end.x - start.x);
            int yStep = (int) Math.signum(end.y - start.y);

            var length = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y)) + 1;
            for (int i = 0; i < length; i++) {
                consumer.accept(new Point(start.x + i * xStep, start.y + i * yStep));
            }
        }
    }

    record Point(int x, int y) {
        static Point parse(String input) {
            var coordinates = input.split(",");
            return new Point(
                Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1])
            );
        }
    }
}
