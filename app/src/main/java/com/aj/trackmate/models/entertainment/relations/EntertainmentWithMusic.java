package com.aj.trackmate.models.entertainment.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.Music;

public class EntertainmentWithMusic implements Parcelable {
    @Embedded
    public Entertainment entertainment;

    @Relation(
            parentColumn = "id",
            entityColumn = "entertainmentId",
            entity = Music.class
    )
    public Music music;

    public EntertainmentWithMusic() {
        entertainment = new Entertainment();
        music = new Music();
    }

    protected EntertainmentWithMusic(Parcel in) {
        entertainment = in.readParcelable(Entertainment.class.getClassLoader());
        music = in.readParcelable(Music.class.getClassLoader());
    }

    public static final Creator<EntertainmentWithMusic> CREATOR = new Creator<EntertainmentWithMusic>() {
        @Override
        public EntertainmentWithMusic createFromParcel(Parcel in) {
            return new EntertainmentWithMusic(in);
        }

        @Override
        public EntertainmentWithMusic[] newArray(int size) {
            return new EntertainmentWithMusic[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(entertainment, flags);
        dest.writeParcelable(music, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
