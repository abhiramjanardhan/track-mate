package com.aj.trackmate.operations;

import android.app.AlertDialog;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;
import com.aj.trackmate.operations.templates.ItemUpdateListener;

import java.util.List;

public class LongPressCallBack {

    private final RecyclerView.Adapter adapter;
    private final ItemUpdateListener itemUpdateListener;
    private final ItemTouchListener itemTouchListener;
    private final ItemRemovalListener itemRemovalListener;

    public LongPressCallBack(RecyclerView.Adapter adapter, ItemUpdateListener itemUpdateListener, ItemTouchListener itemTouchListener, ItemRemovalListener itemRemovalListener) {
        this.adapter = adapter;
        this.itemUpdateListener = itemUpdateListener;
        this.itemTouchListener = itemTouchListener;
        this.itemRemovalListener = itemRemovalListener;
    }

    public void handleLongPress(View view, int position, String type) {
        boolean isReadOnly = itemTouchListener.isReadOnly(position);
        List<String> statusOptions = itemUpdateListener.getItems();
        String savedItem = itemUpdateListener.getSavedItem(position);

        if (isReadOnly) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Action Not Allowed")
                    .setMessage("This item cannot be updated.")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        } else {
            int preSelectedIndex = statusOptions.indexOf(savedItem);
            if (preSelectedIndex == -1) preSelectedIndex = 0;

            final int[] selectedIndex = {preSelectedIndex};

            new AlertDialog.Builder(view.getContext())
                    .setTitle("Update " + type)
                    .setSingleChoiceItems(
                            statusOptions.toArray(new String[0]),
                            selectedIndex[0],
                            (dialog, which) -> selectedIndex[0] = which
                    )
                    .setPositiveButton("Update Status", (dialog, which) -> {
                        String selectedStatus = statusOptions.get(selectedIndex[0]);
                        itemUpdateListener.updateItem(position, selectedStatus);
                    })
                    .setNegativeButton("Cancel", null)
                    .setNeutralButton("Delete " + type, ((dialog, which) -> {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to delete this item?")
                                .setPositiveButton("Delete", (childDialog, childWhich) -> {
                                    itemRemovalListener.removeItem(position); // Call the method passed via the listener
                                })
                                .setNegativeButton("Cancel", null) // Restore item
                                .setCancelable(false)
                                .show();
                    }))
                    .setCancelable(false)
                    .show();
        }
    }
}
