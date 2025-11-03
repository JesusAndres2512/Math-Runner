package Game.mathrunner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import Game.mathrunner.databinding.ActivityMainMenuBinding;
import Game.mathrunner.score.HighScoresActivity;

public class MainMenuActivity extends AppCompatActivity {
    private ActivityMainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPlay.setOnClickListener(v ->
                startActivity(new Intent(MainMenuActivity.this, MainActivity.class))
        );

        binding.btnScores.setOnClickListener(v ->
                startActivity(new Intent(MainMenuActivity.this, HighScoresActivity.class))
        );
    }
}
