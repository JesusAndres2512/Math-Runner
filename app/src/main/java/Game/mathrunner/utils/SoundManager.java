package Game.mathrunner.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

import Game.mathrunner.R;

/**
 * Gestor central de sonidos y música de fondo para MathRunner.
 * Compatible con Android 12+.
 */
public class SoundManager {

    private static final String TAG = "SoundManager";

    public static final String SOUND_JUMP = "jump";
    public static final String SOUND_CORRECT = "correct";
    public static final String SOUND_WRONG = "wrong";
    public static final String SOUND_GOAL = "goal";
    public static final String SOUND_COLLISION = "collision";
    public static final String SOUND_HIT = "hit";
    public static final String SOUND_MOVE = "move";
    public static final String SOUND_GAMEOVER = "gameover";

    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundMap;
    private static MediaPlayer bgMusic;
    private static boolean loaded = false;
    private static Context appContext;

    // ==========================
    //      INICIALIZACIÓN
    // ==========================
    public static void init(Context context) {
        if (loaded) {
            Log.d(TAG, "SoundManager ya inicializado.");
            return;
        }

        appContext = context.getApplicationContext();

        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();

            soundMap = new HashMap<>();

            // --- Carga de sonidos ---
            soundMap.put(SOUND_JUMP, soundPool.load(appContext, R.raw.jump, 1));
            soundMap.put(SOUND_CORRECT, soundPool.load(appContext, R.raw.correct_answer, 1));
            soundMap.put(SOUND_WRONG, soundPool.load(appContext, R.raw.wrong, 1));
            soundMap.put(SOUND_GOAL, soundPool.load(appContext, R.raw.goal, 1));
            soundMap.put(SOUND_COLLISION, soundPool.load(appContext, R.raw.collision, 1));
            soundMap.put(SOUND_HIT, soundPool.load(appContext, R.raw.hit_sound, 1));

            // Opcionales (si existen)
            tryLoadOptionalSound(SOUND_MOVE, R.raw.move);
            tryLoadOptionalSound(SOUND_GAMEOVER, R.raw.gameover);

            loaded = true;
            Log.d(TAG, "✅ SoundManager inicializado correctamente.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al inicializar SoundManager: " + e.getMessage());
        }
    }

    private static void tryLoadOptionalSound(String key, int resId) {
        try {
            soundMap.put(key, soundPool.load(appContext, resId, 1));
        } catch (Exception ignored) {
            Log.w(TAG, "Sonido opcional no encontrado: " + key);
        }
    }

    // ==========================
    //      EFECTOS DE SONIDO
    // ==========================
    public static void play(String soundName) {
        if (!loaded || soundPool == null) {
            Log.w(TAG, "SoundManager no cargado. Llama a init(context) primero.");
            return;
        }

        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);
        } else {
            Log.w(TAG, "⚠️ Sonido no encontrado: " + soundName);
        }
    }

    // ==========================
    //   MÚSICA DE FONDO
    // ==========================
    /** Reproduce música de fondo específica en bucle. */
    public static void playBgMusic(int resId) {
        try {
            stopBgMusic(); // detiene si ya había música sonando

            bgMusic = MediaPlayer.create(appContext, resId);
            if (bgMusic != null) {
                bgMusic.setLooping(true);
                bgMusic.setVolume(0.4f, 0.4f);
                bgMusic.start();
                Log.d(TAG, "🎵 Música de fondo iniciada.");
            } else {
                Log.w(TAG, "No se pudo crear el MediaPlayer para la música.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al reproducir música: " + e.getMessage());
        }
    }

    /** Reanuda música pausada. */
    public static void playBgMusic() {
        try {
            if (bgMusic != null && !bgMusic.isPlaying()) {
                bgMusic.start();
                Log.d(TAG, "🎶 Música reanudada.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al reanudar música: " + e.getMessage());
        }
    }

    public static void pauseBgMusic() {
        try {
            if (bgMusic != null && bgMusic.isPlaying()) {
                bgMusic.pause();
                Log.d(TAG, "⏸️ Música pausada.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al pausar música: " + e.getMessage());
        }
    }

    public static void stopBgMusic() {
        try {
            if (bgMusic != null) {
                if (bgMusic.isPlaying()) {
                    bgMusic.stop();
                }
                bgMusic.release();
                bgMusic = null;
                Log.d(TAG, "🛑 Música detenida y liberada.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al detener música: " + e.getMessage());
        }
    }

    // ==========================
    //     LIBERAR RECURSOS
    // ==========================
    public static void release() {
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            if (bgMusic != null) {
                bgMusic.release();
                bgMusic = null;
            }
            if (soundMap != null) {
                soundMap.clear();
                soundMap = null;
            }
            loaded = false;
            Log.d(TAG, "✅ SoundManager liberado correctamente.");
        } catch (Exception e) {
            Log.e(TAG, "Error al liberar SoundManager: " + e.getMessage());
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
