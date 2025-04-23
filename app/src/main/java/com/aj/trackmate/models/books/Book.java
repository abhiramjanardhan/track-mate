package com.aj.trackmate.models.books;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.aj.trackmate.models.books.converters.BookForConverter;
import com.aj.trackmate.models.books.converters.BookStatusConverter;
import com.aj.trackmate.models.books.converters.BookGenreConverter;

import java.util.List;

@Entity(tableName = "books")
public class Book implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(BookForConverter.class)
    private BookFor bookFor;
    @TypeConverters(BookGenreConverter.class)
    private List<BookGenre> genre;
    private String author;
    private String publication;
    private boolean favorite;
    private boolean purchased;
    private boolean wishlist;
    private boolean started;
    private boolean completed;
    @TypeConverters(BookStatusConverter.class)
    private BookStatus status;
    private boolean backlog;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BookFor getBookFor() {
        return bookFor;
    }

    public void setBookFor(BookFor bookFor) {
        this.bookFor = bookFor;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public List<BookGenre> getGenre() {
        return genre;
    }

    public void setGenre(List<BookGenre> genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isWishlist() {
        return wishlist;
    }

    public void setWishlist(boolean wishlist) {
        this.wishlist = wishlist;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public BookStatus getBookStatus() {
        return status;
    }

    public void setBookStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isBacklog() {
        return backlog;
    }

    public void setBacklog(boolean backlog) {
        this.backlog = backlog;
    }

    // Blank Constructor
    public Book() {
        // Initialize fields to default values if needed
        this.name = "";
        this.author = "";
        this.publication = "";
        this.favorite = false;
        this.purchased = false;
        this.wishlist = false;
        this.started = false;
        this.completed = false;
        this.status = BookStatus.NOT_STARTED; // or default status
        this.backlog = false;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        author = in.readString();
        bookFor = BookFor.fromGenre(in.readString());
        genre = in.createStringArrayList().stream()
                .map(BookGenre::valueOf)
                .toList();
        publication = in.readString();
        favorite = in.readByte() != 0;
        purchased = in.readByte() != 0;
        wishlist = in.readByte() != 0;
        started = in.readByte() != 0;
        completed = in.readByte() != 0;
        status = BookStatus.fromStatus(in.readString());
        backlog = in.readByte() != 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(bookFor.getBookFor());
        dest.writeStringList(genre.stream().map(Enum::name).toList());
        dest.writeString(publication);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeByte((byte) (purchased ? 1 : 0));
        dest.writeByte((byte) (wishlist ? 1 : 0));
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(status.getStatus());
        dest.writeByte((byte) (backlog ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
