// Player.java
/**
 * يمثل أحد اللاعبين في اللعبة (الأبيض أو الأسود).
 * استخدام Enum يجعل الكود أكثر أماناً وقابلية للقراءة.
 */
public enum Player {
    WHITE('W'),
    BLACK('B');

    private final char symbol;

    Player(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    /**
     * @return الخصم (اللاعب الآخر).
     */
    public Player getOpponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}