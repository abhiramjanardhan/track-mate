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
import com.aj.trackmate.models.entertainment.Movie;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<EntertainmentWithMovies> entertainmentWithMovies;
    private OnMovieClickListener onMovieClickListener;

    public MovieAdapter(Context context, List<EntertainmentWithMovies> entertainmentWithMovies, OnMovieClickListener listener) {
        this.context = context;
        this.entertainmentWithMovies = entertainmentWithMovies;
        this.onMovieClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        // Get the movie at the current position
        EntertainmentWithMovies movies = entertainmentWithMovies.get(position);
        Movie movie = movies.movie;
        Entertainment entertainment = movies.entertainment;

        Log.d("Items", "Loaded: " + entertainment.getName());

        // Bind data to the view components
        holder.movieName.setText(entertainment.getName());
        holder.movieStatus.setText(movie.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + entertainment.getName());
            if (onMovieClickListener != null) {
                onMovieClickListener.onMovieClick(movies);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entertainmentWithMovies.size();
    }

    public void updateMovies(List<EntertainmentWithMovies> newMovies) {
        this.entertainmentWithMovies = newMovies;
        notifyDataSetChanged();
    }

    public void removeMovie(int position) {
        if (position >= 0 && position < entertainmentWithMovies.size()) {
            entertainmentWithMovies.remove(position);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder class to hold the views for each item
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView movieName;
        TextView movieStatus;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieName = itemView.findViewById(R.id.movieName);
            movieStatus = itemView.findViewById(R.id.movieStatus);
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(EntertainmentWithMovies entertainmentWithMovies);
    }
}
