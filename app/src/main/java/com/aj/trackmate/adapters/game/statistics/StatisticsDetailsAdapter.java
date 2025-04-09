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
import com.aj.trackmate.models.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsDetailsAdapter extends RecyclerView.Adapter<StatisticsDetailsAdapter.GameViewHolder> {
    private final Context context;
    private List<Game> games;
    private Map<String, String> valueMap;
    private String statisticsType;

    public static final String STATISTICS_CURRENCY = "Currency";
    public static final String STATISTICS_STATUS = "Status";
    public static final String STATISTICS_YEAR = "Year";

    private void configureValueMap() {
        valueMap.put(STATISTICS_CURRENCY, "AMOUNT");
        valueMap.put(STATISTICS_STATUS, "");
        valueMap.put(STATISTICS_YEAR, "");
    }

    public StatisticsDetailsAdapter(Context context, List<Game> games, String statisticsType) {
        this.context = context;
        this.games = games;
        this.valueMap = new HashMap<>();
        this.statisticsType = statisticsType;
        this.configureValueMap();
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_statistics_details, parent, false);
        return new GameViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        // Get the game at the current position
        Game game = games.get(position);
        Log.d("Items", "Loaded: " + game.getName());

        // Bind data to the view components
        holder.statisticsLabel.setText(String.valueOf(game.getName()));

        String value = valueMap.get(statisticsType);
        if (value != null && !value.isEmpty()) {
            String displayValue = "";
            switch (value) {
                case "AMOUNT" -> displayValue = String.valueOf(game.getAmount());
                default -> displayValue = "NA";
            }
            holder.statisticsValue.setText(displayValue);
        } else {
            holder.statisticsValue.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<Game> newGames) {
        this.games = newGames;
        notifyDataSetChanged();
    }

    // ViewHolder class to hold the views for each item
    public static class GameViewHolder extends RecyclerView.ViewHolder {

        TextView statisticsLabel;
        TextView statisticsValue;

        public GameViewHolder(View itemView) {
            super(itemView);
            statisticsLabel = itemView.findViewById(R.id.statisticsDetailsLabel);
            statisticsValue = itemView.findViewById(R.id.statisticsDetailsValue);
        }
    }
}
