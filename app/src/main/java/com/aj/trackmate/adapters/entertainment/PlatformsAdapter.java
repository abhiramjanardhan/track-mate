package com.aj.trackmate.adapters.entertainment;

import android.app.AlertDialog;
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

import java.util.List;

public class PlatformsAdapter extends RecyclerView.Adapter<PlatformsAdapter.PlatformsViewHolder> {
    private final Context context;
    private List<SubApplication> subApplications;
    private final OnSubApplicationsClickListener onSubApplicationsClickListener;

    public PlatformsAdapter(Context context, List<SubApplication> subApplications, OnSubApplicationsClickListener listener) {
        this.context = context;
        this.subApplications = subApplications;
        this.onSubApplicationsClickListener = listener;
    }

    @NonNull
    @Override
    public PlatformsAdapter.PlatformsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platform, parent, false);
        return new PlatformsAdapter.PlatformsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatformsAdapter.PlatformsViewHolder holder, int position) {
        SubApplication subApplication = subApplications.get(position);

        holder.textViewPlatformName.setText(subApplication.getName());
        holder.textViewPlatformDescription.setText(subApplication.getDescription());
        Log.d("Platform", "Loaded: " + subApplication.getName());

        holder.itemView.setEnabled(!subApplication.isReadOnly());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + subApplication.getName());
            if (onSubApplicationsClickListener != null) {
                onSubApplicationsClickListener.onSubApplicationClick(subApplication);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subApplications.size();
    }

    public void updateSubApplications(List<SubApplication> newSubApplications) {
        this.subApplications = newSubApplications;
        notifyDataSetChanged();
    }

    public void removePlatform(int position) {
        if (position >= 0 && position < subApplications.size()) {
            subApplications.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Inside your activity or adapter, where you handle the delete button click
    private void showDeleteConfirmationDialog(String itemName, Runnable onDeleteConfirmed) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete \"" + itemName + "\"?")
                .setPositiveButton("OK", (dialog, which) -> {
                    onDeleteConfirmed.run(); // Execute the delete action
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()) // Cancel button just closes the dialog
                .show();
    }

    static class PlatformsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPlatformName, textViewPlatformDescription;
        CardView platformCard;

        public PlatformsViewHolder(@NonNull View itemView) {
            super(itemView);
            platformCard = itemView.findViewById(R.id.platformCard);
            textViewPlatformName = itemView.findViewById(R.id.platformName);
            textViewPlatformDescription = itemView.findViewById(R.id.platformDescription);
        }
    }

    public interface OnSubApplicationsClickListener {
        void onSubApplicationClick(SubApplication subApplication);
    }
}
