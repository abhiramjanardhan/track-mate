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
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.dao.helpers.YearCount;

import java.util.List;

public class StatisticsYearAdapter extends RecyclerView.Adapter<StatisticsYearAdapter.GameViewHolder> {
    private final Context context;
    private final Platform platform;
    private List<YearCount> games;
    private final OnGameClickListener onGameClickListener;

    public StatisticsYearAdapter(Context context, Platform platform, List<YearCount> games, OnGameClickListener listener) {
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
        YearCount yearCount = games.get(position);
        Log.d("Items", "Loaded: " + yearCount.year);

        // Bind data to the view components
        holder.statisticsLabel.setText(String.valueOf(yearCount.year));
        holder.statisticsValue.setText(String.valueOf(yearCount.count));

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + yearCount.year);
            if (onGameClickListener != null) {
                onGameClickListener.onGameClick(platform, yearCount.year);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<YearCount> newGames) {
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
        void onGameClick(Platform platform, int year);
    }
}
