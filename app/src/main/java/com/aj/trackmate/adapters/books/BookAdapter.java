package com.aj.trackmate.adapters.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aj.trackmate.R;
import com.aj.trackmate.models.books.Book;
import com.aj.trackmate.models.books.BookStatus;
import com.aj.trackmate.models.books.relations.BookWithNotes;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final Context context;
    private List<BookWithNotes> books;
    private final OnBookClickListener onBookClickListener;
    private final OnBookLongClickListener onBookLongClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public BookAdapter(Context context, List<BookWithNotes> books, OnBookClickListener listener, OnBookLongClickListener onBookLongClickListener, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.books = books;
        this.onBookClickListener = listener;
        this.onBookLongClickListener = onBookLongClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
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

        boolean isFavorite = book.isFavorite(); // From your model

        holder.favoriteStar.setImageResource(
                isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24
        );

        holder.favoriteStar.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteStarClick(book, !isFavorite);
            }
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

    public void sortBooks() {
        // Default sort based on status priority
        books.sort((a, b) -> {
            int aPriority = BookStatus.getStatusPriority().getOrDefault(a.book.getStatus(), Integer.MAX_VALUE);
            int bPriority = BookStatus.getStatusPriority().getOrDefault(b.book.getStatus(), Integer.MAX_VALUE);
            return Integer.compare(aPriority, bPriority);
        });
        updateBooks(books);
    }

    // ViewHolder class to hold the views for each item
    public static class BookViewHolder extends RecyclerView.ViewHolder {

        TextView bookName;
        TextView bookStatus;
        ImageView favoriteStar;

        public BookViewHolder(View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookStatus = itemView.findViewById(R.id.bookStatus);
            favoriteStar = itemView.findViewById(R.id.favoriteStar);
        }
    }

    public interface OnBookClickListener {
        void onBookClick(BookWithNotes bookWithNotes);
    }

    public interface OnBookLongClickListener {
        void onBookClick(View view, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteStarClick(Book book, boolean isFavorite);
    }
}
