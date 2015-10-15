package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/24.
 */
public class Language implements Serializable{
    private int id;
    private String name;
    private int projects_count;
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProjects_count() {
        return projects_count;
    }

    public void setProjects_count(int projects_count) {
        this.projects_count = projects_count;
    }

    public String getCreated_at() {

        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return name;
    }
}

/*[{"created_at":"2013-08-01T22:39:56+08:00",
    "detail":null,
    "id":5,
    "ident":"Java",
    "name":"Java",
    "order":4,
    "parent_id":1,
    "projects_count":18988,
    "updated_at":"2013-08-01T22:39:56+08:00"},*/
