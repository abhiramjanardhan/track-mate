package com.aj.trackmate.models.application;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sub_application",
        foreignKeys = @ForeignKey(
                entity = Application.class,
                parentColumns = "id",
                childColumns = "applicationId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "applicationId")} // Improves query performance
)
public class SubApplication implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int applicationId; // Foreign Key
    private String name;
    private String description;
    private boolean readOnly;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    // Blank Constructor
    public SubApplication() {
        name = "";
        description = "";
        readOnly = false;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public SubApplication(Parcel in) {
        id = in.readInt();
        applicationId = in.readInt();
        name = in.readString();
        description = in.readString();
        readOnly = in.readBoolean();
    }

    public static final Creator<SubApplication> CREATOR = new Creator<SubApplication>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public SubApplication createFromParcel(Parcel in) {
            return new SubApplication(in);
        }

        @Override
        public SubApplication[] newArray(int size) {
            return new SubApplication[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(applicationId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeBoolean(readOnly);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
