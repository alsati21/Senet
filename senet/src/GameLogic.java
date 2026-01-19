import java.util.*;

public class GameLogic {

    /* ==============================
       توليد الحركات الممكنة
       ============================== */
    public List<GameState> getPossibleMoves(GameState currentState, StickThrow stickThrow) {
        List<GameState> possibleStates = new ArrayList<>();
        int moveValue = stickThrow.getTotalValue();

        if (moveValue == 0) {
            return possibleStates;
        }

        Player player = currentState.getCurrentPlayer();
        GamePath path = currentState.getBoard().getGamePath();

        // نسخة مؤقتة للحسابات
        GameState tempState = currentState.clone();
        SenetBoard tempBoard = tempState.getBoard();

        // معالجة حورس (30) في بداية الدور
        handleHorusAtTurnStart(tempState);

        // حالة 28 و29 قبل الحركة
        House h28 = tempBoard.getHouse(28);
        House h29 = tempBoard.getHouse(29);

        boolean hasPieceOn28 = h28.isOccupied() && h28.getOccupant() == player;
        boolean hasPieceOn29 = h29.isOccupied() && h29.getOccupant() == player;

        boolean correctRollFor28 = (moveValue == 3);
        boolean correctRollFor29 = (moveValue == 2);

        // تجميد إذا الرمية غلط
        boolean freeze28 = hasPieceOn28 && !correctRollFor28;
        boolean freeze29 = hasPieceOn29 && !correctRollFor29;

        // حالة 30 قبل الحركة
        House h30_before = tempBoard.getHouse(30);
        boolean hadPieceOn30 = h30_before.isOccupied() && h30_before.getOccupant() == player;

        // توليد الحركات
        for (int houseNumber : path.getPathOrder()) {
            House house = tempBoard.getHouse(houseNumber);

            if (!house.isOccupied() || house.getOccupant() != player) {
                continue;
            }

            // منع تحريك 28 إذا مجمّد
            if (freeze28 && houseNumber == 28) {
                continue;
            }

            // منع تحريك 29 إذا مجمّد
            if (freeze29 && houseNumber == 29) {
                continue;
            }

            GameState newState = tryMovePiece(tempState, houseNumber, moveValue);
            if (newState != null) {

                // ===============================
                //  قانون بيت حورس (30)
                // ===============================
                if (hadPieceOn30 && houseNumber != 30) {
                    House h30_after = newState.getBoard().getHouse(30);
                    if (h30_after.isOccupied() && h30_after.getOccupant() == player) {
                        h30_after.setOccupant(null);
                        moveToRebirth(newState, player);
                    }
                }

                // ===============================
                //  قانون بيت الحقائق (28)
                // ===============================
                if (freeze28) {
                    House newH28 = newState.getBoard().getHouse(28);
                    if (newH28.isOccupied() && newH28.getOccupant() == player) {
                        newH28.setOccupant(null);
                        moveToRebirth(newState, player);
                    }
                }

                // ===============================
                //  قانون بيت أتوم (29)
                // ===============================
                if (freeze29) {
                    House newH29 = newState.getBoard().getHouse(29);
                    if (newH29.isOccupied() && newH29.getOccupant() == player) {
                        newH29.setOccupant(null);
                        moveToRebirth(newState, player);
                    }
                }

                possibleStates.add(newState);
            }
        }

        return possibleStates;
    }






