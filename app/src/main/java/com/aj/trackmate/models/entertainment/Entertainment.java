package com.aj.trackmate.models.entertainment;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.aj.trackmate.models.entertainment.converters.EntertainmentCategoryConverter;
import com.aj.trackmate.models.entertainment.converters.LanguageConverter;

@Entity(tableName = "entertainment")
public class Entertainment implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(LanguageConverter.class)
    private Language language;
    @TypeConverters(EntertainmentCategoryConverter.class)
    private EntertainmentCategory category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public EntertainmentCategory getCategory() {
        return category;
    }

    public void setCategory(EntertainmentCategory category) {
        this.category = category;
    }

    // Blank Constructor
    public Entertainment() {
        this.name = "";
        this.language = null;
        this.category = null;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Entertainment(Parcel in) {
        id = in.readInt();
        name = in.readString();
        language = Language.fromLanguage(in.readString());
        category = EntertainmentCategory.fromCategory(in.readString());
    }

    public static final Creator<Entertainment> CREATOR = new Creator<Entertainment>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Entertainment createFromParcel(Parcel in) {
            return new Entertainment(in);
        }

        @Override
        public Entertainment[] newArray(int size) {
            return new Entertainment[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(language.getLanguage());
        dest.writeString(category.getCategory());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
