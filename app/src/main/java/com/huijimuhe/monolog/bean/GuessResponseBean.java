package com.huijimuhe.monolog.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GuessResponseBean implements Parcelable {

    private int result;
    private UserBean user;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.result);
        dest.writeParcelable(this.user, 0);
    }

    public GuessResponseBean() {
    }

    private GuessResponseBean(Parcel in) {
        this.result = in.readInt();
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<GuessResponseBean> CREATOR = new Creator<GuessResponseBean>() {
        public GuessResponseBean createFromParcel(Parcel source) {
            return new GuessResponseBean(source);
        }

        public GuessResponseBean[] newArray(int size) {
            return new GuessResponseBean[size];
        }
    };
}
