package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.IssueCommentAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * issue评论列表
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *         <p/>
 *         最后更新
 *         更新者
 * @created 2014-05-14 下午16:57
 */
public class IssueCommentFragment extends BaseSwipeRefreshFragment<GitNote> {

    private Project mProject;

    private Issue mIssue;

    public static IssueCommentFragment newInstance(Project project, Issue issue) {
        IssueCommentFragment fragment = new IssueCommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(Contanst.PROJECT, project);
        args.putSerializable(Contanst.COMMIT, issue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mProject = (Project) args.getSerializable(Contanst.PROJECT);
            mIssue = (Issue) args.getSerializable(Contanst.ISSUE);
        }
    }

    @Override
    public CommonAdapter<GitNote> getAdapter() {
        return new IssueCommentAdapter(getActivity(), R.layout.list_item_issue_commtent);
    }

    @Override
    public List<GitNote> getDatas(byte[] responeString) {
        return JsonUtils.getList(GitNote[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getIssueComments(mProject.getId(), mIssue.getId(), mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(int position, GitNote data) {

    }
}
