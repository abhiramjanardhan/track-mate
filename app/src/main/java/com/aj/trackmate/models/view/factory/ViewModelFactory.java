package com.aj.trackmate.models.view.factory;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.aj.trackmate.models.view.MainViewModel;
import com.aj.trackmate.models.view.games.GameStatisticsViewModel;
import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    private final LifecycleOwner lifecycleOwner;

    public ViewModelFactory(Context context, LifecycleOwner lifecycleOwner) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        this.lifecycleOwner  = lifecycleOwner;
    }

    @Override
    public <T extends ViewModel> @NotNull T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(context, lifecycleOwner);
        }
        if (modelClass.isAssignableFrom(GameStatisticsViewModel.class)) {
            return (T) new GameStatisticsViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
