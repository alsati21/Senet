// GamePath.java
import java.util.*;

/**
 * يمثل المسار الرسمي "الثعباني" لأحجار اللعبة على رقعة سينت.
 * هذا الفصل يفصل بين التمثيل المرئي للوحة والمنطق المتسلسل للحركة.
 */
public class GamePath {
    public static final int OFF_BOARD = -2; // الثابت الجديد للإشارة إلى الخروج من اللوحة
    private final List<Integer> pathOrder;

    public GamePath() {
        this.pathOrder = new ArrayList<>();
        initializePath();
    }

    private void initializePath() {
        // الصف الأول: من 1 إلى 10
        for (int i = 1; i <= 10; i++) {
            pathOrder.add(i);
        }
        // الصف الثاني: من 20 إلى 11 (عكسياً)
        for (int i = 20; i >= 11; i--) {
            pathOrder.add(i);
        }
        // الصف الثالث: من 21 إلى 30
        for (int i = 21; i <= 30; i++) {
            pathOrder.add(i);
        }
    }

    /**
     * يعطي الموقع التالي في المسار بناءً على الموقع الحالي وخطوات الحركة.
     * @param currentPosition الموقع الحالي للحجر.
     * @param steps عدد الخطوات (نتيجة رمي العصي).
     * @return رقم المربع التالي. إذا تجاوز المسار، يعيد قيمة OFF_BOARD.
     */
    public int getNextPosition(int currentPosition, int steps) {
        int currentIndex = pathOrder.indexOf(currentPosition);
        if (currentIndex == -1) return OFF_BOARD;

        int nextIndex = currentIndex + steps;
        if (nextIndex >= pathOrder.size()) {
            return OFF_BOARD;
        }

        return pathOrder.get(nextIndex);
    }




    /**
     * هذه الدالة الجديدة تسمح للكلاسات الأخرى (مثل SenetBoard) بالحصول على
     * قائمة المسار بشكل آمن دون تعديلها.
     * @return نسخة من قائمة المسار.
     */
    public List<Integer> getPathOrder() {
        return pathOrder; // لا نرجع نسخة جديدة
    }

}