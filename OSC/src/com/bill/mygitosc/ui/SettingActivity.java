package com.bill.mygitosc.ui;

import android.os.Bundle;

import com.bill.mygitosc.R;
import com.bill.mygitosc.fragment.SettingsFragment;


public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment, new SettingsFragment(), null).commit();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.menu_item_settings));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_setting;
    }

}
