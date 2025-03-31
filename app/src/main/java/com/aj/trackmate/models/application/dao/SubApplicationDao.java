package com.aj.trackmate.models.application.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aj.trackmate.models.application.SubApplication;

import java.util.List;

@Dao
public interface SubApplicationDao {
    @Insert
    long insert(SubApplication subApplication);

    @Update
    int update(SubApplication subApplication);

    @Delete
    void delete(SubApplication subApplication);

    @Query("SELECT * FROM sub_application WHERE applicationId = :applicationId")
    LiveData<List<SubApplication>> getSubApplicationsByApplicationId(int applicationId);

    @Query("SELECT * FROM sub_application WHERE name = :name")
    LiveData<SubApplication> getSubApplicationsByName(String name);

    @Query("SELECT * FROM sub_application WHERE name LIKE '%' || :name || '%'")
    SubApplication getSubApplicationsByNameSync(String name);

    @Query("SELECT * FROM sub_application")
    LiveData<List<SubApplication>> getAllSubApplications();

    @Query("SELECT * FROM sub_application WHERE id = :subApplicationId LIMIT 1")
    LiveData<SubApplication> getSubApplicationById(int subApplicationId);
}
