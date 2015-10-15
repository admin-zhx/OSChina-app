package com.bill.mygitosc.fragment;

import android.os.Bundle;

import com.bill.mygitosc.utils.OscApiUtils;

/**
 * Created by liaobb on 2015/8/3.
 */
public class SearchProjectFragment extends BaseProjectsRefreshFragment {
    private String SEARCH_PROJECT_CACHE_PREFIX = "project_search_";
    private String searchKey;

    public static SearchProjectFragment newInstance(String searchKey) {
        SearchProjectFragment fragment = new SearchProjectFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, searchKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchKey = getArguments().getString(PROJECT_TYPE);
        }
    }

    @Override
    protected String getItemURL(int page) {
        return OscApiUtils.getSearchProjectURL(searchKey, page);
    }

    @Override
    protected String getCacheKey() {
        return SEARCH_PROJECT_CACHE_PREFIX + searchKey + "_";
    }
}
