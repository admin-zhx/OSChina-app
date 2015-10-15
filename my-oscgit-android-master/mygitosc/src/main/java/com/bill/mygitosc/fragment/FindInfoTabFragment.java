package com.bill.mygitosc.fragment;

import android.support.v4.app.Fragment;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.ProjectType;

import java.util.List;

/**
 * Created by liaobb on 2015/7/22.
 */
public class FindInfoTabFragment extends BaseTabViewpageFragment {
    @Override
    protected void initViewpagerFragmentList(List<Fragment> viewpagerFragmentList, List<String> mTitles) {

        mTitles.add(getString(R.string.tab_find_featured_project));
        viewpagerFragmentList.add(AllProjectFragment.newInstance(ProjectType.FeaturedProject));

        mTitles.add(getString(R.string.tab_find_popular_project));
        viewpagerFragmentList.add(AllProjectFragment.newInstance(ProjectType.PopularProject));

        mTitles.add(getString(R.string.tab_find_latest_project));
        viewpagerFragmentList.add(AllProjectFragment.newInstance(ProjectType.LatestProject));
    }
}
