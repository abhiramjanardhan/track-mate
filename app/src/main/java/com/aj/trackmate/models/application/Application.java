package com.aj.trackmate.models.application;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.*;
import com.aj.trackmate.models.CategoryEnum;
import com.aj.trackmate.models.application.converters.ApplicationCategoryConverter;

@Entity(
        tableName = "application",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "categoryId"), @Index(value = "name", unique = true)} // Improves query performance
)
public class Application implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int categoryId;  // Foreign key to Category
    private String title;
    private String name;
    @TypeConverters(ApplicationCategoryConverter.class)
    private CategoryEnum category;
    private boolean hasSubApplication;
    private String description;
    private boolean visible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public boolean isHasSubApplication() {
        return hasSubApplication;
    }

    public void setHasSubApplication(boolean hasSubApplication) {
        this.hasSubApplication = hasSubApplication;
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
    public Application() {
        name = "";
        title = "";
        description = "";
        hasSubApplication = false;
        category = null;
        visible = true;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Application(Parcel in) {
        id = in.readInt();
        categoryId = in.readInt();
        title = in.readString();
        name = in.readString();
        description = in.readString();
        category = CategoryEnum.valueOf(in.readString());
        hasSubApplication = in.readBoolean();
        visible = in.readBoolean();
    }

    public static final Creator<Application> CREATOR = new Creator<>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Application createFromParcel(Parcel in) {
            return new Application(in);
        }

        @Override
        public Application[] newArray(int size) {
            return new Application[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(categoryId);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category.name());
        dest.writeBoolean(hasSubApplication);
        dest.writeBoolean(visible);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
