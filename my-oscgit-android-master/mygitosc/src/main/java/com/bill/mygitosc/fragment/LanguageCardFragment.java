package com.bill.mygitosc.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.adapter.LanguageCardAdapter;
import com.bill.mygitosc.bean.Language;
import com.bill.mygitosc.cache.CacheManager;
import com.bill.mygitosc.ui.BaseActivity;
import com.bill.mygitosc.utils.OscApiUtils;
import com.bill.mygitosc.widget.TipInfoLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageCardFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private String LANGUAGE_CACHE_KEY = "language_list_cache";

    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TipInfoLayout tipInfoLayout;
    private LanguageCardAdapter languageCardAdapter;

    private int orderType = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_recycleview_layout, container, false);

        tipInfoLayout = (TipInfoLayout) view.findViewById(R.id.tip_info);
        tipInfoLayout.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        if (getActivity() instanceof BaseActivity) {
            swipeRefreshLayout.setColorSchemeColors(((BaseActivity) getActivity()).getColorPrimary());
        }
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.language_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        languageCardAdapter = new LanguageCardAdapter(getActivity());
        recyclerView.setAdapter(languageCardAdapter);

        requestData(false);
        return view;
    }

    private void requestData(boolean refreshFlag) {
        swipeRefreshLayout.setEnabled(false);
        if (refreshFlag) {
            RequestQueue mQueue = Volley.newRequestQueue(getActivity());
            final Gson gson = new Gson();
            StringRequest stringRequest = new StringRequest(OscApiUtils.getLanguageListURL(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            List<Language> newLanguageList = gson.fromJson(response, new TypeToken<List<Language>>() {
                            }.getType());
                            languageCardAdapter.initLanguageDataSet(newLanguageList);
                            setSwipeRefreshLoadedState();
                            new SaveCacheTask(getActivity(), (Serializable) newLanguageList, LANGUAGE_CACHE_KEY).execute();
                            tipInfoLayout.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setSwipeRefreshLoadedState();
                            if (languageCardAdapter.getItemCount() == 0) {
                                tipInfoLayout.setVisibility(View.VISIBLE);
                                tipInfoLayout.setLoadError();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.request_data_error_hint), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            mQueue.add(stringRequest);
        } else {
            tipInfoLayout.setVisibility(View.VISIBLE);
            tipInfoLayout.setLoading();
            new ReadCacheTask(getActivity()).execute(LANGUAGE_CACHE_KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_language_fragment, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_remind).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search_language);
        initSearchView(searchItem);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.action_order_language) {
            orderLanguageList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void orderLanguageList() {
        AlertDialog.Builder builder = generateAlterDialog();
        builder.setTitle(getString(R.string.title_order_language));
        String orderItem[] = {getString(R.string.order_item_themenum), getString(R.string.order_item_name)};

        final int oldOrderType = orderType;
        builder.setSingleChoiceItems(orderItem, orderType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderType = which;
            }
        });
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (oldOrderType != orderType) {
                    languageCardAdapter.orderLanguageList(orderType);
                }
            }
        });
        builder.show();
    }

    private void initSearchView(MenuItem searchItem) {
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);

        searchView.setQueryHint(getString(R.string.search_language_hint));
        SearchView.SearchAutoComplete textView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(getActivity().getResources().getColor(R.color.no_select_white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                languageCardAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    @Override
    public void onRefresh() {
        requestData(true);
    }

    private void setSwipeRefreshLoadedState() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(true);
    }

    private void readCacheListSuccess(Serializable list) {
        if (list == null) {
            requestData(true);
        } else {
            List<Language> languages = (List<Language>) list;
            if (languages.size() == 0) {
                requestData(true);
            } else {
                swipeRefreshLayout.setEnabled(true);
                tipInfoLayout.setVisibility(View.GONE);
                languageCardAdapter.initLanguageDataSet(languages);
            }
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    private class ReadCacheTask extends AsyncTask<String, Void, Serializable> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected Serializable doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return seri;
            }
        }

        @Override
        protected void onPostExecute(Serializable list) {
            super.onPostExecute(list);
            if (list != null) {
                readCacheListSuccess(list);
            } else {
                readCacheListSuccess(null);
            }
        }
    }
}

