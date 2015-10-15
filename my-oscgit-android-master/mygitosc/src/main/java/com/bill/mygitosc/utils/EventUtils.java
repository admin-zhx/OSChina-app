package com.bill.mygitosc.utils;

import android.content.Context;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Event;

/**
 * Created by liaobb on 2015/8/3.
 */
public class EventUtils {
    private static String getEventsTitle(Event event) {
        String title = "";
        if (event.getEvents().getIssue() != null) {
            title = " #" + event.getEvents().getIssue().getIid();
        }
        if (event.getEvents().getPull_request() != null) {
            title = " #" + event.getEvents().getPull_request().getIid();
        }
        return title;
    }

    public static String parseEventTitle(Context context, String author_name, String pAuthor_And_pName, Event event) {
        author_name = "(" + author_name + ")";
        pAuthor_And_pName = "<" + pAuthor_And_pName + ">";
        String title = "";
        String eventTitle = "";
        int action = event.getAction();
        switch (action) {
            case Event.EVENT_TYPE_CREATED:// 创建了issue
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.in_project_title) + pAuthor_And_pName + context.getString(R.string.create_project_title) + eventTitle;
                break;
            case Event.EVENT_TYPE_UPDATED:// 更新项目
                title = context.getString(R.string.update_project_title) + pAuthor_And_pName;
                break;
            case Event.EVENT_TYPE_CLOSED:// 关闭项目
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.close_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case Event.EVENT_TYPE_REOPENED:// 重新打开了项目
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.reopen_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case Event.EVENT_TYPE_PUSHED:// push
                eventTitle = event.getData().getRef()
                        .substring(event.getData().getRef().lastIndexOf("/") + 1);
                title = context.getString(R.string.pull_project_title) + pAuthor_And_pName + " -- " + eventTitle + context.getString(R.string.branch_title);
                break;
            case Event.EVENT_TYPE_COMMENTED:// 评论
                if (event.getEvents().getIssue() != null) {
                    eventTitle = "Issues";
                } else if (event.getEvents().getPull_request() != null) {
                    eventTitle = "PullRequest";
                }
                eventTitle = eventTitle + getEventsTitle(event);
                title = context.getString(R.string.comment_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case Event.EVENT_TYPE_MERGED:// 合并
                eventTitle = event.getTarget_type() + getEventsTitle(event);
                title = context.getString(R.string.accept_project_title) + pAuthor_And_pName + " -- " + eventTitle;
                break;
            case Event.EVENT_TYPE_JOINED:// # User joined project
                title = context.getString(R.string.enter_project_title) + pAuthor_And_pName;
                break;
            case Event.EVENT_TYPE_LEFT:// # User left project
                title = context.getString(R.string.leave_project_title) + pAuthor_And_pName;
                break;
            case Event.EVENT_TYPE_FORKED:// fork了项目
                title = context.getString(R.string.fork_project_title) + pAuthor_And_pName;
                break;
            default:
                title = context.getString(R.string.update_project_default_title);
                break;
        }
        title = author_name + " " + title;
        return title;
    }
}
