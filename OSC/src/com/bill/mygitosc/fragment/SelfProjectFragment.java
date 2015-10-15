package com.bill.mygitosc.fragment;

import android.os.Bundle;

import com.bill.mygitosc.utils.OscApiUtils;

/**
 * Created by liaobb on 2015/8/3.
 */
public class SelfProjectFragment extends BaseProjectsRefreshFragment {
    private String SELF_PROJECT_CACHE_PREFIX = "project_";
    private static String USER_ID = "user_id";

    private int userID;
    private String projectType;

    public static SelfProjectFragment newInstance(int userid, String projectType) {
        SelfProjectFragment fragment = new SelfProjectFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_TYPE, projectType);
        args.putInt(USER_ID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectType = getArguments().getString(PROJECT_TYPE);
            userID = getArguments().getInt(USER_ID);
        }
    }

    @Override
    protected String getItemURL(int page) {
        return OscApiUtils.getSelfProjectsURL(userID, projectType, page);
    }

    @Override
    protected String getCacheKey() {
        return SELF_PROJECT_CACHE_PREFIX + projectType + "_" + userID + "_";
    }

}
