package Game.mathrunner.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;
import android.animation.AnimatorListenerAdapter;

import java.util.List;

import Game.mathrunner.R;
import Game.mathrunner.utils.SoundManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Player player;
    private Goal goal;
    private List<Obstacle> obstacles;
    private boolean surfaceReady = false;

    public static final int GRID_COLS = 8;
    public static final int GRID_ROWS = 6;
    private int cellWidth;
    private int cellHeight;

    private Bitmap chickBitmap;
    private Bitmap obstacleBitmap;
    private Bitmap goalBitmap;
    private Bitmap backgroundBitmap;

    private float bgOffset = 0f;
    private float bgSpeed = 0.2f;

    private Paint textPaint;
    private GameEventListener listener;

    public enum Direction { UP, DOWN, LEFT, RIGHT }

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
        setWillNotDraw(false);
        setBackgroundResource(R.drawable.background_farm_gradient);
        Log.d("GameView", "Inicializado correctamente");

        textPaint = new Paint();
        textPaint.setColor(0xFF2E7D32);
        textPaint.setTextSize(48);
        textPaint.setShadowLayer(4, 2, 2, 0xFFFFFFFF);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GameView", "surfaceCreated()");
        surfaceReady = true;

        int width = getWidth();
        int height = getHeight();
        cellWidth = width / GRID_COLS;
        cellHeight = height / GRID_ROWS;

        SoundManager.init(getContext());
        SoundManager.playBgMusic(R.raw.bg_music);

        chickBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.chick),
                cellWidth, cellHeight, true
        );

        obstacleBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.obstacle),
                cellWidth, cellHeight, true
        );

        goalBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.apple),
                cellWidth, cellHeight, true
        );

        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_farm_gradient);

        // 🐣 Jugador centrado en su celda de inicio
        player = new Player(
                cellWidth / 2f,
                (GRID_ROWS - 0.5f) * cellHeight,
                cellWidth, cellHeight, chickBitmap
        );

        // 🍎 Meta en esquina superior derecha
        goal = new Goal(
                (GRID_COLS - 0.5f) * cellWidth,
                cellHeight / 2f,
                cellWidth, cellHeight, goalBitmap
        );

        createCircuitConObstaculos();

        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

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
    }

    public void update() {
        if (player != null) {
            player.update();
            checkCollisions();
        }

        bgOffset += bgSpeed;
        if (backgroundBitmap != null && bgOffset > backgroundBitmap.getWidth()) {
            bgOffset = 0;
        }
    }

    private void checkCollisions() {
        if (player == null || goal == null || obstacles == null) return;

        Rect playerRect = player.getRectWithPadding(10);

        if (Rect.intersects(playerRect, goal.getRectWithPadding(12))) {
            SoundManager.play("goal");
            player.setScore(player.getScore() + 10);
            if (listener != null) listener.onReachedGoal();
            resetLevel();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        if (backgroundBitmap != null) {
            int bgWidth = backgroundBitmap.getWidth();
            Rect src1 = new Rect((int) bgOffset, 0, (int) (bgOffset + getWidth()), backgroundBitmap.getHeight());
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundBitmap, src1, dst, null);

            if (bgOffset + getWidth() > bgWidth) {
                Rect src2 = new Rect(0, 0, (int) ((bgOffset + getWidth()) - bgWidth), backgroundBitmap.getHeight());
                dst = new Rect((int) (bgWidth - bgOffset), 0, getWidth(), getHeight());
                canvas.drawBitmap(backgroundBitmap, src2, dst, null);
            }
        }

        if (goal != null) goal.draw(canvas);
        if (obstacles != null) for (Obstacle o : obstacles) o.draw(canvas);
        if (player != null) player.draw(canvas);

        canvas.drawText("Puntaje: " + player.getScore(), 40, 70, textPaint);
    }

    public void createCircuitConObstaculos() {
        GameMapGenerator generator = new GameMapGenerator(GRID_COLS, GRID_ROWS, cellWidth, cellHeight);
        obstacles = generator.generateObstacles(obstacleBitmap);
    }

    public void resetLevel() {
        if (!surfaceReady) return;
        createCircuitConObstaculos();
        player.resetPosition();
        Log.d("GameView", "Nivel reiniciado correctamente");
    }

    // ✅ Movimiento con animación de salto realista
    public boolean requestMove(Direction dir) {
        if (player == null) return false;

        final float dx, dy;
        switch (dir) {
            case UP: dx = 0; dy = -cellHeight; break;
            case DOWN: dx = 0; dy = cellHeight; break;
            case LEFT: dx = -cellWidth; dy = 0; break;
            case RIGHT: dx = cellWidth; dy = 0; break;
            default: return false;
        }

        final float newX = player.getX() + dx;
        final float newY = player.getY() + dy;

        // 📏 Límites del escenario
        if (newX - cellWidth / 2f < 0 || newY - cellHeight / 2f < 0 ||
                newX + cellWidth / 2f > getWidth() || newY + cellHeight / 2f > getHeight()) {
            SoundManager.play("wrong");
            Log.d("GameView", "Movimiento fuera de límites");
            return false;
        }

        // 🚫 Detección de colisiones
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
                    Log.d("GameView", "Colisión detectada, movimiento cancelado");
                    return false;
                }
            }
        }

        // 🐣 Animación de salto suave con movimiento real
        final float startX = player.getX();
        final float startY = player.getY();
        final float jumpHeight = 25f;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            float currentX = startX + dx * t;
            float currentY = startY + dy * t - (float) (Math.sin(Math.PI * t) * jumpHeight);
            player.setPosition(currentX, currentY);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                player.setPosition(newX, newY);
                Log.d("GameView", "Posición final del jugador: X=" + newX + " Y=" + newY);
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
}
