package com.aj.trackmate.adapters;

import android.util.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.Category;
import com.aj.trackmate.models.application.relations.CategoryWithApplicationsAndSubApplications;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final Context context;
    private final List<CategoryWithApplicationsAndSubApplications> categories;

    public CategoryAdapter(Context context, List<CategoryWithApplicationsAndSubApplications> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryWithApplicationsAndSubApplications categoryApplications = categories.get(position);
        Category category = categoryApplications.category;

        holder.textViewCategory.setText(category.getTitle());
        Log.d("Category", "Loaded: " + category.getTitle());

        // Set up the grid for items inside each category
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        holder.recyclerViewItems.setLayoutManager(gridLayoutManager);
        holder.recyclerViewItems.setAdapter(new ItemAdapter(context, categoryApplications.applications));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategory;
        RecyclerView recyclerViewItems;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            recyclerViewItems = itemView.findViewById(R.id.recyclerViewItems);
        }
    }
}
