package com.bill.mygitosc.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.bill.mygitosc.fragment.SearchProjectFragment;
import com.bill.mygitosc.widget.ClearEditText;

import butterknife.InjectView;

public class SearchActivity extends BaseActivity {
    /*@InjectView(R.id.searchview)
    SearchView searchView;*/
    @InjectView(R.id.custom_searchview)
    ClearEditText customSearchview;

    private InputMethodManager inputMethodManager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        /*searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*/

        fragmentManager = getSupportFragmentManager();

        customSearchview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(customSearchview.getText())) {
                        fragmentManager.beginTransaction().replace(R.id.main_content, SearchProjectFragment.
                                newInstance(customSearchview.getText().toString()), null).commit();
                        inputMethodManager.hideSoftInputFromWindow(customSearchview.getWindowToken(), 0);
                    }else {
                        AlertDialog.Builder builder = generateAlterDialog();
                        builder.setTitle(getString(R.string.search_dialog_title)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.project_search_title));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_search;
    }

    /*@Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            fragmentManager.beginTransaction().replace(R.id.main_content, SearchProjectFragment.newInstance(query), null).commit();
            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        } else {
            AlertDialog.Builder builder = generateAlterDialog();
            builder.setTitle(getString(R.string.search_dialog_title)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }*/
}
