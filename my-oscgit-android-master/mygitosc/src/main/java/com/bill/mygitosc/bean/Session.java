package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/24.
 */
public class Session implements Serializable {
    private int id;
    private String name;
    private String new_portrait;
    private String created_at;
    private String private_token;
    private String email;
    private String bio;
    private String blog;
    private String weibo;
    private Follow follow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNew_portrait() {
        return new_portrait;
    }

    public void setNew_portrait(String new_portrait) {
        this.new_portrait = new_portrait;
    }

    public String getPrivate_token() {
        return private_token;
    }

    public void setPrivate_token(String private_token) {
        this.private_token = private_token;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public Follow getFollow() {
        return follow;
    }

    public void setFollow(Follow follow) {
        this.follow = follow;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }
}

/*
* {"id":395040,
 "username":"Mr.LiaBin",
 "name":"Mr.LiaBin",
 "bio":null,
 "weibo":null,
 "blog":null,
 "theme_id":1,
 "state":"active",
 "created_at":"2015-05-14T12:16:40+08:00",
 "portrait":null,
 "email":"1250440341@qq.com",
 "new_portrait":"http://secure.gravatar.com/avatar/b104bec9cb8927589480fe83952f4751?s=40&d=mm",
 "follow":{"followers":0,
		   "starred":1,
		   "following":0,
		   "watched":1
		   },
 "private_token":"LsvU4AMp6PLVvDMMHUZ7",
 "is_admin":false,
 "can_create_group":true,
 "can_create_project":true,
 "can_create_team":true
 }
 */
