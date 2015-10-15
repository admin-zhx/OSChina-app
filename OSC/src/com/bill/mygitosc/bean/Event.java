package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/27.
 */
public class Event implements Serializable {
    /**
     * 动态的类型
     */
    public final static byte EVENT_TYPE_CREATED = 0x1;// 创建了issue
    public final static byte EVENT_TYPE_UPDATED = 0x2;// 更新项目
    public final static byte EVENT_TYPE_CLOSED = 0x3;// 关闭项目
    public final static byte EVENT_TYPE_REOPENED = 0x4;// 重新打开了项目
    public final static byte EVENT_TYPE_PUSHED = 0x5;// push
    public final static byte EVENT_TYPE_COMMENTED = 0x6;// 评论
    public final static byte EVENT_TYPE_MERGED = 0x7;// 合并
    public final static byte EVENT_TYPE_JOINED = 0x8; //# User joined project
    public final static byte EVENT_TYPE_LEFT = 0x9; //# User left project
    public final static byte EVENT_TYPE_FORKED = 0xb;// fork了项目
    private int id;
    private int action;
    private String target_type;
    private Owner author;
    private Project project;
    private String updated_at;
    private Data data;
    private Events events;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Owner getAuthor() {
        return author;
    }

    public void setAuthor(Owner author) {
        this.author = author;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }
}
