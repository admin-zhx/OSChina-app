package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ProjectIssuesAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 项目issue列表
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectIssuesFragment extends BaseSwipeRefreshFragment<Issue> {
	
	private Project mProject;

	public static ProjectIssuesFragment newInstance(Project project) {
		ProjectIssuesFragment fragment = new ProjectIssuesFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
	}

    @Override
    public CommonAdapter<Issue> getAdapter() {
        return new ProjectIssuesAdapter(getActivity());
    }

    @Override
    public List<Issue> getDatas(byte[] responeString) {
        return JsonUtils.getList(Issue[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getProjectIssues(mProject.getId(), mCurrentPage, mHandler);
    }

	@Override
	public void onItemClick(int position, Issue issue) {
		UIHelper.showIssueDetail(getActivity(), mProject, issue, null, null);
	}

    @Override
    protected String getEmptyTip() {
        return "暂无issue";
    }

}
