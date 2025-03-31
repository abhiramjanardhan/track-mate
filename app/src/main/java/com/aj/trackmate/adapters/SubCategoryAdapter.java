package com.aj.trackmate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.application.SubApplication;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {
    private Context context;
    private List<SubApplication> subcategories;
    private String categoryName;

    public SubCategoryAdapter(Context context, String categoryName, List<SubApplication> subcategories) {
        this.context = context;
        this.subcategories = subcategories;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_sub_applications, parent, false);
        return new SubCategoryAdapter.SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        SubApplication category = subcategories.get(position);
        holder.textViewSubCategory.setText("");
        Log.d("Sub Category", "Loaded: " + category.getName());

        // Set up the grid for items inside each sub category
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        holder.recyclerViewSubcategories.setLayoutManager(gridLayoutManager);
        holder.recyclerViewSubcategories.setAdapter(new SubItemAdapter(context, categoryName, subcategories));
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public void updateSubApplications(List<SubApplication> newSubApplications) {
        this.subcategories = newSubApplications;
        notifyDataSetChanged();
    }

    static class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSubCategory;
        RecyclerView recyclerViewSubcategories;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubCategory = itemView.findViewById(R.id.textViewEntertainmentCategory);
            recyclerViewSubcategories = itemView.findViewById(R.id.recyclerViewEntertainmentCategory);
        }
    }
}
