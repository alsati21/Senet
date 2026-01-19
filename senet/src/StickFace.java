// StickFace.java
/**
 * يمثل وجهي العصي الواحدة: فاتح (أبيض) أو داكن (أسود).
 */
public enum StickFace {
    LIGHT(0),  // الوجه الفاتح
    DARK(1);   // الوجه الداكن

    private final int value;

    StickFace(int value) {
        this.value = value;
    }

    public int getValue() {

        return value;
    }
}