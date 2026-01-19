import java.util.List;
/**
 * يحتوي على دالة تقييم (حدس) لتقييم حالة لعبة سينت.
 * هذه الدالة تستخدمها خوارزمية البحث لاتخاذ القرارات.
 */
public class HeuristicEvaluator {

    /**
     * يقيم حالة اللعبة من منظور لاعب معين.
     * @param state حالة اللعبة الحالية.
     * @param maximizingPlayer اللاعب الذي نريد تقييم الحالة لصالحه.
     * @return قيمة التقييم. كلما زادت القيمة، كانت الحالة أفضل للاعب.
     */public double evaluate(GameState state, Player maximizingPlayer) {
        Player minimizingPlayer = maximizingPlayer.getOpponent();

        if (state.isTerminal()) {
            Player winner = state.getWinner();
            if (winner == maximizingPlayer) return 1_000_000.0;
            if (winner == minimizingPlayer) return -1_000_000.0;
        }

        double score = 0.0;
        SenetBoard board = state.getBoard();
        GamePath path = board.getGamePath();

        int myPiecesOff = (maximizingPlayer == Player.WHITE) ? state.getWhitePiecesOffBoard() : state.getBlackPiecesOffBoard();
        int opponentPiecesOff = (minimizingPlayer == Player.WHITE) ? state.getWhitePiecesOffBoard() : state.getBlackPiecesOffBoard();
        score += (myPiecesOff - opponentPiecesOff) * 1000.0;

        double myProgress = calculatePlayerProgress(board, maximizingPlayer, path);
        double opponentProgress = calculatePlayerProgress(board, minimizingPlayer, path);
        score += (myProgress - opponentProgress) * 10.0;

        // يمكن إضافة عوامل إضافية هنا لاحقًا

        return score;
    }

    private double calculatePlayerProgress(SenetBoard board, Player player, GamePath path) {
        double totalProgress = 0.0;
        int pieceCount = 0;
        List<Integer> pathOrder = path.getPathOrder();

        for (int i = 0; i < pathOrder.size(); i++) {
            int houseNumber = pathOrder.get(i);
            if (board.getHouse(houseNumber).getOccupant() == player) {
                totalProgress += (i + 1);
                pieceCount++;
            }
        }
        if (pieceCount == 0) return 0.0;
        return totalProgress / pieceCount;
    }


}