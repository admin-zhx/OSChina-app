package com.bill.mygitosc.fragment;

import android.os.Bundle;

import com.bill.mygitosc.utils.OscApiUtils;

/**
 * Created by liaobb on 2015/8/3.
 */
public class LanguageProjectFragment extends BaseProjectsRefreshFragment {
    private String LANGUAGE_PROJECT_CACHE_PREFIX = "project_language_";
    private int languageID;

    public static LanguageProjectFragment newInstance(int languageType) {
        LanguageProjectFragment fragment = new LanguageProjectFragment();
        Bundle args = new Bundle();
        args.putInt(PROJECT_TYPE, languageType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            languageID = getArguments().getInt(PROJECT_TYPE);
        }
    }

    @Override
    protected String getItemURL(int page) {
        return OscApiUtils.getLanguageProjectsURL(languageID, page);
    }

    @Override
    protected String getCacheKey() {
        return LANGUAGE_PROJECT_CACHE_PREFIX + languageID + "_";
    }
}
