package com.aj.trackmate.operations;

import android.app.AlertDialog;
import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.operations.templates.ItemRemovalListener;
import com.aj.trackmate.operations.templates.ItemTouchListener;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final RecyclerView.Adapter adapter;
    private final ItemRemovalListener itemRemovalListener;
    private final ItemTouchListener itemTouchListener;

    public SwipeToDeleteCallback(RecyclerView.Adapter adapter, ItemRemovalListener itemRemovalListener, ItemTouchListener itemTouchListener) {
        super(0, ItemTouchHelper.RIGHT); // Swipe left to delete
        this.adapter = adapter;
        this.itemRemovalListener = itemRemovalListener;
        this.itemTouchListener = itemTouchListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Not handling drag & drop
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Get the position of the item
        int position = viewHolder.getAdapterPosition();
        boolean isReadOnly = itemTouchListener.isReadOnly(position); // Check readOnly flag or any condition

        if (isReadOnly) {
            dX = 0;  // Disable swipe if item is read-only or other condition is met
        } else {
            float maxSwipeDistance = recyclerView.getWidth() * 0.3f; // Allow only 30% swipe
            dX = Math.min(dX, maxSwipeDistance); // Restrict swipe distance
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        boolean isReadOnly = itemTouchListener.isReadOnly(position); // Check readOnly flag or any condition

        if (isReadOnly) {
            // Show delete confirmation dialog when swiped partially
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setTitle("Action Not Allowed")
                    .setMessage("This item cannot be deleted.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        adapter.notifyItemChanged(position); // Restore item to prevent deletion
                    })
                    .setCancelable(false)
                    .show();
        } else {
            // Show delete confirmation dialog when swiped partially
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        itemRemovalListener.removeItem(position); // Call the method passed via the listener
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position)) // Restore item
                    .setCancelable(false)
                    .show();
        }
    }
}
