package com.bill.mygitosc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.ProjectType;

import java.util.List;

/**
 * Created by liaobb on 2015/7/22.
 */
public class MySelfInfoTabFragment extends BaseTabViewpageFragment {
    public static String USER_ID = "user_id";
    private int userID;

    public static MySelfInfoTabFragment newInstance(int userid) {
        MySelfInfoTabFragment fragment = new MySelfInfoTabFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getInt(USER_ID);
        }
    }

    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {
        mTitles.add(getString(R.string.tab_myself_news));
        viewpagerFragmentList.add(EventsRefreshFragment.newInstance(userID));

        mTitles.add(getString(R.string.tab_myself_project));
        viewpagerFragmentList.add(SelfProjectFragment.newInstance(userID, ProjectType.MyProject));

        mTitles.add(getString(R.string.tab_myself_star));
        viewpagerFragmentList.add(SelfProjectFragment.newInstance(userID, ProjectType.StaredProject));

        mTitles.add(getString(R.string.tab_myself_watch));
        viewpagerFragmentList.add(SelfProjectFragment.newInstance(userID, ProjectType.WatchedProject));
    }
}