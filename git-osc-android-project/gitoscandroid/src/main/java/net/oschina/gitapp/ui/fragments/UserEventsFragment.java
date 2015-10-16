package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.EventAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 用户最新动态列表Fragment
 * @created 2014-7-11 下午15:47
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class UserEventsFragment extends BaseSwipeRefreshFragment<Event> {
	
	private User mUser;
	
	public static UserEventsFragment newInstance(User user) {
		UserEventsFragment fragment = new UserEventsFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.USER, user);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
	}

    @Override
    public CommonAdapter<Event> getAdapter() {
        return new EventAdapter(getActivity(), R.layout.list_item_event);
    }

    @Override
    public List<Event> getDatas(byte[] responeString) {
        return JsonUtils.getList(Event[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getUserEvents(mUser.getId(), mCurrentPage, mHandler);
    }

	@Override
	public void onItemClick(int position, Event event) {
		UIHelper.showEventDetail(getActivity(), event);
	}

    @Override
    protected String getEmptyTip() {
        return "暂无动态";
    }
}
