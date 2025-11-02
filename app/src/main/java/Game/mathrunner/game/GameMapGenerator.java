package Game.mathrunner.game;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generador del mapa del juego "Pollito en Marcha".
 * Crea obstáculos con la imagen definida (obstacle.png)
 * evitando las posiciones inicial y final del jugador.
 */
public class GameMapGenerator {

    private int cols, rows;
    private int cellW, cellH;
    private Random random;

    public GameMapGenerator(int cols, int rows, int cellW, int cellH) {
        this.cols = cols;
        this.rows = rows;
        this.cellW = cellW;
        this.cellH = cellH;
        this.random = new Random();
    }

    /**
     * Genera una lista de obstáculos con imagen asignada.
     * @param obstacleBitmap Bitmap del obstáculo (obstacle.png)
     * @return lista de obstáculos
     */
    public List<Obstacle> generateObstacles(Bitmap obstacleBitmap) {
        List<Obstacle> obstacles = new ArrayList<>();
        int numObstaculos = random.nextInt(5) + 3; // entre 3 y 7

        for (int i = 0; i < numObstaculos; i++) {
            int c = random.nextInt(cols);
            int r = random.nextInt(rows);

            // Evitar inicio (abajo-izquierda) y meta (arriba-derecha)
            if ((r == rows - 1 && c == 0) || (r == 0 && c == cols - 1))
                continue;

            int x = c * cellW + cellW / 2;
            int y = r * cellH + cellH / 2;

            // Crear obstáculo con imagen
            Obstacle o = new Obstacle(x, y, cellW, cellH, obstacleBitmap);
            obstacles.add(o);
        }

        return obstacles;
    }
}
