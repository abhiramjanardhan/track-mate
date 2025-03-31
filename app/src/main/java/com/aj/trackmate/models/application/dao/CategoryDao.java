package com.aj.trackmate.models.application.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.relations.CategoryWithApplications;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Category category);

    @Update
    int update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM category WHERE title = :title")
    LiveData<Category> getCategoryByTitle(String title);

    @Query("SELECT * FROM category WHERE title = :title")
    Category getCategoryByTitleSync(String title);

    @Transaction
    @Query("SELECT * FROM category")
    LiveData<List<CategoryWithApplications>> getAllCategoriesWithApplications();

    @Transaction
    @Query("SELECT * FROM category")
    LiveData<List<CategoryWithApplicationsAndSubApplications>> getAllCategoriesWithApplicationsAndSubApplications();

    @Transaction
    @Query("SELECT * FROM category")
    List<CategoryWithApplicationsAndSubApplications> getAllCategoriesWithApplicationsAndSubApplicationsSync();

    @Transaction
    @Query("SELECT * FROM category WHERE id = :categoryId")
    LiveData<CategoryWithApplications> getApplicationsByCategory(int categoryId);

    @Transaction
    @Query("SELECT * FROM category WHERE id = :categoryId")
    LiveData<CategoryWithApplicationsAndSubApplications> getCategoryWithAppsAndSubApps(int categoryId);
}
