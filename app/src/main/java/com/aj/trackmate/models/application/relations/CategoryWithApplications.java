package com.aj.trackmate.models.application.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryWithApplications implements Parcelable {
    @Embedded
    public Category category;

    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<Application> applications;

    public CategoryWithApplications() {
        category = new Category();
        applications = new ArrayList<>();
    }

    protected CategoryWithApplications(Parcel in) {
        category = in.readParcelable(Category.class.getClassLoader());
        applications = new ArrayList<>();
        in.readTypedList(applications, Application.CREATOR);
    }

    public static final Creator<CategoryWithApplications> CREATOR = new Creator<CategoryWithApplications>() {
        @Override
        public CategoryWithApplications createFromParcel(Parcel in) {
            return new CategoryWithApplications(in);
        }

        @Override
        public CategoryWithApplications[] newArray(int size) {
            return new CategoryWithApplications[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(category, flags);
        dest.writeTypedList(applications);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
