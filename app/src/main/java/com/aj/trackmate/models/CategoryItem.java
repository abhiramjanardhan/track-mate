package com.aj.trackmate.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CategoryItem implements Parcelable {
    private String categoryName;
    private List<String> items;
    private List<CategoryItem> subcategories;

    public CategoryItem(String categoryName, List<String> items, List<CategoryItem> subcategories) {
        this.categoryName = categoryName;
        this.items = items;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<String> getItems() {
        return items;
    }

    public List<CategoryItem> getSubcategories() {
        return subcategories;
    }

    public boolean hasSubcategories() {
        return subcategories != null && !subcategories.isEmpty();
    }

    protected CategoryItem(Parcel in) {
        categoryName = in.readString();
        items = in.createStringArrayList();
        subcategories = in.createTypedArrayList(CategoryItem.CREATOR);
    }

    public static final Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel in) {
            return new CategoryItem(in);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryName);
        dest.writeStringList(items);
        dest.writeTypedList(subcategories);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
