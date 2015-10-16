package net.oschina.gitapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectAdapter;
import net.oschina.gitapp.api.GitOSCApi;
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

/**
 * 搜索项目界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-07-10
 */
public class SearchActivity extends BaseActivity implements
        OnQueryTextListener, OnItemClickListener {

    @InjectView(R.id.search_view)
    SearchView searchView;
    @InjectView(R.id.listView)
    EnhanceListView listView;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;

    private InputMethodManager imm;

    private ProjectAdapter adapter;

    private String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        initView();
        steupList();
    }

    private void initView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        tipInfo.setHiden();
        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load(mKey, 1);
            }
        });
    }

    private void steupList() {
        adapter = new ProjectAdapter(this,
                R.layout.list_item_project);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setPageSize(15);
        listView.setOnLoadMoreListener(new EnhanceListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int pageNum, int pageSize) {
                load(mKey, pageNum);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String arg0) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        mKey = arg0;
        adapter.clear();
        load(arg0, 1);
        imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Project p = adapter.getItem(position);
        if (p != null) {
            UIHelper.showProjectDetail(this, p, null);
        }
    }

    private void load(final String key, final int page) {
        GitOSCApi.searchProjects(key, page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Project> projects = JsonUtils.getList(Project[].class, responseBody);
                tipInfo.setHiden();
                if (projects.size() > 0) {
                    adapter.addItem(projects);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    if (page == 1 || page == 0) {
                        listView.setVisibility(View.GONE);
                        tipInfo.setEmptyData("未找到相关的项目");
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
                if (page != 0 && page != 1) {

                } else {
                    tipInfo.setLoading();
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}
