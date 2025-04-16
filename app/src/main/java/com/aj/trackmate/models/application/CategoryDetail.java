package com.aj.trackmate.models.application;

import java.util.List;

public class CategoryDetail {
    private String title;
    private String description;
    private boolean visible;
    private List<SubCategoryDetail> subCategories;

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

    public List<SubCategoryDetail> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategoryDetail> subCategories) {
        this.subCategories = subCategories;
    }
}
