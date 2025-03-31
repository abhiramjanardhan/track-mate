package com.aj.trackmate.adapters.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.books.BookNote;

import java.util.List;

public class BookNotesAdapter extends RecyclerView.Adapter<BookNotesAdapter.BookNoteViewHolder> {
    private final Context context;
    private List<BookNote> notes;
    private final OnBookNoteClickListener onBookNoteClickListener;

    public BookNotesAdapter(Context context, List<BookNote> notes, OnBookNoteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.onBookNoteClickListener = listener;
    }

    @Override
    public BookNotesAdapter.BookNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_book_note, parent, false);
        return new BookNotesAdapter.BookNoteViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BookNotesAdapter.BookNoteViewHolder holder, int position) {
        // Get the game at the current position
        BookNote bookNote = notes.get(position);
        Log.d("Items", "Loaded: " + bookNote.getHeading());

        // Bind data to the view components
        holder.bookNoteName.setText(bookNote.getHeading());
        holder.bookNoteStatus.setText(bookNote.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + bookNote.getHeading());
            if (onBookNoteClickListener != null) {
                onBookNoteClickListener.onBookNoteClick(bookNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateBookNotes(List<BookNote> newBookNotes) {
        this.notes = newBookNotes;
        notifyDataSetChanged();
    }

    public void removeBookNote(int position) {
        if (position >= 0 && position < notes.size()) {
            notes.remove(position);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder class to hold the views for each item
    public static class BookNoteViewHolder extends RecyclerView.ViewHolder {

        TextView bookNoteName;
        TextView bookNoteStatus;

        public BookNoteViewHolder(View itemView) {
            super(itemView);
            bookNoteName = itemView.findViewById(R.id.bookNoteName);
            bookNoteStatus = itemView.findViewById(R.id.bookNoteStatus);
        }
    }

    public interface OnBookNoteClickListener {
        void onBookNoteClick(BookNote bookNote);
    }
}
