package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectCodeTreeAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.dialog.ProjectRefSelectDialog;
import net.oschina.gitapp.photoBrowse.PhotoBrowseActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.util.TypefaceUtils;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 仓库代码
 * Created by 火蚁 on 15/4/21.
 */
public class ProjectCodeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.tv_paths)
    TextView tvPaths;

    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;
    @InjectView(R.id.tv_branch_icon)
    TextView tvBranchIcon;
    @InjectView(R.id.tv_branch_name)
    TextView tvBranchName;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.rl_branch)
    View switchBranch;

    private ProjectCodeTreeAdapter codeTreeAdapter;
    private Project project;

    private String path = "";
    private String refName = "master";

    private Menu optionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectcode);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            project = (Project) intent.getSerializableExtra(Contanst.PROJECT);
            mTitle = "代码";
            setActionBarTitle(mTitle);
            mSubTitle = project.getOwner().getName() + "/" + project.getName();
            setActionBarSubTitle(mSubTitle);
        }
        tipInfo.setLoading();
        setBranchInfo();
        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCode(path, false);
            }
        });
        codeTreeAdapter = new ProjectCodeTreeAdapter(this, R.layout.list_item_projectcodetree);
        listView.setAdapter(codeTreeAdapter);
        listView.setOnItemClickListener(this);
        loadCode(path, false);
        tvPaths.setMovementMethod(new LinkMovementMethod());
    }

    private Stack<List<CodeTree>> codeFloders = new Stack<>();
    private Stack<String> paths = new Stack<>();
    private boolean isLoading;

    /**
     * 加载代码树
     */
    private void loadCode(final String path, final boolean refresh) {
        GitOSCApi.getProjectCodeTree(project.getId(), getPath() + path, refName, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!refresh) {
                    paths.push(path);
                }
                checkShowPaths();
                tipInfo.setHiden();
                List<CodeTree> list = JsonUtils.getList(CodeTree[].class, responseBody);
                if (list != null && !list.isEmpty()) {
                    if (refresh) {
                        if (!codeFloders.isEmpty()) {
                            codeFloders.pop();
                        }
                    }
                    codeFloders.push(list);
                    switchBranch.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    codeTreeAdapter.clear();
                    codeTreeAdapter.addItem(list);
                } else {
                    GitViewUtils.showToast("该文件夹下面暂无文件");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!paths.isEmpty()) {
                    paths.pop();
                }
                if (path.isEmpty()) {
                    tipInfo.setLoadError("加载代码失败");
                } else {
                    GitViewUtils.showToast("加载代码失败");
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                isLoading = true;
                if (path.isEmpty() || refresh) {
                    tipInfo.setLoading();
                } else {
                    MenuItemCompat.setActionView(optionsMenu.findItem(0), R.layout.actionbar_indeterminate_progress);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoading = false;
                MenuItemCompat.setActionView(optionsMenu.findItem(0), null);
            }
        });
    }


    private void popPreCodeTree() {
        if (!paths.isEmpty()) {
            paths.pop();
        }
        if (!codeFloders.isEmpty()) {
            codeFloders.pop();
        }
        codeTreeAdapter.clear();
        codeTreeAdapter.addItem(codeFloders.get(codeFloders.size() - 1));
        checkShowPaths();
    }

    /**
     * 获取路径地址
     *
     * @return 仓库的路径
     */
    private String getPath() {
        if (paths.isEmpty()) {
            return "";
        }
        StringBuilder pathString = new StringBuilder("");
        for (int i = 0; i < paths.size(); i++) {
            pathString.append(paths.get(i));
            if (i != 0) {
                pathString.append("/");
            }
        }

        return pathString.toString();
    }

    private void checkShowPaths() {
        if (paths.empty() || paths.size() == 1) {
            tvPaths.setVisibility(View.GONE);
            return;
        }

        tvPaths.setVisibility(View.VISIBLE);

        String floders = project.getName() + "/" + getPath();
        PathString ps = new PathString(floders.replaceAll("/", " / "));
        tvPaths.setText(ps);
    }

    /**
     * 设置分支的信息
     */
    private void setBranchInfo() {
        TypefaceUtils.setOcticons(tvBranchIcon);
        tvBranchName.setText(refName);
    }

    @Override
    @OnClick({R.id.rl_branch})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_branch:
                loadBranchAndTag();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CodeTree codeTree = codeTreeAdapter.getItem(position);
        if (codeTree.getType().equalsIgnoreCase(CodeTree.TYPE_TREE)) {
            if (isLoading) return;
            loadCode(codeTree.getName(), false);
        } else {
            tryShowCode(codeTree);
        }
    }

    /**
     * 判断code的文件的类型显示不同的操作
     *
     * @param codeTree 判断文件，处理打开方式
     */
    private void tryShowCode(CodeTree codeTree) {

        String fileName = codeTree.getName();

        if (codeTree.isCodeTextFile(fileName)) {

            showDetail(fileName, refName);

        } else if (codeTree.isImage(fileName)) {
            showImageView(codeTree);
        } else {
            openWithBrowser(codeTree);
        }
    }

    private void showImageView(CodeTree currenCodeTree) {
        List<CodeTree> codeTrees = codeTreeAdapter.getDatas();
        List<String> images = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < codeTrees.size(); i++) {
            CodeTree codeTree = codeTrees.get(i);
            if (codeTree.isImage(codeTree.getName())) {
                String url = GitOSCApi.NO_API_BASE_URL + project.getOwner().getUsername() + "/" + project.getPath() + "/" + "raw" + "/" + refName + "/" + URLEncoder.encode(getPath() + codeTree.getName()) + "?private_token=" + AppContext.getToken();
                images.add(url);
            }
            if (codeTree.getId() == currenCodeTree.getId()) {
                index = i;
            }
        }
        PhotoBrowseActivity.showPhotoBrowse(this, images.toArray(new String[]{}), index);
    }

    /**
     * 在浏览器中在打开
     *
     * @param codeTree 代码文件
     */
    private void openWithBrowser(CodeTree codeTree) {
//        String url = URLs.URL_HOST + project.getOwner().getUsername() + URLs.URL_SPLITTER + project.getPath() + URLs.URL_SPLITTER + "blob" + URLs.URL_SPLITTER + refName + URLs.URL_SPLITTER + URLEncoder.encode(getFilePath(codeTree.getName())) + "?private_token=" + ApiClient.getToken(mAppContext);
//        UIHelper.openBrowser(this, url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        // 刷新按钮
        MenuItem refreshItem = menu.add(0, 0, 0, "刷新");
        refreshItem.setIcon(R.drawable.action_refresh);
        MenuItemCompat.setShowAsAction(refreshItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case 0:
                refresh();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        tvPaths.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        switchBranch.setVisibility(View.GONE);
        tipInfo.setLoading();
        loadCode("", true);
    }

    /**
     * 查看代码文件详情
     */
    private void showDetail(String fileName, String ref) {
        Intent intent = new Intent(AppContext.getInstance(), CodeFileDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contanst.PROJECT, project);
        bundle.putString("fileName", fileName);
        bundle.putString("path", getPath() + fileName);
        bundle.putString("ref", ref);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (codeFloders.isEmpty()) {
            return super.onSupportNavigateUp();
        }
        if (codeFloders.size() != 1) {
            popPreCodeTree();
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!codeFloders.isEmpty() && codeFloders.size() != 1) {
                popPreCodeTree();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onPathClick(String path, int index) {
        codeTreeAdapter.clear();

        // 使用缓存数据
        // 移除当前层级之后的数据
        for (int i = 0, count = codeFloders.size() - index; i < count - 1; i++) {
            codeFloders.pop();
            paths.pop();
        }

        codeTreeAdapter.addItem(codeFloders.peek());
        checkShowPaths();
    }

    class PathString extends SpannableString {
        public PathString(String text) {
            super(text);
            setup(text);
        }

        private void setup(String text) {
            int start = 0;
            if (text.replaceAll(" ", "").endsWith("/")) {
                text = text.substring(0, text.length() - 2);
            }
            int chatIndex = text.indexOf(File.separatorChar);
            int pathStart = chatIndex + 1;// 路径String位置，text最开始为工程名称，不包含在Path内，所以标注开始位置用于截取Path
            int pathIndex = 0;// 标注层级，用于获取缓存
            while (chatIndex >= 0) {
                String path = chatIndex > pathStart ? text.substring(pathStart, chatIndex) : " ";
                setSpan(new Clickable(path, pathIndex), start, chatIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                pathIndex++;
                start = chatIndex + 1;
                chatIndex = text.indexOf(File.separatorChar, start);
            }
        }

        class Clickable extends ClickableSpan {
            private final String mPath;
            private final int mIndex;

            public Clickable(String path, int index) {
                mPath = path;
                mIndex = index;
            }

            @Override
            public void onClick(View widget) {
                onPathClick(mPath, mIndex);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }
        }
    }

    private ProjectRefSelectDialog refSelectDialog;

    private ProjectRefSelectDialog.CallBack callBak = new ProjectRefSelectDialog.CallBack() {
        @Override
        public void onCallBack(Branch branch) {
            if (branch == null) {
                return;
            }
            ProjectCodeActivity.this.refName = branch.getName();
            ProjectCodeActivity.this.path = "";
            paths.clear();
            codeFloders.clear();
            tvPaths.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            switchBranch.setVisibility(View.GONE);
            tipInfo.setLoading();
            loadCode(path, false);
            tvBranchIcon.setText(branch.getIconRes());
            setBranchInfo();
        }
    };

    private void loadBranchAndTag() {
        if (refSelectDialog == null) {
            refSelectDialog = new ProjectRefSelectDialog(this, project.getId(), callBak);
        }
        refSelectDialog.show(refName);
    }
}
