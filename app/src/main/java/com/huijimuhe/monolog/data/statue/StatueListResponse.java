package com.huijimuhe.monolog.data.statue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/17.
 */
public class StatueListResponse implements Parcelable {

    /**
     *
    * {"items":
     * [{
     *  "id":1,"img_path":"","text":"Sit ipsa ut vel. Rerum aut aut ut id voluptatem quod. Est ipsum similique ex numquam necessitatibus ipsam blanditiis.","isbanned":0,"created_at":"1978-06-13 00:52:28","lat":"0","lng":"0"
     * }],
     * "total_number":1}
     */

    private int total_number;
    private ArrayList<Statue> items;

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public ArrayList<Statue> getItems() {
        return items;
    }

    public void setItems(ArrayList<Statue> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total_number);
        dest.writeTypedList(this.items);
    }

    public StatueListResponse() {
    }

    private StatueListResponse(Parcel in) {
        this.total_number = in.readInt();
        this.items =new ArrayList<>();
        in.readTypedList(this.items, Statue.CREATOR);
    }

    public static final Creator<StatueListResponse> CREATOR = new Creator<StatueListResponse>() {
        public StatueListResponse createFromParcel(Parcel source) {
            return new StatueListResponse(source);
        }

        public StatueListResponse[] newArray(int size) {
            return new StatueListResponse[size];
        }
    };
}
