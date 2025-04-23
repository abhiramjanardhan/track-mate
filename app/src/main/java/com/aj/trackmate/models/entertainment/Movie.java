package com.aj.trackmate.models.entertainment;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.*;
import com.aj.trackmate.models.entertainment.converters.MovieStatusConverter;
import com.aj.trackmate.models.entertainment.converters.MovieGenreConverter;

import java.util.List;

@Entity(
        tableName = "movies",
        foreignKeys = @ForeignKey(
                entity = Entertainment.class,
                parentColumns = "id",
                childColumns = "entertainmentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "entertainmentId")} // Improves query performance
)
public class Movie implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int entertainmentId; // Foreign Key
    @TypeConverters(MovieGenreConverter.class)
    private List<MovieGenre> genre;
    private String platform;
    @TypeConverters(MovieStatusConverter.class)
    private MovieStatus status;
    private boolean favorite;
    private boolean wishlist;
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

    public List<MovieGenre> getGenre() {
        return genre;
    }

    public void setGenre(List<MovieGenre> genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public void setStatus(MovieStatus status) {
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

    // Blank Constructor
    public Movie() {
        wishlist = false;
        started = false;
        completed = false;
        backlog = false;
        favorite = false;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Movie(Parcel in) {
        id = in.readInt();
        entertainmentId = in.readInt();
        status = MovieStatus.fromStatus(in.readString()); // Read status first
        platform = in.readString(); // Read platform second
        genre = in.createStringArrayList().stream()
                .map(MovieGenre::valueOf)
                .toList();
        favorite = in.readByte() != 0;
        wishlist = in.readByte() != 0;
        started = in.readByte() != 0;
        completed = in.readByte() != 0;
        backlog = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(entertainmentId);
        dest.writeString(status.getStatus());  // Write status first
        dest.writeString(platform);  // Write platform second
        dest.writeStringList(genre.stream().map(Enum::name).toList()); // Convert enum list to string list
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeByte((byte) (wishlist ? 1 : 0));
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeByte((byte) (backlog ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
