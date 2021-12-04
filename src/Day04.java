import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Day04 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input04.txt"));

        var scanner = new Scanner(input);

        var calls = Arrays.stream(scanner.nextLine().split(","))
            .mapToInt(Integer::parseInt)
            .toArray();

        var cards = new ArrayList<Card>();
        while (scanner.hasNextLine()) {
            scanner.nextLine(); // separator

            var lines = new String[5];
            for (int i = 0; i < lines.length; i++) {
                lines[i] = scanner.nextLine();
            }
            cards.add(Card.parse(lines));
        }

        var totalCards = cards.size();
        var winning = new ArrayList<Integer>();
        for (var call : calls) {
            if (winning.size() == totalCards) break;

            var iterator = cards.iterator();
            while (iterator.hasNext()) {
                var card = iterator.next();
                card.apply(call);
                if (card.isDone()) {
                    winning.add(card.sum() * call);
                    iterator.remove();
                }
            }
        }
        // Part 01
        System.out.println(winning.get(0));
        // Part 02
        System.out.println(winning.get(winning.size() - 1));
    }

    record Card(int[][] numbers) {
        void apply(int call) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (numbers[i][j] == call) {
                        numbers[i][j] = -1;
                    }
                }
            }
        }

        boolean isDone() {
            for (int i = 0; i < 5; i++) {
                if (numbers[i][0] == -1 && numbers[i][1] == -1 && numbers[i][2] == -1 && numbers[i][3] == -1 && numbers[i][4] == -1) {
                    return true;
                }
                if (numbers[0][i] == -1 && numbers[1][i] == -1 && numbers[2][i] == -1 && numbers[3][i] == -1 && numbers[4][i] == -1) {
                    return true;
                }
            }
            return false;
        }

        int sum() {
            return Arrays.stream(numbers)
                .flatMapToInt(Arrays::stream)
                .filter(i -> i != -1)
                .sum();
        }

        static Card parse(String[] lines) {
            var numbers = new int[5][5];
            for (int i = 0; i < lines.length; i++) {
                numbers[i] = Arrays.stream(lines[i].strip().split("\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            }
            return new Card(numbers);
        }
    }
}
