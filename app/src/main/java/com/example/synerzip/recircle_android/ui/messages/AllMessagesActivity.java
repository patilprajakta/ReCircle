package com.example.synerzip.recircle_android.ui.messages;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.synerzip.recircle_android.R;
import com.example.synerzip.recircle_android.models.user_messages.RootMessageInfo;
import com.example.synerzip.recircle_android.network.ApiClient;
import com.example.synerzip.recircle_android.network.RCAPInterface;
import com.example.synerzip.recircle_android.ui.HomeActivity;
import com.example.synerzip.recircle_android.utilities.RCAppConstants;
import com.example.synerzip.recircle_android.utilities.RCLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Prajakta Patil on 7/6/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */

//TODO class implemetation is in progress

public class AllMessagesActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    protected RelativeLayout mProgressBar;

    @BindView(R.id.frame_layout)
    protected FrameLayout mFrameLayout;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.view_pager_msgs)
    protected ViewPager mViewPager;

    @BindView(R.id.tab_layout_msgs)
    protected TabLayout mTabLayout;

    private RCAPInterface service;

    private String mAccessToken = "";

    private boolean isLoggedIn;

    private OwnerMsgFragment ownerMsgFragment;

    private RenterMsgFragment renterMsgFragment;

    public static String userProductMsgId;

    private RootMessageInfo rootMessageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.common_white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar.setVisibility(View.VISIBLE);
        mFrameLayout.setAlpha((float) 0.6);

        mToolbar.setTitle(R.string.toolbar_msgs);

        ownerMsgFragment = new OwnerMsgFragment();
        renterMsgFragment = new RenterMsgFragment();

        //TODO code commented to add search messages

   /*     LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.layout_search_msgs, null);

        mToolbar.addView(v);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, HomeActivity.mOwnerNameList);

        AutoCompleteTextView textView = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_search_msgs);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String selected = (String) arg0.getAdapter().getItem(arg2);
                Toast.makeText(AllMessagesActivity.this,
                        "Clicked " + arg2 + " name: " + selected,
                        Toast.LENGTH_SHORT).show();

            }
        });
        MsgAutocompleteAdapter mAutocompleteAdapter = new MsgAutocompleteAdapter
                (this, R.layout.activity_all_messages, R.id.txtProductName, HomeActivity.mOwnerNameList);
        textView.setAdapter(mAutocompleteAdapter);*/

        //get data from shared preferences+
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(RCAppConstants.RC_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(RCAppConstants.RC_SHARED_PREFERENCES_LOGIN_STATUS, false);
        mAccessToken = sharedPreferences.getString(RCAppConstants.RC_SHARED_PREFERENCES_ACCESS_TOKEN, mAccessToken);

        if (isLoggedIn) {
            getMessageDetails();
        } else {
            RCLog.showToast(this, getString(R.string.user_must_login));
        }

    }//end onCreate()

    /**
     * get details of user messages
     */
    public void getMessageDetails() {
        service = ApiClient.getClient(this).create(RCAPInterface.class);
        Call<RootMessageInfo> call = service.getUserMessage("Bearer " + mAccessToken);
        call.enqueue(new Callback<RootMessageInfo>() {
            @Override
            public void onResponse(Call<RootMessageInfo> call, Response<RootMessageInfo> response) {
                mProgressBar.setVisibility(View.GONE);
                mFrameLayout.setAlpha((float) 1.0);
                if (response.isSuccessful()) {
                    rootMessageInfo = response.body();
                    if (!response.body().getOwnerProdRelatedMsgs().isEmpty()) {
                        userProductMsgId = response.body().getOwnerProdRelatedMsgs().get(0).getUser_prod_msg_id();
                        ownerMsgFragment.getMessageDetails(rootMessageInfo);
                    }
                    if (!response.body().getOwnerRequestMsgs().isEmpty()) {
                        userProductMsgId = response.body().getOwnerRequestMsgs().get(0).getUser_prod_msg_id();
                        renterMsgFragment.getRenterMessageDetails(rootMessageInfo);
                    }
                }
            }

            @Override
            public void onFailure(Call<RootMessageInfo> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mFrameLayout.setAlpha((float) 1.0);
            }
        });

        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(ownerMsgFragment, getString(R.string.tab_msgs_owner));
        mPagerAdapter.addFragment(renterMsgFragment, getString(R.string.tab_msgs_renter));
        mViewPager.setAdapter(mPagerAdapter);
        ownerMsgFragment.getMessageDetails(rootMessageInfo);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    //TODO code commented to add search messages
 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
*/

    /**
     * PagerAdapter Class
     */
    class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mFragmentList.remove(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
