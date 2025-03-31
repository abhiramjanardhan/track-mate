package com.aj.trackmate.models.entertainment.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.Movie;

import java.util.ArrayList;
import java.util.List;

public class EntertainmentWithMovies implements Parcelable {
    @Embedded
    public Entertainment entertainment;

    @Relation(
            parentColumn = "id",
            entityColumn = "entertainmentId",
            entity = Movie.class
    )
    public Movie movie;

    public EntertainmentWithMovies() {
        entertainment = new Entertainment();
        movie = new Movie();
    }

    protected EntertainmentWithMovies(Parcel in) {
        entertainment = in.readParcelable(Entertainment.class.getClassLoader());
        movie = in.readParcelable(Movie.class.getClassLoader());
    }

    public static final Creator<EntertainmentWithMovies> CREATOR = new Creator<EntertainmentWithMovies>() {
        @Override
        public EntertainmentWithMovies createFromParcel(Parcel in) {
            return new EntertainmentWithMovies(in);
        }

        @Override
        public EntertainmentWithMovies[] newArray(int size) {
            return new EntertainmentWithMovies[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(entertainment, flags);
        dest.writeParcelable(movie, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
