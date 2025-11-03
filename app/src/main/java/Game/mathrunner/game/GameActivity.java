package Game.mathrunner.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import Game.mathrunner.R;
import Game.mathrunner.databinding.ActivityGameBinding;
import Game.mathrunner.math.EquationGenerator;
import Game.mathrunner.score.model.ScoreItem;
import Game.mathrunner.utils.SoundManager;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    private EquationGenerator eqGen;
    private int score = 0;
    private int lives = 3;
    private boolean canMove = false;
    private String playerName = "Jugador";

    private SharedPreferences prefs;
    private final String PREF_KEY = "score_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setBackgroundResource(R.drawable.bg_forest);
        prefs = getSharedPreferences("MathRunnerPrefs", Context.MODE_PRIVATE);

        pedirNombreJugador();

        eqGen = new EquationGenerator();
        SoundManager.init(this);
        SoundManager.playBgMusic(R.raw.bg_music);

        newEquation();
        updateUI();

        binding.btnEvaluate.setOnClickListener(v -> evaluarRespuesta());
        binding.btnUp.setOnClickListener(v -> tryMove(GameView.Direction.UP));
        binding.btnDown.setOnClickListener(v -> tryMove(GameView.Direction.DOWN));
        binding.btnLeft.setOnClickListener(v -> tryMove(GameView.Direction.LEFT));
        binding.btnRight.setOnClickListener(v -> tryMove(GameView.Direction.RIGHT));
        binding.btnExit.setOnClickListener(v -> finish());

        binding.gameView.setGameEventListener(new GameView.GameEventListener() {
            @Override public void onReachedGoal() {
                score += 10;
                SoundManager.play("goal");
                Toast.makeText(GameActivity.this, "🎉 Nivel superado", Toast.LENGTH_SHORT).show();
                newEquation();
                updateUI();
            }

            @Override public void onHitObstacle() {
                lives--;
                SoundManager.play("hit");
                updateUI();
                if (lives <= 0) endGame();
            }
        });
    }

    private void evaluarRespuesta() {
        String input = binding.editResult.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "⚠️ Ingresa una raíz", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean correct = eqGen.validateUserInput(input, 2.0);
        binding.editResult.setText("");

        if (correct) {
            canMove = true;
            SoundManager.play("correct");
            score += 5;
            Toast.makeText(this, "✅ Correcto, puedes moverte", Toast.LENGTH_SHORT).show();
            newEquation();
        } else {
            lives--;
            canMove = false;
            SoundManager.play("wrong");
            Toast.makeText(this, "❌ Incorrecto", Toast.LENGTH_SHORT).show();
            if (lives <= 0) endGame();
        }
        updateUI();
    }

    private void pedirNombreJugador() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("👤 Ingresa tu nombre");

        final EditText input = new EditText(this);
        input.setHint("Tu nombre...");
        builder.setView(input);
        builder.setCancelable(false);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) playerName = name;
            Toast.makeText(this, "¡Bienvenido, " + playerName + "!", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        builder.show();
    }

    private void tryMove(GameView.Direction dir) {
        if (!canMove) {
            Toast.makeText(this, "Resuelve primero la ecuación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.gameView.requestMove(dir)) {
            score += 1;
            updateUI();
        }

        canMove = false;
    }

    private void newEquation() {
        eqGen.generateNew();
        String latex = eqGen.getLatexForCurrent().replace("x^2", "x<sup>2</sup>");
        binding.tvFunction.setText(Html.fromHtml(latex, Html.FROM_HTML_MODE_LEGACY));
    }

    private void endGame() {
        Toast.makeText(this, "🏁 Fin del juego. Puntaje: " + score, Toast.LENGTH_LONG).show();
        saveScore(playerName, score);
        resetGame();
    }

    private void saveScore(String name, int score) {
        List<ScoreItem> list = loadScores();
        list.add(new ScoreItem(name, score, "⭐"));
        prefs.edit().putString(PREF_KEY, new Gson().toJson(list)).apply();
    }

    private List<ScoreItem> loadScores() {
        String json = prefs.getString(PREF_KEY, null);
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<List<ScoreItem>>() {}.getType());
        }
        return new ArrayList<>();
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        binding.gameView.resetLevel();
        newEquation();
        updateUI();
    }

    private void updateUI() {
        binding.tvStatus.setText("Jugador: " + playerName + " | Puntaje: " + score);
        ImageView[] hearts = {binding.heart1, binding.heart2, binding.heart3};
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setImageResource(i < lives ? R.drawable.heart_full : R.drawable.heart_empty);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundManager.release();
    }
}
