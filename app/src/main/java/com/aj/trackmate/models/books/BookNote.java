package com.aj.trackmate.models.books;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.room.*;
import com.aj.trackmate.models.books.converters.BookNoteStatusConverter;

@Entity(
        tableName = "book_notes",
        foreignKeys = @ForeignKey(
                entity = Book.class,
                parentColumns = "id",
                childColumns = "bookId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "bookId")} // Improves query performance
)
public class BookNote implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int bookId; // Foreign Key
    private String heading;
    private String description;
    @TypeConverters(BookNoteStatusConverter.class)
    private BookNoteStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BookNoteStatus getStatus() {
        return status;
    }

    public void setStatus(BookNoteStatus status) {
        this.status = status;
    }

    public BookNote() {
        heading = "";
        description = "";
        status = BookNoteStatus.NOT_APPLICABLE;
    }

    // Parcelable Implementation
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public BookNote(Parcel in) {
        id = in.readInt();
        bookId = in.readInt();
        heading = in.readString();
        description = in.readString();
        status = BookNoteStatus.fromStatus(in.readString());
    }

    public static final Creator<BookNote> CREATOR = new Creator<BookNote>() {
        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Override
        public BookNote createFromParcel(Parcel in) {
            return new BookNote(in);
        }

        @Override
        public BookNote[] newArray(int size) {
            return new BookNote[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(bookId);
        dest.writeString(heading);
        dest.writeString(description);
        dest.writeString(status.getStatus());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
