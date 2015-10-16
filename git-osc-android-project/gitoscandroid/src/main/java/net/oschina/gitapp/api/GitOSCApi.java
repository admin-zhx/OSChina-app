package net.oschina.gitapp.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.ShippingAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;

import static net.oschina.gitapp.api.AsyncHttpHelp.get;
import static net.oschina.gitapp.api.AsyncHttpHelp.getHttpClient;
import static net.oschina.gitapp.api.AsyncHttpHelp.getPrivateTokenWithParams;
import static net.oschina.gitapp.api.AsyncHttpHelp.post;

/**
 * Git@OSC API
 * <p/>
 * Created by 火蚁 on 15/4/10.
 */
public class GitOSCApi {

    public final static String HOST = "git.oschina.net/";
    private static final String API_VERSION = "api/v3/";// API版本
    public final static String HTTPS = "https://";
    public final static String HTTP = "http://";
    public final static String BASE_URL = HTTP + HOST + API_VERSION;
    public final static String NO_API_BASE_URL = HTTP + HOST;
    public final static String PROJECTS = BASE_URL + "projects/";
    public final static String USER = BASE_URL + "user/";
    public final static String EVENT = BASE_URL + "events/";
    public final static String UPLOAD = BASE_URL + "upload";
    public final static String NOTIFICATION = USER + "notifications/";
    public final static String VERSION = BASE_URL + "app_version/new/android";

    public static void login(String account, String passwod, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("email", account);
        params.put("password", passwod);
        AsyncHttpHelp.post(BASE_URL + "session", params, handler);
    }

    public static void getExploreLatestProject(int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        AsyncHttpHelp.get(PROJECTS + "latest", params, handler);
    }

    public static void getExploreFeaturedProject(int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(PROJECTS + "featured", params, handler);
    }

    public static void getExplorePopularProject(int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(PROJECTS + "popular", params, handler);
    }

    public static void getMyProjects(int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(PROJECTS, params, handler);
    }

    public static void getMyEvents(int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(EVENT, params, handler);
    }

