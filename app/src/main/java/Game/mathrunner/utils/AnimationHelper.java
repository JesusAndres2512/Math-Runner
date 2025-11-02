package Game.mathrunner.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Clase auxiliar para centralizar las animaciones del juego “Pollito en Marcha”.
 * Sincronizada con los archivos XML ubicados en res/anim/.
 */
public class AnimationHelper {

    /**
     * Mueve suavemente una vista (el pollito) en una dirección.
     * @param view Vista del personaje o elemento a mover.
     * @param deltaX Movimiento horizontal (en píxeles).
     * @param deltaY Movimiento vertical (en píxeles).
     * @param duration Duración de la animación en milisegundos.
     */
    public static void moveSmoothly(View view, float deltaX, float deltaY, long duration) {
        view.animate()
                .translationXBy(deltaX)
                .translationYBy(deltaY)
                .setDuration(duration)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Aplica una animación de rebote (salto) al personaje.
     * Utiliza el recurso XML res/anim/bounce.xml si está disponible.
     */
    public static void bounce(View view) {
        Context context = view.getContext();
        try {
            Animation bounceAnim = AnimationUtils.loadAnimation(context, Game.mathrunner.R.anim.bounce);
            view.startAnimation(bounceAnim);
        } catch (Exception e) {
            // Fallback por si no existe el XML
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -30, 0);
            animator.setDuration(400);
            animator.start();
        }
    }

    /**
     * Efecto de parpadeo cuando el jugador responde correctamente.
     * Sincronizado con res/anim/blink.xml.
     */
    public static void correctAnswerEffect(View view) {
        Context context = view.getContext();
        try {
            Animation blink = AnimationUtils.loadAnimation(context, Game.mathrunner.R.anim.blink);
            view.startAnimation(blink);
        } catch (Exception e) {
            // Fallback manual
            ObjectAnimator blink = ObjectAnimator.ofFloat(view, "alpha", 0.3f, 1f);
            blink.setDuration(150);
            blink.setRepeatCount(2);
            blink.start();
        }
    }

    /**
     * Efecto de vibración o sacudida cuando el jugador se equivoca o choca con un obstáculo.
     * Usa el recurso XML res/anim/shake.xml.
     */
    public static void wrongAnswerEffect(View view) {
        Context context = view.getContext();
        try {
            Animation shake = AnimationUtils.loadAnimation(context, Game.mathrunner.R.anim.shake);
            view.startAnimation(shake);
        } catch (Exception e) {
            // Fallback por si no existe el XML
            ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 15, -15, 6, -6, 0);
            shake.setDuration(500);
            shake.start();
        }
    }

    /**
     * Celebración al alcanzar la meta: animación de salto y brillo.
     * Sincronizada con res/anim/celebrate.xml.
     */
    public static void celebrate(View view) {
        Context context = view.getContext();
        try {
            Animation celebrateAnim = AnimationUtils.loadAnimation(context, Game.mathrunner.R.anim.celebrate);
            view.startAnimation(celebrateAnim);
        } catch (Exception e) {
            // Fallback animación manual
            ValueAnimator jump = ObjectAnimator.ofFloat(view, "translationY", 0, -60, 0);
            jump.setDuration(600);
            jump.setRepeatCount(3);
            jump.start();
        }
    }
}
