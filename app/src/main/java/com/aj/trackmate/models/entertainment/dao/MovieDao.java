package com.aj.trackmate.models.entertainment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.Movie;
import com.aj.trackmate.models.entertainment.MoviePlatform;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    long insert(Movie movie);

    @Update
    int update(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("SELECT * FROM movies WHERE platform = :platform")
    LiveData<List<Movie>> getMoviesByPlatform(String platform);

    @Query("SELECT * FROM movies WHERE genre LIKE '%' || :genre || '%'")
    LiveData<List<Movie>> getMoviesByGenre(String genre);

    @Query("SELECT * FROM movies WHERE wishlist = 1")
    LiveData<List<Movie>> getMoviesInWishlist();

    @Query("SELECT * FROM movies WHERE started = 1")
    LiveData<List<Movie>> getMoviesStarted();

    @Query("SELECT * FROM movies WHERE completed = 1")
    LiveData<List<Movie>> getMoviesCompleted();

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Transaction
    @Query("SELECT * FROM entertainment")
    LiveData<List<EntertainmentWithMovies>> getAllMoviesDetails();

    @Query("SELECT * FROM movies WHERE id = :moviesId LIMIT 1")
    LiveData<Movie> getMoviesById(int moviesId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id = (SELECT entertainmentId FROM movies WHERE id = :movieId AND entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithMovies> getMoviesEntertainmentByMoviesId(int entertainmentId, int movieId);
}
