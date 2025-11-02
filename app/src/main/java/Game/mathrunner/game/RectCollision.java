package Game.mathrunner.game;

import android.graphics.RectF;

public class RectCollision {
    public static boolean check(RectF a, RectF b) {
        return a.left < b.right && a.right > b.left &&
                a.top < b.bottom && a.bottom > b.top;
    }
}