    public static void getStarProjects(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + "/" + uid + "/stared_projects", handler);
    }

    public static void getWatchProjects(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uid + "/watched_projects", handler);
    }

    public static void getProject(String pId, AsyncHttpResponseHandler handler) {
        RequestParams params = AsyncHttpHelp.getPrivateTokenWithParams();
        get(PROJECTS + pId, params, handler);
    }

    public static void getProject(String userName, String projectName, AsyncHttpResponseHandler handler) {
        RequestParams params = AsyncHttpHelp.getPrivateTokenWithParams();
        get(PROJECTS + userName + "%2F" + projectName, params, handler);
    }

    public static void searchProjects(String query, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(PROJECTS + "search/" + URLEncoder.encode(query), params, handler);
    }

    public static void getUserProjects(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uid + "/" + "projects", params, handler);
    }

    public static void getUserEvents(String uid, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(EVENT + "user" + "/" + uid, params, handler);
    }

    public static void getProjectCommits(String pId, int page, String refName, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        params.put("ref_name", refName);
        get(PROJECTS + pId + "/" + "/repository/commits", params, handler);
    }

    public static void getProjectCodeTree(String pId, String path, String refName, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("path", path);
        params.put("ref_name", refName);
        get(PROJECTS + pId + "/repository/tree", params, handler);
    }

    public static void getProjectIssues(String pId, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(PROJECTS + pId + "/" + "issues", params, handler);
    }

    public static void getProjectBranchs(String pId, AsyncHttpResponseHandler handler) {
        get(PROJECTS + pId + "/repository/branches", handler);
    }

    public static void getProjectTags(String pId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + pId + "/repository/tags", params, handler);
    }

    public static void getIssueDetail(String pId, String issueId, AsyncHttpResponseHandler handler) {
        get(PROJECTS + pId + "/issues/" + issueId, handler);
    }

    public static void getIssueComments(String pId, String issueId, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        get(PROJECTS + pId + "/issues/" + issueId + "/notes", params, handler);
    }

    public static void pubIssueComment(String pId, String issueId, String body, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("body", body);
        post(PROJECTS + pId + "/issues/" + issueId + "/notes", params, handler);
    }

    public static void getCodeFileDetail(String projectId, String file_path, String ref, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("file_path", file_path);
        params.put("ref", ref);
        get(PROJECTS + projectId + "/repository/files", params, handler);
    }

    public static void getReadMeFile(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + projectId + "/readme", params, handler);
    }

    public static void getCommitDiffList(String projectId, String commitId, AsyncHttpResponseHandler handler) {
        RequestParams params = AsyncHttpHelp.getPrivateTokenWithParams();
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/diff", params, handler);
    }

    public static void getCommitCommentList(String projectId, String commitId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/comment", params, handler);
    }

    public static void getCommitFileDetail(String projectId, String commitId, String filePath, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("filepath", filePath);
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/blob", params, handler);
    }

    public static void getProjectMembers(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + projectId + "/members", params, handler);
    }

    /**
     * 加载项目的里程碑
     */
    public static void getProjectMilestone(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + projectId + "/milestones", params, handler);
    }

    /**
     * 创建一个issue
     *
     * @param projectId
     * @param title
     * @param description
     * @param assignee_id
     * @param milestone_id
     * @return
     * @throws AppException
     */
    public static void pubCreateIssue(String projectId, String title, String description, String assignee_id, String milestone_id, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("description", description);
        params.put("title", title);
        params.put("assignee_id", assignee_id);
        params.put("milestone_id", milestone_id);
        post(PROJECTS + projectId + "/issues", params, handler);
    }

    /**
     * 上传文件
     *
     * @return
     */
    public static void upLoadFile(File file, AsyncHttpResponseHandler handler) throws FileNotFoundException {
        RequestParams params = getPrivateTokenWithParams();
        params.put("file", file);
        AsyncHttpClient client = getHttpClient();
        client.addHeader("Content-disposition", "filename=\"" + file.getName() + "\"");
        client.post(UPLOAD, params, handler);
    }

    /***
     * 更新用户头像
     * @param protraitUrl
     * @param handler
     */
    public static void updateUserProtrait(String protraitUrl, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("path", protraitUrl);
        post(USER + "portrait", params, handler);
    }

    /***
     * 获取通知
     * @param filter
     * @param all
     * @param projectId
     * @param handler
     */
    public static void getNotification(String filter, String all, String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("filter", filter);
        params.put("all", all);
        get(NOTIFICATION, params, handler);
    }

    /**
     * 设置通知为已读
     */
    public static void setNotificationReaded(String notificationId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(NOTIFICATION + notificationId, params, handler);
    }

    /**
     * 获得App更新的信息
     */
    public static void getUpdateInfo(AsyncHttpResponseHandler handler) {
        get(VERSION, handler);
    }

    /**
     * 获得语言列表
     */
    public static void getLanguageList(AsyncHttpResponseHandler handler) {
        get(PROJECTS + "languages", handler);
    }

    /**
     * 根据语言的ID获得项目的列表
     *
     * @param languageId
     * @param page
     * @return
     * @throws AppException
     */
    public static void getLanguageProjectList(String languageId, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(PROJECTS + "languages/" + languageId, params, handler);
    }

    /**
     * star or unstar一个项目
     *
     * @param projectId
     * @return
     * @throws AppException
     */
    public static void starProject(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/star", params, handler);
    }

    public static void unStarProject(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/unstar", params, handler);
    }

    public static void watchProject(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/watch", params, handler);
    }

    public static void unWatchProject(String projectId, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/unwatch", params, handler);
    }

    public static void getRandomProject(AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("luck", 1);
        get(PROJECTS + "random", params, handler);
    }

    public static void updateRepositoryFiles(String projectId, String ref, String file_path, String branch_name, String content, String commit_message, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("ref", ref);
        params.put("file_path", file_path);
        params.put("branch_name", branch_name);
        params.put("content", content);
        params.put("commit_message", commit_message);
        post(PROJECTS + projectId + "/repository/files", params, handler);
    }

    /**
     * 获得某个用户star的项目列表
     */
    public static void getUserStarProjects(String uId, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uId + "/stared_projects", params, handler);
    }

    public static void getUserWatchProjects(String uId, int page, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uId + "/watched_projects", params, handler);
    }

    /**
     * 获取用户的收货信息
     */
    public static void getUserShippingAddress(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(BASE_URL + "users/" + uid, params, handler);
    }

    /**
     * 更新用户的收货信息
     */
    public static void updateUserShippingAddress(String uid, ShippingAddress shippingAddress, AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        params.put("name", shippingAddress.getName());
        params.put("tel", shippingAddress.getTel());
        params.put("address", shippingAddress.getAddress());
        params.put("comment", shippingAddress.getComment());
        post(BASE_URL + "users/" + uid, params, handler);
    }

    /**
     * 获得抽奖活动的信息
     */
    public static void getLuckMsg(AsyncHttpResponseHandler handler) {
        RequestParams params = getPrivateTokenWithParams();
        get(PROJECTS + "luck_msg", handler);
    }

}
