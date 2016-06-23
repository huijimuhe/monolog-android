package com.huijimuhe.monolog.data.statue;

import android.os.Parcel;
import android.os.Parcelable;

import com.huijimuhe.monolog.data.account.Account;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/17.
 */
public class IndexResponse implements Parcelable {
    /*
        {"statue":
            {"id":535,"img_path":"","text":"sdsdsddsds","isbanned":0,"created_at":"2015-09-15 20:11:12","lat":"30.581564","lng":"104.073336",
                "user":{"id":8532,"name":"Abshire.Myriam","postAvatar":"-1","gender":"m"}
            },
        "users":[
            {"id":8566,"name":"rBruen","postAvatar":"-1","gender":"m"},
            {"id":8532,"name":"Abshire.Myriam","postAvatar":"-1","gender":"m"},
            {"id":8540,"name":"Deja02","postAvatar":"-1","gender":"m"}
        ]}
     */
    private Statue statue;
    private ArrayList<Account> users;

    public Statue getStatue() {
        return statue;
    }

    public void setStatue(Statue statue) {
        this.statue = statue;
    }

    public ArrayList<Account> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Account> users) {
        this.users = users;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.statue, 0);
        dest.writeTypedList(this.users);
    }

    public IndexResponse() {
        this.statue = new Statue();
        this.users = new ArrayList<>();
    }

    private IndexResponse(Parcel in) {
        this.statue = in.readParcelable(Statue.class.getClassLoader());
        this.users = new ArrayList<>();
        in.readTypedList(this.users, Account.CREATOR);
    }

    public static final Creator<IndexResponse> CREATOR = new Creator<IndexResponse>() {
        public IndexResponse createFromParcel(Parcel source) {
            return new IndexResponse(source);
        }

        public IndexResponse[] newArray(int size) {
            return new IndexResponse[size];
        }
    };
}
