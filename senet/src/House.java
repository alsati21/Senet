// House.java

public class House {
    private final int number;
    private final HouseType type;
    private Player occupant;

    public House(int number, HouseType type) {
        this.number = number;
        this.type = type;
        this.occupant = null;
    }

    // Getters
    public int getNumber() { return number; }
    public HouseType getType() { return type; }
    public Player getOccupant() { return occupant; }

    // Setter
    public void setOccupant(Player occupant) {
        this.occupant = occupant;
    }

    public boolean isOccupied() {
        return this.occupant != null;
    }

    /**
     * تمثيل نصي للمربع لطباعة الرقعة.
     * - إذا كان المربع مشغولاً، يعرض رمز اللاعب (W أو B).
     * - إذا كان المربع فارغاً، يعرض 'N'.
     */
    @Override
    public String toString() {
        // 1. إذا كان المربع مشغولاً، رمز اللاعب له الأولوية القصوى
        if (isOccupied()) {
            return String.valueOf(occupant.getSymbol());
        }

        // 2. إذا كان المربع فارغًا، تحقق مما إذا كان خاصاً واعرض رمزه
        if (type != HouseType.NORMAL) {
            return String.valueOf(type.getSymbol());
        }

        // 3. إذا كان المربع فارغًا وعاديًا، اعرض 'N'
        return "N";
    }
}