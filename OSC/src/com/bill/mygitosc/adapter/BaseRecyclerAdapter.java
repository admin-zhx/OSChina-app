package com.bill.mygitosc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liaobb on 2015/8/2.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected List<T> mData;
    protected Context context;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.mData = new ArrayList<T>();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void addDataSetToStart(List<T> list) {
        mData.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addDataSetToEnd(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void resetDataSet(List<T> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public List<T> getDataSet() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
