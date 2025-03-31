package com.aj.trackmate.models.view;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.aj.trackmate.database.ApplicationDatabase;
import com.aj.trackmate.managers.ApplicationDataManager;
import com.aj.trackmate.models.application.dao.CategoryDao;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final CategoryDao categoryDao;
    private LiveData<List<CategoryWithApplicationsAndSubApplications>> categoryLiveData;
    private final MutableLiveData<Boolean> isDataInitialized = new MutableLiveData<>(false);
    private final Context context;
    private final LifecycleOwner lifecycleOwner;

    public MainViewModel(Context context, LifecycleOwner lifecycleOwner) {
        super();
        ApplicationDatabase db = ApplicationDatabase.getInstance(context);
        categoryDao = db.categoryDao();
        this.context = context.getApplicationContext(); // Store the context safely
        this.lifecycleOwner = lifecycleOwner;
    }

    public void initializeData(Runnable onComplete) {
        ApplicationDataManager.getInstance(context).initializeData(lifecycleOwner, () -> {
            categoryLiveData = categoryDao.getAllCategoriesWithApplicationsAndSubApplications();
            isDataInitialized.postValue(true); // Notify that data is ready
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public LiveData<List<CategoryWithApplicationsAndSubApplications>> getCategories() {
        return categoryLiveData;
    }

    public LiveData<Boolean> getIsDataInitialized() {
        return isDataInitialized;
    }
}
