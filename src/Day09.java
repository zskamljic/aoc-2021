import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day09 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input09.txt"));

        var map = new int[input.get(0).length()][input.size()];
        for (int j = 0; j < map[0].length; j++) {
            for (int i = 0; i < map.length; i++) {
                map[i][j] = input.get(j).charAt(i) - '0';
            }
        }

        var minimums = part01(map);
        part02(map, minimums);
    }

    private static List<Point> part01(int[][] map) {
        var sum = 0;
        var minimums = new ArrayList<Point>();
        for (int j = 0; j < map[0].length; j++) {
            for (int i = 0; i < map.length; i++) {
                var current = map[i][j];
                if (i != 0 && map[i - 1][j] <= current) continue;
                if (j != 0 && map[i][j - 1] <= current) continue;
                if (i != map.length - 1 && map[i + 1][j] <= current) continue;
                if (j != map[i].length - 1 && map[i][j + 1] <= current) continue;

                minimums.add(new Point(i, j));
                sum += current + 1;
            }
        }
        System.out.println(sum);
        return minimums;
    }

    private static void part02(int[][] map, List<Point> minimums) {
        var basins = minimums.stream()
            .map(point -> findBasin(map, point))
            .map(Set::size)
            .sorted(Comparator.reverseOrder())
            .mapToInt(i -> i)
            .limit(3)
            .reduce(1, (acc, item) -> acc * item);
        System.out.println(basins);
    }

    private static Set<Point> findBasin(int[][] map, Point point) {
        var candidates = new ArrayDeque<Point>();
        candidates.add(point);

        var basinMembers = new HashSet<Point>();
        basinMembers.add(point);
        while (!candidates.isEmpty()) {
            var current = candidates.poll();
            var neighbours = current.neighbours(map.length, map[0].length);
            neighbours.removeAll(basinMembers);
            var valid = neighbours.stream()
                .filter(n -> map[n.x][n.y] < 9)
                .toList();
            basinMembers.addAll(valid);
            candidates.addAll(valid);
        }
        return basinMembers;
    }


    record Point(int x, int y) {
        Set<Point> neighbours(int width, int height) {
            var neighbours = new HashSet<Point>();
            if (x > 0) {
                neighbours.add(new Point(x - 1, y));
            }
            if (y > 0) {
                neighbours.add(new Point(x, y - 1));
            }
            if (x < width - 1) {
                neighbours.add(new Point(x + 1, y));
            }
            if (y < height - 1) {
                neighbours.add(new Point(x, y + 1));
            }
            return neighbours;
        }
    }
}
