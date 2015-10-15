package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/30.
 */
public class Follow implements Serializable {
    private int followers;
    private int starred;
    private int following;
    private int watched;

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }
}
