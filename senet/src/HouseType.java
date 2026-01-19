// HouseType.java
/**
 * يمثل أنواع المربعات الخاصة في لعبة سينت.
 * استخدام Enum يمنع الأخطاء الإملائية ويوضح النية.
 */
public enum HouseType {
    NORMAL('n'),      // سيظهر كـ 'n' عندما يكون فارغاً
    REBIRTH('A'),
    HAPPINESS('H'),
    WATER('M'),
    THREE_TRUTHS('3'),
    RE_ATOUM('2'),
    HORUS('F');

    private final char symbol;

    HouseType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}