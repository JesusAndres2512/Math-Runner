package Game.mathrunner.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import Game.mathrunner.R;

public class Obstacle {
    private int x, y;
    private int sizeW, sizeH;
    private Bitmap sprite;
    private Rect hitbox;

    public Obstacle(Context context, int x, int y, int sizeW, int sizeH) {
        this.x = x;
        this.y = y;
        this.sizeW = sizeW;
        this.sizeH = sizeH;

        // Cargar y escalar imagen del obstáculo
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);
        this.sprite = Bitmap.createScaledBitmap(original, sizeW, sizeH, true);

        hitbox = new Rect();
        updateHitbox();
    }

    public void draw(Canvas canvas) {
        if (sprite != null) {
            canvas.drawBitmap(sprite, x - sizeW / 2f, y - sizeH / 2f, null);
        }
        updateHitbox();
    }


    private void updateHitbox() {
        int pad = sizeW / 8;
        hitbox.set(
                x - sizeW / 2 + pad,
                y - sizeH / 2 + pad,
                x + sizeW / 2 - pad,
                y + sizeH / 2 - pad
        );
    }

    public Rect getRectWithPadding(int padding) {
        Rect padded = new Rect(hitbox);
        padded.inset(padding, padding);
        return padded;
    }

    public Obstacle(int x, int y, int sizeW, int sizeH, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.sizeW = sizeW;
        this.sizeH = sizeH;
        this.sprite = sprite;
        hitbox = new Rect();
        updateHitbox();
    }

}
