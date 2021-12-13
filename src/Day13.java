import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Day13 {
    public static void main(String[] args) throws IOException {
        var input2 = """
            6,10
            0,14
            9,10
            0,3
            10,4
            4,11
            6,0
            6,12
            4,1
            0,13
            10,12
            3,4
            3,0
            8,4
            1,10
            2,14
            8,10
            9,0
                        
            fold along y=7
            fold along x=5
            """;
        var input = Files.readString(Paths.get("input13.txt"));

        var parts = input.split("\n\n");
        var coordinates = parseCoordinates(parts[0]);
        var folds = parseFolds(parts[1]);

        part01(coordinates, folds.get(0));
        part02(coordinates, folds);

    }

    private static void part01(List<Point> coordinates, Fold fold) {
        var folded = fold.apply(coordinates);
        System.out.println(folded.size());
    }

    private static void part02(List<Point> coordinates, List<Fold> folds) {
        var folded = coordinates;
        for (var fold : folds) {
            folded = fold.apply(folded);
        }

        print(folded);
    }

    private static void print(List<Point> folded) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (var point : folded) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }
        for (int j = minY; j <= maxY; j++) {
            for (int i = minX; i <= maxX; i++) {
                var point = new Point(i, j);
                if (folded.contains(point)) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private static List<Point> parseCoordinates(String part) {
        return Arrays.stream(part.split("\n"))
            .map(Point::parse)
            .toList();
    }

    private static List<Fold> parseFolds(String part) {
        return Arrays.stream(part.split("\n"))
            .map(Fold::parse)
            .toList();
    }

    record Point(int x, int y) {
        static Point parse(String coordinates) {
            var parts = coordinates.split(",");
            return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    sealed interface Fold {
        List<Point> apply(List<Point> coordinates);

        record Horizontal(int x) implements Fold {
            @Override
            public List<Point> apply(List<Point> coordinates) {
                var result = new HashSet<Point>();
                for (var coordinate : coordinates) {
                    if (coordinate.x < x) {
                        result.add(coordinate);
                    } else {
                        result.add(new Point(x + (x - coordinate.x), coordinate.y));
                    }
                }
                return result.stream().toList();
            }
        }

        record Vertical(int y) implements Fold {
            @Override
            public List<Point> apply(List<Point> coordinates) {
                var result = new HashSet<Point>();
                for (var coordinate : coordinates) {
                    if (coordinate.y < y) {
                        result.add(coordinate);
                    } else {
                        result.add(new Point(coordinate.x, y + (y - coordinate.y)));
                    }
                }
                return result.stream().toList();
            }
        }

        static Fold parse(String line) {
            var words = line.split("\s");
            var coordinate = words[2].split("=");
            if ("x".equals(coordinate[0])) {
                return new Horizontal(Integer.parseInt(coordinate[1]));
            } else if ("y".equals(coordinate[0])) {
                return new Vertical(Integer.parseInt(coordinate[1]));
            }
            throw new IllegalArgumentException("Invalid fold specification");
        }
    }
}
