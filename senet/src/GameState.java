// GameState.java
import java.util.*;

/**
 * يمثل حالة اللعبة الكاملة في لحظة زمنية معينة.
 * هذه الفئة هي أساس خوارزمية البحث، حيث سيتم إنشاء العديد من النسخ
 * منها لاستكشاف الحركات المستقبلية.
 */
public class GameState {
    private final SenetBoard board;
    private Player currentPlayer;
    private int whitePiecesOffBoard;
    private int blackPiecesOffBoard;

    /**
     * المُنشئ الرئيسي، يستخدم لإنشاء نسخ من الحالات.
     * @param board لوحة اللعبة الحالية.
     * @param currentPlayer اللاعب الذي له الدور الآن.
     * @param whitePiecesOffBoard عدد أحجار اللاعب الأبيض التي خرجت.
     * @param blackPiecesOffBoard عدد أحجار اللاعب الأسود التي خرجت.
     */
    public GameState(SenetBoard board, Player currentPlayer, int whitePiecesOffBoard, int blackPiecesOffBoard) {
        // نستخدم copy constructor في SenetBoard لضمان أن كل حالة لديها نسختها الخاصة
        // هذا سيمنع التلاعب باللوحة الأصلية عند استكشاف الحركات
        this.board = new SenetBoard(board); // سننشئ هذا المُنشئ لاحقاً
        this.currentPlayer = currentPlayer;
        this.whitePiecesOffBoard = whitePiecesOffBoard;
        this.blackPiecesOffBoard = blackPiecesOffBoard;
    }

    /**
     * مُنشئ مساعد لإنشاء حالة البداية.
     */
    public GameState() {
        this(new SenetBoard(), Player.WHITE, 0, 0);
        this.getBoard().setupInitialPieces();
    }

    // Getters
    public SenetBoard getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public int getWhitePiecesOffBoard() { return whitePiecesOffBoard; }
    public int getBlackPiecesOffBoard() { return blackPiecesOffBoard; }

    public void incrementWhitePiecesOff() {
        this.whitePiecesOffBoard++;
    }

    public void incrementBlackPiecesOff() {
        this.blackPiecesOffBoard++;
    }
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    /**
     * ينشئ نسخة طبق الأصل من حالة اللعبة الحالية.
     * هذه الدالة حيوية جداً لخوارزميات البحث.
     * @return نسخة جديدة من GameState.
     */
    public GameState clone() {
        return new GameState(this.board, this.currentPlayer, this.whitePiecesOffBoard, this.blackPiecesOffBoard);
    }

    /**
     * يتحقق مما إذا كانت الحالة الحالية هي حالة نهاية اللعبة.
     * @return true إذا فاز أحد اللاعبين، false خلاف ذلك.
     */
    public boolean isTerminal() {
        return whitePiecesOffBoard == 7 || blackPiecesOffBoard == 7;
    }

    /**
     * يعيد الفائز في حالة نهاية اللعبة.
     * @return اللاعب الفائز، أو null إذا لم تنتهِ اللعبة بعد.
     */
    public Player getWinner() {
        if (whitePiecesOffBoard == 7) return Player.WHITE;
        if (blackPiecesOffBoard == 7) return Player.BLACK;
        return null;
    }


    private int lastMoveFrom = -1;
    private int lastMoveTo = -1;

    public void setLastMove(int from, int to) {
        this.lastMoveFrom = from;
        this.lastMoveTo = to;
    }

    public int getLastMoveFrom() { return lastMoveFrom; }
    public int getLastMoveTo() { return lastMoveTo; }


    // داخل GameState class

    // تتبع رقم الدور العام (يزيد في endTurn)
    private int turnNumber = 0;

    // خريطة لتسجيل متى هبط حجر على بيت حورس لكل لاعب أو لكل بيت (نستخدم مفتاح houseNumber)
    private Map<Integer, Integer> horusLandedTurn = new HashMap<>();

    public int getTurnNumber() {
        return turnNumber;
    }

    public void incrementTurnNumber() {
        this.turnNumber++;
    }

    public void setHorusLanded(int houseNumber, int turn) {
        horusLandedTurn.put(houseNumber, turn);
    }

    public Integer getHorusLandedTurn(int houseNumber) {
        return horusLandedTurn.get(houseNumber);
    }

    public void clearHorusLanded(int houseNumber) {
        horusLandedTurn.remove(houseNumber);
    }


}