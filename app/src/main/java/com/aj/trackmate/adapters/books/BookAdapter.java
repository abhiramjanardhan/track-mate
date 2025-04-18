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
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.relations.BookWithNotes;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final Context context;
    private List<BookWithNotes> books;
    private final OnBookClickListener onBookClickListener;
    private final OnBookLongClickListener onBookLongClickListener;

    public BookAdapter(Context context, List<BookWithNotes> books, OnBookClickListener listener, OnBookLongClickListener onBookLongClickListener) {
        this.context = context;
        this.books = books;
        this.onBookClickListener = listener;
        this.onBookLongClickListener = onBookLongClickListener;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the game item layout for each row in the RecyclerView
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        // Get the game at the current position
        BookWithNotes bookWithNotes = books.get(position);
        Book book = bookWithNotes.book;
        Log.d("Items", "Loaded: " + book.getName());

        // Bind data to the view components
        holder.bookName.setText(book.getName());
        holder.bookStatus.setText(book.getStatus().getStatus());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Log.d("ItemClicked", "Item clicked: " + book.getName());
            if (onBookClickListener != null) {
                onBookClickListener.onBookClick(bookWithNotes);
            }
        });

        // handle item long press
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("ItemClicked", "Item pressed: " + book.getName());
            if (onBookLongClickListener != null) {
                onBookLongClickListener.onBookClick(v, position);
            }
            return true; // Long press handled
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<BookWithNotes> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    public void removeBook(int position) {
        if (position >= 0 && position < books.size()) {
            books.remove(position);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder class to hold the views for each item
    public static class BookViewHolder extends RecyclerView.ViewHolder {

        TextView bookName;
        TextView bookStatus;

        public BookViewHolder(View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookStatus = itemView.findViewById(R.id.bookStatus);
        }
    }

    public interface OnBookClickListener {
        void onBookClick(BookWithNotes bookWithNotes);
    }

    public interface OnBookLongClickListener {
        void onBookClick(View view, int position);
    }
}
