package com.aj.trackmate.models.game.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.relations.GameWithDownloadableContent;

import java.util.List;

@Dao
public interface GameDao {

    @Insert
    long insert(Game game);

    @Update
    int update(Game game);

    @Delete
    void delete(Game game);

    @Insert
    long insertDLC(DownloadableContent dlc);

    @Update
    int updateDLC(DownloadableContent dlc);

    @Delete
    void deleteDLC(DownloadableContent dlc);

    @Query("SELECT * FROM games WHERE platform = :platform")
    LiveData<List<Game>> getGamesByPlatform(Platform platform);

    @Query("SELECT * FROM games WHERE wishlist = 1")
    LiveData<List<Game>> getGamesInWishlist();

    @Query("SELECT * FROM games WHERE started = 1")
    LiveData<List<Game>> getGamesStarted();

    @Query("SELECT * FROM games WHERE completed = 1")
    LiveData<List<Game>> getGamesCompleted();

    @Query("SELECT * FROM games")
    LiveData<List<Game>> getAllGames();

    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    LiveData<Game> getGameById(int gameId);

    @Query("SELECT * FROM game_downloadable_content WHERE id = :dlcId and gameId = :gameId LIMIT 1")
    LiveData<DownloadableContent> getDLCById(int dlcId, int gameId);

    @Transaction
    @Query("SELECT * FROM games")
    LiveData<List<GameWithDownloadableContent>> getAllGamesWithDLCs();

    @Transaction
    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    LiveData<GameWithDownloadableContent> getGameWithDLCsForGameId(int gameId);

    @Query("SELECT * FROM game_downloadable_content WHERE gameId = :gameId")
    LiveData<List<DownloadableContent>> getAllDLCsForGameId(int gameId);

    @Transaction
    @Query("SELECT * FROM games WHERE platform = :platform")
    LiveData<List<GameWithDownloadableContent>> getGamesWithDLCsByPlatform(Platform platform);
}
