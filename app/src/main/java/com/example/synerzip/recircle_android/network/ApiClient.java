package com.example.synerzip.recircle_android.network;

import android.content.Context;

import com.example.synerzip.recircle_android.R;
import com.example.synerzip.recircle_android.utilities.NetworkUtility;
import com.example.synerzip.recircle_android.utilities.RCLog;
import com.example.synerzip.recircle_android.utilities.RCWebConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Snehal Tembare on 9/3/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */

public class ApiClient {
    public static final String BASE_URL = "http://ff11a676.ngrok.io";
    private static Retrofit retrofit = null;


    /*public static Retrofit getClient() {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (null == retrofit) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(RCWebConstants.RC_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;


    }*/

    public static Retrofit getClient(Context context) {
        if (NetworkUtility.isNetworkAvailable()) {


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            if (null == retrofit) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(RCWebConstants.RC_BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        } else {
            RCLog.showLongToast(context, context.getString(R.string.check_nw_connectivity));
            return null;
        }


    }
}
