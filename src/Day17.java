import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Day17 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input17.txt"));

        var area = Area.parse(input);
        solve(area);
    }

    private static void solve(Area area) {
        var maxY = Integer.MIN_VALUE;
        var validTargets = 0;
        for (int x = 0; x <= area.maxX; x++) {
            for (int y = -1000; y < 1000; y++) {
                var result = shootHigh(area, x, y);
                if (result != Integer.MIN_VALUE) {
                    validTargets++;
                }
                if (result > maxY) {
                    maxY = result;
                }
            }
        }
        // Part 1
        System.out.println(maxY);
        // Part 2
        System.out.println(validTargets);
    }

    private static int shootHigh(Area area, int x, int y) {
        var probe = new Probe(0, 0, x, y);
        var maxY = Integer.MIN_VALUE;
        var hitArea = false;
        while (!area.isOvershot(probe)) {
            if (area.contains(probe)) {
                hitArea = true;
                break;
            }
            if (probe.y > maxY) {
                maxY = probe.y;
            }
            probe = probe.next();
        }
        return hitArea ? maxY : Integer.MIN_VALUE;
    }

    record Probe(int x, int y, int xSpeed, int ySpeed) {
        Probe next() {
            var newX = x + xSpeed;
            var newY = y + ySpeed;
            var newXSpeed = (int) (xSpeed - Math.signum(xSpeed));
            var newYSpeed = ySpeed - 1;
            return new Probe(newX, newY, newXSpeed, newYSpeed);
        }
    }

    record Area(int minX, int maxX, int minY, int maxY) {
        private static final String COORDINATES_REGEX = "\\w=(-?\\d+)..(-?\\d+),?";

        boolean contains(Probe probe) {
            return probe.x >= minX && probe.x <= maxX && probe.y >= minY && probe.y <= maxY;
        }

        boolean isOvershot(Probe probe) {
            return probe.x > maxX || probe.y < minY;
        }

        static Area parse(String input) {
            var scanner = new Scanner(input);
            scanner.next(); // target
            scanner.next(); // area:
            var x = scanner.next().replaceAll(COORDINATES_REGEX, "$1 $2").split("\\s"); // x=min..max;
            var y = scanner.next().replaceAll(COORDINATES_REGEX, "$1 $2").split("\\s"); // y=min..max;

            var x1 = Math.abs(Integer.parseInt(x[0]));
            var x2 = Math.abs(Integer.parseInt(x[1]));
            var minX = Math.min(x1, x2);
            var maxX = Math.max(x1, x2);
            return new Area(
                minX, maxX,
                Integer.parseInt(y[0]),
                Integer.parseInt(y[1])
            );
        }
    }
}
