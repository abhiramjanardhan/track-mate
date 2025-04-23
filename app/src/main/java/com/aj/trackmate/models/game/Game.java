package com.aj.trackmate.models.game;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.aj.trackmate.models.application.Currency;
import com.aj.trackmate.models.application.converters.CurrencyConverter;
import com.aj.trackmate.models.game.converters.GamePurchaseModeConverter;
import com.aj.trackmate.models.game.converters.GamePurchaseTypeConverter;
import com.aj.trackmate.models.game.converters.GameStatusConverter;
import com.aj.trackmate.models.game.converters.GamePlatformConverter;

import java.util.Calendar;

@Entity(tableName = "games")
public class Game implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(GamePlatformConverter.class)
    private Platform platform;
    private boolean favorite;
    private boolean purchased;
    private boolean wishlist;
    @TypeConverters(GamePurchaseModeConverter.class)
    private GamePurchaseMode purchaseMode;
    @TypeConverters(GamePurchaseTypeConverter.class)
    private GamePurchaseType purchaseType;
    private boolean started;
    private boolean completed;
    @TypeConverters(GameStatusConverter.class)
    private GameStatus status;  // Not Started, In Progress, Story Completed, 100% done
    private boolean wantToGoFor100Percent;
    private boolean backlog;
    private double amount;
    @TypeConverters(CurrencyConverter.class)
    private Currency currency;
    private int year;

    // Getters and Setters
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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public GamePurchaseMode getPurchaseMode() {
        return purchaseMode;
    }

    public void setPurchaseMode(GamePurchaseMode purchaseMode) {
        this.purchaseMode = purchaseMode;
    }

    public GamePurchaseType getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(GamePurchaseType purchaseType) {
        this.purchaseType = purchaseType;
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

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public boolean isWantToGoFor100Percent() {
        return wantToGoFor100Percent;
    }

    public void setWantToGoFor100Percent(boolean wantToGoFor100Percent) {
        this.wantToGoFor100Percent = wantToGoFor100Percent;
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

    // Blank constructor
    public Game() {
        // Initialize fields to default values if needed
        this.name = "";
        this.favorite = false;
        this.purchased = false;
        this.wishlist = false;
        this.started = false;
        this.completed = false;
        this.status = GameStatus.NOT_STARTED; // or default status
        this.purchaseType = GamePurchaseType.NOT_DECIDED;
        this.purchaseMode = GamePurchaseMode.NOT_YET;
        this.wantToGoFor100Percent = false;
        this.backlog = false;
        this.amount = 0.0;
        this.currency = Currency.INR;
        this.year = Calendar.getInstance().get(Calendar.YEAR);
    }

    // Parcelable Implementation
    public Game(Parcel in) {
        id = in.readInt();
        name = in.readString();
        platform = Platform.fromName(in.readString());
        favorite = in.readByte() != 0;
        purchased = in.readByte() != 0;
        wishlist = in.readByte() != 0;
        purchaseMode = GamePurchaseMode.fromMode(in.readString());
        purchaseType = GamePurchaseType.fromType(in.readString());
        started = in.readByte() != 0;
        completed = in.readByte() != 0;
        status = GameStatus.fromStatus(in.readString());
        wantToGoFor100Percent = in.readByte() != 0;
        backlog = in.readByte() != 0;
        amount = in.readDouble();
        currency = Currency.fromCurrency(in.readString());
        year = in.readInt();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(platform.getName());
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeByte((byte) (purchased ? 1 : 0));
        dest.writeByte((byte) (wishlist ? 1 : 0));
        dest.writeString(purchaseMode.getPurchaseMode());
        dest.writeString(purchaseType.getPurchaseType());
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(status.getStatus());
        dest.writeByte((byte) (wantToGoFor100Percent ? 1 : 0));
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
