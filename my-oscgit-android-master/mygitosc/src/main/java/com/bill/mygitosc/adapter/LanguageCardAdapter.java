package com.bill.mygitosc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Language;
import com.bill.mygitosc.common.LanguageFilter;
import com.bill.mygitosc.ui.ViewLanguageProjectActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageCardAdapter extends BaseRecyclerAdapter<Language> implements Filterable {
    private List<Language> mOriginData;

    private View.OnClickListener languageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Language language = (Language) v.getTag();
            Intent intent = new Intent(context, ViewLanguageProjectActivity.class);
            intent.putExtra(ViewLanguageProjectActivity.LANGUAGE_ID, language.getId());
            intent.putExtra(ViewLanguageProjectActivity.LANGUAGE_NAME, language.getName());
            context.startActivity(intent);
        }
    };

    public LanguageCardAdapter(Context context) {
        super(context);
        mOriginData = new ArrayList<Language>();
    }

    @Override
    public Filter getFilter() {
        return new LanguageFilter(this, mOriginData);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_language_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Language language = mData.get(position);
        LanguageViewHolder languageHolder = (LanguageViewHolder) holder;
        languageHolder.languageName.setText(language.getName());
        languageHolder.languageThemeNum.setText(context.getString(R.string.theme_num_hint, language.getProjects_count()));

        languageHolder.itemView.setTag(language);
        languageHolder.itemView.setOnClickListener(languageClickListener);
    }

    public void initLanguageDataSet(List<Language> languages) {
        mOriginData.clear();
        mOriginData.addAll(languages);
        resetDataSet(languages);
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView languageName;
        TextView languageThemeNum;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            languageName = (TextView) itemView.findViewById(R.id.language_textview);
            languageThemeNum = (TextView) itemView.findViewById(R.id.languageThemeNum_textview);
        }
    }

    public void orderLanguageList(int orderIndicator) {
        Comparator<Language> comparator = null;
        switch (orderIndicator) {
            case 0:
                comparator = new Comparator<Language>() {
                    @Override
                    public int compare(Language l1, Language l2) {
                        if (l1.getProjects_count() > l2.getProjects_count()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                };
                break;
            case 1:
                comparator = new Comparator<Language>() {
                    @Override
                    public int compare(Language l1, Language l2) {
                        return l1.getName().compareTo(l2.getName());
                    }
                };
                break;
            default:
                break;
        }
        Collections.sort(mData, comparator);
        notifyDataSetChanged();
    }
}