import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day16 {
    private static final int SUM = 0;
    private static final int PRODUCT = 1;
    private static final int MIN = 2;
    private static final int MAX = 3;
    private static final int LITERAL = 4;
    private static final int GREATER = 5;
    private static final int LESS = 6;
    private static final int EQUAL = 7;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input16.txt")).trim();

        var result = new FormatParser(input).parse();
        part01(result);
        part02(result);
    }

    private static void part01(Packet result) {
        var queue = new ArrayDeque<Packet>();
        queue.add(result);
        var sum = 0L;
        while (!queue.isEmpty()) {
            var packet = queue.poll();
            sum += packet.header().version();
            if (packet instanceof Packet.Operator operator) {
                queue.addAll(operator.packets);
            }
        }
        System.out.println(sum);
    }

    private static void part02(Packet result) {
        var value = result.value();
        System.out.println(value);
    }

    sealed interface Packet {
        Header header();

        long value();

        record Literal(Header header, long value) implements Packet {
        }

        record Operator(Header header, List<Packet> packets) implements Packet {
            @Override
            public long value() {
                var id = header.typeId;
                return switch (id) {
                    case SUM -> packets.stream().mapToLong(Packet::value).sum();
                    case PRODUCT -> packets.stream().mapToLong(Packet::value).reduce(1, (acc, val) -> acc * val);
                    case MIN -> packets.stream().mapToLong(Packet::value).min().orElseThrow();
                    case MAX -> packets.stream().mapToLong(Packet::value).max().orElseThrow();
                    case GREATER -> evaluateGreaterThan(packets);
                    case LESS -> evaluateLessThan(packets);
                    case EQUAL -> evaluateEquals(packets);
                    default -> throw new IllegalArgumentException("Invalid id: " + id);
                };
            }

            private long evaluateGreaterThan(List<Packet> packets) {
                var first = packets.get(0).value();
                var second = packets.get(1).value();

                if (first > second) {
                    return 1;
                } else {
                    return 0;
                }
            }

            private long evaluateLessThan(List<Packet> packets) {
                var first = packets.get(0).value();
                var second = packets.get(1).value();

                if (first < second) {
                    return 1;
                } else {
                    return 0;
                }
            }

            private long evaluateEquals(List<Packet> packets) {
                var first = packets.get(0).value();
                var second = packets.get(1).value();

                if (first == second) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    record Header(int version, int typeId) {
    }

    static class HexParser {
        int index;
        private final boolean[] bits;

        HexParser(String input) {
            var number = new BigInteger(input, 16);
            bits = new boolean[input.length() * 4];
            for (int i = 0; i < number.bitLength(); i++) {
                bits[bits.length - 1 - i] = number.testBit(i);
            }
        }

        public int getBits(int count) {
            var result = 0;
            for (int i = 0; i < count; i++) {
                result <<= 1;
                result |= bits[index] ? 1 : 0;
                index++;
            }
            return result;
        }
    }

    static class FormatParser {
        private final HexParser parser;
        private List<Packet> currentPackets = new ArrayList<>();
        private final Stack<State> state = new Stack<>();
        private Header header;
        private int requiredBits = 0;
        private int requiredPackets = 0;

        FormatParser(String input) {
            this.parser = new HexParser(input);
        }

        Packet parse() {
            while (true) {
                if (
                    requiredPackets != 0 && currentPackets.size() == requiredPackets ||
                        requiredBits != 0 && requiredBits == parser.index - state.peek().previousIndex
                ) {
                    var packets = currentPackets;
                    popState();
                    addPacket(new Packet.Operator(header, packets));
                    if (state.isEmpty()) {
                        break;
                    }
                    continue;
                }

                header = parseHeader();
                if (header.typeId == LITERAL) {
                    var literal = parseLiteral();
                    addPacket(literal);
                    continue;
                }
                var lengthType = parser.getBits(1);
                if (lengthType == 0) {
                    var bits = parser.getBits(15);
                    pushState();
                    requiredBits = bits;
                } else {
                    var subPackets = parser.getBits(11);
                    pushState();
                    requiredPackets = subPackets;
                }
            }
            return currentPackets.get(0);
        }

        private void addPacket(Packet literal) {
            currentPackets.add(literal);
        }

        private Header parseHeader() {
            var version = parser.getBits(3);
            var typeId = parser.getBits(3);
            return new Header(version, typeId);
        }

        private Packet.Literal parseLiteral() {
            long hasMoreBit;
            long value = 0;
            do {
                hasMoreBit = parser.getBits(1);
                value <<= 4;
                value |= parser.getBits(4);
            } while (hasMoreBit == 1);
            return new Packet.Literal(header, value);
        }

        private void pushState() {
            state.push(new State(header, requiredBits, requiredPackets, parser.index, currentPackets));
            requiredBits = 0;
            requiredPackets = 0;
            currentPackets = new ArrayList<>();
        }

        private void popState() {
            var previousState = state.pop();
            header = previousState.header;
            currentPackets = previousState.currentPackets;
            requiredPackets = previousState.requiredPackets;
            requiredBits = previousState.requiredBits;
        }

        record State(
            Header header,
            int requiredBits,
            int requiredPackets,
            int previousIndex,
            List<Packet> currentPackets
        ) {
        }
    }
}
