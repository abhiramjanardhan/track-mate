package com.aj.trackmate.adapters.game.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.game.GameStatus;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.dao.helpers.StatusCount;

import java.util.List;

public class StatisticsStatusAdapter extends RecyclerView.Adapter<StatisticsStatusAdapter.GameViewHolder> {
    private final Context context;
    private final Platform platform;
    private List<StatusCount> games;
    private final OnGameClickListener onGameClickListener;

    public StatisticsStatusAdapter(Context context, Platform platform, List<StatusCount> games, OnGameClickListener listener) {
        this.context = context;
        this.platform = platform;
        this.games = games;
        this.onGameClickListener = listener;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_statistics, parent, false);
        return new GameViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        // Get the game at the current position
        StatusCount statusCount = games.get(position);
        Log.d("Items", "Loaded: " + statusCount.status.getStatus());

        // Bind data to the view components
        holder.statisticsLabel.setText(statusCount.status.getStatus());
        holder.statisticsValue.setText(String.valueOf(statusCount.count));

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + statusCount.status.getStatus());
            if (onGameClickListener != null) {
                onGameClickListener.onGameClick(platform, statusCount.status);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<StatusCount> newGames) {
        this.games = newGames;
        notifyDataSetChanged();
    }

    // ViewHolder class to hold the views for each item
    public static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView statisticsLabel;
        TextView statisticsValue;

        public GameViewHolder(View itemView) {
            super(itemView);
            statisticsLabel = itemView.findViewById(R.id.statisticsLabel);
            statisticsValue = itemView.findViewById(R.id.statisticsValue);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(Platform platform, GameStatus status);
    }
}
