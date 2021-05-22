package edu.neu.madcourse.noobclub.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class GamePostTopic implements Parcelable {
    public String id;
    public String title;
    public String content;
    public int gameId;
    public String gameName;
    public long createTime;
    public String creatorId;
    public String creatorName;
    public int replyCount;
    public int avatar;

    public GamePostTopic(){}

    protected GamePostTopic(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        gameId = in.readInt();
        gameName = in.readString();
        createTime = in.readLong();
        creatorId = in.readString();
        creatorName = in.readString();
        replyCount = in.readInt();
        avatar = in.readInt();
    }

    public static final Creator<GamePostTopic> CREATOR = new Creator<GamePostTopic>() {
        @Override
        public GamePostTopic createFromParcel(Parcel in) {
            return new GamePostTopic(in);
        }

        @Override
        public GamePostTopic[] newArray(int size) {
            return new GamePostTopic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeInt(gameId);
        parcel.writeString(gameName);
        parcel.writeLong(createTime);
        parcel.writeString(creatorId);
        parcel.writeString(creatorName);
        parcel.writeInt(replyCount);
        parcel.writeInt(avatar);
    }
}
