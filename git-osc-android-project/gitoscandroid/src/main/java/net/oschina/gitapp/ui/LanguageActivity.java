package net.oschina.gitapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ProjectAdapter;
import net.oschina.gitapp.adapter.ViewHolder;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Language;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.widget.EnhanceListView;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LanguageActivity extends BaseActivity implements
        ActionBar.OnNavigationListener, OnItemClickListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;
    @InjectView(R.id.listView)
    EnhanceListView listView;

    private ProjectAdapter mProjectAdapter;

    private CommonAdapter<Language> mLanguageAdapter;

    private String mLanguageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        ButterKnife.inject(this);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mProjectAdapter = new ProjectAdapter(this, R.layout.list_item_project);

        mLanguageAdapter = new CommonAdapter<Language>(getApplicationContext(), R.layout.languages) {
            @Override
            public void convert(ViewHolder vh, Language item) {
                vh.setText(R.id.language_name, item.getName());
            }
        };
        mActionBar.setListNavigationCallbacks(mLanguageAdapter, this);
        listView.setAdapter(mProjectAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(new EnhanceListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int pageNum, int pageSize) {
                loadProjects(mLanguageId, pageNum);
            }
        });
        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLanguageAdapter.getCount() == 0) {
                    loadLanguagesList();
                } else {
                    loadProjects(mLanguageId, 1);
                }
            }
        });
        loadLanguagesList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadProjects(final String languageId, final int page) {
        GitOSCApi.getLanguageProjectList(languageId, page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tipInfo.setHiden();
                List<Project> projects = JsonUtils.getList(Project[].class, responseBody);
                if (projects != null && !projects.isEmpty()) {
                    listView.setVisibility(View.VISIBLE);
                    mProjectAdapter.addItem(projects);
                } else {
                    if (page == 1) {
                        tipInfo.setEmptyData("暂无该语言相关的项目");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tipInfo.setLoadError();
            }

            @Override
            public void onStart() {
                super.onStart();
                if (page == 1) {
                    tipInfo.setLoading();
                    listView.setVisibility(View.GONE);
                }
            }
        });
    }

    void setFooterNotLanguages() {
        listView.setVisibility(View.GONE);
        tipInfo.setEmptyData("加载语言列表失败");
    }

    // 加载语言列表
    private void loadLanguagesList() {
        GitOSCApi.getLanguageList(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Language> languageList = JsonUtils.getList(Language[].class, responseBody);
                if (languageList != null && !languageList.isEmpty()) {
                    mLanguageAdapter.addItem(languageList);
                } else {
                    setFooterNotLanguages();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setFooterNotLanguages();
            }

            @Override
            public void onStart() {
                super.onStart();
                tipInfo.setLoading();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        Language language = mLanguageAdapter.getItem(arg0);
        if (language != null) {
            mProjectAdapter.clear();
            mLanguageId = language.getId();
            loadProjects(mLanguageId, 1);
            return true;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Project p = mProjectAdapter.getItem(position);
        if (p != null) {
            UIHelper.showProjectDetail(this, p, p.getId());
        }
    }
}
