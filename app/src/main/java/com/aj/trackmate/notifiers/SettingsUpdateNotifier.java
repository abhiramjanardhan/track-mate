package com.aj.trackmate.notifiers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsUpdateNotifier {
    private static final MutableLiveData<Boolean> visibilityUpdated = new MutableLiveData<>(false);
    private static final MutableLiveData<Boolean> canAddSubApplicationUpdated = new MutableLiveData<>(false);

    public static LiveData<Boolean> getVisibilityUpdates() {
        return visibilityUpdated;
    }

    public static void notifyVisibilityUpdated() {
        visibilityUpdated.postValue(true);
    }

    public static void resetVisibility() {
        visibilityUpdated.postValue(false);
    }

    public static LiveData<Boolean> getCanAddSubApplicationUpdates() {
        return canAddSubApplicationUpdated;
    }

    public static void notifyCanAddSubApplicationUpdated() {
        canAddSubApplicationUpdated.postValue(true);
    }

    public static void resetCanAddSubApplication() {
        canAddSubApplicationUpdated.postValue(false);
    }
}
