package com.aj.trackmate.database.repositories;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.dao.ApplicationDao;
import com.aj.trackmate.models.application.dao.CategoryDao;
import com.aj.trackmate.models.application.dao.SubApplicationDao;
import com.aj.trackmate.models.application.relations.ApplicationWithSubApplication;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final ApplicationDao applicationDao;
    private final SubApplicationDao subApplicationDao;

    public CategoryRepository(Context context) {
        ApplicationDatabase db = ApplicationDatabase.getInstance(context);
        this.categoryDao = db.categoryDao();
        this.applicationDao = db.applicationDao();
        this.subApplicationDao = db.subApplicationDao();
    }

    public LiveData<List<CategoryWithApplicationsAndSubApplications>> getVisibleCategoryTree() {
        MutableLiveData<List<CategoryWithApplicationsAndSubApplications>> liveData = new MutableLiveData<>();
        Log.d("Application Get", "List retrieved");

        Executors.newSingleThreadExecutor().execute(() -> {
            List<CategoryWithApplicationsAndSubApplications> result = new ArrayList<>();
            List<Category> categories = categoryDao.getVisibleCategoriesSync(); // synchronous version

            for (Category category : categories) {
                CategoryWithApplicationsAndSubApplications item = new CategoryWithApplicationsAndSubApplications();
                item.category = category;

                List<Application> visibleApps = applicationDao.getVisibleApplicationsForCategory(category.getId());
                List<ApplicationWithSubApplication> appWithSubs = new ArrayList<>();

                for (Application app : visibleApps) {
                    ApplicationWithSubApplication aws = new ApplicationWithSubApplication();
                    aws.application = app;
                    aws.subApplications = subApplicationDao.getVisibleSubApplicationsForApplication(app.getId());
                    appWithSubs.add(aws);
                }

                item.applications = appWithSubs;
                result.add(item);
            }

            liveData.postValue(result);
        });

        return liveData;
    }
}
