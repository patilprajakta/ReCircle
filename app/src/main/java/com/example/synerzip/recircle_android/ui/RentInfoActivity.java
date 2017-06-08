package com.example.synerzip.recircle_android.ui;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.synerzip.recircle_android.R;
import com.example.synerzip.recircle_android.models.Products;
import com.example.synerzip.recircle_android.models.RentItem;
import com.example.synerzip.recircle_android.models.UserProductUnAvailability;
import com.example.synerzip.recircle_android.utilities.RCLog;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Snehal Tembare on 26/4/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */

public class RentInfoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "RentInfoActivity";
    public static boolean isDateChanged = false;
    public static boolean isDateEdited = false;
    public String formatedFromDate;
    public String formatedToDate;
    private int discount = 0;
    private int finalTotal = 0;
    private int subTotal = 0;
    private int total = 0;
    private int percentage;
    private int forDays;
    private double protectionPlanFee;
    private int serviceFee;
    private int preAuthFee;
    public static RentItem mRentItem;

    private String user_id;

    private Products mProduct;
    private Bundle mBundle;
    private Date fromDate;
    private Date toDate;
    private ArrayList<UserProductUnAvailability> userProductUnAvailabilities;
    private int dayCount;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.txt_item_title)
    protected TextView mTxtTitle;

    @BindView(R.id.txt_manufaturer_name)
    protected TextView mTxtManufaturerName;

    @BindView(R.id.txt_price)
    protected TextView mTxtPrice;

    @BindView(R.id.txt_days)
    protected TextView mTxtDays;

    @BindView(R.id.txt_from_date)
    protected TextView mTxtFromDate;

    @BindView(R.id.txt_to_date)
    protected TextView mTxtToDate;

    @BindView(R.id.txt_subtotal)
    protected TextView mTxtSubTotal;

    @BindView(R.id.txt_discount)
    protected TextView mTxtDiscounts;

    @BindView(R.id.txt_total)
    protected TextView mTxtTotal;

    @BindView(R.id.txt_owner_name)
    protected TextView mTxtOwnerName;

    @BindView(R.id.txt_selected_dates)
    protected TextView mTxtSelectedDates;

    @BindView(R.id.img_product)
    protected ImageView mImgProduct;

    @BindView(R.id.img_owner)
    protected CircularImageView mImgOwner;

    @BindView(R.id.edt_user_msg)
    protected EditText mEdtUserMsg;

    @BindView(R.id.check_protection_plan)
    protected CheckBox mChckProtectionPlan;


    @BindView(R.id.txt_service_fee)
    protected TextView mTxtServiceFee;

    @BindView(R.id.txt_pre_auth_fee)
    protected TextView mTxtPreAuthFee;

    @BindView(R.id.txt_protection_fee)
    protected TextView mTxtProtectionFee;

    @BindView(R.id.protection_layout)
    protected LinearLayout mProtectionLayout;

    @BindView(R.id.pre_auth_layout)
    protected LinearLayout mPreAuthLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_info);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.price_details);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.common_white));

        mBundle = getIntent().getExtras();

        if (mBundle != null) {
            dayCount = calculateDayCount(mBundle.getString(getString(R.string.from_date)),
                    mBundle.getString(getString(R.string.to_date)));
        }
    }

    /**
     * Calculate selected no of days
     */

    public int calculateDayCount(String from, String to) {

        DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
        try {
            fromDate = formatter.parse(from);
            toDate = formatter.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calFromDate = Calendar.getInstance();
        Calendar calToDate = Calendar.getInstance();
        calFromDate.setTime(fromDate);
        calToDate.setTime(toDate);
        CharSequence monthFromDate = android.text.format.DateFormat
                .format(getString(R.string.month_format), fromDate);
        CharSequence monthToDate = android.text.format.DateFormat
                .format(getString(R.string.month_format), toDate);
        formatedFromDate = calFromDate.get(Calendar.DATE) + " " + monthFromDate + ", " + calFromDate.get(Calendar.YEAR);
        formatedToDate = calToDate.get(Calendar.DATE) + " " + monthToDate + ", " + calToDate.get(Calendar.YEAR);
        long diff = toDate.getTime() - fromDate.getTime();
        dayCount = (int) diff / (24 * 60 * 60 * 1000);

        return dayCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String from = data.getStringExtra(getString(R.string.from_date));
                String to = data.getStringExtra(getString(R.string.to_date));

                dayCount = calculateDayCount(from, to);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mBundle != null) {

            mProduct = mBundle.getParcelable(getString(R.string.product));
            userProductUnAvailabilities = mBundle.getParcelableArrayList(getString(R.string.unavail_dates));


            if (mProduct != null) {

                mRentItem = new RentItem();
                mRentItem.setUser_product_id(mProduct.getUser_product_info().getUser_product_id());

                String from = mBundle.getString(getString(R.string.from_date));
                String to = mBundle.getString(getString(R.string.to_date));

                DateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));
                DateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.ddd_mm));
                try {
                    fromDate = formatter.parse(from.toString());
                    toDate = formatter.parse(to.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mRentItem.setOrder_from_date(simpleDateFormat.format(fromDate));
                mRentItem.setOrder_to_date(simpleDateFormat.format(toDate));


                //Products data
                if (mProduct.getUser_info().getUser_id() != null) {
                    user_id = mProduct.getUser_info().getUser_id();
                }

                mTxtTitle.setText(mProduct.getProduct_info().getProduct_title());

                mTxtManufaturerName.setText(mProduct.getProduct_info().getProduct_manufacturer_name());
                mTxtPrice.setText("$" + mProduct.getUser_product_info().getPrice_per_day() + "/day");

                if (mProduct.getUser_product_info().getUser_prod_images() != null
                        && mProduct.getUser_product_info().getUser_prod_images().size() != 0 &&
                        mProduct.getUser_product_info().getUser_prod_images().get(0).getUser_prod_image_url() != null) {
                    Picasso.with(this).load(mProduct.getUser_product_info()
                            .getUser_prod_images().get(0).getUser_prod_image_url())
                            .into(mImgProduct);
                }

                if (mProduct.getUser_info().getUser_image_url() != null) {
                    Picasso.with(this).load(mProduct.getUser_info().getUser_image_url())
                            .into(mImgOwner);
                }

                mTxtOwnerName.setText(mProduct.getUser_info().getFirst_name() + " " + mProduct.getUser_info().getLast_name());

                mTxtFromDate.setText(formatedFromDate);
                mTxtToDate.setText(formatedToDate);

                subTotal = Math.abs(dayCount) * Integer.parseInt(mProduct.getUser_product_info().getPrice_per_day());
                serviceFee = (int) Math.round(subTotal * (0.08));
                finalTotal = subTotal + serviceFee;

                //Calculate fees
                protectionPlanFee = (double) (Integer.parseInt(mProduct.getUser_product_info().getPrice_per_day()) * 0.01);
                preAuthFee = (int) (Integer.parseInt(mProduct.getUser_product_info().getPrice_per_day()) * 0.25);

                //Calculate discount
                if (mProduct.getUser_product_info().getUser_product_discounts() != null
                        && mProduct.getUser_product_info().getUser_product_discounts().size() > 0) {

                    for (int i = 0; i < mProduct.getUser_product_info().getUser_product_discounts().size(); i++) {
                        if (mProduct.getUser_product_info().getUser_product_discounts().get(i).getIsActive() != null
                                && dayCount >= mProduct.getUser_product_info().getUser_product_discounts().get(i).getDiscount_for_days()
                                && mProduct.getUser_product_info().getUser_product_discounts().get(i).getIsActive()) {
                            discount = subTotal * mProduct.getUser_product_info().getUser_product_discounts().get(i).getPercentage() / 100;
                            total = subTotal - discount;
                            finalTotal = (int) (total + protectionPlanFee + serviceFee);
                            percentage = mProduct.getUser_product_info().getUser_product_discounts().get(i).getPercentage();
                            forDays = mProduct.getUser_product_info().getUser_product_discounts().get(i).getDiscount_for_days();
                        }
                    }
                }


                //It handles edit dates functionality
                mTxtSelectedDates.setOnTouchListener(new View.OnTouchListener() {
                    final int DRAWABLE_RIGHT = 2;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (event.getRawX() >= (mTxtSelectedDates.getRight() - mTxtSelectedDates.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                Intent intent = new Intent(RentInfoActivity.this, CalendarActivity.class);
                                intent.putExtra(getString(R.string.from_date), mBundle.getString(getString(R.string.from_date)));
                                intent.putExtra(getString(R.string.to_date), mBundle.getString(getString(R.string.to_date)));
                                intent.putExtra(getString(R.string.selected_dates_list), CalendarActivity.selectedDates);
                                if (userProductUnAvailabilities != null && userProductUnAvailabilities.size() != 0) {
                                    intent.putParcelableArrayListExtra(getString(R.string.unavail_dates), userProductUnAvailabilities);
                                }
                                isDateEdited = true;
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        }
                        return true;
                    }
                });

                mChckProtectionPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mProtectionLayout.setVisibility(View.VISIBLE);
                            mPreAuthLayout.setVisibility(View.GONE);

                            finalTotal = (int) (total + protectionPlanFee + serviceFee);
                            mTxtTotal.setText("$" + String.valueOf(finalTotal));
                        } else {
                            mProtectionLayout.setVisibility(View.GONE);
                            mPreAuthLayout.setVisibility(View.VISIBLE);
                            finalTotal = total + serviceFee;
                            mTxtTotal.setText("$" + String.valueOf(finalTotal));
                            mTxtPreAuthFee.setText("$" + String.valueOf(preAuthFee));
                        }
                    }
                });

                if (discount == 0) {
                    mTxtDiscounts.setText("$0.0");
                    mTxtSubTotal.setText("$" + String.valueOf(subTotal));
                    mTxtTotal.setText("$" + String.valueOf(finalTotal));
                } else {
                    mTxtDiscounts.setText("$" + discount + " (" + percentage + "% for " + forDays + " " + getString(R.string.days) + ")");
                    mTxtSubTotal.setText("$" + String.valueOf(subTotal));
                    mTxtTotal.setText("$" + String.valueOf(finalTotal));
                }

                mTxtDays.setText(String.valueOf(dayCount) + " " + getString(R.string.days));
                mTxtSubTotal.setText("$" + String.valueOf(subTotal));
                mTxtServiceFee.setText("$" + String.valueOf(serviceFee));
                mTxtProtectionFee.setText("$" + String.valueOf(protectionPlanFee));
                mTxtPreAuthFee.setText("$" + String.valueOf(preAuthFee));

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailsActivity.isShowInfo = true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /**
     * Show payment mode screen
     */

    @OnClick(R.id.btn_proceed_to_pay)
    public void showPaymentModes() {

        mRentItem.setUser_msg(mEdtUserMsg.getText().toString());

        mRentItem.setService_fee(serviceFee);
        mRentItem.setFinal_payment(finalTotal);

        if (mChckProtectionPlan.isChecked()) {
            mRentItem.setProtection_plan(1);
            mRentItem.setProtection_plan_fee(protectionPlanFee);
        } else {
            mRentItem.setProtection_plan(0);
            mRentItem.setProtection_plan_fee(0);
            mRentItem.setPre_auth_fee(preAuthFee);
        }

        //Waiting for stripe integration
       /* Intent intentPayMode = new Intent(this, PaymentModeActivity.class);
        intentPayMode.putExtra(getString(R.string.total), finalTotal);
        intentPayMode.putExtra(getString(R.string.user_id), user_id);
        startActivity(intentPayMode);*/

        startActivity(new Intent(this, RentItemSuccessActivity.class));
        RCLog.showToast(this, getString(R.string.item_requested_successfully));


    }
}
