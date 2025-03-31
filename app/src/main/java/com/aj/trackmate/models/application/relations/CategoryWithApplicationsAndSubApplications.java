package com.aj.trackmate.models.application.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryWithApplicationsAndSubApplications implements Parcelable {
    @Embedded
    public Category category;

    @Relation(
            entity = Application.class,
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<ApplicationWithSubApplication> applications;

    public CategoryWithApplicationsAndSubApplications() {
        category = new Category();
        applications = new ArrayList<>();
    }

    protected CategoryWithApplicationsAndSubApplications(Parcel in) {
        category = in.readParcelable(Category.class.getClassLoader());
        applications = new ArrayList<>();
        in.readTypedList(applications, ApplicationWithSubApplication.CREATOR);
    }

    public static final Creator<CategoryWithApplicationsAndSubApplications> CREATOR = new Creator<CategoryWithApplicationsAndSubApplications>() {
        @Override
        public CategoryWithApplicationsAndSubApplications createFromParcel(Parcel in) {
            return new CategoryWithApplicationsAndSubApplications(in);
        }

        @Override
        public CategoryWithApplicationsAndSubApplications[] newArray(int size) {
            return new CategoryWithApplicationsAndSubApplications[size];
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
