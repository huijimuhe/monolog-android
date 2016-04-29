package com.huijimuhe.monolog.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/17.
 */
public class IndexResponseBean implements Parcelable {
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
    private StatueBean statue;
    private ArrayList<UserBean> users;

    public StatueBean getStatue() {
        return statue;
    }

    public void setStatue(StatueBean statue) {
        this.statue = statue;
    }

    public ArrayList<UserBean> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserBean> users) {
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

    public IndexResponseBean() {
        this.statue = new StatueBean();
        this.users = new ArrayList<>();
    }

    private IndexResponseBean(Parcel in) {
        this.statue = in.readParcelable(StatueBean.class.getClassLoader());
        this.users = new ArrayList<>();
        in.readTypedList(this.users, UserBean.CREATOR);
    }

    public static final Creator<IndexResponseBean> CREATOR = new Creator<IndexResponseBean>() {
        public IndexResponseBean createFromParcel(Parcel source) {
            return new IndexResponseBean(source);
        }

        public IndexResponseBean[] newArray(int size) {
            return new IndexResponseBean[size];
        }
    };
}
