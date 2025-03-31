package com.aj.trackmate.models.application;

import java.util.List;

public class ApplicationCategory {
    private String title;
    private String description;
    private boolean canAddSubApplications;
    private List<CategoryDetail> categories;

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

    public boolean isCanAddSubApplications() {
        return canAddSubApplications;
    }

    public void setCanAddSubApplications(boolean canAddSubApplications) {
        this.canAddSubApplications = canAddSubApplications;
    }

    public List<CategoryDetail> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDetail> categories) {
        this.categories = categories;
    }
}
