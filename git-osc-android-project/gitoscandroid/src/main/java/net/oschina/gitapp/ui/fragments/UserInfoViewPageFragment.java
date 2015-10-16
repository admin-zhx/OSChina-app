package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class UserInfoViewPageFragment extends BaseViewPagerFragment {
	
	private User mUser;
	
	public static UserInfoViewPageFragment newInstance(User user) {
		UserInfoViewPageFragment fragment = new UserInfoViewPageFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.USER, user);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
		String[] title = getResources().getStringArray(R.array.userinfo_title_array);
		
		adapter.addTab(title[0], "user_events", UserEventsFragment.class, args);
		adapter.addTab(title[1], "user_projects", UserProjectsFragment.class, args);
		adapter.addTab(title[2], "user_star_projects", UserStarProjectFragment.class, args);
		adapter.addTab(title[3], "user_watch_projects", UserWatchProjectsFragment.class, args);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
}
