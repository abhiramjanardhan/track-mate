package com.aj.trackmate.models.view.games;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.aj.trackmate.database.GameDatabase;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.game.Game;
import com.aj.trackmate.models.game.GameStatus;
import com.aj.trackmate.models.game.Platform;
import com.aj.trackmate.models.game.dao.GameDao;
import com.aj.trackmate.models.game.dao.helpers.AmountWithCurrency;
import com.aj.trackmate.models.game.dao.helpers.StatusCount;
import com.aj.trackmate.models.game.dao.helpers.YearCount;

import java.util.List;

public class GameStatisticsViewModel extends ViewModel {
    private final GameDao gameDao;

    public GameStatisticsViewModel(Context context) {
        GameDatabase db = GameDatabase.getInstance(context);
        gameDao = db.gameDao();
    }

    public LiveData<Integer> getTotalGamesCount(Platform platform) {
        return gameDao.getTotalGamesCount(platform);
    }

    public LiveData<List<AmountWithCurrency>> getTotalAmountSpent(Platform platform) {
        return gameDao.getTotalAmountSpent(platform);
    }

    public LiveData<List<AmountWithCurrency>> getTotalAmountSpentForYear(Platform platform, int year) {
        return gameDao.getTotalAmountSpentForYear(platform, year);
    }

    public LiveData<List<StatusCount>> getGameCountByStatus(Platform platform) {
        return gameDao.getGameCountByStatus(platform);
    }

    public LiveData<List<YearCount>> getGameCountByYear(Platform platform) {
        return gameDao.getGameCountByYear(platform);
    }

    public LiveData<List<Game>> getGamesByCurrency(Platform platform, Currency currency) {
        return gameDao.getGamesByCurrency(platform, currency);
    }

    public LiveData<List<Game>> getGamesByStatus(Platform platform, GameStatus status) {
        return gameDao.getGamesByStatus(platform, status);
    }

    public LiveData<List<Game>> getGamesByYear(Platform platform, int year) {
        return gameDao.getGamesByYear(platform, year);
    }
}
