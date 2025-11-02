package Game.mathrunner.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class PaintUtils {

    public static void drawRoundedRect(Canvas canvas, float x, float y, int width, int height, String hexColor) {
        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.parseColor(hexColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(new RectF(x, y, x + width, y + height), 25, 25, paint);
    }
}

