package com.aj.trackmate.models.game;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.*;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.application.converters.CurrencyConverter;
import com.aj.trackmate.models.game.converters.DLCStatusConverter;
import com.aj.trackmate.models.game.converters.DownloadableContentTypeConverter;
import com.aj.trackmate.models.game.converters.GamePurchaseModeConverter;
import com.aj.trackmate.models.game.converters.GamePurchaseTypeConverter;

import java.util.Calendar;

@Entity(
        tableName = "game_downloadable_content",
        foreignKeys = @ForeignKey(
                entity = Game.class,
                parentColumns = "id",
                childColumns = "gameId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "gameId")} // Improves query performance
)
public class DownloadableContent implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int gameId; // Foreign Key
    private String name;
    @TypeConverters(DownloadableContentTypeConverter.class)
    private DownloadableContentType dlcType;
    private boolean purchased;
    private boolean wishlist;
    @TypeConverters(GamePurchaseTypeConverter.class)
    private GamePurchaseType purchaseType;
    @TypeConverters(GamePurchaseModeConverter.class)
    private GamePurchaseMode purchaseMode;
    @TypeConverters(DLCStatusConverter.class)
    private DLCStatus status;
    private boolean started;
    private boolean completed;
    private boolean backlog;
    private double amount;
    @TypeConverters(CurrencyConverter.class)
    private Currency currency;
    private int year;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DownloadableContentType getDlcType() {
        return dlcType;
    }

    public void setDlcType(DownloadableContentType dlcType) {
        this.dlcType = dlcType;
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

    public GamePurchaseType getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(GamePurchaseType purchaseType) {
        this.purchaseType = purchaseType;
    }

    public GamePurchaseMode getPurchaseMode() {
        return purchaseMode;
    }

    public void setPurchaseMode(GamePurchaseMode purchaseMode) {
        this.purchaseMode = purchaseMode;
    }

    public DLCStatus getStatus() {
        return status;
    }

    public void setStatus(DLCStatus status) {
        this.status = status;
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

    public boolean isBacklog() {
        return backlog;
    }

    public void setBacklog(boolean backlog) {
        this.backlog = backlog;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public DownloadableContent() {
        name = "";
        dlcType = DownloadableContentType.OTHER;
        purchased = false;
        wishlist = false;
        purchaseType = GamePurchaseType.NOT_DECIDED;
        purchaseMode = GamePurchaseMode.NOT_YET;
        started = false;
        completed = false;
        backlog = false;
        amount = 0.0;
        currency = Currency.INR;
        year = Calendar.getInstance().get(Calendar.YEAR);
    }

    // Parcelable Implementation
    public DownloadableContent(Parcel in) {
        id = in.readInt();
        name = in.readString();
        gameId = in.readInt();
        purchased = in.readByte() != 0;
        wishlist = in.readByte() != 0;
        purchaseMode = GamePurchaseMode.fromMode(in.readString());
        purchaseType = GamePurchaseType.fromType(in.readString());
        started = in.readByte() != 0;
        completed = in.readByte() != 0;
        status = DLCStatus.fromStatus(in.readString());
        backlog = in.readByte() != 0;
        amount = in.readDouble();
        currency = Currency.fromCurrency(in.readString());
        year = in.readInt();
    }

    public static final Parcelable.Creator<DownloadableContent> CREATOR = new Parcelable.Creator<DownloadableContent>() {
        @Override
        public DownloadableContent createFromParcel(Parcel in) {
            return new DownloadableContent(in);
        }

        @Override
        public DownloadableContent[] newArray(int size) {
            return new DownloadableContent[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(gameId);
        dest.writeByte((byte) (purchased ? 1 : 0));
        dest.writeByte((byte) (wishlist ? 1 : 0));
        dest.writeString(purchaseMode.getPurchaseMode());
        dest.writeString(purchaseType.getPurchaseType());
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(status.getStatus());
        dest.writeByte((byte) (backlog ? 1 : 0));
        dest.writeDouble(amount);
        dest.writeString(currency.getCurrency());
        dest.writeInt(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
