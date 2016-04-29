package com.huijimuhe.monolog.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class AuthResponseBean implements Parcelable {

    /**
     * token : $2y$10$TuwW15mDauJ8v1qes/MbAepTBZsxz1h8P7N5z0f919N.3qfEPIRXK
     */
    private String token;
    private UserBean user;


    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeParcelable(this.user, 0);
    }

    public AuthResponseBean() {
    }

    private AuthResponseBean(Parcel in) {
        this.token = in.readString();
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<AuthResponseBean> CREATOR = new Creator<AuthResponseBean>() {
        public AuthResponseBean createFromParcel(Parcel source) {
            return new AuthResponseBean(source);
        }

        public AuthResponseBean[] newArray(int size) {
            return new AuthResponseBean[size];
        }
    };
}
