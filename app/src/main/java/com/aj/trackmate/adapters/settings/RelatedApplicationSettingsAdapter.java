package com.aj.trackmate.adapters.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.models.application.relations.ApplicationWithSubApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aj.trackmate.constants.CategoryConstants.*;

public class RelatedApplicationSettingsAdapter extends RecyclerView.Adapter<RelatedApplicationSettingsAdapter.CategoryViewHolder> {
    private final Context context;
    private final List<ApplicationWithSubApplication> applications;
    private final OnVisibilityChangeListener onVisibilityChangeListener;
    private final OnCanAddSubApplicationsListener onCanAddSubApplicationsListener;
    private final OnSubApplicationsLinkClickListener onApplicationsLinkClickListener;
    private final Map<String, Boolean> canAddSubApplicationsMap;

    private void configureValues() {
        canAddSubApplicationsMap.put(GAME_PLAY_STATION, false);
        canAddSubApplicationsMap.put(GAME_NINTENDO, false);
        canAddSubApplicationsMap.put(GAME_XBOX, false);
        canAddSubApplicationsMap.put(GAME_PC, false);
        canAddSubApplicationsMap.put(ENTERTAINMENT_MOVIES, true);
        canAddSubApplicationsMap.put(ENTERTAINMENT_TV_SERIES, true);
        canAddSubApplicationsMap.put(ENTERTAINMENT_MUSIC, true);
        canAddSubApplicationsMap.put(BOOKS_READING, false);
        canAddSubApplicationsMap.put(BOOKS_WRITING, false);
    }

    public RelatedApplicationSettingsAdapter(Context context, List<ApplicationWithSubApplication> applications, OnVisibilityChangeListener onVisibilityChangeListener, OnCanAddSubApplicationsListener onCanAddSubApplicationsListener, OnSubApplicationsLinkClickListener onApplicationsLinkClickListener) {
        this.context = context;
        this.applications = applications;
        this.onVisibilityChangeListener = onVisibilityChangeListener;
        this.onCanAddSubApplicationsListener = onCanAddSubApplicationsListener;
        this.onApplicationsLinkClickListener = onApplicationsLinkClickListener;
        this.canAddSubApplicationsMap = new HashMap<>();
        configureValues();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_related_application_settings, parent, false);
        return new CategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ApplicationWithSubApplication applicationWithSubApplication = applications.get(position);
        Application application = applicationWithSubApplication.application;
        List<SubApplication> subApplications = applicationWithSubApplication.subApplications;

        holder.categorySettingsHeading.setText(application.getName());
        Log.d("Application Settings", "Loaded: " + application.getName());

        holder.categoryVisibleSwitch.setChecked(application.isVisible());
        holder.addSubApplicationSwitch.setChecked(application.isHasSubApplication());

        boolean canAddSubApplications = Boolean.TRUE.equals(canAddSubApplicationsMap.get(application.getName()));
        holder.addSubApplicationSwitch.setEnabled(canAddSubApplications);
        holder.addSubApplicationRow.setVisibility(canAddSubApplications ? View.VISIBLE : View.GONE);

        // Handle switch operation
        final CompoundButton.OnCheckedChangeListener[] visibilitySwitchListener = new CompoundButton.OnCheckedChangeListener[1];
        visibilitySwitchListener[0] = (buttonView, isChecked) -> {
            String message = application.isVisible()
                    ? "Are you sure you want to disable all the related applications?"
                    : "Are you sure you want to enable all the related applications?";

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Visibility")
                    .setMessage(message)
                    .setPositiveButton(application.isVisible() ? "Disable" : "Enable", (dialog, which) -> {
                        if (onVisibilityChangeListener != null) {
                            onVisibilityChangeListener.onVisibilityChange(application, subApplications, isChecked);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(null); // Temporarily detach
                        holder.categoryVisibleSwitch.setChecked(application.isVisible()); // Revert
                        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]); // Re-attach
                    })
                    .setCancelable(false)
                    .show();
        };

        // Attach the listener
        holder.categoryVisibleSwitch.setOnCheckedChangeListener(visibilitySwitchListener[0]);

        final CompoundButton.OnCheckedChangeListener[] addSubApplicationSwitchListener = new CompoundButton.OnCheckedChangeListener[1];
        addSubApplicationSwitchListener[0] = (buttonView, isChecked) -> {
            String message = application.isHasSubApplication()
                    ? "Are you sure you want to enable the application to add sub applications?"
                    : "Are you sure you want to disable the application to add sub applications?";

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Add Sub-Applications")
                    .setMessage(message)
                    .setPositiveButton(application.isHasSubApplication() ? "Disable" : "Enable", (dialog, which) -> {
                        if (onCanAddSubApplicationsListener != null) {
                            onCanAddSubApplicationsListener.onCanAddSubApplications(application, isChecked);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        holder.addSubApplicationSwitch.setOnCheckedChangeListener(null); // Temporarily detach
                        holder.addSubApplicationSwitch.setChecked(application.isHasSubApplication()); // Revert
                        holder.addSubApplicationSwitch.setOnCheckedChangeListener(addSubApplicationSwitchListener[0]); // Re-attach
                    }) // Restore item
                    .setCancelable(false)
                    .show();
        };

        // Attach the listener
        holder.addSubApplicationSwitch.setOnCheckedChangeListener(addSubApplicationSwitchListener[0]);

        if (subApplications.isEmpty()) {
            holder.subApplicationsCard.setEnabled(false);
            holder.applicationLink.setText("No Sub Applications");
            holder.subApplicationsCard.setVisibility(View.GONE);
        } else {
            holder.subApplicationsCard.setVisibility(View.VISIBLE);
            holder.subApplicationsCard.setOnClickListener(v -> {
                if (onApplicationsLinkClickListener != null) {
                    onApplicationsLinkClickListener.onSubApplicationsLinkClick(application.getId(), application.getName());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categorySettingsHeading, applicationLink;
        Switch categoryVisibleSwitch, addSubApplicationSwitch;
        CardView subApplicationsCard;
        LinearLayout addSubApplicationRow;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categorySettingsHeading = itemView.findViewById(R.id.categorySettingsHeading);
            categoryVisibleSwitch = itemView.findViewById(R.id.categoryVisibleSwitch);
            addSubApplicationSwitch = itemView.findViewById(R.id.addSubApplicationSwitch);
            applicationLink = itemView.findViewById(R.id.applicationLink);
            subApplicationsCard = itemView.findViewById(R.id.subApplicationsCard);
            addSubApplicationRow = itemView.findViewById(R.id.addSubApplicationRow);
        }
    }

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(Application application, List<SubApplication> subApplications, boolean isChecked);
    }

    public interface OnCanAddSubApplicationsListener {
        void onCanAddSubApplications(Application application, boolean isChecked);
    }

    public interface OnSubApplicationsLinkClickListener {
        void onSubApplicationsLinkClick(int applicationId, String applicationName);
    }
}
