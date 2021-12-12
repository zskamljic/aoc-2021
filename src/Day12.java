import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day12 {
    public static final String START = "start";
    public static final String END = "end";

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input12.txt"));

        var connections = new HashMap<String, List<String>>();
        for (var connection : input) {
            var parts = connection.split("-");
            addPath(connections, parts[0], parts[1]);
            addPath(connections, parts[1], parts[0]);
        }

        part01(connections);
        part02(connections);
    }

    private static void addPath(Map<String, List<String>> connections, String start, String end) {
        connections.compute(start, (key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(end);
            return value;
        });
    }

    private static void part01(Map<String, List<String>> connections) {
        findPathsWithPredicate(connections, (nodes, s) -> !nodes.contains(s));
    }

    private static void part02(Map<String, List<String>> connections) {
        findPathsWithPredicate(connections, (nodes, candidate) -> {
            if (!nodes.contains(candidate)) return true;

            return nodes.stream()
                .filter(s -> s.toLowerCase().equals(s))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .stream()
                .noneMatch(count -> count > 1);
        });
    }

    private static void findPathsWithPredicate(
        Map<String, List<String>> connections,
        BiFunction<List<String>, String, Boolean> predicate
    ) {
        var queue = new ArrayDeque<Path>();
        queue.add(new Path(START));

        var paths = 0;
        while (!queue.isEmpty()) {
            var current = queue.poll();
            if (current.isDone()) {
                paths++;
                continue;
            }
            current.findValidConnections(connections, predicate).ifPresent(queue::addAll);
        }
        System.out.println(paths);
    }

    record Path(List<String> nodes) {
        Path(String node) {
            this(new ArrayList<>(List.of(node)));
        }

        String lastNode() {
            return nodes.get(nodes.size() - 1);
        }

        boolean isDone() {
            return lastNode().equals(END);
        }

        public Optional<List<Path>> findValidConnections(
            Map<String, List<String>> connections,
            BiFunction<List<String>, String, Boolean> predicate
        ) {
            if (!connections.containsKey(lastNode())) {
                return Optional.empty();
            }

            var candidates = connections.get(lastNode());
            var paths = candidates.stream()
                .filter(s -> !s.equals(START))
                .filter(s -> s.toUpperCase().equals(s) || predicate.apply(nodes, s))
                .map(this::appending)
                .toList();
            return Optional.of(paths);
        }

        Path appending(String node) {
            var newPath = new ArrayList<>(nodes);
            newPath.add(node);
            return new Path(newPath);
        }
    }
}
