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
import com.aj.trackmate.models.entertainment.TelevisionSeries;
import com.aj.trackmate.models.entertainment.TelevisionSeriesStatus;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;

import java.util.List;

public class TelevisionSeriesAdapter extends RecyclerView.Adapter<TelevisionSeriesAdapter.TelevisionSeriesViewHolder> {
    private final Context context;
    private List<EntertainmentWithTelevisionSeries> entertainmentWithTelevisionSeries;
    private final OnTelevisionSeriesClickListener onTelevisionSeriesClickListener;
    private final OnTelevisionSeriesLongClickListener onTelevisionSeriesLongClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public TelevisionSeriesAdapter(Context context, List<EntertainmentWithTelevisionSeries> entertainmentWithTelevisionSeries, OnTelevisionSeriesClickListener listener, OnTelevisionSeriesLongClickListener onTelevisionSeriesLongClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.entertainmentWithTelevisionSeries = entertainmentWithTelevisionSeries;
        this.onTelevisionSeriesClickListener = listener;
        this.onTelevisionSeriesLongClickListener = onTelevisionSeriesLongClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    public TelevisionSeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_television_series, parent, false);
        return new TelevisionSeriesViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(TelevisionSeriesViewHolder holder, int position) {
        // Get the movie at the current position
        EntertainmentWithTelevisionSeries tvSeries = entertainmentWithTelevisionSeries.get(position);
        TelevisionSeries series = tvSeries.televisionSeries;
        Entertainment entertainment = tvSeries.entertainment;

        Log.d("Items", "Loaded: " + entertainment.getName());
        Log.d("Items", "Loaded: " + series);

        // Bind data to the view components
        holder.tvSeriesName.setText(entertainment.getName());
        holder.tvSeriesStatus.setText(series.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + entertainment.getName());
            if (onTelevisionSeriesClickListener != null) {
                onTelevisionSeriesClickListener.onTelevisionSeriesClick(tvSeries);
            }
        });

        // handle item long press
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("ItemClicked", "Item pressed: " + entertainment.getName());
            if (onTelevisionSeriesLongClickListener != null) {
                onTelevisionSeriesLongClickListener.onTelevisionSeriesClick(v, position);
            }
            return true; // Long press handled
        });

        boolean isFavorite = series.isFavorite(); // From your model

        holder.favoriteStar.setImageResource(
                isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24
        );

        holder.favoriteStar.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteStarClick(series, !isFavorite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entertainmentWithTelevisionSeries.size();
    }

    public void updateTelevisionSeries(List<EntertainmentWithTelevisionSeries> newTvSeries) {
        this.entertainmentWithTelevisionSeries = newTvSeries;
        notifyDataSetChanged();
    }

    public void removeTelevisionSeries(int position) {
        if (position >= 0 && position < entertainmentWithTelevisionSeries.size()) {
            entertainmentWithTelevisionSeries.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void sortTelevisionSeries() {
        // Default sort based on status priority
        entertainmentWithTelevisionSeries.sort((a, b) -> {
            int aPriority = TelevisionSeriesStatus.getStatusPriority().getOrDefault(a.televisionSeries.getStatus(), Integer.MAX_VALUE);
            int bPriority = TelevisionSeriesStatus.getStatusPriority().getOrDefault(b.televisionSeries.getStatus(), Integer.MAX_VALUE);
            return Integer.compare(aPriority, bPriority);
        });
        updateTelevisionSeries(entertainmentWithTelevisionSeries);
    }

    // ViewHolder class to hold the views for each item
    public static class TelevisionSeriesViewHolder extends RecyclerView.ViewHolder {

        TextView tvSeriesName;
        TextView tvSeriesStatus;
        ImageView favoriteStar;

        public TelevisionSeriesViewHolder(View itemView) {
            super(itemView);
            tvSeriesName = itemView.findViewById(R.id.tvSeriesName);
            tvSeriesStatus = itemView.findViewById(R.id.tvSeriesStatus);
            favoriteStar = itemView.findViewById(R.id.favoriteStar);
        }
    }

    public interface OnTelevisionSeriesClickListener {
        void onTelevisionSeriesClick(EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries);
    }

    public interface OnTelevisionSeriesLongClickListener {
        void onTelevisionSeriesClick(View view, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteStarClick(TelevisionSeries televisionSeries, boolean isFavorite);
    }
}
