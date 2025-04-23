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
import com.aj.trackmate.models.entertainment.Movie;
import com.aj.trackmate.models.entertainment.MovieStatus;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private List<EntertainmentWithMovies> entertainmentWithMovies;
    private final OnMovieClickListener onMovieClickListener;
    private final OnMovieLongClickListener onMovieLongClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public MovieAdapter(Context context, List<EntertainmentWithMovies> entertainmentWithMovies, OnMovieClickListener listener, OnMovieLongClickListener onMovieLongClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.entertainmentWithMovies = entertainmentWithMovies;
        this.onMovieClickListener = listener;
        this.onMovieLongClickListener = onMovieLongClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
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

        // handle item long press
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("ItemClicked", "Item pressed: " + entertainment.getName());
            if (onMovieLongClickListener != null) {
                onMovieLongClickListener.onMovieClick(v, position);
            }
            return true; // Long press handled
        });

        boolean isFavorite = movie.isFavorite(); // From your model

        holder.favoriteStar.setImageResource(
                isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24
        );

        holder.favoriteStar.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteStarClick(movie, !isFavorite);
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

    public void sortMovie() {
        // Default sort based on status priority
        entertainmentWithMovies.sort((a, b) -> {
            int aPriority = MovieStatus.getStatusPriority().getOrDefault(a.movie.getStatus(), Integer.MAX_VALUE);
            int bPriority = MovieStatus.getStatusPriority().getOrDefault(b.movie.getStatus(), Integer.MAX_VALUE);
            return Integer.compare(aPriority, bPriority);
        });
        updateMovies(entertainmentWithMovies);
    }

    // ViewHolder class to hold the views for each item
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView movieName;
        TextView movieStatus;
        ImageView favoriteStar;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieName = itemView.findViewById(R.id.movieName);
            movieStatus = itemView.findViewById(R.id.movieStatus);
            favoriteStar = itemView.findViewById(R.id.favoriteStar);
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(EntertainmentWithMovies entertainmentWithMovies);
    }

    public interface OnMovieLongClickListener {
        void onMovieClick(View view, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteStarClick(Movie movie, boolean isFavorite);
    }
}
