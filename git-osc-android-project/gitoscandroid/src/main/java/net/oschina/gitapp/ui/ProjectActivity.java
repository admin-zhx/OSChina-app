package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.StarWatchOptionResult;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.util.TypefaceUtils;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 项目详情界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-05-26 上午10：26
 * @最后更新：
 * @更新内容：
 * @更新者：
 */
@SuppressWarnings("deprecation")
public class ProjectActivity extends BaseActivity implements
        OnClickListener {

    private final int ACTION_LOAD_PROJECT = 0;// 加载项目
    private final int ACTION_LOAD_PARENT_PROJECT = 1;// 加载项目的父项目信息
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_update)
    TextView tvUpdate;
    @InjectView(R.id.project_flag)
    TextView projectFlag;
    @InjectView(R.id.tv_description)
    TextView tvDescription;
    @InjectView(R.id.project_star_stared)
    TextView projectStarStared;
    @InjectView(R.id.project_starnum)
    TextView projectStarnum;
    @InjectView(R.id.project_watch_stared)
    TextView projectWatchStared;
    @InjectView(R.id.project_watchnum)
    TextView projectWatchnum;
    @InjectView(R.id.tv_createed)
    TextView tvCreateed;
    @InjectView(R.id.tv_forknum)
    TextView tvForknum;
    @InjectView(R.id.tv_locked)
    TextView tvLocked;
    @InjectView(R.id.tv_language)
    TextView tvLanguage;
    @InjectView(R.id.tv_ownername)
    TextView tvOwnername;
    @InjectView(R.id.project_fork_form)
    TextView projectForkForm;
    @InjectView(R.id.content)
    ScrollView content;
    @InjectView(R.id.ll_fork_from)
    LinearLayout forkFrom;

    private Menu menu;

    private Project mProject;

    private String projectId;

    private AppContext mAppContext;

    private String url_link = null;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.inject(this);
        mAppContext = AppContext.getInstance();
        initView();
    }

    private void initView() {
        // 拿到传过来的project对象
        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        projectId = intent.getStringExtra(Contanst.PROJECTID);
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            String[] res = intent.getDataString().split("/");
            if (res.length >= 2) {
                String userName = res[res.length - 2];
                String projectName = res[res.length - 1];
                loadProjectForName(userName, projectName);
            }
        } else {

            if (null == mProject) {
                loadProject(ACTION_LOAD_PROJECT, projectId);

            } else {
                initData();
            }
        }

        // set font icon
        TypefaceUtils.setFontAwsome((TextView) findView(R.id.fi_time));
        TypefaceUtils.setSemantic((TextView) findView(R.id.fi_ower), (TextView) findView(R.id.fi_language),
                (TextView) findView(R.id.fi_lock), (TextView) findView(R.id.fi_fork), (TextView) findView(R.id.fi_ll_fork));
        TypefaceUtils.setOcticons((TextView) findView(R.id.fi_readme), (TextView) findView(R.id.fi_code),
                (TextView) findView(R.id.fi_commit), (TextView) findView(R.id.fi_issue));

        tipInfo.setOnClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProject(ACTION_LOAD_PROJECT, projectId);
            }
        });
    }

    private void initData() {
        mActionBar.setTitle(mProject.getName());
        mActionBar.setSubtitle(mProject.getOwner().getName());

        tvName.setText(mProject.getName());
        tvUpdate.setText("更新于 " + getUpdateTime());
        setFlag();

        tvDescription.setText(getDescription(mProject.getDescription()));
        projectStarnum.setText(mProject.getStars_count() + "");
        setStared(mProject.isStared());
        projectWatchnum.setText(mProject.getWatches_count() + "");
        setWatched(mProject.isWatched());
        tvCreateed.setText(StringUtils.friendly_time(mProject.getCreatedAt()));
        tvForknum.setText(mProject.getForks_count() + "");
        tvLocked.setText(getLocked());
        tvLanguage.setText(getLanguage());
        tvOwnername.setText(mProject.getOwner().getName());

        // 显示是否有fork信息
        initForkMess();

        // 记录项目的地址链接：
        url_link = GitOSCApi.NO_API_BASE_URL + mProject.getOwner().getUsername() + "/" + mProject.getPath();
        // 截取屏幕
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (bitmap == null) {
                    bitmap = UIHelper.takeScreenShot(ProjectActivity.this);
                }
            }
        }, 500);

        updateMenuState(true);
    }

    private void setStared(boolean stared) {
        int textRes = R.string.sem_star;
        if (stared) {
            textRes = R.string.sem_star;
            ((TextView) findViewById(R.id.project_star_text)).setText("unstar");
        } else {
            textRes = R.string.sem_empty_star;
            ((TextView) findViewById(R.id.project_star_text)).setText("star");
        }
        projectStarStared.setText(textRes);
        TypefaceUtils.setSemantic(projectStarStared);
    }

    private void setWatched(boolean watched) {
        int textRes = R.string.sem_watch;
        if (watched) {
            textRes = R.string.sem_empty_watch;
            ((TextView) findViewById(R.id.project_watch_text)).setText("unwatch");
        } else {
            textRes = R.string.sem_watch;
            ((TextView) findViewById(R.id.project_watch_text)).setText("watch");
        }
        projectWatchStared.setText(textRes);
        TypefaceUtils.setSemantic(projectWatchStared);
    }

    private void setFlag() {
        // 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）
        int textRes = R.string.oct_fork;
        if (mProject.getParent_id() != null) {
            textRes = R.string.oct_fork;
        } else if (mProject.isPublic()) {
            textRes = R.string.oct_repo;
        } else {
            textRes = R.string.oct_lock;
        }
        projectFlag.setText(textRes);
        TypefaceUtils.setOcticons(projectFlag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.projet_menu, menu);
        this.menu = menu;
        updateMenuState(false);
        return true;
    }

    private void updateMenuState(boolean isShow) {
//        if (isShow) {
//
//            menu.findItem(R.id.new_issue).setVisible(true);
//            menu.findItem(R.id.share).setVisible(true);
//            menu.findItem(R.id.copy).setVisible(true);
//            menu.findItem(R.id.open_browser).setVisible(true);
//
//        } else {
//
//            menu.findItem(R.id.new_issue).setVisible(false);
//            menu.findItem(R.id.share).setVisible(false);
//            menu.findItem(R.id.copy).setVisible(false);
//            menu.findItem(R.id.open_browser).setVisible(false);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mProject == null) {
            return false;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.share:
                UIHelper.showShareOption(ProjectActivity.this, mProject.getName(), url_link,
                        "我在关注《" + mProject.getOwner().getName() + "的项目" + mProject.getName() + "》" + "，你也来瞧瞧呗！", bitmap);
                break;
            case R.id.copy:
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cbm.setText(url_link);
                UIHelper.ToastMessage(mAppContext, "已复制到剪贴板");
                break;
            case R.id.open_browser:
                UIHelper.openBrowser(ProjectActivity.this, url_link);
                break;
            case R.id.new_issue:
                // 新增issue
                UIHelper.showIssueEditOrCreate(AppContext.getInstance(), mProject, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getUpdateTime() {
        if (mProject.getLast_push_at() != null) {
            return StringUtils.friendly_time(mProject.getLast_push_at());
        } else {
            return StringUtils.friendly_time(mProject.getCreatedAt());
        }
    }

    private String getDescription(String description) {
        if (description == null || StringUtils.isEmpty(description)) {
            return "该项目暂无简介！";
        } else {
            return description.replaceAll(" ", "");
        }
    }

    private String getLocked() {
        if (mProject.isPublic()) {
            return "Public";
        } else {
            return "Private";
        }
    }

    private String getLanguage() {
        if (mProject.getLanguage() == null) {
            return "未指定";
        } else {
            return mProject.getLanguage();
        }
    }

    private void initForkMess() {
        if (mProject.getParent_id() == null) {
            return;
        } else {
            forkFrom.setVisibility(View.VISIBLE);
            findViewById(R.id.project_fork_ll_line).setVisibility(View.VISIBLE);
            loadProject(ACTION_LOAD_PARENT_PROJECT, mProject.getParent_id() + "");
            forkFrom.setOnClickListener(this);
        }
    }

    private void loadProject(final int action, final String projectId) {
        GitOSCApi.getProject(projectId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                content.setVisibility(View.VISIBLE);
                tipInfo.setHiden();
                Project p = JsonUtils.toBean(Project.class, responseBody);
                if (p != null) {

                    if (action == ACTION_LOAD_PROJECT) {
                        mProject = p;
                        initData();
                    } else {
                        projectForkForm.setText(p.getOwner().getName() + " / " + p.getName());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (action == ACTION_LOAD_PROJECT) {
                    tipInfo.setLoadError();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                tipInfo.setLoading();
            }
        });
    }

    private void loadProjectForName(String userName, String projectName) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(projectName)) {
            setProjectNotFound();
            return;
        }
        GitOSCApi.getProject(userName, projectName, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                content.setVisibility(View.VISIBLE);
                tipInfo.setHiden();
                Project p = JsonUtils.toBean(Project.class, responseBody);
                if (p != null) {
                    mProject = p;
                    initData();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tipInfo.setLoadError();
                if (statusCode == 404) {
                    setProjectNotFound();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                tipInfo.setLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                tipInfo.setHiden();
            }
        });
    }

    private void setProjectNotFound() {
        tipInfo.setLoadError("仓库中找不到该项目喔");
    }

    @Override
    @OnClick({R.id.ll_star, R.id.ll_watch, R.id.ll_owner, R.id.ll_fork_from,
            R.id.ll_readme, R.id.ll_code, R.id.ll_commits, R.id.ll_issues})
    public void onClick(View v) {

        if (mProject == null) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.ll_star:
                starOption();
                break;
            case R.id.ll_watch:
                watchOption();
                break;
            case R.id.ll_owner:
                if (mProject.getOwner() != null) {
                    UIHelper.showUserInfoDetail(ProjectActivity.this, mProject.getOwner(), mProject.getOwner().getId());
                }
                break;
            case R.id.ll_fork_from:
                if (mProject.getParent_id() != null) {
                    UIHelper.showProjectDetail(ProjectActivity.this, null, mProject.getParent_id() + "");
                }
                break;
            case R.id.ll_readme:
                UIHelper.showProjectReadMeActivity(ProjectActivity.this, mProject);
                break;
            case R.id.ll_code:
                UIHelper.showProjectCodeActivity(ProjectActivity.this, mProject);
                break;
            case R.id.ll_commits:
                UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_COMMITS);
                break;
            case R.id.ll_issues:
                UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_ISSUES);
                break;
        }
    }

    private void watchOption() {
        if (mProject == null) {
            return;
        }
        if (!mAppContext.isLogin()) {
            UIHelper.showLoginActivity(ProjectActivity.this);
            return;
        }

        String message = "";
        if (mProject.isWatched()) {
            message = "正在unwatch该项目...";
        } else {
            message = "正在watch该项目...";
        }
        final AlertDialog loadingDialog = LightProgressDialog.create(this, message);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resMsg = "";
                StarWatchOptionResult res = JsonUtils.toBean(StarWatchOptionResult.class, responseBody);
                if (res.getCount() > mProject.getWatches_count()) {
                    setWatched(true);
                    mProject.setWatched(true);
                    resMsg = "watch成功";
                } else {
                    setWatched(false);
                    mProject.setWatched(false);
                    resMsg = "unwatch成功";
                }
                mProject.setWatches_count(res.getCount());
                projectWatchnum.setText(res.getCount() + "");
                UIHelper.ToastMessage(mAppContext, resMsg);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                UIHelper.ToastMessage(mAppContext, "操作失败");
            }

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingDialog.dismiss();
            }
        };
        if (mProject.isWatched()) {
            GitOSCApi.unWatchProject(mProject.getId(), handler);
        } else {
            GitOSCApi.watchProject(mProject.getId(), handler);
        }
    }

    private void starOption() {
        if (mProject == null) {
            return;
        }
        if (!mAppContext.isLogin()) {
            UIHelper.showLoginActivity(ProjectActivity.this);
            return;
        }
        String message = "";
        if (mProject.isStared()) {
            message = "正在unstar该项目...";
        } else {
            message = "正在star该项目...";
        }
        final AlertDialog loadingDialog = LightProgressDialog.create(this, message);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resMsg = "";
                StarWatchOptionResult res = JsonUtils.toBean(StarWatchOptionResult.class, responseBody);
                if (res.getCount() > mProject.getStars_count()) {
                    setStared(true);
                    mProject.setStared(true);
                    resMsg = "star成功";
                } else {
                    setStared(false);
                    mProject.setStared(false);
                    resMsg = "unstar成功";
                }
                mProject.setStars_count(res.getCount());
                projectStarnum.setText(res.getCount() + "");
                UIHelper.ToastMessage(mAppContext, resMsg);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingDialog.dismiss();
            }
        };
        if (mProject.isStared()) {
            GitOSCApi.unStarProject(mProject.getId(), handler);
        } else {
            GitOSCApi.starProject(mProject.getId(), handler);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mProject == null) {
            return false;
        }
        return super.onMenuOpened(featureId, menu);
    }
}