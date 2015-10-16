package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.ProjectMember;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.dialog.ProjectMembersSelectDialog;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.util.TypefaceUtils;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/4/23.
 */
public class NewIssueActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.et_tile)
    EditText etTile;
    @InjectView(R.id.et_desc)
    EditText etDesc;
    @InjectView(R.id.tv_touser_icon)
    TextView tvTouserIcon;
    @InjectView(R.id.tv_touser)
    TextView tvTouser;
    @InjectView(R.id.ll_touser)
    LinearLayout llTouser;
    @InjectView(R.id.tv_milestone_icon)
    TextView tvMilestoneIcon;
    @InjectView(R.id.tv_milestone)
    TextView tvMilestone;
    @InjectView(R.id.ll_milestone)
    LinearLayout llMilestone;

    private Project mProject;

    private Issue mIssue;

    private MenuItem send;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);

        mActionBar.setTitle("新建issue");
        mActionBar.setSubtitle(mProject.getOwner().getName() + "/" + mProject.getName());
        TypefaceUtils.setSemantic(tvTouserIcon);
        TypefaceUtils.setOcticons(tvMilestoneIcon);

        etTile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateMenuState();
            }
        });
    }

    private ProjectMembersSelectDialog membersSelectDialog;
    private String memberId = "";
    private ProjectMembersSelectDialog.CallBack memberSelectCallBack = new ProjectMembersSelectDialog.CallBack() {
        @Override
        public void callBack(ProjectMember projectMember) {
            memberId = projectMember.getId();
            tvTouser.setText(projectMember.getName());
        }

        @Override
        public void clear() {
            memberId = "";
            tvTouser.setText("未指派");
        }
    };

    @Override
    @OnClick({R.id.ll_touser, R.id.ll_milestone})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_touser:
                showMemberSelectDialog();
                break;
            case R.id.ll_milestone:
                break;
            default:
                break;
        }
    }

    private void showMemberSelectDialog() {
        if (membersSelectDialog == null) {
            membersSelectDialog = new ProjectMembersSelectDialog(this, mProject.getId(), memberSelectCallBack);
        }
        membersSelectDialog.show(memberId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_issue_menu, menu);
        send = menu.findItem(R.id.send);
        send.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                pubNewIssue();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenuState() {
        if (send == null) return;
        if (etTile.getText().toString().isEmpty()) {
            send.setEnabled(false);
        } else {
            send.setEnabled(true);
        }
    }

    private void pubNewIssue() {
        String title = etTile.getText().toString();
        String desc = etDesc.getText().toString();
        final AlertDialog pubing = LightProgressDialog.create(this, "提交中...");
        GitOSCApi.pubCreateIssue(mProject.getId(), title, desc, memberId, "", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Issue issue = JsonUtils.toBean(Issue.class, responseBody);
                if (issue != null) {
                    GitViewUtils.showToast("创建成功");
                    NewIssueActivity.this.finish();
                } else {
                    GitViewUtils.showToast("创建失败");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                GitViewUtils.showToast("issue创建失败");
            }

            @Override
            public void onStart() {
                super.onStart();
                pubing.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pubing.dismiss();
            }
        });
    }
}
