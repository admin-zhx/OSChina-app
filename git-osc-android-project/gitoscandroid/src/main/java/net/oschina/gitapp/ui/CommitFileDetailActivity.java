package net.oschina.gitapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.SourceEditor;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 代码文件详情
 *
 * @author 火蚁
 * @created 2014-06-13
 */
@SuppressLint("SetJavaScriptEnabled")
public class CommitFileDetailActivity extends BaseActivity {

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;

    private Menu optionsMenu;

    private SourceEditor mEditor;

    private Project mProject;

    private CommitDiff mCommitDiff;

    private Commit mCommit;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置actionbar加载动态
        setContentView(R.layout.activity_code_file_view);
        ButterKnife.inject(this);
        appContext = AppContext.getInstance();
        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        mCommitDiff = (CommitDiff) intent.getSerializableExtra(Contanst.COMMITDIFF);
        mCommit = (Commit) intent.getSerializableExtra(Contanst.COMMIT);
        init();
    }

    private void init() {
        String path = mCommitDiff.getNew_path();
        int index = path.lastIndexOf("/");
        if (index == -1) {
            mActionBar.setTitle(path);
        } else {
            mActionBar.setTitle(path.substring(index + 1));
        }
        mActionBar.setSubtitle("提交" + mCommit.getShortId());

        mEditor = new SourceEditor(webview);

        loadCode(mProject.getId(), mCommit.getId(), mCommitDiff.getNew_path());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.commit_file_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                loadCode(mProject.getId(), mCommit.getId(), mCommitDiff.getNew_path());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCode(final String projectId, final String commitId, final String filePath) {

        GitOSCApi.getCommitFileDetail(projectId, commitId, filePath, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                if (body != null) {
                    webview.setVisibility(View.VISIBLE);
                    mEditor.setSource(filePath, body, false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    tipInfo.setLoadError("读取失败，文件可能已被删除");
                } else {
                    tipInfo.setLoadError();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                webview.setVisibility(View.GONE);
                tipInfo.setLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                tipInfo.setHiden();
            }
        });
    }
}
