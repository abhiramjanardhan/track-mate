package com.aj.trackmate.models.entertainment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.EntertainmentCategory;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMovies;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMusic;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithTelevisionSeries;

import java.util.List;

@Dao
public interface EntertainmentDao {
    @Insert
    long insert(Entertainment entertainment);

    @Update
    int update(Entertainment entertainment);

    @Delete
    void delete(Entertainment entertainment);

    @Query("SELECT * FROM entertainment")
    LiveData<List<Entertainment>> getAllEntertainment();

    @Query("SELECT * FROM entertainment WHERE id = :entertainmentId LIMIT 1")
    LiveData<Entertainment> getEntertainmentById(int entertainmentId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id in (SELECT entertainmentId from movies where entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithMovies> getMoviesEntertainmentByEntertainmentId(int entertainmentId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id in (SELECT entertainmentId from music where entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithMusic> getMusicEntertainmentByEntertainmentId(int entertainmentId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id in (SELECT entertainmentId from television_series where entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithTelevisionSeries> getTelevisionSeriesEntertainmentByEntertainmentId(int entertainmentId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE category = :category AND id IN (SELECT entertainmentId FROM movies WHERE platform = :platform)")
    LiveData<List<EntertainmentWithMovies>> getAllEntertainmentForMovies(EntertainmentCategory category, String platform);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE category = :category AND id IN (SELECT entertainmentId FROM music WHERE platform = :platform)")
    LiveData<List<EntertainmentWithMusic>> getAllEntertainmentForMusic(EntertainmentCategory category, String platform);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE category = :category AND id IN (SELECT entertainmentId FROM television_series WHERE platform = :platform)")
    LiveData<List<EntertainmentWithTelevisionSeries>> getAllEntertainmentForTelevisionSeries(EntertainmentCategory category, String platform);
}
