package com.aj.trackmate.models.application.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.CategoryEnum;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.relations.ApplicationWithSubApplication;

import java.util.List;

@Dao
public interface ApplicationDao {
    @Insert
    long insert(Application application);

    @Update
    int update(Application application);

    @Delete
    void delete(Application application);

    @Query("SELECT * FROM application WHERE category = :category LIMIT 1")
    LiveData<Application> getApplicationByCategory(CategoryEnum category);

    @Query("SELECT * FROM application WHERE category = :category LIMIT 1")
    Application getApplicationByCategorySync(CategoryEnum category);

    @Query("SELECT * FROM application WHERE title LIKE '%' || :title || '%'")
    LiveData<List<Application>> getApplicationsByTitle(String title);

    @Query("SELECT * FROM application WHERE name = :name")
    LiveData<Application> getApplicationsByName(String name);

    @Query("SELECT * FROM application WHERE name = :name")
    Application getApplicationsByNameSync(String name);

    @Query("SELECT * FROM application WHERE categoryId = :categoryId")
    LiveData<List<Application>> getApplicationsByCategory(int categoryId);

    @Query("SELECT * FROM application WHERE hasSubApplication = 1")
    LiveData<List<Application>> getApplicationsWithSubApplications();

    @Query("SELECT * FROM application")
    LiveData<List<Application>> getAllApplications();

    @Query("SELECT * FROM application WHERE id = :applicationId LIMIT 1")
    LiveData<Application> getApplicationById(int applicationId);

    @Transaction
    @Query("SELECT * FROM application WHERE id = :applicationId")
    LiveData<ApplicationWithSubApplication> getApplicationDetailsByApplicationId(int applicationId);

    @Transaction
    @Query("SELECT * FROM application")
    LiveData<List<ApplicationWithSubApplication>> getAllApplicationsWithSubApplications();
}
