package com.bill.mygitosc.bean;


import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/22.
 */
public class Project implements Serializable {
    private int id;
    private String name;
    private Owner owner;
    private String description;
    private int forks_count;
    private int stars_count;
    private int watches_count;
    private String language;
    private String created_at;
    private String last_push_at;
    private boolean stared;
    private boolean watched;

    public Project(String s) {
        this.name=s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStared() {
        return stared;
    }

    public void setStared(boolean stared) {
        this.stared = stared;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getForks_count() {
        return forks_count;
    }

    public void setForks_count(int forks_count) {
        this.forks_count = forks_count;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getStars_count() {
        return stars_count;
    }

    public void setStars_count(int stars_count) {
        this.stars_count = stars_count;
    }

    public int getWatches_count() {
        return watches_count;
    }

    public void setWatches_count(int watches_count) {
        this.watches_count = watches_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLast_push_at() {
        return last_push_at;
    }

    public void setLast_push_at(String last_push_at) {
        this.last_push_at = last_push_at;
    }

}

/*[{"id":478257,
"name":"ssdb4j",
"description":"\u53c8\u4e00\u4e2aSSDB\u7684Java\u9a71\u52a8",
"default_branch":"master",
"owner":{"id":4674,
		"username":"wendal",
		"email":"wendal1985@gmail.com",
		"name":"Wendal",
		"state":"active",
		"created_at":"2013-06-03T13:55:28+08:00",
		"portrait":null,
		"new_portrait":"http://secure.gravatar.com/avatar/89bbfb169f008b30a569c2de6b3e62f7?s=40&d=mm"
		},
"public":true,
"path":"ssdb4j",
"path_with_namespace":"wendal/ssdb4j",
"issues_enabled":true,
"pull_requests_enabled":true,
"wiki_enabled":true,
"created_at":"2015-07-22T15:12:43+08:00",
"namespace":{"created_at":"2013-06-03T13:55:28+08:00",
			"description":"",
			"id":4525,
			"name":"wendal",
			"owner_id":4674,
			"path":"wendal",
			"updated_at":"2013-06-03T13:55:28+08:00"
			},
"last_push_at":null,
"parent_id":null,
"fork?":false,
"forks_count":0,
"stars_count":0,
"watches_count":1,
"language":null,
"paas":null,
"stared":null,
"watched":null,
"relation":null,
"recomm":1,
"parent_path_with_namespace":null
},
..........
}*/
