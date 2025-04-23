package com.aj.trackmate.models.entertainment;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.*;

@Entity(
        tableName = "music",
        foreignKeys = @ForeignKey(
                entity = Entertainment.class,
                parentColumns = "id",
                childColumns = "entertainmentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "entertainmentId")} // Improves query performance
)
public class Music implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int entertainmentId; // Foreign Key
    private String artist;
    private String album;
    private String platform;
    private boolean favorite;

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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Blank Constructor
    public Music() {
        this.album = "";
        this.artist = "";
        this.favorite = false;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Music(Parcel in) {
        id = in.readInt();
        entertainmentId = in.readInt();
        artist = in.readString();
        album = in.readString();
        platform = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(entertainmentId);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(platform);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
