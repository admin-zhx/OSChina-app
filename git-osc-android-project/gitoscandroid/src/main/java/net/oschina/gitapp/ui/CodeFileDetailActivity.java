package net.oschina.gitapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppConfig;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.util.MarkdownUtils;
import net.oschina.gitapp.util.SourceEditor;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 代码文件详情
 *
 * @author 火蚁
 * @created 2014-06-04
 */
@SuppressWarnings("deprecation")
public class CodeFileDetailActivity extends BaseActivity {

    @InjectView(R.id.webview)
    WebView webview;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;
    private AppContext mContext;

    private Menu optionsMenu;

    private SourceEditor editor;

    private CodeFile mCodeFile;

    private Project mProject;

    private String mFileName;

    private String mPath;

    private String mRef;

    private String url_link = null;

    private void downloadFile() {
        String path = AppConfig.DEFAULT_SAVE_FILE_PATH;
        boolean res = FileUtils.writeFile(mCodeFile.getContent().getBytes(),
                path, mFileName);
        if (res) {
            UIHelper.ToastMessage(mContext, "文件已经保存在" + path);
        } else {
            UIHelper.ToastMessage(mContext, "保存文件失败");
        }
    }

    private void showEditCodeFileActivity() {
        Intent intent = new Intent(CodeFileDetailActivity.this,
                CodeFileEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contanst.CODE_FILE, mCodeFile);
        bundle.putSerializable(Contanst.PROJECT, mProject);
        bundle.putString(Contanst.BRANCH, mRef);
        bundle.putString(Contanst.PATH, mPath);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置actionbar加载动态
        setContentView(R.layout.activity_code_file_view);
        ButterKnife.inject(this);
        mContext = AppContext.getInstance();
        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        mFileName = intent.getStringExtra("fileName");
        mPath = intent.getStringExtra("path");
        mRef = intent.getStringExtra("ref");
        init();
        loadCode(mProject.getId(), mPath, mRef);

        url_link = GitOSCApi.NO_API_BASE_URL + mProject.getOwner().getUsername()
                + "/" + mProject.getPath() + "/"
                + "blob" + "/" + mRef + "/" + mPath;

        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCode(mProject.getId(), mPath, mRef);
            }
        });
    }

    private void init() {
        mTitle = mFileName;
        mActionBar.setTitle(mFileName);
        mSubTitle = mRef;
        mActionBar.setSubtitle(mSubTitle);
        editor = new SourceEditor(webview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.projet_code_detail_menu, menu);
        optionsMenu = menu;
        updateMenuState();
        return true;
    }

    private void updateMenuState() {
        if (mCodeFile == null) {
            optionsMenu.findItem(R.id.open_browser).setVisible(false);
            optionsMenu.findItem(R.id.copy).setVisible(false);
            optionsMenu.findItem(R.id.download).setVisible(false);
        } else {
            optionsMenu.findItem(R.id.open_browser).setVisible(true);
            optionsMenu.findItem(R.id.copy).setVisible(true);
            optionsMenu.findItem(R.id.download).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                loadCode(mProject.getId(), mPath, mRef);
                break;
            case R.id.copy:
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cbm.setText(url_link);
                GitViewUtils.showToast("复制成功");
                break;
            case R.id.open_browser:
                if (!mProject.isPublic()) {
                    if (!mContext.isLogin()) {
                        UIHelper.showLoginActivity(CodeFileDetailActivity.this);
                        return false;
                    }
                    url_link = url_link + "?private_token="
                            + AppContext.getToken();
                }
                UIHelper.openBrowser(CodeFileDetailActivity.this, url_link);
                break;
            case R.id.download:
                downloadFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCode(final String projectId, final String path,
                               final String ref_name) {
        GitOSCApi.getCodeFileDetail(projectId, path, ref_name, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                webview.setVisibility(View.VISIBLE);
                CodeFile codeFile = JsonUtils.toBean(CodeFile.class, responseBody);
                mCodeFile = codeFile;
                editor.setMarkdown(MarkdownUtils.isMarkdown(mPath));
                editor.setSource(mPath, mCodeFile);

                updateMenuState();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                webview.setVisibility(View.GONE);
                tipInfo.setLoadError();
            }

            @Override
            public void onStart() {
                super.onStart();
                tipInfo.setLoading();
                webview.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                tipInfo.setHiden();
            }
        });
    }
}
