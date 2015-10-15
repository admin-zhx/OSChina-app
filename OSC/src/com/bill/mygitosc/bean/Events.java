package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/28.
 */
public class Events implements Serializable {
    private Issue issue;
    private Pull_Request pull_request;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Pull_Request getPull_request() {
        return pull_request;
    }

    public void setPull_request(Pull_Request pull_request) {
        this.pull_request = pull_request;
    }
}
