package com.aj.trackmate.models.game.relations;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.aj.trackmate.models.game.DownloadableContent;
import com.aj.trackmate.models.game.Game;

import java.util.ArrayList;
import java.util.List;

public class GameWithDownloadableContent implements Parcelable {
    @Embedded
    public Game game;

    @Relation(
            parentColumn = "id",
            entityColumn = "gameId"
    )
    public List<DownloadableContent> dlcs;

    public GameWithDownloadableContent() {
        game = new Game();
        dlcs = new ArrayList<>();
    }

    protected GameWithDownloadableContent(Parcel in) {
        game = in.readParcelable(Game.class.getClassLoader());
        dlcs = new ArrayList<>();
        in.readTypedList(dlcs, DownloadableContent.CREATOR);
    }

    public static final Creator<GameWithDownloadableContent> CREATOR = new Creator<GameWithDownloadableContent>() {
        @Override
        public GameWithDownloadableContent createFromParcel(Parcel in) {
            return new GameWithDownloadableContent(in);
        }

        @Override
        public GameWithDownloadableContent[] newArray(int size) {
            return new GameWithDownloadableContent[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(game, flags);
        dest.writeTypedList(dlcs);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
