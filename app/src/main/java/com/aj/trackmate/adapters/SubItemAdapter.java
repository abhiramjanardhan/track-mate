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
import com.aj.trackmate.models.application.SubApplication;
import com.aj.trackmate.utils.PlatformActivityMapper;

import java.util.List;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {
    private Context context;
    private String categoryName;
    private List<SubApplication> subApplications;

    public SubItemAdapter(Context context, String categoryName, List<SubApplication> subApplications) {
        this.context = context;
        this.categoryName = categoryName;
        this.subApplications = subApplications;
    }

    @NonNull
    @Override
    public SubItemAdapter.SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sub_application, parent, false);
        return new SubItemAdapter.SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemAdapter.SubItemViewHolder holder, int position) {
        // Get the category name
        SubApplication application = subApplications.get(position);

        holder.categoryTitleText.setText(application.getName());
        holder.categoryDescriptionText.setText(application.getDescription());
        Log.d("Sub Item", "Loaded: " + application.getName());

        // Set the click listener for each category item
        holder.itemView.setOnClickListener(v -> {
            // Log the click event with the platform name
            Log.d("Sub Item", "Clicked: " + application.getName());

            // Ensure context is an Activity and start the GamePlatformActivity
            if (context instanceof Activity) {
                PlatformActivityMapper.startPlatformActivity(context, categoryName, application.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subApplications.size();
    }

    static class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitleText;
        TextView categoryDescriptionText;
        CardView cardViewItem;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitleText = itemView.findViewById(R.id.entertainmentCategoryTitleText);
            categoryDescriptionText = itemView.findViewById(R.id.entertainmentCategoryDescription);
            cardViewItem = itemView.findViewById(R.id.entertainmentCategoryCardView);
        }
    }
}
