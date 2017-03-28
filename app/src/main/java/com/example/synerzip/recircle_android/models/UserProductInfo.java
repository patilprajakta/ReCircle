package com.example.synerzip.recircle_android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Snehal Tembare on 9/3/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */

@Getter
@Setter

public class UserProductInfo implements Parcelable{
    private String product_id;

    private String avai_to_date;

    private String avai_from_date;

    private String product_avg_rating;

    private String created_at;

    private ArrayList<UserProdImages> user_prod_images;

    private String user_prod_desc;

    private String price_per_day;

    private String user_product_id;

    protected UserProductInfo(Parcel in) {
        product_id = in.readString();
        avai_to_date = in.readString();
        avai_from_date = in.readString();
        product_avg_rating = in.readString();
        created_at = in.readString();
        user_prod_desc = in.readString();
        price_per_day = in.readString();
        user_product_id = in.readString();
    }

    public static final Creator<UserProductInfo> CREATOR = new Creator<UserProductInfo>() {
        @Override
        public UserProductInfo createFromParcel(Parcel in) {
            return new UserProductInfo(in);
        }

        @Override
        public UserProductInfo[] newArray(int size) {
            return new UserProductInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(avai_to_date);
        dest.writeString(avai_from_date);
        dest.writeString(product_avg_rating);
        dest.writeString(created_at);
        dest.writeString(user_prod_desc);
        dest.writeString(price_per_day);
        dest.writeString(user_product_id);
    }
}
