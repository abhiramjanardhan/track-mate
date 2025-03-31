package com.aj.trackmate.models.entertainment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.TelevisionSeries;
import com.aj.trackmate.models.entertainment.TelevisionSeriesPlatform;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;

import java.util.List;

@Dao
public interface TelevisionSeriesDao {
    @Insert
    long insert(TelevisionSeries televisionSeries);

    @Update
    int update(TelevisionSeries televisionSeries);

    @Delete
    void delete(TelevisionSeries televisionSeries);

    @Query("SELECT * FROM television_series WHERE platform = :platform")
    LiveData<List<TelevisionSeries>> getTelevisionSeriesByPlatform(String platform);

    @Query("SELECT * FROM television_series WHERE genre LIKE '%' || :genre || '%'")
    LiveData<List<TelevisionSeries>> getTelevisionSeriesByGenre(String genre);

    @Query("SELECT * FROM television_series WHERE wishlist = 1")
    LiveData<List<TelevisionSeries>> getTelevisionSeriesInWishlist();

    @Query("SELECT * FROM television_series WHERE started = 1")
    LiveData<List<TelevisionSeries>> getTelevisionSeriesStarted();

    @Query("SELECT * FROM television_series WHERE completed = 1")
    LiveData<List<TelevisionSeries>> getTelevisionSeriesCompleted();

    @Query("SELECT * FROM television_series")
    LiveData<List<TelevisionSeries>> getAllTelevisionSeries();

    @Transaction
    @Query("SELECT * FROM entertainment")
    LiveData<List<EntertainmentWithTelevisionSeries>> getAllTelevisionSeriesDetails();

    @Query("SELECT * FROM television_series WHERE id = :televisionSeriesId LIMIT 1")
    LiveData<TelevisionSeries> getTelevisionSeriesById(int televisionSeriesId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id = (SELECT entertainmentId FROM television_series WHERE id = :televisionSeriesId AND entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithTelevisionSeries> getTelevisionSeriesEntertainmentByTelevisionSeriesId(int entertainmentId, int televisionSeriesId);
}
