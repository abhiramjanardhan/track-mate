package com.aj.trackmate.adapters.entertainment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.TelevisionSeries;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;

import java.util.List;

public class TelevisionSeriesAdapter extends RecyclerView.Adapter<TelevisionSeriesAdapter.TelevisionSeriesViewHolder> {
    private Context context;
    private List<EntertainmentWithTelevisionSeries> entertainmentWithTelevisionSeries;
    private OnTelevisionSeriesClickListener onTelevisionSeriesClickListener;

    public TelevisionSeriesAdapter(Context context, List<EntertainmentWithTelevisionSeries> entertainmentWithTelevisionSeries, OnTelevisionSeriesClickListener listener) {
        this.context = context;
        this.entertainmentWithTelevisionSeries = entertainmentWithTelevisionSeries;
        this.onTelevisionSeriesClickListener = listener;
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

    // ViewHolder class to hold the views for each item
    public static class TelevisionSeriesViewHolder extends RecyclerView.ViewHolder {

        TextView tvSeriesName;
        TextView tvSeriesStatus;

        public TelevisionSeriesViewHolder(View itemView) {
            super(itemView);
            tvSeriesName = itemView.findViewById(R.id.tvSeriesName);
            tvSeriesStatus = itemView.findViewById(R.id.tvSeriesStatus);
        }
    }

    public interface OnTelevisionSeriesClickListener {
        void onTelevisionSeriesClick(EntertainmentWithTelevisionSeries entertainmentWithTelevisionSeries);
    }
}
