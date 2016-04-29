package com.huijimuhe.monolog.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.huijimuhe.monolog.utils.NumUtils;
import com.huijimuhe.monolog.utils.TimeUtils;

/**
 * Created by Administrator on 2015/9/17.
 */
public class StatueBean implements Parcelable {
    /**
     * id : 535
     * img_path :
     * text : sdsdsddsds
     * isbanned : 0
     * created_at : 2015-09-15 20:11:12
     * lat : 30.581564
     * lng : 104.073336
     * user : {"id":8532,"name":"Abshire.Myriam","postAvatar":"-1","gender":"m"}
     */

    @SerializedName("_id")
    private String id;
    private String img_path;
    private String text;
    private int isbanned;
    private String created_at;
    private double lat;
    private double lng;
    private String right_count;
    private String miss_count;
    private UserBean user;


    public void setId(String id) {
        this.id = id;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIsbanned(int isbanned) {
        this.isbanned = isbanned;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getImg_path() {
        return img_path;
    }

    public String getText() {
        return text;
    }

    public int getIsbanned() {
        return isbanned;
    }

    public String getCreated_at() {
        if (TextUtils.isEmpty(created_at)) {
            return TimeUtils.getTime(created_at);
        }
        return created_at;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public UserBean getUser() {
        return user;
    }

    public String getRight_count() {
        return NumUtils.converNumToString(right_count);
    }

    public void setRight_count(String right_count) {
        this.right_count = right_count;
    }

    public String getMiss_count() {
        return NumUtils.converNumToString(miss_count);
    }

    public void setMiss_count(String miss_count) {
        this.miss_count = miss_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.img_path);
        dest.writeString(this.text);
        dest.writeInt(this.isbanned);
        dest.writeString(this.created_at);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.miss_count);
        dest.writeString(this.right_count);
        dest.writeParcelable(this.user, 0);
    }

    public StatueBean() {
    }

    private StatueBean(Parcel in) {
        this.id = in.readString();
        this.img_path = in.readString();
        this.text = in.readString();
        this.isbanned = in.readInt();
        this.created_at = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.miss_count = in.readString();
        this.right_count = in.readString();
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<StatueBean> CREATOR = new Creator<StatueBean>() {
        public StatueBean createFromParcel(Parcel source) {
            return new StatueBean(source);
        }

        public StatueBean[] newArray(int size) {
            return new StatueBean[size];
        }
    };
}