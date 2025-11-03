package Game.mathrunner.score.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreItem implements Comparable<ScoreItem> {
    private String name;
    private int score;
    private String emoji;
    private String date; // Fecha del registro (ej: 2025-11-01 20:15)

    public ScoreItem(String name, int score, String emoji) {
        this.name = name;
        this.score = score;
        this.emoji = emoji;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(ScoreItem other) {
        // Orden descendente (de mayor a menor puntaje)
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return emoji + " " + name + " - " + score + " pts (" + date + ")";
    }
}
