package Game.mathrunner.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import Game.mathrunner.databinding.ActivitySettingsBinding;
import Game.mathrunner.utils.SoundManager;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("MathRunnerPrefs", MODE_PRIVATE);

        // 🔊 Inicializar valores guardados
        boolean musicEnabled = prefs.getBoolean("music_enabled", true);
        boolean sfxEnabled = prefs.getBoolean("sfx_enabled", true);

        binding.switchMusic.setChecked(musicEnabled);
        binding.switchSfx.setChecked(sfxEnabled);

        // 🎶 Listeners
        binding.switchMusic.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            prefs.edit().putBoolean("music_enabled", isChecked).apply();
            if (isChecked) {
                SoundManager.playBgMusic(Game.mathrunner.R.raw.bg_music);
            } else {
                SoundManager.stopBgMusic();
            }
        });

        binding.switchSfx.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) ->
                prefs.edit().putBoolean("sfx_enabled", isChecked).apply()
        );

        // 🔙 Botón volver
        binding.btnBack.setOnClickListener(v -> finish());
    }
}
