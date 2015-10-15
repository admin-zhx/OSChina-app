package com.bill.mygitosc.common;

import android.widget.Filter;

import com.bill.mygitosc.adapter.LanguageCardAdapter;
import com.bill.mygitosc.bean.Language;
import com.bill.mygitosc.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liaobb on 2015/7/30.
 */
public class LanguageFilter extends Filter {

    private final LanguageCardAdapter adapter;

    private final List<Language> originalList;

    private final List<Language> filteredList;

    public LanguageFilter(LanguageCardAdapter adapter, List<Language> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();
        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            for (Language language : originalList) {
                if (StringUtils.ignoreCaseContain(language.getName(), constraint.toString())) {
                    filteredList.add(language);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.resetDataSet((ArrayList<Language>) results.values);
    }
}
