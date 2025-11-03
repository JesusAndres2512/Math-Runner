package Game.mathrunner.score.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Game.mathrunner.R;
import Game.mathrunner.score.model.ScoreItem;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private final List<ScoreItem> scoreList;

    public ScoreAdapter(List<ScoreItem> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreItem item = scoreList.get(position);

        // Posición (1., 2., 3., etc.)
        holder.tvPosition.setText((position + 1) + ".");

        // Emoji: puedes agregar lógica si quieres variar según puntaje o ranking
        holder.tvEmoji.setText(item.getEmoji() != null ? item.getEmoji() : "🙂");

        // Nombre del jugador
        holder.tvName.setText(item.getName());

        // Puntos
        holder.tvPoints.setText(item.getScore() + " PTS");

        // Estilo especial para los primeros puestos
        if (position == 0) {
            holder.tvPosition.setBackgroundResource(R.drawable.circle_rank_bg); // dorado
        } else if (position == 1) {
            holder.tvPosition.setBackgroundResource(R.drawable.circle_rank_bg); // igual dorado o plateado si quieres
        } else if (position == 2) {
            holder.tvPosition.setBackgroundResource(R.drawable.circle_rank_bg);
        }
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition, tvEmoji, tvName, tvPoints;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvName = itemView.findViewById(R.id.tvName);
            tvPoints = itemView.findViewById(R.id.tvPoints);
        }
    }
}
