package Game.mathrunner.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Player {

    private float x, y; // coordenadas del CENTRO
    private int width, height;
    private float startX, startY;
    private Bitmap sprite;
    private int score = 0;
    private Rect rect;

    // 🔹 Variables para animación
    private boolean isJumping = false;
    private float jumpOffset = 0f;
    private float jumpSpeed = 6f;     // velocidad del salto
    private float jumpAmplitude = 25f; // altura máxima
    private float jumpProgress = 0f;

    public Player(float x, float y, int width, int height, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
        this.sprite = sprite;
        updateRect();
    }

    public void draw(Canvas canvas) {
        if (sprite != null) {
            float drawY = y - height / 2f - jumpOffset;
            canvas.drawBitmap(sprite, x - width / 2f, drawY, null);
        }
    }

    public void update() {
        // 🔸 Animación de salto (rebote leve)
        if (isJumping) {
            jumpProgress += jumpSpeed;
            jumpOffset = (float) (Math.sin(Math.toRadians(jumpProgress)) * jumpAmplitude);

            if (jumpProgress >= 180) { // fin del salto
                isJumping = false;
                jumpOffset = 0f;
                jumpProgress = 0f;
            }
        }
    }

    // Mueve al jugador y activa el rebote
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
        startJump();
        updateRect();
    }

    private void startJump() {
        isJumping = true;
        jumpProgress = 0f;
    }

    public void resetPosition() {
        x = startX;
        y = startY;
        isJumping = false;
        jumpOffset = 0f;
        updateRect();
    }

    private void updateRect() {
        rect = new Rect(
                (int) (x - width / 2f),
                (int) (y - height / 2f),
                (int) (x + width / 2f),
                (int) (y + height / 2f)
        );
    }

    public Rect getRectWithPadding(int padding) {
        return new Rect(
                rect.left + padding,
                rect.top + padding,
                rect.right - padding,
                rect.bottom - padding
        );
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateRect();
    }

}
