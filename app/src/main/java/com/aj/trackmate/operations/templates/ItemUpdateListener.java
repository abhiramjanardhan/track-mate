package com.aj.trackmate.operations.templates;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public interface ItemUpdateListener {
    String getSavedItem(int position);
    List<String> getItems();
    void updateItem(int position, String value); // Method to handle item update
}
