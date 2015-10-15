package com.bill.mygitosc.ui;

import android.os.Bundle;

import com.bill.mygitosc.R;
import com.bill.mygitosc.fragment.LanguageProjectFragment;

public class ViewLanguageProjectActivity extends BaseActivity {
    public static String LANGUAGE_ID = "language_id";
    public static String LANGUAGE_NAME = "language_name";
    private int languageID;
    private String languageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            languageID = getIntent().getIntExtra(LANGUAGE_ID, -1);
            languageName = getIntent().getStringExtra(LANGUAGE_NAME);
        }
        toolbar.setTitle(languageName + getString(R.string.language_project_title));

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content,
                LanguageProjectFragment.newInstance(languageID), null).commit();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_view_language_projecto;
    }

}
