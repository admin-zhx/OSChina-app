package com.bill.mygitosc.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.AppContext;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liaobb on 2015/7/22.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String PREFERENCE_FILE_NAME = "mygitosc_settings";
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AppContext.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(getLayoutView());
        ButterKnife.inject(this);

        sharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initToolbar();
    }

    protected void initToolbar() {
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutView();

    public int getColorPrimary() {
        if (AppContext.getInstance().getCurrentTheme() == R.style.AppBaseTheme) {
            return getResources().getColor(R.color.blue);
        } else {
            return getResources().getColor(R.color.green);
        }
    }

    public AlertDialog.Builder generateAlterDialog() {
        int dialogTheme;
        if (AppContext.getInstance().getCurrentTheme() == R.style.AppBaseTheme) {
            dialogTheme = R.style.BlueDialogTheme;
        } else {
            dialogTheme = R.style.GreenDialogTheme;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, dialogTheme);
        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
