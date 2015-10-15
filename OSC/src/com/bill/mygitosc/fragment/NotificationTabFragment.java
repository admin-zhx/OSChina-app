package com.bill.mygitosc.fragment;

import android.support.v4.app.Fragment;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.AppContext;

import java.util.List;

/**
 * Created by liaobb on 2015/7/29.
 */
public class NotificationTabFragment extends BaseTabViewpageFragment {

    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {
        int userID = AppContext.getInstance().getSession().getId();
        mTitles.add(getString(R.string.tab_notread_notification));
        viewpagerFragmentList.add(NotificationRefreshFragment.newInstance(userID));

        mTitles.add(getString(R.string.tab_readable_notification));
        viewpagerFragmentList.add(NotificationRefreshFragment.newInstance(userID));
    }
}
