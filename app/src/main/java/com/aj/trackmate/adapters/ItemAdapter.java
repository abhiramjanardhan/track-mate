package com.aj.trackmate.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.Application;
import com.aj.trackmate.models.application.relations.ApplicationWithSubApplication;
import com.aj.trackmate.utils.PlatformActivityMapper;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final Context context;
    private final List<ApplicationWithSubApplication> applicationWithSubApplications;

    public ItemAdapter(Context context, List<ApplicationWithSubApplication> applications) {
        this.context = context;
        this.applicationWithSubApplications = applications;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Get the category name
        ApplicationWithSubApplication applicationWithSubApplication = applicationWithSubApplications.get(position);
        Application application = applicationWithSubApplication.application;

        Log.d("Item", "Loaded: " + application.getName());
        Log.d("Item", "Has Sub Apps: " + application.isHasSubApplication());

        holder.categoryTitleText.setText(application.getName());
        holder.categoryDescriptionText.setText(application.getDescription());

        // Set the click listener for each category item
        holder.itemView.setOnClickListener(v -> {
            // Log the click event with the platform name
            Log.d("Item", "Clicked: " + application.getName());

            if (context instanceof Activity) {
                if (application.isHasSubApplication()) {
                    PlatformActivityMapper.startPlatformActivity(context, application.getName(), applicationWithSubApplication.subApplications);
                } else {
                    PlatformActivityMapper.startPlatformActivity(context, application.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationWithSubApplications.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitleText;
        TextView categoryDescriptionText;
        CardView cardViewItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitleText = itemView.findViewById(R.id.categoryTitleText);
            categoryDescriptionText = itemView.findViewById(R.id.categoryDescriptionText);
            cardViewItem = itemView.findViewById(R.id.cardViewItem);
        }
    }
}
