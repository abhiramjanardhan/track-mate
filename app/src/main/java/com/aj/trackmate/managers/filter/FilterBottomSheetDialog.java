package com.aj.trackmate.managers.filter;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aj.trackmate.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {
    private FilterListener filterListener;
    private FilterBarManager filterBarManager;
    private String platform;
    private Map<String, String> selectedFilters = new HashMap<>();

    public interface FilterListener {
        void onApplyFilters(Map<String, String> filters);
        void onClearFilters();
    }

    public FilterBottomSheetDialog(String platform, Map<String, String> selectedFilters, FilterListener listener) {
        this.platform = platform;
        this.selectedFilters = selectedFilters;
        this.filterListener = listener;
    }

    public FilterBottomSheetDialog() {
        // Required empty constructor for fragment recreation
    }

    public void setConfig(FilterListener listener) {
        this.filterListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_sort_filter_bar, container, false); // Use your existing layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Advanced Filter", "Platform: " + platform);

        filterBarManager = new FilterBarManager(requireContext(), view, platform);
        if (!selectedFilters.isEmpty()) {
            filterBarManager.restoreSelections(selectedFilters);
        }

        Button apply = view.findViewById(R.id.applyFiltersButton);
        Button clear = view.findViewById(R.id.clearFiltersButton);

        apply.setOnClickListener(v -> {
            if (filterListener != null) {
                filterListener.onApplyFilters(filterBarManager.getSelectedFilters());
                dismiss();
            }
        });

        clear.setOnClickListener(v -> {
            filterBarManager.clearFilters();
            if (filterListener != null) {
                filterListener.onClearFilters();
            }
        });
    }

    @Override
    public void onDestroyView() {
        filterBarManager = null;
        super.onDestroyView();
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
