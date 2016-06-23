package com.huijimuhe.monolog.data.account;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Administrator on 2015/9/17.
 */
public class Account  implements Parcelable {

    @SerializedName("_id")
    private String id;
    private String name;
    private String gender;
    private String avatar;
    private int score;
    private int follow_count;
    private int fan_count;
    private int right_count;
    private int miss_count;
    private int statue_count;

    public Account() {

    }

    private Account(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.gender = in.readString();
        this.score = in.readInt();
        this.follow_count = in.readInt();
        this.fan_count = in.readInt();
        this.right_count = in.readInt();
        this.miss_count = in.readInt();
        this.statue_count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.gender);
        dest.writeInt(this.score);
        dest.writeInt(this.follow_count);
        dest.writeInt(this.fan_count);
        dest.writeInt(this.right_count);
        dest.writeInt(this.miss_count);
        dest.writeInt(this.statue_count);
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public String toString() {
        Gson gson=new Gson();
        String temp=   gson.toJson(this);
        return  gson.toJson(this);//super.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public void setFan_count(int fan_count) {
        this.fan_count = fan_count;
    }

    public void setRight_count(int right_count) {
        this.right_count = right_count;
    }

    public void setMiss_count(int miss_count) {
        this.miss_count = miss_count;
    }

    public void setStatue_count(int statue_count) {
        this.statue_count = statue_count;
    }

    public String getGender() {
        return gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getScore() {
        return score;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public int getFan_count() {
        return fan_count;
    }

    public int getRight_count() {
        return right_count;
    }

    public int getMiss_count() {
        return miss_count;
    }

    public int getStatue_count() {
        return statue_count;
    }
}
