import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static GameState currentState;
    private static GameLogic gameLogic;
    private static ExpectiminimaxAI ai;
    private static Player humanPlayer;
    private static Player aiPlayer;

    public static void main(String[] args) {
        printWelcomeMessage();
        setupGame();
        runGameLoop();
        announceWinner();
        scanner.close();
    }

    private static void printWelcomeMessage() {
        System.out.println("=================================================");
        System.out.println("                Welcome to Senet!");
        System.out.println("=================================================");
        System.out.println("Senet is an ancient Egyptian race game.");
        System.out.println("The goal is to be the first to move all your pieces off the board.");
        System.out.println("Special houses have unique rules. Good luck!");
        System.out.println("=================================================\n");
    }

    private static void setupGame() {
        currentState = new GameState();
        gameLogic = new GameLogic();

        humanPlayer = promptForPlayerColor();
        aiPlayer = humanPlayer.getOpponent();

        int aiDepth = promptForAIDifficulty();
        ai = new ExpectiminimaxAI(aiPlayer, aiDepth);

        System.out.println("\n--- Game Setup Complete ---");
        System.out.println("You are playing as: " + humanPlayer.getSymbol());
        System.out.println("AI is playing as: " + aiPlayer.getSymbol());
        System.out.println("AI search depth: " + aiDepth);
        System.out.println("\nInitial Board State:");
        currentState.getBoard().printBoard();
        System.out.println();
    }

    private static Player promptForPlayerColor() {
        Player chosenPlayer = null;
        while (chosenPlayer == null) {
            System.out.println("Choose your color:");
            System.out.println("  1. White (W)");
            System.out.println("  2. Black (B)");
            System.out.print("Enter your choice (1 or 2): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) chosenPlayer = Player.WHITE;
                else if (choice == 2) chosenPlayer = Player.BLACK;
                else System.out.println("Invalid choice. Please enter 1 or 2.\n");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).\n");
                scanner.nextLine();
            }
        }
        return chosenPlayer;
    }

    private static int promptForAIDifficulty() {
        int depth = 4;
        System.out.println("\nSelect AI difficulty (search depth):");
        System.out.println("  1. Easy (Depth 2)");
        System.out.println("  2. Medium (Depth 4) - Recommended");
        System.out.println("  3. Hard (Depth 6)");
        System.out.print("Enter your choice (1-3): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: depth = 2; break;
                case 2: depth = 4; break;
                case 3: depth = 6; break;
                default:
                    System.out.println("Invalid choice. Defaulting to Medium (Depth 4).");
                    depth = 4;
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to Medium (Depth 4).");
            scanner.nextLine();
        }
        return depth;
    }

    private static void runGameLoop() {
        int turnCounter = 1;
        final int MAX_TURNS = 1000;

        while (!currentState.isTerminal() && turnCounter <= MAX_TURNS) {
            System.out.println("\n--- Turn #" + turnCounter + " ---");
            printCurrentTurnInfo();

            if (currentState.getCurrentPlayer() == humanPlayer) {
                executeHumanTurn();
            } else {
                executeAITurn();
            }

            System.out.println("=".repeat(60));
            turnCounter++;
        }

        if (turnCounter > MAX_TURNS) {
            System.out.println("GAME STOPPED: Maximum turns reached. There might be an infinite loop.");
        }
    }

    private static void printCurrentTurnInfo() {
        System.out.println("Current Turn: " + currentState.getCurrentPlayer().getSymbol());
    }

    /**
     * تنفيذ دور اللاعب البشري:
     * - رمي العصي
     * - توليد الحركات الممكنة
     * - عرض الحركات للسماح للاعب بالاختيار
     */
    private static void executeHumanTurn() {
        StickThrow stickThrow = StickThrow.getRandomThrow();
        System.out.println("You threw: " + stickThrow + " (Move Value: " + stickThrow.getTotalValue() + ")");

        List<GameState> possibleMoves = gameLogic.getPossibleMoves(currentState, stickThrow);

        if (possibleMoves.isEmpty()) {
            System.out.println("You have no valid moves for this throw. Passing turn.");
            currentState.setCurrentPlayer(currentState.getCurrentPlayer().getOpponent());
            return;
        }

        System.out.println("You have " + possibleMoves.size() + " possible moves.");
        for (int i = 0; i < possibleMoves.size(); i++) {
            System.out.println("\nMove #" + (i + 1) + ":");
            possibleMoves.get(i).getBoard().printBoard();
        }

        int choice = -1;
        while (true) {
            System.out.print("Choose move number (1-" + possibleMoves.size() + "): ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= possibleMoves.size()) break;
            } catch (InputMismatchException e) {
                scanner.nextLine();
            }
            System.out.println("Invalid choice. Try again.");
        }

        currentState = possibleMoves.get(choice - 1);
        System.out.println("You made a move.");
        currentState.getBoard().printBoard();
    }

    /**
     * تنفيذ دور الـ AI باستخدام Expectiminimax.
     */
    private static void executeAITurn() {
        System.out.println("AI is thinking...");
        GameState bestMove = ai.findBestMove(currentState);
        if (bestMove != null) {
            currentState = bestMove;
            System.out.println("AI made its move.");
        }
        else {
            System.out.println("AI has no valid moves. Passing turn.");
            currentState.setCurrentPlayer(currentState.getCurrentPlayer().getOpponent());
        }
        // 🔥 الحل هنا
        currentState.getBoard().printBoard();

    }

    private static void announceWinner() {
        System.out.println("=================================================");
        System.out.println("                   G A M E  O V E R");
        System.out.println("=================================================");
        Player winner = currentState.getWinner();

        if (winner == humanPlayer) {
            System.out.println("                 Congratulations! You Win!");
        } else if (winner == aiPlayer) {
            System.out.println("                   AI Wins!");
            System.out.println("             Better luck next time!");
        } else {
            System.out.println("                   It's a Draw!");
        }
        System.out.println("=================================================");
    }
}
