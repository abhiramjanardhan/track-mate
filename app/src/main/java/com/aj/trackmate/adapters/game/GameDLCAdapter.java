package com.aj.trackmate.adapters.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.game.DownloadableContent;

import java.util.List;

public class GameDLCAdapter extends RecyclerView.Adapter<GameDLCAdapter.GameDLCViewHolder> {
    private final Context context;
    private List<DownloadableContent> dlcs;
    private final OnGameClickListener onGameClickListener;
    private final OnGameLongClickListener onGameLongClickListener;

    public GameDLCAdapter(Context context, List<DownloadableContent> dlcs, GameDLCAdapter.OnGameClickListener listener, OnGameLongClickListener onGameLongClickListener) {
        this.context = context;
        this.dlcs = dlcs;
        this.onGameClickListener = listener;
        this.onGameLongClickListener = onGameLongClickListener;
    }

    @Override
    public GameDLCAdapter.GameDLCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_game_dlc, parent, false);
        return new GameDLCAdapter.GameDLCViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GameDLCAdapter.GameDLCViewHolder holder, int position) {
        // Get the game at the current position
        DownloadableContent dlc = dlcs.get(position);
        Log.d("Items", "Loaded: " + dlc.getName());

        // Bind data to the view components
        holder.gameName.setText(dlc.getName());
        holder.gameType.setText(dlc.getDlcType().getDLCType());
        holder.gameStatus.setText(dlc.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + dlc.getName());
            if (onGameClickListener != null) {
                onGameClickListener.onGameClick(dlc);
            }
        });

        // handle item long press
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("ItemClicked", "Item pressed: " + dlc.getName());
            if (onGameLongClickListener != null) {
                onGameLongClickListener.onGameClick(v, position);
            }
            return true; // Long press handled
        });
    }

    @Override
    public int getItemCount() {
        return dlcs.size();
    }

    public void updateGames(List<DownloadableContent> newGames) {
        this.dlcs = newGames;
        notifyDataSetChanged();
    }

    public void removeGame(int position) {
        if (position >= 0 && position < dlcs.size()) {
            dlcs.remove(position);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder class to hold the views for each item
    public static class GameDLCViewHolder extends RecyclerView.ViewHolder {

        TextView gameName;
        TextView gameType;
        TextView gameStatus;

        public GameDLCViewHolder(View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameDLCName);
            gameType = itemView.findViewById(R.id.gameDLCType);
            gameStatus = itemView.findViewById(R.id.gameDLCStatus);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(DownloadableContent game);
    }

    public interface OnGameLongClickListener {
        void onGameClick(View view, int position);
    }
}
