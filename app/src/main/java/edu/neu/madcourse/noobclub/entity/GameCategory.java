package edu.neu.madcourse.noobclub.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class GameCategory implements Parcelable {
    public int id;
    public String gameName;
    public String icon;
    public String firstChar;

    public GameCategory(int id,String firstChar,String name,String icon){
        this.id = id;
        this.firstChar = firstChar;
        this.icon = icon ;
        this.gameName = name;
    }

    public GameCategory(){}

    protected GameCategory(Parcel in) {
        id = in.readInt();
        gameName = in.readString();
        icon = in.readString();
        firstChar = in.readString();
    }

    public static final Creator<GameCategory> CREATOR = new Creator<GameCategory>() {
        @Override
        public GameCategory createFromParcel(Parcel in) {
            return new GameCategory(in);
        }

        @Override
        public GameCategory[] newArray(int size) {
            return new GameCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(gameName);
        parcel.writeString(icon);
        parcel.writeString(firstChar);
    }
}
