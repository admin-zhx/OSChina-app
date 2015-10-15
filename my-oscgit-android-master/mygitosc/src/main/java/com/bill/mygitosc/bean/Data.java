package com.bill.mygitosc.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liaobb on 2015/7/28.
 */
public class Data implements Serializable {
    private String ref;
    private List<Commits> commits;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<Commits> getCommits() {
        return commits;
    }

    public void setCommits(List<Commits> commits) {
        this.commits = commits;
    }
}
