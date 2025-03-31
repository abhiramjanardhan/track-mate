package com.aj.trackmate.models.entertainment.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.entertainment.Entertainment;
import com.aj.trackmate.models.entertainment.TelevisionSeries;

public class EntertainmentWithTelevisionSeries implements Parcelable {
    @Embedded
    public Entertainment entertainment;

    @Relation(
            parentColumn = "id",
            entityColumn = "entertainmentId",
            entity = TelevisionSeries.class
    )
    public TelevisionSeries televisionSeries;

    public EntertainmentWithTelevisionSeries() {
        entertainment = new Entertainment();
        televisionSeries = new TelevisionSeries();
    }

    protected EntertainmentWithTelevisionSeries(Parcel in) {
        entertainment = in.readParcelable(Entertainment.class.getClassLoader());
        televisionSeries = in.readParcelable(TelevisionSeries.class.getClassLoader());
    }

    public static final Creator<EntertainmentWithTelevisionSeries> CREATOR = new Creator<EntertainmentWithTelevisionSeries>() {
        @Override
        public EntertainmentWithTelevisionSeries createFromParcel(Parcel in) {
            return new EntertainmentWithTelevisionSeries(in);
        }

        @Override
        public EntertainmentWithTelevisionSeries[] newArray(int size) {
            return new EntertainmentWithTelevisionSeries[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(entertainment, flags);
        dest.writeParcelable(televisionSeries, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
