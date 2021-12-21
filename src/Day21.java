import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class Day21 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input21.txt"));

        var player1 = parsePlayer(input.get(0));
        var player2 = parsePlayer(input.get(1));

        part1(player1, player2, new DeterministicDie());
        part2(player1, player2);
    }

    private static void part1(int start1, int start2, Die die) {
        var player1 = new Player(start1);
        var player2 = new Player(start2);
        var currentPlayer = true;
        while (player1.notDone() && player2.notDone()) {
            var rolls = IntStream.generate(die)
                .limit(3)
                .sum();
            if (currentPlayer) {
                player1.addRolls(rolls);
            } else {
                player2.addRolls(rolls);
            }
            currentPlayer = !currentPlayer;
        }

        int playerScore;
        if (player1.notDone()) {
            playerScore = player1.score;
        } else {
            playerScore = player2.score;
        }
        System.out.println(playerScore * die.rollCount());
    }

    private static int parsePlayer(String position) {
        return Integer.parseInt(position.replaceAll("Player \\d starting position: (\\d+)", "$1"));
    }

    static class Player {
        int position;
        int score;

        Player(int position) {
            this.position = position;
        }

        void addRolls(int sum) {
            position += sum;
            while (position > 10) {
                position -= 10;
            }
            score += position;
        }

        boolean notDone() {
            return score < 1000;
        }
    }

    static class DeterministicDie implements Die {
        int next = 1;
        int count = 0;

        @Override
        public int getAsInt() {
            var value = next;
            next++;
            if (next > 100) {
                next = 1;
            }
            count++;
            return value;
        }

        @Override
        public int rollCount() {
            return count;
        }
    }

    interface Die extends IntSupplier {
        int rollCount();
    }


    private static void part2(int player1, int player2) {
        var cache = new HashMap<State, Wins>();
        var result = quantumGame(player1, player2, 0, 0, true, cache);
        System.out.println(result);
    }

    private static Wins quantumGame(int player1, int player2, int score1, int score2, boolean firstPlayer, HashMap<State, Wins> cache) {
        var state = new State(player1, player2, score1, score2, firstPlayer);
        if (cache.containsKey(state)) {
            return cache.get(state);
        }

        var rolls = IntStream.rangeClosed(1, 3)
            .flatMap(value -> IntStream.rangeClosed(1, 3).map(i -> i + value))
            .flatMap(value -> IntStream.rangeClosed(1, 3).map(i -> i + value))
            .toArray();

        var p1Wins = 0L;
        var p2Wins = 0L;
        for (var roll : rolls) {
            var position1 = player1;
            var position2 = player2;
            var s1 = score1;
            var s2 = score2;
            if (firstPlayer) {
                position1 = (position1 + roll - 1) % 10 + 1;
                s1 += position1;
            } else {
                position2 = (position2 + roll - 1) % 10 + 1;
                s2 += position2;
            }

            if (firstPlayer && s1 >= 21) {
                p1Wins++;
            } else if (!firstPlayer && s2 >= 21) {
                p2Wins++;
            } else {
                var wins = quantumGame(position1, position2, s1, s2, !firstPlayer, cache);
                p1Wins += wins.player1;
                p2Wins += wins.player2;
            }
        }
        var wins = new Wins(p1Wins, p2Wins);
        cache.put(state, wins);
        return wins;
    }

    record State(int position1, int position2, int score1, int score2, boolean firstPlayer) {
    }

    record Wins(long player1, long player2) {
        @Override
        public String toString() {
            return (player1 > player2 ? player1 : player2) + " / " + (player1 + player2);
        }
    }
}
