package net.oschina.gitapp.ui.fragments;

import static net.oschina.gitapp.ui.fragments.ExploreProjectsFragment.*;
import android.os.Bundle;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

/**
 * 发现页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class ExploreViewPagerFragment extends BaseViewPagerFragment {
	
    public static ExploreViewPagerFragment newInstance() {
        return new ExploreViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.explore_title_array);
		Bundle featuredBundle = new Bundle();
		featuredBundle.putByte(EXPLORE_TYPE, TYPE_FEATURED);
		adapter.addTab(title[0], "featured", ExploreProjectsFragment.class, featuredBundle);
		Bundle popularBundle = new Bundle();
		popularBundle.putByte(EXPLORE_TYPE, TYPE_POPULAR);
		adapter.addTab(title[1], "popular", ExploreProjectsFragment.class, popularBundle);
		Bundle latestdBundle = new Bundle();
		latestdBundle.putByte(EXPLORE_TYPE, TYPE_LATEST);
		adapter.addTab(title[2], "latest", ExploreProjectsFragment.class, latestdBundle);
	}
}
