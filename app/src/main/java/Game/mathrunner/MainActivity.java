package Game.mathrunner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import Game.mathrunner.databinding.ActivityMainMenuBinding;
import Game.mathrunner.utils.SoundManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🎵 Inicializar sonidos
        SoundManager.init(this);
        SoundManager.playBgMusic(R.raw.bg_music);

        // 🎮 Botón "JUGAR"
        binding.btnPlay.setOnClickListener(v -> {
            startActivity(new Intent(this, Game.mathrunner.game.GameActivity.class));
            SoundManager.play("move");
        });

        // 🏆 "PUNTAJES ALTOS"
        binding.btnScores.setOnClickListener(v -> {
            startActivity(new Intent(this, Game.mathrunner.score.HighScoresActivity.class));
            SoundManager.play("correct");
        });

        // ⚙️ "CONFIGURACIÓN"
        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, Game.mathrunner.settings.SettingsActivity.class));
            SoundManager.play("select");
        });
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
