package com.aj.trackmate.adapters.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.SubApplication;

import java.util.List;

public class RelatedSubApplicationSettingsAdapter extends RecyclerView.Adapter<RelatedSubApplicationSettingsAdapter.CategoryViewHolder> {
    private final Context context;
    private final List<SubApplication> subApplications;
    private final OnVisibilityChangeListener onVisibilityChangeListener;

    public RelatedSubApplicationSettingsAdapter(Context context, List<SubApplication> subApplications, OnVisibilityChangeListener onVisibilityChangeListener) {
        this.context = context;
        this.subApplications = subApplications;
        this.onVisibilityChangeListener = onVisibilityChangeListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_related_sub_application_settings, parent, false);
        return new CategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        SubApplication subApplication = subApplications.get(position);

        holder.categorySettingsHeading.setText(subApplication.getName());
        Log.d("Sub Application Settings", "Loaded: " + subApplication.getName());

        holder.categoryVisibleSwitch.setChecked(subApplication.isVisible());

        // Handle switch operation
        final CompoundButton.OnCheckedChangeListener[] visibilitySwitchListener = new CompoundButton.OnCheckedChangeListener[1];
        visibilitySwitchListener[0] = (buttonView, isChecked) -> {
            String message = subApplication.isVisible()
                    ? "Are you sure you want to disable the sub applications?"
                    : "Are you sure you want to enable the sub applications?";

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Visibility")
                    .setMessage(message)
                    .setPositiveButton(subApplication.isVisible() ? "Disable" : "Enable", (dialog, which) -> {
                        if (onVisibilityChangeListener != null) {
                            onVisibilityChangeListener.onVisibilityChange(subApplication, isChecked);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(null); // Temporarily detach
                        holder.categoryVisibleSwitch.setChecked(subApplication.isVisible()); // Revert
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]); // Re-attach
                    })
                    .setCancelable(false)
                    .show();
        };

        // Attach the listener
        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]);
    }

    @Override
    public int getItemCount() {
        return subApplications.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categorySettingsHeading;
        Switch categoryVisibleSwitch;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categorySettingsHeading = itemView.findViewById(R.id.categorySettingsHeading);
            categoryVisibleSwitch = itemView.findViewById(R.id.categoryVisibleSwitch);
        }
    }

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(SubApplication subApplication, boolean isChecked);
    }
}
