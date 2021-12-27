import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class Day23 {
    private static final List<String> EXTRA_LINES = Arrays.asList("""
          #D#C#B#A#
          #D#B#A#C#
        """.split("\n"));

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input23.txt"));

        part1(input);
        part2(input);
    }

    private static void part1(List<String> input) {
        var state = State.parse(input);
        var result = startOrganization(state);
        System.out.println(result);
    }

    private static void part2(List<String> input) {
        input = new ArrayList<>(input);
        input.addAll(3, EXTRA_LINES);
        var state = State.parse(input);
        var result = startOrganization(state);
        System.out.println(result);
    }

    private static List<Integer> startOrganization(State state) {
        var queue = new PriorityQueue<StateCost>();
        queue.add(new StateCost(state, 0));

        var visited = new HashSet<StateCost>();
        var costsForState = new HashMap<State, Integer>();

        while (!queue.isEmpty()) {
            var current = queue.poll();
            visited.add(current);
            var states = current.state.nextStates();

            states.removeAll(visited);
            states.forEach(s -> {
                var newCost = current.cost + s.cost;
                if (newCost < costsForState.getOrDefault(s.state, Integer.MAX_VALUE)) {
                    costsForState.put(s.state, newCost);
                    queue.add(new StateCost(s.state, newCost));
                }
            });
        }

        return costsForState.entrySet()
            .stream()
            .filter(e -> e.getKey().isDone())
            .map(Map.Entry::getValue)
            .toList();
    }

    record State(char[][] map) {
        static State parse(List<String> input) {
            var map = input.subList(1, input.size() - 1)
                .stream()
                .map(s -> s.substring(1, s.length() - 1))
                .map(String::toCharArray)
                .toArray(char[][]::new);
            return new State(map);
        }

        public List<StateCost> nextStates() {
            var nextStates = new ArrayList<StateCost>();
            findHallwayStates(nextStates);
            findWrongRoomStates(nextStates);
            return nextStates;
        }

        private void findHallwayStates(List<StateCost> nextStates) {
            for (int i = 0; i < map[0].length; i++) {
                var character = map[0][i];
                if (!Character.isUpperCase(character) || !roomEmptyOrValid(character)) continue;

                var targetRoomIndex = indexForRoom(character);
                if (hallwayBlocked(i, targetRoomIndex)) continue;

                var y = lastFreeSlot(character);
                var cost = calculateCost(i, targetRoomIndex, y, character);

                var newMap = deepCopy();
                newMap[0][i] = '.';
                newMap[y][targetRoomIndex] = character;
                nextStates.add(new StateCost(new State(newMap), cost));
            }
        }

        private void findWrongRoomStates(ArrayList<StateCost> nextStates) {
            for (var room : List.of(2, 4, 6, 8)) {
                var roomName = nameForRoom(room);
                if (roomEmptyOrValid(roomName)) continue;

                var indexToMove = firstOccupiedSlot(room);
                for (var slot : List.of(0, 1, 3, 5, 7, 9, 10)) {
                    if (map[0][slot] != '.') continue;
                    if (hallwayBlocked(slot, room)) continue;

                    var character = map[indexToMove][room];
                    var cost = calculateCost(room, slot, indexToMove, character);

                    var newMap = deepCopy();
                    newMap[indexToMove][room] = '.';
                    newMap[0][slot] = character;
                    nextStates.add(new StateCost(new State(newMap), cost));
                }
            }
        }

        private char[][] deepCopy() {
            var newMap = new char[map.length][];
            for (int j = 0; j < newMap.length; j++) {
                newMap[j] = Arrays.copyOf(map[j], map[j].length);
            }
            return newMap;
        }

        private int calculateCost(int x1, int x2, int yDiff, char character) {
            return (int) ((Math.abs(x1 - x2) + yDiff) * Math.pow(10, character - 'A'));
        }

        private int firstOccupiedSlot(int room) {
            for (int i = 1; i < map.length; i++) {
                if (map[i][room] != '.') return i;
            }
            throw new IllegalStateException("Room has no occupied slot");
        }

        private int lastFreeSlot(char character) {
            for (int i = 1; i < map.length; i++) {
                if (map[i][indexForRoom(character)] == '.') return i;
            }
            throw new IllegalStateException("No free room");
        }

        private boolean hallwayBlocked(int start, int end) {
            int min;
            int max;
            if (start < end) {
                min = start + 1;
                max = end;
            } else {
                min = end;
                max = start - 1;
            }
            for (int i = min; i <= max; i++) {
                if (map[0][i] != '.') return true;
            }
            return false;
        }

        private int indexForRoom(char character) {
            return switch (character) {
                case 'A' -> 2;
                case 'B' -> 4;
                case 'C' -> 6;
                case 'D' -> 8;
                default -> throw new IllegalArgumentException("Invalid room name");
            };
        }

        private char nameForRoom(int room) {
            return switch (room) {
                case 2 -> 'A';
                case 4 -> 'B';
                case 6 -> 'C';
                case 8 -> 'D';
                default -> throw new IllegalArgumentException("Invalid room index");
            };
        }

        private boolean roomEmptyOrValid(char position) {
            var indexForRoom = indexForRoom(position);
            return IntStream.range(1, map.length)
                .allMatch(i -> map[i][indexForRoom] == '.' || map[i][indexForRoom] == position);
        }

        public boolean isDone() {
            for (var room : List.of(2, 4, 6, 8)) {
                for (int i = 1; i < map.length; i++) {
                    if (map[i][room] != nameForRoom(room)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State state)) return false;

            return Arrays.deepEquals(map, state.map);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(map);
        }
    }

    record StateCost(State state, int cost) implements Comparable<StateCost> {
        @Override
        public int compareTo(StateCost other) {
            return Integer.compare(cost, other.cost);
        }
    }
}
