import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Day22 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input22.txt"));

        var steps = input.stream()
            .map(Instruction::parse)
            .toList();

        part1(steps);
        part2(steps);
    }

    private static void part1(List<Instruction> steps) {
        solve(steps.stream().filter(step -> step.cuboid.minX >= -50 && step.cuboid.maxX < 50 &&
            step.cuboid.minY >= -50 && step.cuboid.maxY <= 50 &&
            step.cuboid.minZ >= -50 && step.cuboid.maxZ <= 50).toList());
    }

    private static void part2(List<Instruction> steps) {
        solve(steps);
    }

    private static void solve(List<Instruction> steps) {
        var cuboids = new HashMap<Cuboid, Long>();
        for (var step : steps) {
            var operation = step.on ? 1L : -1;
            var newCuboid = step.cuboid;

            var intersections = new HashMap<Cuboid, Long>();
            for (var entry : cuboids.entrySet()) {
                var cuboid = entry.getKey();
                var currentOperation = entry.getValue();

                newCuboid.intersect(cuboid).ifPresent(intersection -> intersections.merge(intersection, -currentOperation, Long::sum));
            }

            if (step.on) {
                intersections.merge(newCuboid, operation, Long::sum);
            }
            intersections.forEach((key, value) -> cuboids.merge(key, value, Long::sum));
        }
        var sum = cuboids.entrySet().parallelStream()
            .mapToLong(entry -> entry.getKey().area() * entry.getValue())
            .sum();
        System.out.println(sum);
    }

    record Instruction(boolean on, Cuboid cuboid) {
        static Instruction parse(String line) {
            var parts = line.split(" ");
            var on = "on".equals(parts[0]);

            var coordinates = parts[1].split(",");
            var xRange = coordinates[0].replaceAll("x=(-?\\d+)\\.\\.(-?\\d+)", "$1 $2").split(" ");
            var yRange = coordinates[1].replaceAll("y=(-?\\d+)\\.\\.(-?\\d+)", "$1 $2").split(" ");
            var zRange = coordinates[2].replaceAll("z=(-?\\d+)\\.\\.(-?\\d+)", "$1 $2").split(" ");

            var cuboid = new Cuboid(
                Integer.parseInt(xRange[0]), Integer.parseInt(xRange[1]),
                Integer.parseInt(yRange[0]), Integer.parseInt(yRange[1]),
                Integer.parseInt(zRange[0]), Integer.parseInt(zRange[1])
            );
            return new Instruction(on, cuboid);
        }
    }

    record Cuboid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        long area() {
            return (long) (maxX - minX + 1)
                * (maxY - minY + 1)
                * (maxZ - minZ + 1);
        }

        public Optional<Cuboid> intersect(Cuboid other) {
            var minX = Math.max(this.minX, other.minX);
            var maxX = Math.min(this.maxX, other.maxX);
            var minY = Math.max(this.minY, other.minY);
            var maxY = Math.min(this.maxY, other.maxY);
            var minZ = Math.max(this.minZ, other.minZ);
            var maxZ = Math.min(this.maxZ, other.maxZ);

            if (minX > maxX || minY > maxY || minZ > maxZ) {
                return Optional.empty();
            }
            return Optional.of(new Cuboid(minX, maxX, minY, maxY, minZ, maxZ));
        }
    }
}
