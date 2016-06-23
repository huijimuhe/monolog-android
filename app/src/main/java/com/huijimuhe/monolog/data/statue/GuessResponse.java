package com.huijimuhe.monolog.data.statue;

import android.os.Parcel;
import android.os.Parcelable;

import com.huijimuhe.monolog.data.account.Account;

public class GuessResponse implements Parcelable {

    private int result;
    private Account user;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
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

    public GuessResponse() {
    }

    private GuessResponse(Parcel in) {
        this.result = in.readInt();
        this.user = in.readParcelable(Account.class.getClassLoader());
    }

    public static final Creator<GuessResponse> CREATOR = new Creator<GuessResponse>() {
        public GuessResponse createFromParcel(Parcel source) {
            return new GuessResponse(source);
        }

        public GuessResponse[] newArray(int size) {
            return new GuessResponse[size];
        }
    };
}
