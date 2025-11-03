package Game.mathrunner.score;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Game.mathrunner.databinding.ActivityHighScoresBinding;
import Game.mathrunner.game.GameActivity;
import Game.mathrunner.score.adapter.ScoreAdapter;
import Game.mathrunner.score.model.ScoreItem;

public class HighScoresActivity extends AppCompatActivity {
    private ActivityHighScoresBinding binding;
    private ScoreAdapter adapter;
    private List<ScoreItem> scores;
    private SharedPreferences prefs;
    private final String PREF_KEY = "score_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHighScoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("MathRunnerPrefs", Context.MODE_PRIVATE);
        scores = loadScores();

        // Orden descendente por puntaje
        Collections.sort(scores);

        adapter = new ScoreAdapter(scores);
        binding.recyclerScores.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerScores.setAdapter(adapter);

        // Botón para volver al menú principal
        binding.btnMainMenu.setOnClickListener(v -> finish());

        // Botón para volver a jugar (ir a GameActivity)
        binding.btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(HighScoresActivity.this, GameActivity.class);
            startActivity(intent);
            finish(); // Cierra esta pantalla para no acumular en el back stack
        });
    }

    private List<ScoreItem> loadScores() {
        String json = prefs.getString(PREF_KEY, null);
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<List<ScoreItem>>() {}.getType());
        }
        return new ArrayList<>();
    }
}
