package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;
import br.com.dio.util.BoardTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        final var positions = Stream.of(args).collect(Collectors.toMap(
                k -> k.split(";")[0],
                v -> v.split(";")[1]
        ));
        var option = -1;
        while (true) {
            System.out.println("Select between the options:");
            System.out.println("1 - Start new game");
            System.out.println("2 - Place a number");
            System.out.println("3 - Remove a number");
            System.out.println("4 - Visualize current game");
            System.out.println("5 - Verify game status");
            System.out.println("6 - Clear game");
            System.out.println("7 - Finish the Sudoku");
            System.out.println("8 - Exit the game");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> StartGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Invalid Option");
            }
        }


    }

    private static void StartGame(Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("The game already started.");
            return;
        }
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }
        board = new Board(spaces);
        System.out.println("The game is ready to start");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("The didn't start yet.");
            return;
        }
        System.out.println("Type the column number to be insert");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Type the line number to be insert");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Provide the number that will be positioned[%s, %s]\n", col, row);
        var value = runUntilGetValidNumber(1, 9);
        if (!board.changeValue(col, row, value)) {
            System.out.printf("Position [%s,%s] has a fixed value.\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("The game didn't start yet.");
            return;
        }
        System.out.println("Type the column number to be insert");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Type the line number to be insert");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Provide the number that will be positioned[%s, %s]\n", col, row);
        if (!board.clearValue(col, row)) {
            System.out.printf("Position [%s,%s] has a fixed value.\n", col, row);
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("The game wasn't started yet");
            return;
        }
        System.out.println("Are you sure you want to clear your game? (all progress will be lost)");
        var confirm = scanner.next();
        while(!confirm.equalsIgnoreCase("Yes") || (!confirm.equalsIgnoreCase("No"))) {
            System.out.println("Answers expected [[YES]] or [[NO]]");
        }
        if(confirm.equalsIgnoreCase("yes")) {
            board.reset();
        }
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("The game wasn't started yet");
            return;
        }
        System.out.printf("The game status is: %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("The game has errors");
        } else {
            System.out.println("The game does not has errors");
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("The game wasn't started yet");
            return;
        }
        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col : board.getSpaces()) {
                args[argPos++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Your current game is:");
        System.out.printf((BoardTemplate.BOARD_TEMPLATE) + "\n", args);
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("The game wasn't started yet");
            return;
        }
        if(board.gameIsFinished()) {
            System.out.println("Congrats, You concluded the game!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Your game contains error(s), check your board and try again!");
        } else {
            System.out.println("You still need to fill some slot.\n" +
                    "All slots in board needs to be filled before finishing.");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Type a number between %s, %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }

}
