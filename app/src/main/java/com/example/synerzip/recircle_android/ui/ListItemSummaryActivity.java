package com.example.synerzip.recircle_android.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.synerzip.recircle_android.R;
import com.example.synerzip.recircle_android.models.AllProductInfo;
import com.example.synerzip.recircle_android.models.Discounts;
import com.example.synerzip.recircle_android.models.ListAnItemRequest;
import com.example.synerzip.recircle_android.models.LogInRequest;
import com.example.synerzip.recircle_android.models.SearchProduct;
import com.example.synerzip.recircle_android.models.User;
import com.example.synerzip.recircle_android.models.UserProdImages;
import com.example.synerzip.recircle_android.models.UserProductUnAvailability;
import com.example.synerzip.recircle_android.network.ApiClient;
import com.example.synerzip.recircle_android.network.RCAPInterface;
import com.example.synerzip.recircle_android.utilities.RCAppConstants;
import com.example.synerzip.recircle_android.utilities.RCLog;
import com.example.synerzip.recircle_android.utilities.RCWebConstants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Prajakta Patil on 24/4/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */
public class ListItemSummaryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    private SharedPreferences sharedPreferences;

    private String mAccessToken;

    private RCAPInterface service;

    private String productId = "";

    private String mItemDesc;

    private int mItemPrice, mMinRental;

    private long mZipcode;

    private int fromAustin;

    private ArrayList<Discounts> listDiscounts;

    private ArrayList<UserProdImages> listUploadItemImage;

    private ArrayList<UserProductUnAvailability> mItemAvailability;

    private ArrayList<Date> unavailableDates;

    @BindView(R.id.txt_days_count)
    protected TextView mTxtDaysCount;

    @BindView(R.id.txt_item_price)
    protected TextView mTxtItemPrice;

    @BindView(R.id.txt_item_rental)
    protected TextView mTxtItemRental;

    private ArrayList<String> uploadGalleryImages;

    @BindView(R.id.recycler_view_images)
    protected RecyclerView mRecyclerView;

    private int selectedImgPosition = 0;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.txt_item_desc)
    protected TextView mTxtItemDesc;

    @BindView(R.id.txt_product_title)
    protected TextView mTxtProductTitle;

    @BindView(R.id.txt_listing_days)
    protected TextView mTxtListingDay;

    @BindView(R.id.txt_five_days_disc)
    protected TextView mTxtDiscFiveDays;

    @BindView(R.id.txt_ten_days_disc)
    protected TextView mTxtDiscTenDays;

    @BindView(R.id.progress_bar)
    protected RelativeLayout mProgressBar;

    @BindView(R.id.layout_list_item_summary)
    protected LinearLayout mLinearLayout;

    @BindView(R.id.txt_show_dates)
    protected TextView mTxtShowDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_summary);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.list_an_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.common_white));

        uploadGalleryImages = new ArrayList<>();

        //get data from shared preferences
        sharedPreferences = getSharedPreferences(RCAppConstants.RC_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        mAccessToken = sharedPreferences.getString(RCAppConstants.RC_SHARED_PREFERENCES_ACCESS_TOKEN, mAccessToken);
        unavailableDates = new ArrayList<>();

        unavailableDates = (ArrayList<Date>) getIntent().getSerializableExtra(getString(R.string.unavail_dates));
        int datesCount;
        datesCount = getIntent().getIntExtra(getString(R.string.unavail_dates_count), 0);
        if (datesCount != 0) {
            mTxtDaysCount.setText(datesCount + " days");
        } else {
            mTxtDaysCount.setText("Dates are not selected");
            mTxtShowDates.setVisibility(View.GONE);
        }

        productId = getIntent().getStringExtra(getString(R.string.product_id));
        listDiscounts = getIntent().getParcelableArrayListExtra(getString(R.string.discounts));
        double discFiveDays, discTenDays;
        discFiveDays = getIntent().getDoubleExtra(getString(R.string.disc_five_days), 0);
        discTenDays = getIntent().getDoubleExtra(getString(R.string.disc_ten_days), 0);

        mTxtDiscFiveDays.setText(getString(R.string.disc_five) + " " + String.valueOf(discFiveDays) + " " + getString(R.string.five_days));
        mTxtDiscTenDays.setText(getString(R.string.disc_ten) + " " + String.valueOf(discTenDays) + " " + getString(R.string.ten_days));

        mItemAvailability = getIntent().getParcelableArrayListExtra(getString(R.string.list_unavail_days));

        mZipcode = getIntent().getLongExtra(getString(R.string.zipcode), 0);
        fromAustin = getIntent().getIntExtra(getString(R.string.austin_check), 0);

        String currentDate = new SimpleDateFormat("MMM dd, yyyy").format(new Date());
        mTxtListingDay.setText(currentDate);

        mTxtProductTitle.setText(getIntent().getStringExtra(getString(R.string.product_title)));
        mItemPrice = getIntent().getIntExtra(getString(R.string.item_price), 0);
        mTxtItemPrice.setText("$ " + mItemPrice + "/day");
        mMinRental = getIntent().getIntExtra(getString(R.string.item_min_rental), 0);
        mTxtItemRental.setText(mMinRental + " days");

        mItemDesc = getIntent().getStringExtra(getString(R.string.list_item_desc));
        mTxtItemDesc.setText(mItemDesc);
        listUploadItemImage = (ArrayList<UserProdImages>) getIntent().getSerializableExtra(getString(R.string.upload_image));
        uploadGalleryImages = (ArrayList<String>) getIntent().getSerializableExtra(getString(R.string.uplaod_image_gallery));

        final ListItemImageAdapter mListItemImageAdapter = new ListItemImageAdapter
                (ListItemSummaryActivity.this, selectedImgPosition, uploadGalleryImages,
                        new ListItemImageAdapter.OnImageItemClickListener() {
                            @Override
                            public void onImageClick(int position, String userProdImages) {
                                View view = mRecyclerView.getChildAt(position);
                                view.setBackground(ContextCompat.getDrawable(ListItemSummaryActivity.this, R.drawable.selected_image_background));
                                //TODO recycler view scroll issue is yet to be solved
                                for (int i = 0; i < uploadGalleryImages.size(); i++) {
                                    view = mRecyclerView.getChildAt(i);

                                    if (i != position) {
                                        view.setBackground(ContextCompat.getDrawable(ListItemSummaryActivity.this, R.drawable.custom_imageview));
                                    }
                                }

                                selectedImgPosition = position;
                                mLayoutManager.scrollToPosition(position);
                            }
                        });

        mLayoutManager = new LinearLayoutManager(ListItemSummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mListItemImageAdapter);


    }

    /**
     * api call for list an item
     */
    private void getListAnItem() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setAlpha((float) 0.6);
        ListAnItemRequest listAnItemRequest = new ListAnItemRequest(productId, mItemPrice, mMinRental,
                mItemDesc, listDiscounts, listUploadItemImage, mItemAvailability, mZipcode, fromAustin);

        service = ApiClient.getClient().create(RCAPInterface.class);
        Call<AllProductInfo> call = service.listAnItem("Bearer " + mAccessToken, listAnItemRequest);
        call.enqueue(new Callback<AllProductInfo>() {

            @Override
            public void onResponse(Call<AllProductInfo> call, Response<AllProductInfo> response) {
                mProgressBar.setVisibility(View.GONE);
                mLinearLayout.setAlpha((float) 1.0);
                if (response.isSuccessful()) {
                    RCLog.showToast(ListItemSummaryActivity.this, getString(R.string.item_added));
                    Intent intent = new Intent(ListItemSummaryActivity.this, ListedItemActivity.class);
                    intent.putExtra(getString(R.string.product_id), productId);
                    startActivity(intent);
                } else {
                    if (response.code() == RCWebConstants.RC_ERROR_CODE_BAD_REQUEST) {
                        RCLog.showToast(ListItemSummaryActivity.this, getString(R.string.product_creation_failed));
                    } else {
                        RCLog.showToast(ListItemSummaryActivity.this, getString(R.string.user_not_authenticated));
                        logInDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllProductInfo> call, Throwable t) {
                RCLog.showToast(ListItemSummaryActivity.this, getString(R.string.product_not_created));
            }
        });
    }

    /**
     * Login again dialog
     */
    private void logInDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.log_in_again_dialog);
        dialog.setTitle(getString(R.string.log_in));
        final EditText mEditTxtUserName = (EditText) dialog.findViewById(R.id.edit_login_email_dialog);
        final EditText mEditTxtPwd = (EditText) dialog.findViewById(R.id.edit_login_pwd_dialog);

        Button btnLogin = (Button) dialog.findViewById(R.id.btn_user_log_in_dialog);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mLinearLayout.setAlpha((float) 0.6);
                final String mUserName = mEditTxtUserName.getText().toString();
                final String mUserPwd = mEditTxtPwd.getText().toString();
                LogInRequest logInRequest = new LogInRequest(mUserName, mUserPwd);

                service = ApiClient.getClient().create(RCAPInterface.class);
                Call<User> userCall = service.userLogIn(logInRequest);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        mProgressBar.setVisibility(View.GONE);
                        mLinearLayout.setAlpha((float) 1.0);

                        if (response.isSuccessful()) {
                            mAccessToken = response.body().getToken();
                            sharedPreferences = getSharedPreferences(RCAppConstants.RC_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(RCAppConstants.RC_SHARED_PREFERENCES_ACCESS_TOKEN, mAccessToken);
                            editor.apply();
                            dialog.dismiss();
                        } else {
                            if (response.code() == RCWebConstants.RC_ERROR_UNAUTHORISED) {
                                RCLog.showToast(ListItemSummaryActivity.this, getString(R.string.err_credentials));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        mLinearLayout.setAlpha((float) 1.0);
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * imagebutton to edit product details
     *
     * @param view
     */
    @OnClick(R.id.img_edit)
    public void imgEdit(View view) {
        finish();
    }

    /**
     * button for confirm item
     *
     * @param view
     */
    @OnClick(R.id.btn_confirm_item)
    public void btnConfirmItem(View view) {
        getListAnItem();

    }

    /**
     * textview to show unavailable dates
     *
     * @param view
     */
    @OnClick(R.id.txt_show_dates)
    public void txtShowDates(View view) {
        Intent intent = new Intent(ListItemSummaryActivity.this, ListCalendarSummaryActivity.class);
        intent.putExtra(getString(R.string.unavail_dates), unavailableDates);
        startActivity(intent);
    }

    /**
     * action bar back button
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}