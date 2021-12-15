import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Day15 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input15.txt"));

        var width = input.get(0).length();
        var height = input.size();
        var graph = new HashMap<Point, Integer>();
        for (int j = 0; j < height; j++) {
            var line = input.get(j);
            for (int i = 0; i < width; i++) {
                graph.put(new Point(i, j), line.charAt(i) - '0');
            }
        }

        part01(graph, width, height);
        part02(graph, width, height);
    }

    private static void part01(Map<Point, Integer> graph, int width, int height) {
        solveDijkstra(graph, width, height);
    }

    private static void part02(Map<Point, Integer> graph, int width, int height) {
        int newWidth = width * 5;
        int newHeight = height * 5;
        for (int j = 0; j < newHeight; j++) {
            for (int i = 0; i < newWidth; i++) {
                if (i < width && j < height) continue;

                var risk = (graph.get(new Point(i % width, j % height)) + i / width + j / width);
                while (risk > 9) {
                    risk -= 9;
                }
                graph.put(new Point(i, j), risk);
            }
        }
        solveDijkstra(graph, newWidth, newHeight);
    }

    private static void solveDijkstra(Map<Point, Integer> graph, int width, int height) {
        var distances = new HashMap<Point, Integer>();

        var start = new Point(0, 0);
        var end = new Point(width - 1, height - 1);
        distances.put(start, 0);

        var queue = new PriorityQueue<Point>(Comparator.comparingInt(point -> distances.getOrDefault(point, Integer.MAX_VALUE)));
        queue.add(start);

        while (!queue.isEmpty()) {
            var point = queue.poll();
            var neighbours = point.neighbours(width, height);
            for (var neighbour : neighbours) {
                if (!distances.containsKey(point)) {
                    System.out.println();
                }
                var distance = graph.get(neighbour) + distances.get(point);
                if (distance >= distances.getOrDefault(neighbour, Integer.MAX_VALUE)) continue;

                distances.put(neighbour, distance);
                queue.add(neighbour);
            }
        }
        System.out.println(distances.get(end));
    }

    record Point(int x, int y) {
        List<Point> neighbours(int width, int height) {
            var neighbours = new ArrayList<Point>();
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
