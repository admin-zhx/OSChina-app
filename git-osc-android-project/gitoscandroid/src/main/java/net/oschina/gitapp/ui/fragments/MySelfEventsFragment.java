package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.EventAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 我的最新动态列表
 * @created 2014-05-20 下午15:47
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class MySelfEventsFragment extends BaseSwipeRefreshFragment<Event> {
	
	public static MySelfEventsFragment newInstance() {
		return new MySelfEventsFragment();
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
        GitOSCApi.getMyEvents(mCurrentPage, mHandler);
    }

	@Override
	public void onItemClick(int position, Event event) {
		UIHelper.showEventDetail(getActivity(), event);
	}
}
