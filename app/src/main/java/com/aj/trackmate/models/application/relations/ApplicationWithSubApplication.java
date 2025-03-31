package com.aj.trackmate.models.application.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.SubApplication;

import java.util.ArrayList;
import java.util.List;

public class ApplicationWithSubApplication implements Parcelable {
    @Embedded
    public Application application;

    @Relation(
            parentColumn = "id",
            entityColumn = "applicationId"
    )
    public List<SubApplication> subApplications;

    public ApplicationWithSubApplication() {
        application = new Application();
        subApplications = new ArrayList<>();
    }

    protected ApplicationWithSubApplication(Parcel in) {
        application = in.readParcelable(Application.class.getClassLoader());
        subApplications = new ArrayList<>();
        in.readTypedList(subApplications, SubApplication.CREATOR);
    }

    public static final Creator<ApplicationWithSubApplication> CREATOR = new Creator<ApplicationWithSubApplication>() {
        @Override
        public ApplicationWithSubApplication createFromParcel(Parcel in) {
            return new ApplicationWithSubApplication(in);
        }

        @Override
        public ApplicationWithSubApplication[] newArray(int size) {
            return new ApplicationWithSubApplication[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(application, flags);
        dest.writeTypedList(subApplications);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
