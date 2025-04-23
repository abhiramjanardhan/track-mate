package com.aj.trackmate.adapters.entertainment;

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
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.Music;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMusic;

import java.util.List;
import java.util.stream.Collectors;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private final Context context;
    private List<EntertainmentWithMusic> entertainmentWithMusics;
    private final OnMusicClickListener onMusicClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public MusicAdapter(Context context, List<EntertainmentWithMusic> entertainmentWithMusic, OnMusicClickListener listener, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.entertainmentWithMusics = entertainmentWithMusic;
        this.onMusicClickListener = listener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        // Get the music at the current position
        EntertainmentWithMusic entertainmentWithMusic = entertainmentWithMusics.get(position);
        Music music = entertainmentWithMusic.music;
        Entertainment entertainment = entertainmentWithMusic.entertainment;

        Log.d("Items", "Loaded: " + entertainment.getName());

        // Bind data to the view components
        holder.musicName.setText(entertainment.getName());
        holder.musicStatus.setText(music.getAlbum());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + entertainment.getName());
            if (onMusicClickListener != null) {
                onMusicClickListener.onMusicClick(entertainmentWithMusic);
            }
        });

        boolean isFavorite = music.isFavorite(); // From your model

        holder.favoriteStar.setImageResource(
                isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24
        );

        holder.favoriteStar.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteStarClick(music, !isFavorite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entertainmentWithMusics.size();
    }

    public void updateMusics(List<EntertainmentWithMusic> newMusic) {
        this.entertainmentWithMusics = newMusic;
        notifyDataSetChanged();
    }

    public void removeMusic(int position) {
        if (position >= 0 && position < entertainmentWithMusics.size()) {
            entertainmentWithMusics.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<EntertainmentWithMusic> sortMusic(List<EntertainmentWithMusic> entertainmentWithMusics) {
        return entertainmentWithMusics.stream()
                .sorted((a, b) -> a.entertainment.getName().compareToIgnoreCase(b.entertainment.getName()))
                .collect(Collectors.toList());
    }

    public void defaultSortMusics() {
        // Default sort based on status priority
        entertainmentWithMusics = sortMusic(entertainmentWithMusics);
        updateMusics(entertainmentWithMusics);
    }

    // ViewHolder class to hold the views for each item
    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        TextView musicName;
        TextView musicStatus;
        ImageView favoriteStar;

        public MusicViewHolder(View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.musicName);
            musicStatus = itemView.findViewById(R.id.musicStatus);
            favoriteStar = itemView.findViewById(R.id.favoriteStar);
        }
    }

    public interface OnMusicClickListener {
        void onMusicClick(EntertainmentWithMusic entertainmentWithMusic);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteStarClick(Music music, boolean isFavorite);
    }
}
