package com.bill.mygitosc.ui;

import android.os.Bundle;

import com.bill.mygitosc.R;
import com.bill.mygitosc.fragment.MySelfInfoTabFragment;

public class ViewSelfInfoActivity extends BaseActivity {
    public static String UserID = "user_id";
    public static String UserName = "user_name";
    private int userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            userId = getIntent().getIntExtra(UserID, -1);
            userName = getIntent().getStringExtra(UserName);
        }
        toolbar.setTitle(userName);

        getSupportFragmentManager().beginTransaction().replace(R.id.viewself_info_fragment, MySelfInfoTabFragment.newInstance(userId), null).commit();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_view_self_info;
    }
}
