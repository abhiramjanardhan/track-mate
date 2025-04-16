package com.aj.trackmate.models.application;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "category", indices = {@Index(value = "title", unique = true)})
public class Category implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private boolean visible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // Blank Constructor
    public Category() {
        title = "";
        description = "";
        visible = true;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Category(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        visible = in.readBoolean();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeBoolean(visible);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
