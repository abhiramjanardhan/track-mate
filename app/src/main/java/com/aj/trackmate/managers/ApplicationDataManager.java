package com.aj.trackmate.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.CategoryEnum;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.ApplicationData;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.models.application.dao.ApplicationDao;
import com.aj.trackmate.models.application.dao.CategoryDao;
import com.aj.trackmate.models.application.dao.SubApplicationDao;
import com.aj.trackmate.utils.ApplicationDataProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationDataManager {
    private final Context context;
    private static ApplicationDataManager instance;
    private boolean isDataInitialized = false;
    private final ExecutorService executorService;
    private final CategoryDao categoryDao;
    private final ApplicationDao applicationDao;
    private final SubApplicationDao subApplicationDao;

    private ApplicationDataManager(Context context) {
        // Private constructor to prevent instantiation
        this.context = context;
        executorService = Executors.newSingleThreadExecutor();
        ApplicationDatabase db = ApplicationDatabase.getInstance(context);
        categoryDao = db.categoryDao();
        applicationDao = db.applicationDao();
        subApplicationDao = db.subApplicationDao();
    }

    public static synchronized ApplicationDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationDataManager(context);
        }
        return instance;
    }

    private void deleteExistingData(LifecycleOwner lifecycleOwner) {
        executorService.execute(() -> {
            ApplicationDatabase db = ApplicationDatabase.getInstance(context);
            db.clearAllTables();  // This clears all data in the tables but keeps the schema
        });
    }

    public void initializeData(LifecycleOwner lifecycleOwner, Runnable onComplete) {
        if (!isDataInitialized) {
            // Run data update in a separate thread
            executorService.execute(() -> {
//                deleteExistingData(lifecycleOwner);
                updateApplications(onComplete);
            });
        }
    }

    private void runOnUiThread(Runnable action) {
        // Run on main thread if needed, e.g., using Activity or Application context
        new Handler(Looper.getMainLooper()).post(action);
    }

    private void updateApplications(Runnable onComplete) {
        ApplicationData applicationData = ApplicationDataProvider.loadApplicationData(context);

        applicationData.getApplications().forEach(applicationCategory -> {
            Log.d("Load", "Applications is there: " + applicationCategory.getTitle());
            applicationCategory.getCategories().forEach(categoryDetail -> {
                Log.d("Load", "Applications Category is there: " + categoryDetail.getTitle());
            });
        });

        if (applicationData == null || applicationData.getApplications() == null) {
            Log.e("Application", "No data found in JSON.");
            return;
        }

        applicationData.getApplications().forEach(applicationCategory -> {
            if (applicationCategory.getCategories() == null) return;

            Category existingCategory = categoryDao.getCategoryByTitleSync(applicationCategory.getTitle());

            int categoryId;
            if (existingCategory != null) {
                Log.d("Application", "Category already exists: " + existingCategory.getTitle());
                categoryId = existingCategory.getId();
            } else {
                Log.d("Application", "Adding Category: " + applicationCategory.getTitle());

                Category category = new Category();
                category.setTitle(applicationCategory.getTitle());
                category.setDescription(applicationCategory.getDescription());
                categoryId = (int) categoryDao.insert(category);
            }

            if (categoryId == -1) {
                Log.e("Application", "Category is not present: " + applicationCategory.getTitle());
                return;
            }

            applicationCategory.getCategories().forEach(categoryDetail -> {
                if (categoryDetail.getSubCategories() == null) return;
                CategoryEnum category = CategoryEnum.getCategoryByItem(categoryDetail.getTitle());

                // only process the existing applications
                if (category != null) {
                    Application existingApplication = applicationDao.getApplicationsByNameSync(categoryDetail.getTitle());

                    if (existingApplication != null) {
                        Log.d("Application", "Application already exists: " + categoryDetail.getTitle());

                        categoryDetail.getSubCategories().forEach(subCategoryDetail -> {
                            SubApplication existingSubApplication = subApplicationDao.getSubApplicationsByNameSync(subCategoryDetail.getTitle());

                            if (existingSubApplication != null) {
                                Log.d("Application", "Sub Application already exists: " + subCategoryDetail.getTitle());
                            } else {
                                Log.d("Application", "Sub Application does not exists: " + subCategoryDetail.getTitle());

                                SubApplication subApplication = new SubApplication();
                                subApplication.setApplicationId(existingApplication.getId());
                                subApplication.setName(subCategoryDetail.getTitle());
                                subApplication.setDescription(subCategoryDetail.getDescription());
                                subApplication.setReadOnly(subCategoryDetail.isReadOnly());

                                subApplicationDao.insert(subApplication);
                            }
                        });
                    } else {
                        Log.d("Application", "Adding Application: " + categoryDetail.getTitle());

                        Application application = new Application();
                        application.setCategoryId(categoryId);
                        application.setName(categoryDetail.getTitle());
                        application.setTitle(applicationCategory.getTitle());
                        application.setDescription(categoryDetail.getDescription());
                        application.setCategory(category);
                        application.setHasSubApplication(applicationCategory.isCanAddSubApplications());

                        applicationDao.insert(application);

                        application = applicationDao.getApplicationsByNameSync(categoryDetail.getTitle());
                        int applicationId = application.getId();

                        if (applicationId == -1) {
                            Log.e("Application", "Application is not present" + application.getName());
                            return;
                        }

                        Log.d("Application", "Application Id: " + applicationId);

                        categoryDetail.getSubCategories().forEach(subCategoryDetail -> {
                            Log.d("Application", "Adding Sub Application: " + subCategoryDetail.getTitle());

                            SubApplication subApplication = new SubApplication();
                            subApplication.setApplicationId(applicationId);
                            subApplication.setName(subCategoryDetail.getTitle());
                            subApplication.setDescription(subCategoryDetail.getDescription());
                            subApplication.setReadOnly(subCategoryDetail.isReadOnly());

                            subApplicationDao.insert(subApplication);
                        });
                    }
                }
            });
        });

        // Mark data as initialized
        isDataInitialized = true;

        // Notify completion on the main thread
        runOnUiThread(onComplete);
    }
}
