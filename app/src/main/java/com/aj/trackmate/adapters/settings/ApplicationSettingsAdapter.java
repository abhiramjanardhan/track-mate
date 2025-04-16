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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.relations.ApplicationWithSubApplication;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;

import java.util.List;

public class ApplicationSettingsAdapter extends RecyclerView.Adapter<ApplicationSettingsAdapter.CategoryViewHolder> {
    private final Context context;
    private final List<CategoryWithApplicationsAndSubApplications> categories;
    private final OnVisibilityChangeListener onVisibilityChangeListener;
    private final OnApplicationsLinkClickListener onApplicationsLinkClickListener;

    public ApplicationSettingsAdapter(Context context, List<CategoryWithApplicationsAndSubApplications> categories, OnVisibilityChangeListener onVisibilityChangeListener, OnApplicationsLinkClickListener onApplicationsLinkClickListener) {
        this.context = context;
        this.categories = categories;
        this.onVisibilityChangeListener = onVisibilityChangeListener;
        this.onApplicationsLinkClickListener = onApplicationsLinkClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_settings, parent, false);
        return new CategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryWithApplicationsAndSubApplications categoryApplications = categories.get(position);
        Category category = categoryApplications.category;

        holder.categorySettingsHeading.setText(category.getTitle());
        Log.d("Category Settings", "Loaded: " + category.getTitle());

        holder.categoryVisibleSwitch.setChecked(category.isVisible());

        // Handle switch operation
        final CompoundButton.OnCheckedChangeListener[] visibilitySwitchListener = new CompoundButton.OnCheckedChangeListener[1];
        visibilitySwitchListener[0] = (buttonView, isChecked) -> {
            String message = category.isVisible()
                    ? "Are you sure you want to disable all the related applications?"
                    : "Are you sure you want to enable all the related applications?";

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Visibility")
                    .setMessage(message)
                    .setPositiveButton(category.isVisible() ? "Disable" : "Enable", (dialog, which) -> {
                        if (onVisibilityChangeListener != null) {
                            onVisibilityChangeListener.onVisibilityChange(category, categoryApplications.applications, isChecked);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(null); // Temporarily detach
                        holder.categoryVisibleSwitch.setChecked(category.isVisible()); // Revert
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]); // Re-attach
                    })
                    .setCancelable(false)
                    .show();
        };

        // Attach the listener
        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]);

        if (categoryApplications.applications.isEmpty()) {
            holder.applicationsCard.setEnabled(false);
            holder.applicationLink.setText("No Applications");
            holder.applicationsCard.setVisibility(View.GONE);
        } else {
            holder.applicationsCard.setVisibility(View.VISIBLE);
            holder.applicationsCard.setOnClickListener(v -> {
                if (onApplicationsLinkClickListener != null) {
                    onApplicationsLinkClickListener.onApplicationsLinkClick(category.getId(), category.getTitle());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categorySettingsHeading, applicationLink;
        Switch categoryVisibleSwitch;
        CardView applicationsCard;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categorySettingsHeading = itemView.findViewById(R.id.categorySettingsHeading);
            categoryVisibleSwitch = itemView.findViewById(R.id.categoryVisibleSwitch);
            applicationLink = itemView.findViewById(R.id.applicationLink);
            applicationsCard = itemView.findViewById(R.id.applicationsCard);
        }
    }

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(Category category, List<ApplicationWithSubApplication> applications, boolean isChecked);
    }

    public interface OnApplicationsLinkClickListener {
        void onApplicationsLinkClick(int categoryId, String categoryName);
    }
}
