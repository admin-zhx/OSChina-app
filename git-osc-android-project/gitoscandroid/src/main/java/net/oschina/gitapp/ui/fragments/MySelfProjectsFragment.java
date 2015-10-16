package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.MySelfProjectsAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 我的项目列表Fragment
 * @created 2014-05-12 下午14：24
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class MySelfProjectsFragment extends BaseSwipeRefreshFragment<Project> {
	
	public static MySelfProjectsFragment newInstance() {
		return new MySelfProjectsFragment();
	}
	
    @Override
    public CommonAdapter<Project> getAdapter() {
        return new MySelfProjectsAdapter(getActivity(), R.layout.list_item_myproject);
    }

    @Override
    public List<Project> getDatas(byte[] responeString) {
        return JsonUtils.getList(Project[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getMyProjects(mCurrentPage, mHandler);
    }

    @Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(getActivity(), null, project.getId());
	}
}
