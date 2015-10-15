package com.bill.mygitosc.fragment;

import com.bill.mygitosc.adapter.BaseStateRecyclerAdapter;
import com.bill.mygitosc.adapter.ProjectAdapter;
import com.bill.mygitosc.bean.Project;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by liaobb on 2015/7/23.
 */
public abstract class BaseProjectsRefreshFragment extends BaseSwipeRefreshFragment<Project> {
    protected static final String PROJECT_TYPE = "project_type";

    @Override
    protected boolean itemCompareTo(List<Project> list, Project item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (item.getId() == list.get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Type getGsonArrayType() {
        return new TypeToken<List<Project>>() {
        }.getType();
    }

    @Override
    protected BaseStateRecyclerAdapter getRecyclerAdapter() {
        BaseStateRecyclerAdapter mDataAdapter = new ProjectAdapter(getActivity());
        /*if (currentUserID != -1) {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(false);
        } else {
            ((ProjectAdapter) mDataAdapter).setPortraitClickable(true);
        }*/
        return mDataAdapter;
    }
}
