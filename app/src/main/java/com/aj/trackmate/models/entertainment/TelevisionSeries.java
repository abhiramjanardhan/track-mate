package com.aj.trackmate.models.entertainment;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.converters.*;

import java.util.List;

@Entity(
        tableName = "television_series",
        foreignKeys = @ForeignKey(
                entity = Entertainment.class,
                parentColumns = "id",
                childColumns = "entertainmentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "entertainmentId")} // Improves query performance
)
public class TelevisionSeries implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int entertainmentId; // Foreign Key
    @TypeConverters(TelevisionSeriesGenreConverter.class)
    private List<TelevisionSeriesGenre> genre;
    private String platform;
    @TypeConverters(TelevisionSeriesStatusConverter.class)
    private TelevisionSeriesStatus status;
    private boolean favorite;
    private boolean wishlist;
    private int totalSeasons;
    private int currentSeason;
    private int totalEpisodesCount;
    private int currentEpisodeNumber;
    private boolean started;
    private boolean completed;
    private boolean backlog;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntertainmentId() {
        return entertainmentId;
    }

    public void setEntertainmentId(int entertainmentId) {
        this.entertainmentId = entertainmentId;
    }

    public List<TelevisionSeriesGenre> getGenre() {
        return genre;
    }

    public void setGenre(List<TelevisionSeriesGenre> genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public TelevisionSeriesStatus getStatus() {
        return status;
    }

    public void setStatus(TelevisionSeriesStatus status) {
        this.status = status;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isWishlist() {
        return wishlist;
    }

    public void setWishlist(boolean wishlist) {
        this.wishlist = wishlist;
    }

    public int getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(int totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getTotalEpisodesCount() {
        return totalEpisodesCount;
    }

    public void setTotalEpisodesCount(int totalEpisodesCount) {
        this.totalEpisodesCount = totalEpisodesCount;
    }

    public int getCurrentEpisodeNumber() {
        return currentEpisodeNumber;
    }

    public void setCurrentEpisodeNumber(int currentEpisodeNumber) {
        this.currentEpisodeNumber = currentEpisodeNumber;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isBacklog() {
        return backlog;
    }

    public void setBacklog(boolean backlog) {
        this.backlog = backlog;
    }

    public TelevisionSeries() {
        favorite = false;
        wishlist = false;
        started = false;
        completed = false;
        backlog = false;
        totalSeasons = 0;
        currentSeason = 0;
        currentEpisodeNumber = 0;
        totalEpisodesCount = 0;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public TelevisionSeries(Parcel in) {
        id = in.readInt();
        entertainmentId = in.readInt();
        totalSeasons = in.readInt();
        currentSeason = in.readInt();
        totalEpisodesCount = in.readInt();
        currentEpisodeNumber = in.readInt();
        platform = in.readString();
        genre = in.createStringArrayList().stream()
                .map(TelevisionSeriesGenre::valueOf)
                .toList();
        favorite = in.readByte() != 0;
        wishlist = in.readByte() != 0;
        started = in.readByte() != 0;
        completed = in.readByte() != 0;
        backlog = in.readByte() != 0;
        status = TelevisionSeriesStatus.valueOf(in.readString());
    }

    public static final Creator<TelevisionSeries> CREATOR = new Creator<TelevisionSeries>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public TelevisionSeries createFromParcel(Parcel in) {
            return new TelevisionSeries(in);
        }

        @Override
        public TelevisionSeries[] newArray(int size) {
            return new TelevisionSeries[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(entertainmentId);
        dest.writeInt(totalSeasons);
        dest.writeInt(currentSeason);
        dest.writeInt(totalEpisodesCount);
        dest.writeInt(currentEpisodeNumber);
        dest.writeString(platform);
        dest.writeStringList(genre.stream().map(Enum::name).toList());
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeByte((byte) (wishlist ? 1 : 0));
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeByte((byte) (backlog ? 1 : 0));
        dest.writeString(status.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
