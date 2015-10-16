package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.ui.fragments.IssueDetailViewPagerFragment;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * issue详情activity
 *
 * @author 火蚁(http://my.oschina.net/LittleDY)
 * @created 2014-08-25
 */
public class IssueDetailActivity extends BaseActivity {

    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;

    private FragmentManager mFragmentManager;

    private Project mProject;

    private Issue mIssue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        mFragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
        final String projectId = intent.getStringExtra(Contanst.PROJECTID);
        final String issueId = intent.getStringExtra(Contanst.ISSUEID);
        if (mIssue == null || mProject == null) {
            loadIssueAndProject(projectId, issueId);
        } else {
            initData();
        }
        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIssueAndProject(projectId, issueId);
            }
        });
    }

    private void initData() {
        String title = "Issue " + (mIssue.getIid() == 0 ? "" : "#" + mIssue.getIid());
        mActionBar.setTitle(title);
        mActionBar.setSubtitle(mProject.getOwner().getName() + "/"
                + mProject.getName());
        tipInfo.setHiden();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.issue_content, IssueDetailViewPagerFragment.newInstance(mProject, mIssue)).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void loadIssueAndProject(final String projectId, final String issueId) {
        GitOSCApi.getProject(projectId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Project p = JsonUtils.toBean(Project.class, responseBody);
                if (p != null) {
                    mProject = p;
                    GitOSCApi.getIssueDetail(projectId, issueId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Issue issue = JsonUtils.toBean(Issue.class, responseBody);
                            if (issue != null) {
                                mIssue = issue;
                                initData();
                            } else {
                                setLoadError();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            setLoadError();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            tipInfo.setHiden();
                        }
                    });
                } else {
                    setLoadError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setLoadError();
            }

            @Override
            public void onStart() {
                super.onStart();
                tipInfo.setLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void setLoadError() {
        tipInfo.setLoadError();
    }
}
