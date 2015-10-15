package com.bill.mygitosc.utils;

import com.bill.mygitosc.common.AppContext;

/**
 * Created by liaobb on 2015/7/22.
 */
public class OscApiUtils {
    public final static String HOST = "git.oschina.net/";
    public final static String API_VERSION = "api/v3/";// API°æ±¾
    public final static String HTTP = "http://";
    public final static String BASE_URL = HTTP + HOST + API_VERSION;
    public final static String PROJECTS = BASE_URL + "projects/";
    public final static String USER = BASE_URL + "user/";
    public final static String EVENT = BASE_URL + "events/user/";
    public final static String MYEVENT = BASE_URL + "events/";
    public final static String UPLOAD = BASE_URL + "upload";
    public final static String NOTIFICATION = USER + "notifications/";
    public final static String VERSION = BASE_URL + "app_version/new/android";


    public static String getProjectsURL(String projectType, int page) {
        return PROJECTS + projectType + "?page=" + page;
    }

    public static String getSelfProjectsURL(int userID, String selfProjectType, int page) {
        return USER + userID + "/" + selfProjectType + "?page=" + page;
    }

    public static String getLanguageProjectsURL(int languageID, int page) {
        return PROJECTS + "languages/" + languageID + "/?page=" + page;
    }

    public static String getProjectURl(int userID) {
        return PROJECTS + userID + "?private_token=" + AppContext.getInstance().getSession().getPrivate_token();
    }

    public static String getSelfEventURL(int userID, int page) {
        return EVENT + userID + "?page=" + page;
    }

    public static String getMySelfEventURL(int page) {
        return MYEVENT + "?page=" + page + "&private_token=" + AppContext.getInstance().getSession().getPrivate_token();
    }


    public static String getLanguageListURL() {
        return PROJECTS + "languages";
    }


    public static String getSearchProjectURL(String searchKey, int page) {
        return PROJECTS + "search/" + searchKey + "?page=" + page;
    }

    public static String getReadmeURL(int projectId) {
        return PROJECTS + projectId + "/readme";
    }


    public static String getLoginURL() {
        return BASE_URL + "session";
    }

    public static String getRomProjectURL() {
        return PROJECTS + "random?luck=1";
    }

    public static String starProject(int projectId) {
        return PROJECTS + projectId + "/star";
    }

    public static String unStarProject(int projectId) {
        return PROJECTS + projectId + "/unstar";
    }

    public static String watchProject(int projectId) {
        return PROJECTS + projectId + "/watch";
    }

    public static String unWatchProject(int projectId) {
        return PROJECTS + projectId + "/unwatch";
    }
}
