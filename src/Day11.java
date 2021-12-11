import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class Day11 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input11.txt"));

        var grid = new int[input.get(0).length()][input.size()];
        for (int j = 0; j < input.size(); j++) {
            var line = input.get(j);
            for (int i = 0; i < line.length(); i++) {
                grid[i][j] = line.charAt(i) - '0';
            }
        }
        part01(grid);
        part02(grid);
    }

    private static void part01(int[][] grid) {
        grid = Arrays.stream(grid)
            .map(array -> Arrays.copyOf(array, array.length))
            .toArray(int[][]::new);

        var flashes = 0;
        for (var i = 0; i < 100; i++) {
            flashes += step(grid);
        }
        System.out.println(flashes);
    }

    private static void part02(int[][] grid) {
        for (int i = 0; ; i++) {
            var flashes = step(grid);
            if (flashes == 100) {
                System.out.println(i + 1);
                break;
            }
        }
    }

    private static int step(int[][] grid) {
        increment(grid);
        var flashed = flashAll(grid);
        flashed.forEach(point -> grid[point.x][point.y] = 0);
        return flashed.size();
    }

    private static void increment(int[][] grid) {
        iterateAll(grid, (x, y) -> grid[x][y]++);
    }

    private static Set<Point> flashAll(int[][] grid) {
        var flashes = new HashSet<Point>();
        iterateAll(grid, (x, y) -> flash(grid, x, y, flashes));
        return flashes;
    }

    private static void flash(int[][] grid, int x, int y, Set<Point> flashes) {
        if (grid[x][y] <= 9) return;

        var current = new Point(x, y);
        if (!flashes.add(current)) {
            return;
        }
        var width = grid[0].length;
        var height = grid.length;
        var pending = new ArrayDeque<>(current.neighbours(width, height));
        while (!pending.isEmpty()) {
            current = pending.poll();
            grid[current.x][current.y]++;
            if (grid[current.x][current.y] <= 9) continue;

            if (flashes.add(current)) {
                pending.addAll(current.neighbours(width, height));
            }
        }
    }

    private static void iterateAll(int[][] grid, BiConsumer<Integer, Integer> operator) {
        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                operator.accept(i, j);
            }
        }
    }

    private static void print(int[][] grid) {
        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 0; i < grid.length; i++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    record Point(int x, int y) {
        List<Point> neighbours(int width, int height) {
            var neighbours = new ArrayList<Point>();
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) continue;
                    if (x + i < 0 || x + i >= width) continue;
                    if (y + j < 0 || y + j >= height) continue;

                    neighbours.add(new Point(x + i, y + j));
                }
            }
            return neighbours;
        }
    }
}
