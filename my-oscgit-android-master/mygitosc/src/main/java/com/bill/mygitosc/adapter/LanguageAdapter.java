package com.bill.mygitosc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Language;

import java.util.List;

/**
 * Created by liaobb on 2015/7/24.
 */
public class LanguageAdapter extends BaseAdapter {
    private Context context;
    private List<Language> languageList;

    public LanguageAdapter(Context context, List<Language> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    @Override
    public int getCount() {
        return languageList.size();
    }

    @Override
    public Object getItem(int position) {
        return languageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.language_spinner_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.spinner_language);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(languageList.get(position).getName());
        return convertView;
    }

    public void addAllDataSets(List<Language> newLanguageList) {
        languageList.clear();
        languageList.addAll(newLanguageList);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView textView;
    }
}
