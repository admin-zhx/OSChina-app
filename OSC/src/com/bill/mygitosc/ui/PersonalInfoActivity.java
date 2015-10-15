package com.bill.mygitosc.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Session;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends BaseActivity {

    @InjectView(R.id.listview)
    ListView listView;

    @InjectView(R.id.portrait)
    CircleImageView protrait;

    @InjectView(R.id.username)
    TextView username;

    @InjectView(R.id.description)
    TextView usedescription;

    @InjectView(R.id.bt_logout)
    Button logoutButton;

    private Session currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = AppContext.getInstance().getSession();
        if (currentSession != null) {
            initBaseInfo();
            initOtherInfo();
        }
    }

    private void initBaseInfo() {
        username.setText(currentSession.getName());
        if (!TextUtils.isEmpty(currentSession.getBio())) {
            usedescription.setText(currentSession.getBio());
        } else {
            usedescription.setText(getString(R.string.no_content));
        }

        Utils.getPorTraitFormURL(this, protrait, currentSession.getNew_portrait());
    }

    private void initOtherInfo() {
        List<String> mData = new ArrayList<String>();
        String email = currentSession.getEmail();
        if (TextUtils.isEmpty(email)) {
            email = getString(R.string.no_content);
        }
        String weibo = currentSession.getWeibo();
        if (TextUtils.isEmpty(weibo)) {
            weibo = getString(R.string.no_content);
        }
        String blog = currentSession.getBlog();
        if (TextUtils.isEmpty(blog)) {
            blog = getString(R.string.no_content);
        }

        mData.add(getString(R.string.email) + email);
        mData.add(getString(R.string.weibo) + weibo);
        mData.add(getString(R.string.blog) + blog);
        mData.add(getString(R.string.followersTitle) + currentSession.getFollow().getFollowers());
        mData.add(getString(R.string.followingTitle) + currentSession.getFollow().getFollowing());
        mData.add(getString(R.string.watchedTitle) + currentSession.getFollow().getWatched());
        mData.add(getString(R.string.starredTitle) + currentSession.getFollow().getStarred());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_listview_item, mData);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.personal_info_title));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_personal_info;
    }

    @OnClick(R.id.bt_logout)
    public void logout() {
        if (currentSession != null) {
            AppContext.getInstance().setSession(null);
            setResult(AppContext.LOGOUT_SUCCESS_EVNET);
            finish();
        }
    }
}
