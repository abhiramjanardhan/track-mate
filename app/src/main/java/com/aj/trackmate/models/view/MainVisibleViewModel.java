package com.aj.trackmate.models.view;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.aj.trackmate.database.repositories.CategoryRepository;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;
import com.aj.trackmate.notifiers.SettingsUpdateNotifier;

import java.util.ArrayList;
import java.util.List;

public class MainVisibleViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<CategoryWithApplicationsAndSubApplications>> categories = new MutableLiveData<>();

    public MainVisibleViewModel(Context context) {
        repository = new CategoryRepository(context);
        categories.setValue(new ArrayList<>()); // Optional init
        fetchData();

        // Observe for visibility updates
        SettingsUpdateNotifier.getVisibilityUpdates().observeForever(updated -> {
            if (Boolean.TRUE.equals(updated)) {
                fetchData();
                SettingsUpdateNotifier.resetVisibility();
            }
        });
    }

    public LiveData<List<CategoryWithApplicationsAndSubApplications>> getCategories() {
        return categories;
    }

    public void fetchData() {
        repository.getVisibleCategoryTree().observeForever(categories::postValue);
    }
}
