import java.util.*;

/**
 * يمثل رمية العصي (أربع عصي) مع حساب القيمة والاحتمالية.
 * - القيمة: مجموع الوجوه المظلمة (DARK) حيث تمثل كل واحدة 1، وإذا كانت جميعها فاتحة (LIGHT) فالقيمة = 5.
 * - الاحتمالية: محسوبة بناءً على عدد التوليفات التي تعطي نفس القيمة من بين 16 توليفة ممكنة.
 */
public class StickThrow {
    private final StickFace[] sticks;
    private final int totalValue;

    // عدّاد عدد التوليفات لكل قيمة (1..5)
    private static final Map<Integer, Integer> COUNT_BY_VALUE;
    // قائمة تمثل كل قيمة ممكنة مرة واحدة (للتكرار في الخوارزميات)
    private static final List<StickThrow> ALL_UNIQUE_THROWS_CACHE;

    static {
        COUNT_BY_VALUE = new HashMap<>();
        Map<Integer, StickThrow> uniqueByValue = new LinkedHashMap<>();

        // نولد كل التوليفات الممكنة للعصي الأربعة (2^4 = 16)
        for (int mask = 0; mask < 16; mask++) {
            StickFace[] combo = new StickFace[4];
            for (int j = 0; j < 4; j++) {
                combo[j] = ((mask >> j) & 1) == 1 ? StickFace.LIGHT : StickFace.DARK;
            }
            StickThrow t = new StickThrow(combo);
            int v = t.getTotalValue();
            COUNT_BY_VALUE.put(v, COUNT_BY_VALUE.getOrDefault(v, 0) + 1);

            // نخزن تمثيلاً واحداً لكل قيمة (لا نهتم بترتيب العصي لأن القيمة والاحتمال هما المهمان)
            if (!uniqueByValue.containsKey(v)) {
                uniqueByValue.put(v, t);
            }
        }

        ALL_UNIQUE_THROWS_CACHE = new ArrayList<>(uniqueByValue.values());
        // ملاحظة: طباعة معلومات التهيئة مفيدة أثناء التطوير، يمكن إزالتها لاحقاً
        System.out.println("INFO: StickThrow initialized with " + ALL_UNIQUE_THROWS_CACHE.size() + " unique values.");
    }

    public StickThrow(StickFace[] stickStates) {
        if (stickStates == null || stickStates.length != 4) {
            throw new IllegalArgumentException("A Senet throw must consist of exactly 4 sticks.");
        }
        this.sticks = stickStates.clone();
        this.totalValue = calculateTotalValue();
    }

    /**
     * يولد رمية عشوائية (للاستخدام أثناء اللعب).
     */
    public static StickThrow getRandomThrow() {
        Random random = new Random();
        StickFace[] randomSticks = new StickFace[4];
        for (int i = 0; i < 4; i++) {
            randomSticks[i] = random.nextBoolean() ? StickFace.LIGHT : StickFace.DARK;
        }
        return new StickThrow(randomSticks);
    }

    private int calculateTotalValue() {
        int sum = 0;
        for (StickFace s : sticks) {
            sum += s.getValue();
        }
        // إذا كانت جميع العصي فاتحة (sum == 0) فالقيمة = 5
        return (sum == 0) ? 5 : sum;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public StickFace[] getStickStates() {
        return sticks.clone();
    }

    /**
     * تعيد الاحتمالية لظهور هذه القيمة (مبنية على COUNT_BY_VALUE / 16).
     */
    public double getProbability() {
        int count = COUNT_BY_VALUE.getOrDefault(this.totalValue, 0);
        return (double) count / 16.0;
    }

    @Override
    public String toString() {
        // نعرض أول حرف من كل وجه (L أو D) ثم القيمة
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < sticks.length; i++) {
            sb.append(sticks[i].name().charAt(0));
            if (i < sticks.length - 1) sb.append(",");
        }
        sb.append("] (Value: ").append(this.totalValue).append(")");
        return sb.toString();
    }

    /**
     * يعيد قائمة القيم الفريدة الممكنة (كل قيمة مرة واحدة) مع احتمالاتها المحسوبة.
     * هذا يجعل التكامل مع عقد CHANCE في Expectiminimax أسهل.
     */
    public static List<StickThrow> values() {
        return Collections.unmodifiableList(ALL_UNIQUE_THROWS_CACHE);
    }
}