    /* ==============================
       تنفيذ الحركة
       ============================== */
    public GameState tryMovePiece(GameState currentState, int fromHouse, int steps) {
        // نعمل clone داخلية حتى لا نغيّر الحالة الممررة (tempState أو currentState الحقيقي)
        GameState newState = currentState.clone();
        SenetBoard board = newState.getBoard();
        GamePath path = board.getGamePath();
        Player player = newState.getCurrentPlayer();

        int toHouse = path.getNextPosition(fromHouse, steps);

        House from = board.getHouse(fromHouse);

        // 1) خروج عادي (تجاوز المسار)
        if (toHouse == GamePath.OFF_BOARD) {
            from.setOccupant(null);
            incrementOff(newState, player);
            endTurn(newState, fromHouse, toHouse);
            return newState;
        }

        // 2) قاعدة بيت السعادة (26) الخاصة: من 26 بخطوة 1 → نصل 27 → ثم إرجاع فوري للبعث
        if (fromHouse == 26 && steps == 1) {
            if (toHouse == 27) {
                from.setOccupant(null);
                moveToRebirth(newState, player);
                endTurn(newState, fromHouse, 27);
                return newState;
            }
        }

        House target = board.getHouse(toHouse);

        // 3) الهدف مشغول بنفس اللاعب → حركة غير صالحة
        if (target.isOccupied() && target.getOccupant() == player) {
            return null;
        }

        // 4) قاعدة عدم القفز فوق بيت السعادة (26)
        List<Integer> order = path.getPathOrder();
        int currentIndex = order.indexOf(fromHouse);
        int nextIndex = order.indexOf(toHouse);
        int happinessIndex = order.indexOf(26);
        if (happinessIndex != -1 && currentIndex < happinessIndex && nextIndex > happinessIndex) {
            return null;
        }

        // 5) تنفيذ الحركة: إزالة الحجر من المصدر أولاً
        Player targetOccupant = target.getOccupant();
        from.setOccupant(null);

        // استبدال (swap) إذا الهدف فيه خصم
        if (targetOccupant != null && targetOccupant != player) {
            board.getHouse(fromHouse).setOccupant(targetOccupant);
        }

        // ======= حالة الوصول إلى 30 (حورس) =======
        if (toHouse == 30) {
            // ضع الحجر على 30 (لا تخرجه الآن)، وسجّل رقم الدور الذي هبط فيه
            target.setOccupant(player);
            newState.setHorusLanded(30, newState.getTurnNumber());
            endTurn(newState, fromHouse, 30);
            return newState;
        }

        // ضع اللاعب في الهدف
        target.setOccupant(player);

        // بيت الماء (27): إذا هبطنا عليه نعيد الحجر فوراً إلى البعث
        if (toHouse == 27) {
            target.setOccupant(null);
            moveToRebirth(newState, player);
            endTurn(newState, fromHouse, 27);
            return newState;
        }

        // إنهاء الدور وتسجيل الحركة
        endTurn(newState, fromHouse, toHouse);
        return newState;
    }


    /* ==============================
       معالجة حالة حورس في بداية الدور
       ============================== */
    private void handleHorusAtTurnStart(GameState state) {
        SenetBoard board = state.getBoard();
        Player current = state.getCurrentPlayer();
        House h30 = board.getHouse(30);
        Integer landedTurn = state.getHorusLandedTurn(30);

        if (h30.isOccupied() && h30.getOccupant() == current) {
            if (landedTurn == null) {
                // إذا لم يُسجل الهبوط، نسجّله كهبوط في الدور السابق لضمان الاتساق
                state.setHorusLanded(30, Math.max(0, state.getTurnNumber() - 1));
                return;
            }

            int currentTurn = state.getTurnNumber();
            // إذا هبط في الدور السابق → الحجر مؤهل للخروج الآن (لا نفعل شيئاً هنا)
            if (landedTurn == currentTurn - 1) {
                return;
            } else {
                // هبط قبل الدور السابق ولم يتحرك → نعيده للبعث الآن
                h30.setOccupant(null);
                state.clearHorusLanded(30);
                moveToRebirth(state, current);
            }
        }
    }


    /* ==============================
       بيت البعث (15)
       ============================== */
    private void moveToRebirth(GameState state, Player player) {
        SenetBoard board = state.getBoard();
        GamePath path = board.getGamePath();
        List<Integer> order = path.getPathOrder();

        // بيت البعث حسب اللوحة هو 16
        int rebirthIndex = order.indexOf(16);
        if (rebirthIndex == -1) {
            // حالة بنيوية غير متوقعة
            return;
        }

        // إذا بيت البعث (16) فارغ
        House rebirthHouse = board.getHouse(16);
        if (!rebirthHouse.isOccupied()) {
            rebirthHouse.setOccupant(player);
            return;
        }

        // خلاف ذلك، ابحث للخلف عن أول خانة فارغة قبل 16 على مسار الحركة
        for (int i = rebirthIndex - 1; i >= 0; i--) {
            int h = order.get(i);
            House candidate = board.getHouse(h);
            if (!candidate.isOccupied()) {
                candidate.setOccupant(player);
                return;
            }
        }

        // إذا لم نجد أي خانة فارغة قبل 16، لا نفعل شيئاً إضافياً
    }



    /* ==============================
       أدوات مساعدة
       ============================== */
    private void incrementOff(GameState state, Player player) {
        if (player == Player.WHITE) {
            state.incrementWhitePiecesOff();
        } else {
            state.incrementBlackPiecesOff();
        }
    }

    private void endTurn(GameState state, int from, int to) {
        state.setLastMove(from, to);
        state.incrementTurnNumber();
        state.setCurrentPlayer(state.getCurrentPlayer().getOpponent());
    }
}
