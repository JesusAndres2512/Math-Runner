package Game.mathrunner.game;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.List;

import Game.mathrunner.R;
import Game.mathrunner.utils.SoundManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Player player;
    private Goal goal;
    private List<Obstacle> obstacles;

    private boolean surfaceReady = false;
    private boolean isMoving = false;

    public static final int GRID_COLS = 8;
    public static final int GRID_ROWS = 6;

    private int cellWidth;
    private int cellHeight;

    private Bitmap chickBitmap;
    private Bitmap obstacleBitmap;
    private Bitmap goalBitmap;

    private Paint textPaint;
    private GameEventListener listener;

    public enum Direction {UP, DOWN, LEFT, RIGHT}

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);

        // ✅ Usar el fondo verde del juego
        setBackgroundResource(R.drawable.background_blue);

        // ❌ Eliminar transparencia
        // setZOrderOnTop(true);
        // getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // setBackgroundColor(Color.TRANSPARENT);

        Log.d("GameView", "SurfaceView con fondo background_green.xml configurado.");

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setShadowLayer(5, 0, 0, Color.BLACK);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;

        int width = getWidth();
        int height = getHeight();

        cellWidth = Math.max(1, width / GRID_COLS);
        cellHeight = Math.max(1, height / GRID_ROWS);

        SoundManager.init(getContext());
        SoundManager.playBgMusic(R.raw.bg_music);

        chickBitmap = loadAndScale(R.drawable.chick, cellWidth, cellHeight);
        obstacleBitmap = loadAndScale(R.drawable.obstacle, cellWidth, cellHeight);
        goalBitmap = loadAndScale(R.drawable.apple, cellWidth, cellHeight);

        player = new Player(cellWidth / 2f, (GRID_ROWS - 0.5f) * cellHeight, cellWidth, cellHeight, chickBitmap);
        goal = new Goal((GRID_COLS - 0.5f) * cellWidth, cellHeight / 2f, cellWidth, cellHeight, goalBitmap);

        createCircuitConObstaculos();

        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    private Bitmap loadAndScale(int resId, int targetW, int targetH) {
        Bitmap src = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap scaled = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(scaled);

        float scale = Math.min((float) targetW / src.getWidth(), (float) targetH / src.getHeight());
        float dx = (targetW - src.getWidth() * scale) / 2f;
        float dy = (targetH - src.getHeight() * scale) / 2f;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        c.save();
        c.translate(dx, dy);
        c.scale(scale, scale);
        c.drawBitmap(src, 0, 0, paint);
        c.restore();

        src.recycle();
        return scaled;
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SoundManager.release();

        recycleBitmap(chickBitmap);
        recycleBitmap(obstacleBitmap);
        recycleBitmap(goalBitmap);
    }

    private void recycleBitmap(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) bmp.recycle();
    }

    public void update() {
        if (player != null) player.update();
        checkCollisions();
    }

    private void checkCollisions() {
        if (player == null || goal == null || obstacles == null) return;

        Rect playerRect = player.getRectWithPadding(10);
        if (Rect.intersects(playerRect, goal.getRectWithPadding(12))) {
            SoundManager.play("goal");
            if (listener != null) listener.onReachedGoal();
            resetLevel();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas); // ✅ Llama primero al draw base (mantiene el fondo visible)

        if (canvas == null) return;

        // ❌ Ya no limpiamos con transparencia, para no borrar el fondo del layout
        // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // ✅ Dibuja los elementos del juego en orden
        if (goal != null) goal.draw(canvas);

        if (obstacles != null) {
            for (Obstacle o : obstacles) {
                if (o != null) o.draw(canvas);
            }
        }

        if (player != null) player.draw(canvas);

        // ✅ Dibuja el puntaje sobre el canvas del juego
        if (textPaint != null && player != null) {
            canvas.drawText("Puntaje: " + player.getScore(), 40, 70, textPaint);
        }
    }


    public void createCircuitConObstaculos() {
        GameMapGenerator generator = new GameMapGenerator(GRID_COLS, GRID_ROWS, cellWidth, cellHeight);
        obstacles = generator.generateObstacles(obstacleBitmap);
    }

    public void resetLevel() {
        if (!surfaceReady) return;
        createCircuitConObstaculos();
        if (player != null) {
            player.resetPosition();
            player.update();
        }
        isMoving = false;
        postInvalidate();
    }

    public boolean requestMove(Direction dir) {
        if (player == null || isMoving) return false;

        final float dx, dy;
        switch (dir) {
            case UP -> { dx = 0; dy = -cellHeight; }
            case DOWN -> { dx = 0; dy = cellHeight; }
            case LEFT -> { dx = -cellWidth; dy = 0; }
            case RIGHT -> { dx = cellWidth; dy = 0; }
            default -> { return false; }
        }

        final float newX = player.getX() + dx;
        final float newY = player.getY() + dy;

        if (newX - cellWidth / 2f < 0 || newY - cellHeight / 2f < 0
                || newX + cellWidth / 2f > getWidth() || newY + cellHeight / 2f > getHeight()) {
            SoundManager.play("wrong");
            return false;
        }

        Rect futureRect = new Rect(
                (int) (newX - cellWidth / 2f),
                (int) (newY - cellHeight / 2f),
                (int) (newX + cellWidth / 2f),
                (int) (newY + cellHeight / 2f)
        );

        if (obstacles != null) {
            for (Obstacle o : obstacles) {
                if (Rect.intersects(futureRect, o.getRectWithPadding(8))) {
                    SoundManager.play("collision");
                    if (listener != null) listener.onHitObstacle();
                    return false;
                }
            }
        }

        final float startX = player.getX();
        final float startY = player.getY();
        final float jumpHeight = 25f;

        isMoving = true;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            float currentX = startX + dx * t;
            float currentY = startY + dy * t - (float) (Math.sin(Math.PI * t) * jumpHeight);
            player.setPosition(currentX, currentY);
            postInvalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                player.setPosition(newX, newY);
                isMoving = false;
                postInvalidate();
            }
        });

        animator.start();
        SoundManager.play("jump");
        return true;
    }

    public interface GameEventListener {
        void onReachedGoal();
        void onHitObstacle();
    }

    public void setGameEventListener(GameEventListener listener) {
        this.listener = listener;
    }

    private static class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private final GameView gameView;
        private boolean running = false;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) { this.running = running; }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        if (gameView != null) {
                            gameView.update();
                            if (canvas != null) gameView.draw(canvas);
                        }
                    }
                } catch (Exception e) {
                    Log.e("GameThread", "Error en loop: " + e.getMessage());
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            Log.e("GameThread", "unlockCanvasAndPost error: " + e.getMessage());
                        }
                    }
                }

                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
