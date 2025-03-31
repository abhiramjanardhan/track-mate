package com.aj.trackmate.models.entertainment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.Music;
import com.aj.trackmate.models.entertainment.relations.EntertainmentWithMusic;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    long insert(Music music);

    @Update
    int update(Music music);

    @Delete
    void delete(Music music);

    @Query("SELECT * FROM music WHERE platform = :platform")
    LiveData<List<Music>> getMoviesByPlatform(String platform);

    @Query("SELECT * FROM music WHERE artist = 1")
    LiveData<List<Music>> getMusicByArtist();

    @Query("SELECT * FROM music WHERE album = 1")
    LiveData<List<Music>> getMusicByAlbum();

    @Query("SELECT * FROM music")
    LiveData<List<Music>> getAllMusic();

    @Transaction
    @Query("SELECT * FROM entertainment")
    LiveData<List<EntertainmentWithMusic>> getAllMusicDetails();

    @Query("SELECT * FROM music WHERE id = :musicId LIMIT 1")
    LiveData<Music> getMusicById(int musicId);

    @Transaction
    @Query("SELECT * FROM entertainment WHERE id = (SELECT entertainmentId FROM music WHERE id = :musicId AND entertainmentId = :entertainmentId)")
    LiveData<EntertainmentWithMusic> getMusicEntertainmentByMusicId(int entertainmentId, int musicId);
}
