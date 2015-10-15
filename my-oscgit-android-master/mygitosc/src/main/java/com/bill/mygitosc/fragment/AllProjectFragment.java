package com.bill.mygitosc.fragment;

import android.os.Bundle;

import com.bill.mygitosc.utils.OscApiUtils;

/**
 * Created by liaobb on 2015/8/3.
 */
public class AllProjectFragment extends BaseProjectsRefreshFragment {
    private String ALL_PROJECT_CACHE_PREFIX = "project_all_";
    private String projectType;

    public static AllProjectFragment newInstance(String projectType) {
        AllProjectFragment fragment = new AllProjectFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, projectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectType = getArguments().getString(PROJECT_TYPE);
        }
    }

    @Override
    protected String getItemURL(int page) {
        return OscApiUtils.getProjectsURL(projectType, page);
    }

    @Override
    protected String getCacheKey() {
        return ALL_PROJECT_CACHE_PREFIX + projectType + "_";
    }
}
