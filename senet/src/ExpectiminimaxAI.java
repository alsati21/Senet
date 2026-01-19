import java.util.*;

/**
 * Represents an AI player using the Expectiminimax algorithm.
 */
public class ExpectiminimaxAI {
    private final Player aiPlayer;
    private final Player opponent;
    private final HeuristicEvaluator evaluator;
    private final GameLogic gameLogic;
    private int maxDepth;

    public ExpectiminimaxAI(Player aiPlayer, int maxDepth) {
        this.aiPlayer = aiPlayer;
        this.opponent = aiPlayer.getOpponent();
        this.evaluator = new HeuristicEvaluator();
        this.gameLogic = new GameLogic();
        this.maxDepth = maxDepth;
    }

    /**
     * NEW: AI now uses a RANDOM stick throw like a human.
     */
    public GameState findBestMove(GameState currentState) {

        // AI throws sticks randomly
        StickThrow randomThrow = StickThrow.getRandomThrow();
        System.out.println("AI threw: " + randomThrow + " (Move Value: " + randomThrow.getTotalValue() + ")");

        // Use expectiminimax but with ONE fixed throw
        MoveResult bestResult = expectiminimaxWithFixedThrow(
                currentState,
                randomThrow,
                this.maxDepth,
                NodeType.MAX,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY
        );

        return bestResult.state;
    }

    private enum NodeType { MAX, MIN }

    /**
     * NEW: Expectiminimax that uses ONE fixed stick throw instead of all throws.
     */
    private MoveResult expectiminimaxWithFixedThrow(
            GameState state,
            StickThrow fixedThrow,
            int depth,
            NodeType nodeType,
            double alpha,
            double beta
    ) {
        if (depth == 0 || state.isTerminal()) {
            double score = evaluator.evaluate(state, this.aiPlayer);
            return new MoveResult(state, score);
        }

        // MAX node (AI turn)
        if (nodeType == NodeType.MAX) {
            MoveResult maxResult = new MoveResult(null, Double.NEGATIVE_INFINITY);

            List<GameState> possibleStates = this.gameLogic.getPossibleMoves(state, fixedThrow);

            // NEW: Print number of possible moves
            System.out.println("AI found " + possibleStates.size() + " possible moves.");

            if (possibleStates.isEmpty()) {
                GameState passed = state.clone();
                passed.setCurrentPlayer(state.getCurrentPlayer().getOpponent());
                return expectiminimaxWithFixedThrow(passed, fixedThrow, depth - 1, NodeType.MIN, alpha, beta);
            }

            for (GameState nextState : possibleStates) {
                MoveResult result = expectiminimaxWithFixedThrow(nextState, fixedThrow, depth - 1, NodeType.MIN, alpha, beta);

                // NEW: Print evaluation of each move
                System.out.println(
                        "AI evaluating move: from " + nextState.getLastMoveFrom() +
                                " to " + nextState.getLastMoveTo() +
                                " | Score = " + result.value
                );

                if (result.value > maxResult.value) {
                    maxResult = new MoveResult(nextState, result.value);
                }

                if (maxResult.value >= beta) return maxResult;
                alpha = Math.max(alpha, maxResult.value);
            }

            // NEW: Print selected move
            System.out.println(
                    "AI selected move: from " + maxResult.state.getLastMoveFrom() +
                            " to " + maxResult.state.getLastMoveTo() +
                            " | Best Score = " + maxResult.value
            );

            return maxResult;
        }

        // MIN node (opponent turn)
        MoveResult minResult = new MoveResult(null, Double.POSITIVE_INFINITY);

        List<GameState> possibleStates = this.gameLogic.getPossibleMoves(state, fixedThrow);

        if (possibleStates.isEmpty()) {
            GameState passed = state.clone();
            passed.setCurrentPlayer(state.getCurrentPlayer().getOpponent());
            return expectiminimaxWithFixedThrow(passed, fixedThrow, depth - 1, NodeType.MAX, alpha, beta);
        }

        for (GameState nextState : possibleStates) {
            MoveResult result = expectiminimaxWithFixedThrow(nextState, fixedThrow, depth - 1, NodeType.MAX, alpha, beta);

            if (result.value < minResult.value) {
                minResult = new MoveResult(nextState, result.value);
            }

            if (minResult.value <= alpha) return minResult;
            beta = Math.min(beta, minResult.value);
        }

        return minResult;
    }

    private static class MoveResult {
        final GameState state;
        final double value;

        MoveResult(GameState state, double value) {
            this.state = state;
            this.value = value;
        }
    }
}
