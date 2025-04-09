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
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.dao.helpers.AmountWithCurrency;

import java.util.List;

public class StatisticsCurrencyAdapter extends RecyclerView.Adapter<StatisticsCurrencyAdapter.GameViewHolder> {
    private final Context context;
    private final Platform platform;
    private List<AmountWithCurrency> games;
    private final OnGameClickListener onGameClickListener;

    public StatisticsCurrencyAdapter(Context context, Platform platform, List<AmountWithCurrency> games, OnGameClickListener listener) {
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
        AmountWithCurrency amountWithCurrency = games.get(position);
        Log.d("Items", "Loaded: " + amountWithCurrency.currency.getCurrency());

        // Bind data to the view components
        holder.statisticsLabel.setText(amountWithCurrency.currency.getCurrency());
        holder.statisticsValue.setText(String.valueOf(amountWithCurrency.amount));

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + amountWithCurrency.currency.getCurrency());
            if (onGameClickListener != null) {
                onGameClickListener.onGameClick(platform, amountWithCurrency.currency);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<AmountWithCurrency> newGames) {
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
        void onGameClick(Platform platform, Currency currency);
    }
}
