package Game.mathrunner.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Goal {

    private float x, y;
    private int width, height;
    private Bitmap sprite;
    private Rect rect;

    public Goal(float x, float y, int width, int height, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.rect = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
    }

    public void draw(Canvas canvas) {
        if (sprite != null) {
            canvas.drawBitmap(sprite, x - width / 2f, y - height / 2f, null);
        }
    }

    public Rect getRectWithPadding(int padding) {
        return new Rect(
                (int)(x - width / 2f + padding),
                (int)(y - height / 2f + padding),
                (int)(x + width / 2f - padding),
                (int)(y + height / 2f - padding)
        );
    }
}
