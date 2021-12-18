import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Day18 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input18.txt"));

        part01(lines);
        part02(lines);
    }

    private static void part01(List<String> lines) {
        var numbers = lines.stream()
            .map(Number::parse)
            .toList();

        var sum = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            sum = sum.add(numbers.get(i));
            sum = sum.reduce();
        }
        System.out.println(sum.magnitude());
    }

    private static void part02(List<String> lines) {
        var numbers = lines.stream()
            .map(Number::parse)
            .toList();
        var maxMagnitude = Integer.MIN_VALUE;
        for (int i = 0; i < numbers.size() - 1; i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                var first = numbers.get(i).copy();
                var second = numbers.get(j).copy();
                var magnitude = first.add(second).reduce().magnitude();
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
                first = numbers.get(i).copy();
                second = numbers.get(j).copy();
                magnitude = second.add(first).reduce().magnitude();
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
            }
        }
        System.out.println(maxMagnitude);
    }

    abstract static sealed class Number {
        Pair parent;

        final static class Pair extends Number {
            Number left;
            Number right;

            public Pair(Number left, Number right) {
                this.left = left;
                this.right = right;
                left.parent = this;
                right.parent = this;
            }

            public Number getLeft() {
                return left;
            }

            public Number getRight() {
                return right;
            }

            public Optional<Pair> findSide(Function<Pair, Number> selector) {
                var current = this;
                while (current.parent != null) {
                    if (selector.apply(current.parent) == current) {
                        current = current.parent;
                    } else {
                        return Optional.of(current.parent);
                    }
                }
                return Optional.empty();
            }

            @Override
            public String toString() {
                return "[" + left + "," + right + "]";
            }

            @Override
            int magnitude() {
                return 3 * left.magnitude() + 2 * right.magnitude();
            }

            @Override
            Number copy() {
                return new Pair(left.copy(), right.copy());
            }

            static Pair read(PushbackReader reader) throws IOException {
                reader.read(); // [
                var left = Number.read(reader);
                reader.read(); // ,
                var right = Number.read(reader);
                reader.read(); // ]
                return new Pair(left, right);
            }
        }

        final static class Regular extends Number {
            int value;

            public Regular(int value) {
                this.value = value;
            }

            static Regular read(PushbackReader reader) throws IOException {
                var builder = new StringBuilder();
                while (true) {
                    var character = reader.read();
                    if (character == ',' || character == ']') {
                        reader.unread(character);
                        break;
                    }
                    builder.append((char) character);
                }
                return new Regular(Integer.parseInt(builder.toString()));
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }

            @Override
            int magnitude() {
                return value;
            }

            @Override
            Number copy() {
                return new Regular(value);
            }
        }

        abstract int magnitude();

        abstract Number copy();

        static Number parse(String line) {
            try {
                var reader = new PushbackReader(new StringReader(line));
                return Pair.read(reader);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        static Number read(PushbackReader reader) throws IOException {
            var character = reader.read();
            reader.unread(character);
            if (character == '[') {
                return Pair.read(reader);
            } else {
                return Regular.read(reader);
            }
        }

        Number add(Number right) {
            return new Pair(this, right);
        }

        Number reduce() {
            while (true) {
                var exploding = explodingNumber(0);
                if (exploding.isPresent()) {
                    var toExplode = exploding.get();
                    toExplode.findSide(Pair::getLeft)
                        .flatMap(parent -> findRightmost(parent.left))
                        .ifPresent(value -> value.value += ((Regular) toExplode.left).value);
                    toExplode.findSide(Pair::getRight)
                        .flatMap(parent -> findLeftmost(parent.right))
                        .ifPresent(value -> value.value += ((Regular) toExplode.right).value);
                    var replacement = new Number.Regular(0);
                    replacement.parent = toExplode.parent;
                    if (toExplode.parent.left == toExplode) {
                        toExplode.parent.left = replacement;
                    } else if (toExplode.parent.right == toExplode) {
                        toExplode.parent.right = replacement;
                    }
                    continue;
                }
                var splitting = findLeftmostSplit(this);
                if (splitting.isPresent()) {
                    var toSplit = splitting.get();
                    var left = Math.floor(toSplit.value / 2.0);
                    var right = Math.ceil(toSplit.value / 2.0);
                    var newPair = new Pair(new Regular((int) left), new Regular((int) right));
                    newPair.parent = toSplit.parent;

                    if (toSplit.parent.left == toSplit) {
                        toSplit.parent.left = newPair;
                    } else {
                        toSplit.parent.right = newPair;
                    }
                    continue;
                }
                break;
            }
            return this;
        }

        Optional<Pair> explodingNumber(int depth) {
            if (this instanceof Regular) {
                return Optional.empty();
            } else if (this instanceof Pair pair) {
                if (depth == 4) return Optional.of(pair);

                var leftExploding = pair.left.explodingNumber(depth + 1);
                if (leftExploding.isPresent()) {
                    return leftExploding;
                }
                return pair.right.explodingNumber(depth + 1);
            }
            throw new IllegalArgumentException("This is not a valid subclass of number");
        }
    }

    private static Optional<Number.Regular> findLeftmostSplit(Number number) {
        if (number instanceof Number.Regular regular && regular.value >= 10) {
            return Optional.of(regular);
        } else if (number instanceof Number.Pair pair) {
            var leftSplit = findLeftmostSplit(pair.left);
            if (leftSplit.isPresent()) return leftSplit;

            return findLeftmostSplit(pair.right);
        }
        return Optional.empty();
    }

    private static Optional<Number.Regular> findRightmost(Number number) {
        if (number instanceof Number.Regular regular) {
            return Optional.of(regular);
        } else if (number instanceof Number.Pair pair) {
            return findRightmost(pair.right);
        }
        throw new IllegalArgumentException("This is not a valid subclass of number");
    }

    private static Optional<Number.Regular> findLeftmost(Number number) {
        if (number instanceof Number.Regular regular) {
            return Optional.of(regular);
        } else if (number instanceof Number.Pair pair) {
            return findLeftmost(pair.left);
        }
        throw new IllegalArgumentException("This is not a valid subclass of number");
    }
}
