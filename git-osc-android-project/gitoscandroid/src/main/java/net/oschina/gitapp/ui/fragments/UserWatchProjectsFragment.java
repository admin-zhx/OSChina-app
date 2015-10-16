package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ProjectAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 用户watch项目列表
 * @created 2014-08-27
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class UserWatchProjectsFragment extends BaseSwipeRefreshFragment<Project> {
	
	private User mUser;
	
	public static UserWatchProjectsFragment newInstance(User user) {
		UserWatchProjectsFragment fragment = new UserWatchProjectsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
		setUserVisibleHint(true);
	}

    @Override
    public CommonAdapter<Project> getAdapter() {
        return new ProjectAdapter(getActivity(), R.layout.list_item_project);
    }

    @Override
    public List<Project> getDatas(byte[] responeString) {
        return JsonUtils.getList(Project[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getWatchProjects(mUser.getId(), mCurrentPage, mHandler);
    }

	@Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(getActivity(), null, project.getId());
	}

    @Override
    protected String getEmptyTip() {
        return "暂无watch";
    }
}
