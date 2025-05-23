package com.aj.trackmate.adapters.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.GameStatus;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;

import java.util.List;
import java.util.stream.Collectors;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private Context context;
    private List<GameWithDownloadableContent> games;
    private final OnGameClickListener onGameClickListener;
    private final OnGameLongClickListener onGameLongClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public GameAdapter(Context context, List<GameWithDownloadableContent> games, OnGameClickListener listener, OnGameLongClickListener onGameLongClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.games = games;
        this.onGameClickListener = listener;
        this.onGameLongClickListener = onGameLongClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        // Get the game at the current position
        GameWithDownloadableContent gameWithDLCs = games.get(position);
        Game game = gameWithDLCs.game;
        Log.d("Items", "Loaded: " + game.getName());

        // Bind data to the view components
        holder.gameName.setText(game.getName());
        holder.gameStatus.setText(game.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + game.getName());
            if (onGameClickListener != null) {
                onGameClickListener.onGameClick(gameWithDLCs);
            }
        });

        // handle item long press
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("ItemClicked", "Item pressed: " + game.getName());
            if (onGameLongClickListener != null) {
                onGameLongClickListener.onGameClick(v, position);
            }
            return true; // Long press handled
        });

        boolean isFavorite = game.isFavorite(); // From your model

        holder.favoriteStar.setImageResource(
                isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24
        );

        holder.favoriteStar.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteStarClick(game, !isFavorite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<GameWithDownloadableContent> newGames) {
        this.games = newGames;
        notifyDataSetChanged();
    }

    public void removeGame(int position) {
        if (position >= 0 && position < games.size()) {
            games.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<GameWithDownloadableContent> sortGames(List<GameWithDownloadableContent> games) {
        return games.stream()
                .sorted((a, b) -> {
                    int aPriority = GameStatus.getStatusPriority().getOrDefault(a.game.getStatus(), Integer.MAX_VALUE);
                    int bPriority = GameStatus.getStatusPriority().getOrDefault(b.game.getStatus(), Integer.MAX_VALUE);
                    return Integer.compare(aPriority, bPriority);
                })
                .collect(Collectors.toList());
    }

    public void defaultSortGames() {
        // Default sort based on status priority
        games = sortGames(games);
        updateGames(games);
    }

    // ViewHolder class to hold the views for each item
    public static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView gameName;
        TextView gameStatus;
        ImageView favoriteStar;

        public GameViewHolder(View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameStatus = itemView.findViewById(R.id.gameStatus);
            favoriteStar = itemView.findViewById(R.id.favoriteStar);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(GameWithDownloadableContent game);
    }

    public interface OnGameLongClickListener {
        void onGameClick(View view, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteStarClick(Game game, boolean isFavorite);
    }
}
