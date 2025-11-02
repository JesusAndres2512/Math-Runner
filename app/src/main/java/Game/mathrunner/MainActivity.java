package Game.mathrunner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Game.mathrunner.game.GameView;
import Game.mathrunner.utils.SoundManager;
import Game.mathrunner.math.EquationGenerator;

public class MainActivity extends AppCompatActivity {

    private TextView tvFunction;
    private EditText editResult;
    private Button btnEvaluate, btnUp, btnDown, btnLeft, btnRight;
    private TextView tvStatus;
    private GameView gameView;
    private EquationGenerator eqGen;

    private int score = 0;
    private int lives = 3;
    private boolean canMove = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔊 Inicializar sonidos
        SoundManager.init(this);
        SoundManager.playBgMusic(R.raw.bg_music);

        // 🔗 Referencias UI principales
        tvFunction = findViewById(R.id.tv_function);
        editResult = findViewById(R.id.edit_result);
        btnEvaluate = findViewById(R.id.btn_evaluate);
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        tvStatus = findViewById(R.id.tv_status);

        // 🔗 Obtener referencia al GameView dentro del include (game_view_container)
        gameView = findViewById(R.id.game_view);

        // Inicializador del generador de ecuaciones
        eqGen = new EquationGenerator();

        // 🕹 Esperar a que el GameView esté listo antes de crear el nivel
        if (gameView != null && gameView.getHolder() != null) {
            gameView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    newLevel();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {}
            });
        }

        // 🎯 Evaluar respuesta
        btnEvaluate.setOnClickListener(v -> {
            String input = editResult.getText().toString().trim();
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "Ingresa una raíz.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = eqGen.validateUserInput(input, 2.0); // valida raíz
            if (ok) {
                canMove = true;
                SoundManager.play("correct");
                Toast.makeText(this, "✅ Correcto — elige dirección para avanzar", Toast.LENGTH_SHORT).show();
            } else {
                canMove = false;
                SoundManager.play("wrong");
                lives--;
                updateStatus();
                Toast.makeText(this, "❌ Incorrecto", Toast.LENGTH_SHORT).show();
                if (lives <= 0) {
                    SoundManager.play("gameover");
                    Toast.makeText(this, "Has perdido. Reiniciando partida.", Toast.LENGTH_LONG).show();
                    resetGame();
                }
            }
            editResult.setText("");
        });

        // 🔼 Controles direccionales
        btnUp.setOnClickListener(v -> tryMove(GameView.Direction.UP));
        btnDown.setOnClickListener(v -> tryMove(GameView.Direction.DOWN));
        btnLeft.setOnClickListener(v -> tryMove(GameView.Direction.LEFT));
        btnRight.setOnClickListener(v -> tryMove(GameView.Direction.RIGHT));

        // 🎮 Eventos del juego (objetivos, colisiones, etc.)
        if (gameView != null) {
            gameView.setGameEventListener(new GameView.GameEventListener() {
                @Override
                public void onReachedGoal() {
                    SoundManager.play("goal");
                    score += 10;
                    updateStatus();
                    Toast.makeText(MainActivity.this, "🎉 ¡Nivel completado!", Toast.LENGTH_LONG).show();
                    newLevel();
                }

                @Override
                public void onHitObstacle() {
                    SoundManager.play("hit");
                    Toast.makeText(MainActivity.this, "💥 Chocaste con un obstáculo.", Toast.LENGTH_SHORT).show();
                    lives--;
                    updateStatus();
                    if (lives <= 0) {
                        SoundManager.play("gameover");
                        Toast.makeText(MainActivity.this, "Has perdido. Reiniciando partida.", Toast.LENGTH_LONG).show();
                        resetGame();
                    } else {
                        gameView.resetLevel();
                    }
                }
            });
        }

        updateStatus();
    }

    /** Intenta mover el personaje solo si la respuesta fue correcta */
    private void tryMove(GameView.Direction dir) {
        if (!canMove) {
            Toast.makeText(this, "Debes responder correctamente antes de moverte.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean moved = gameView.requestMove(dir);
        if (moved) {
            score += 1;
            SoundManager.play("move");
            updateStatus();
        }
        canMove = false;
    }

    /** Actualiza puntaje y vidas en pantalla */
    private void updateStatus() {
        tvStatus.setText(String.format("Puntaje: %d   Vidas: %d", score, lives));
    }

    /** Reinicia la partida desde cero */
    private void resetGame() {
        score = 0;
        lives = 3;
        updateStatus();
        newLevel();
    }

    /** Genera una nueva ecuación y reinicia el nivel */
    private void newLevel() {
        eqGen.generateNew();
        String latex = eqGen.getLatexForCurrent();
        String html = formatLatexToHtml(latex);
        tvFunction.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        if (gameView != null) {
            gameView.resetLevel();
        }
    }

    /** Convierte LaTeX a HTML legible */
    private String formatLatexToHtml(String latex) {
        if (latex == null) return "";
        String s = latex.replaceAll("\\$+", "").trim();
        s = s.replaceAll("x\\^2", "x<sup>2</sup>");
        s = s.replaceAll("x\\^3", "x<sup>3</sup>");
        s = s.replaceAll("\\s*\\+\\s*", " + ");
        s = s.replaceAll("\\s*\\-\\s*", " - ");
        s = s.replaceAll("\\s+", " ");
        return s;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundManager.release();
    }
}
