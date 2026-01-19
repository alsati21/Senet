// SenetBoard.java
import java.util.*;

/**
 * يمثل رقعة لعبة سينت، تحتوي على جميع المربعات ومنطق طباعتها.
 */
public class SenetBoard {
    private final Map<Integer, House> houses;
    private final GamePath gamePath;

    public SenetBoard() {
        this.houses = new LinkedHashMap<>();
        this.gamePath = new GamePath();
        initializeBoard();
    }
    public SenetBoard(SenetBoard otherBoard) {
        this.houses = new LinkedHashMap<>();
        this.gamePath = new GamePath(); // GamePath هو immutable، يمكن مشاركته

        // نسخ حالة كل بيت (house) من اللوحة الأصلية
        for (Map.Entry<Integer, House> entry : otherBoard.houses.entrySet()) {
            House originalHouse = entry.getValue();
            House newHouse = new House(originalHouse.getNumber(), originalHouse.getType());
            newHouse.setOccupant(originalHouse.getOccupant());
            this.houses.put(entry.getKey(), newHouse);
        }
    }
    private void initializeBoard() {
        for (int i = 1; i <= 30; i++) {
            HouseType type = HouseType.NORMAL;
            switch (i) {
                case 16: type = HouseType.REBIRTH; break;
                case 26: type = HouseType.HAPPINESS; break;
                case 27: type = HouseType.WATER; break;
                case 28: type = HouseType.THREE_TRUTHS; break;
                case 29: type = HouseType.RE_ATOUM; break;
                case 30: type = HouseType.HORUS; break;
            }
            houses.put(i, new House(i, type));
        }
    }

    public void setupInitialPieces() {
        Player currentPlayer = Player.WHITE;
        // استخدم الدالة الجديدة للحصول على المسار
        List<Integer> currentPath = gamePath.getPathOrder();

        for (int i = 0; i < 14; i++) {
            int houseNumber = currentPath.get(i);
            houses.get(houseNumber).setOccupant(currentPlayer);
            currentPlayer = currentPlayer.getOpponent();
        }
    }

    public House getHouse(int number) {
        return houses.get(number);
    }

    public GamePath getGamePath() {
        return gamePath;
    }

    /**
     * يطبع الرقعة بالشكل "الثعباني" الصحيح للعبة سينت.
     */// داخل SenetBoard.java
    public void printBoard() {
        System.out.println("=========================================");
        System.out.println("            S E N E T  B O A R D");
        System.out.println("=========================================");

        StringBuilder rowBuilder = new StringBuilder();

        // --- الصف الأول: من المربع 1 إلى المربع 10 (من اليسار لليمين) ---
        rowBuilder.append("   ");
        for (int i = 1; i <= 10; i++) {
            rowBuilder.append(String.format(" %s ", houses.get(i).toString()));
        }
        System.out.println(rowBuilder.toString());

        System.out.println("   " + "-----------------------------------------");

        // --- الصف الثاني: من المربع 11 إلى المربع 20 (من اليسار لليمين) ---
        rowBuilder.setLength(0); // تفريغ الـ StringBuilder
        rowBuilder.append("   ");
        for (int i = 11; i <= 20; i++) {
            rowBuilder.append(String.format(" %s ", houses.get(i).toString()));
        }
        System.out.println(rowBuilder.toString());

        System.out.println("   " + "-----------------------------------------");

        // --- الصف الثالث: من المربع 21 إلى المربع 30 (من اليسار لليمين) ---
        rowBuilder.setLength(0); // تفريغ الـ StringBuilder
        rowBuilder.append("   ");
        for (int i = 21; i <= 30; i++) {
            rowBuilder.append(String.format(" %s ", houses.get(i).toString()));
        }
        System.out.println(rowBuilder.toString());
        System.out.println("=========================================");
    }


}