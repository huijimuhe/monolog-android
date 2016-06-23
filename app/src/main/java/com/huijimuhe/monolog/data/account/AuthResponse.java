package com.huijimuhe.monolog.data.account;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/9/25.
 */
public class AuthResponse implements Parcelable {

    /**
     * token : $2y$10$TuwW15mDauJ8v1qes/MbAepTBZsxz1h8P7N5z0f919N.3qfEPIRXK
     */
    private String token;
    private Account user;


    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
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

    public AuthResponse() {
    }

    private AuthResponse(Parcel in) {
        this.token = in.readString();
        this.user = in.readParcelable(Account.class.getClassLoader());
    }

    public static final Creator<AuthResponse> CREATOR = new Creator<AuthResponse>() {
        public AuthResponse createFromParcel(Parcel source) {
            return new AuthResponse(source);
        }

        public AuthResponse[] newArray(int size) {
            return new AuthResponse[size];
        }
    };
}
