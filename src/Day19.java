import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day19 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input19.txt"));

        var scanners = Arrays.stream(input.split("\n\n"))
            .map(Sensor::parse)
            .collect(Collectors.toCollection(ArrayList::new));

        var current = scanners.remove(0);
        var scannerLocations = new ArrayList<>(List.of(new Point3(0, 0, 0)));
        while (!scanners.isEmpty()) {
            var iterator = scanners.iterator();
            while (iterator.hasNext()) {
                var scanner = iterator.next();
                for (var variant : scanner.rotations()) {
                    current.translate(variant).ifPresent(translation -> {
                        scannerLocations.add(translation);
                        current.addTranslated(variant.beacons, translation);
                        iterator.remove();
                    });
                }
            }
        }
        // Part 1
        System.out.println(current.beacons.size());

        // Part 2
        var max = Integer.MIN_VALUE;
        for (int i = 0; i < scannerLocations.size() - 1; i++) {
            for (int j = i + 1; j < scannerLocations.size(); j++) {
                var distance = scannerLocations.get(i).manhattan(scannerLocations.get(j));
                if (distance > max) {
                    max = distance;
                }
            }
        }
        System.out.println(max);
    }

    record Sensor(String name, List<Point3> beacons) {
        static Sensor parse(String input) {
            var lines = input.split("\n");
            var name = lines[0].replaceAll("--- ([\\w\\d\\s]+) ---", "$1");
            var beacons = Arrays.stream(lines)
                .skip(1)
                .map(Point3::parse)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
            return new Sensor(name, beacons);
        }

        public List<Sensor> rotations() {
            var rotations = new ArrayList<Sensor>();
            for (var beacon : beacons) {
                var variations = beacon.rotations();
                for (int i = 0; i < variations.size(); i++) {
                    if (rotations.size() <= i) {
                        rotations.add(new Sensor(name, new ArrayList<>()));
                    }
                    var scanner = rotations.get(i);
                    scanner.beacons.add(variations.get(i));
                }
            }
            return rotations;
        }

        public Optional<Point3> translate(Sensor variant) {
            var map = new HashMap<Point3, Integer>();
            for (var beacon : beacons) {
                for (var otherBeacon : variant.beacons) {
                    var delta = beacon.minus(otherBeacon);
                    map.merge(delta, 1, Integer::sum);
                }
            }
            return map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 12)
                .map(Map.Entry::getKey)
                .findFirst();
        }

        public void addTranslated(List<Point3> beacons, Point3 translation) {
            beacons.stream()
                .map(translation::plus)
                .filter(Predicate.not(this.beacons::contains))
                .forEach(this.beacons::add);
        }
    }

    record Point3(int x, int y, int z) implements Comparable<Point3> {
        static Point3 parse(String coordinates) {
            var parts = coordinates.split(",");
            return new Point3(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }

        Point3 roll() {
            return new Point3(x, z, -y);
        }

        Point3 turn() {
            return new Point3(-y, x, z);
        }

        public List<Point3> rotations() {
            var current = this;
            var rotations = new ArrayList<Point3>();
            for (var cycle = 0; cycle < 2; cycle++) {
                for (var step = 0; step < 3; step++) {
                    current = current.roll();
                    rotations.add(current);
                    for (var turns = 0; turns < 3; turns++) {
                        current = current.turn();
                        rotations.add(current);
                    }
                }
                current = current.roll().turn().roll();
            }
            return rotations;
        }

        public Point3 minus(Point3 other) {
            return new Point3(x - other.x, y - other.y, z - other.z);
        }

        public Point3 plus(Point3 other) {
            return new Point3(x + other.x, y + other.y, z + other.z);
        }

        public int manhattan(Point3 other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
        }

        @Override
        public int compareTo(Point3 other) {
            if (x != other.x) return x - other.x;
            if (y != other.y) return y - other.y;
            return z - other.z;
        }
    }
}
