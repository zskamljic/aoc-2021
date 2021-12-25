import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day25 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input25.txt"));

        var grid = new Grid(input);
        var steps = 0;
        while (true) {
            steps++;

            var moved = grid.step();
            if (!moved) break;
        }
        System.out.println(steps);
    }

    static class Grid {
        private Map<Point, Direction> grid = new HashMap<>();
        private final int width;
        private final int height;

        Grid(List<String> lines) {
            for (var y = 0; y < lines.size(); y++) {
                var line = lines.get(y);
                for (var x = 0; x < line.length(); x++) {
                    if (line.charAt(x) == 'v') {
                        grid.put(new Point(x, y), Direction.SOUTH);
                    } else if (line.charAt(x) == '>') {
                        grid.put(new Point(x, y), Direction.EAST);
                    }
                }
            }
            width = lines.get(0).length();
            height = lines.size();
        }

        public boolean step() {
            var moved = move(Direction.EAST);
            moved |= move(Direction.SOUTH);
            return moved;
        }

        private boolean move(Direction direction) {
            var moved = false;
            var next = new HashMap<Point, Direction>();
            var cucumbers = grid.entrySet().stream()
                .peek(entry -> {
                    if (entry.getValue() != direction) {
                        next.put(entry.getKey(), entry.getValue());
                    }
                })
                .filter(entry -> entry.getValue() == direction)
                .map(Map.Entry::getKey)
                .toList();
            for (var cucumber : cucumbers) {
                var nextPosition = cucumber.next(direction, width, height);
                if (grid.containsKey(nextPosition)) {
                    next.put(cucumber, direction);
                } else {
                    next.put(nextPosition, direction);
                    moved = true;
                }
            }
            grid = next;
            return moved;
        }

        public void print() {
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    var direction = grid.get(new Point(x, y));
                    switch (direction) {
                        case null -> System.out.print(".");
                        case SOUTH -> System.out.print("v");
                        case EAST -> System.out.print(">");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    record Point(int x, int y) {
        Point next(Direction direction, int width, int height) {
            return switch (direction) {
                case SOUTH -> new Point(x, (y + 1) % height);
                case EAST -> new Point((x + 1) % width, y);
            };
        }
    }

    enum Direction {
        SOUTH, EAST
    }
}
