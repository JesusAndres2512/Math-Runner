package Game.mathrunner.game;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generador del mapa del juego "Pollito en Marcha".
 * Crea obstáculos (obstacle.png) con distribución controlada.
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
     * Genera una lista de obstáculos con:
     * - mínimo 8, máximo 10 obstáculos
     * - máximo 4 obstáculos adyacentes en línea
     * - evitando inicio (abajo-izquierda) y meta (arriba-derecha)
     */
    public List<Obstacle> generateObstacles(Bitmap obstacleBitmap) {
        List<Obstacle> obstacles = new ArrayList<>();

        int numObstaculos = 8 + random.nextInt(3); // entre 8 y 10

        boolean[][] ocupado = new boolean[rows][cols];

        int intentos = 0;
        while (obstacles.size() < numObstaculos && intentos < 500) {
            intentos++;

            int c = random.nextInt(cols);
            int r = random.nextInt(rows);

            // Evitar inicio (abajo-izquierda) y meta (arriba-derecha)
            if ((r == rows - 1 && c == 0) || (r == 0 && c == cols - 1))
                continue;

            // Evitar colocar sobre otra celda
            if (ocupado[r][c]) continue;

            // Evitar que haya más de 4 obstáculos contiguos
            if (formaBloqueGrande(ocupado, r, c))
                continue;

            ocupado[r][c] = true;

            int x = c * cellW + cellW / 2;
            int y = r * cellH + cellH / 2;

            obstacles.add(new Obstacle(x, y, cellW, cellH, obstacleBitmap));
        }

        return obstacles;
    }

    /**
     * Verifica si al colocar un obstáculo en (r, c)
     * se formaría una secuencia de más de 4 celdas seguidas
     * horizontal o verticalmente.
     */
    private boolean formaBloqueGrande(boolean[][] ocupado, int r, int c) {
        int contH = 1; // cuenta horizontal
        int contV = 1; // cuenta vertical

        // Horizontal
        for (int i = c - 1; i >= 0 && ocupado[r][i]; i--) contH++;
        for (int i = c + 1; i < cols && ocupado[r][i]; i++) contH++;

        // Vertical
        for (int j = r - 1; j >= 0 && ocupado[j][c]; j--) contV++;
        for (int j = r + 1; j < rows && ocupado[j][c]; j++) contV++;

        // Si se forma una línea de más de 4, no colocar
        return (contH > 4 || contV > 4);
    }
}
