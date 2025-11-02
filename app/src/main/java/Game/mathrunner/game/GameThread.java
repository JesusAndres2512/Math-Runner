package Game.mathrunner.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.util.Log;

/**
 * Hilo principal del juego "Pollito en Marcha".
 * Controla el ciclo de actualización y renderizado a 30 FPS.
 */
public class GameThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running;
    private static final int FPS = 30;
    private double averageFPS;

    public GameThread(SurfaceHolder holder, GameView gameView) {
        this.surfaceHolder = holder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;

        Log.i("GameThread", "🔄 Ciclo principal iniciado");

        while (running) {
            startTime = System.nanoTime();
            Canvas canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameView.update(); // Actualiza lógica
                        gameView.draw(canvas); // Redibuja
                    }
                }
            } catch (Exception e) {
                Log.e("GameThread", "Error en el bucle del juego: ", e);
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e("GameThread", "Error liberando el Canvas", e);
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1_000_000;
            waitTime = targetTime - timeMillis;

            if (waitTime > 0) {
                try {
                    sleep(waitTime);
                } catch (InterruptedException e) {
                    Log.w("GameThread", "Sleep interrumpido", e);
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == FPS) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1_000_000.0);
                frameCount = 0;
                totalTime = 0;
                Log.d("GameThread", "FPS promedio: " + String.format("%.2f", averageFPS));
            }
        }

        Log.i("GameThread", "🛑 Hilo del juego detenido correctamente.");
    }
}
