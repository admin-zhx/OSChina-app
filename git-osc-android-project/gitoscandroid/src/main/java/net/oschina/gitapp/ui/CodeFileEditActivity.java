package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;

import org.apache.http.Header;

public class CodeFileEditActivity extends BaseActivity implements OnClickListener {
	
	private AppContext mAppContext;
	
	private CodeFile mCodeFile;
	
	private Project mProject;
	
	private String mPath;
	
	private String mBranch;
	
	private EditText mEditContent;
	
	private EditText mCommitMsg;
	
	private Button mCodeFilePub;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (mEditContent.getText().toString().equals(mCodeFile.getContent()) || StringUtils.isEmpty(mCommitMsg.getText().toString())) {
				mCodeFilePub.setEnabled(false);
			} else {
				mCodeFilePub.setEnabled(true);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_codefile);
		mAppContext = AppContext.getInstance();
		initView();
		initData();
	}
	
	private void initView() {
		mEditContent = (EditText) findViewById(R.id.codefile_edit);
		mCommitMsg = (EditText) findViewById(R.id.codefile_edit_msg);
		mCodeFilePub = (Button) findViewById(R.id.codefile_edit_pub);
		
		mEditContent.addTextChangedListener(mTextWatcher);
		mCommitMsg.addTextChangedListener(mTextWatcher);
		mCodeFilePub.setOnClickListener(this);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mCodeFile = (CodeFile) intent.getSerializableExtra(Contanst.CODE_FILE);
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mPath = intent.getStringExtra(Contanst.PATH);
		mBranch = intent.getStringExtra(Contanst.BRANCH);
		if (mCodeFile != null) {
			mEditContent.setText(mCodeFile.getContent());
			mTitle = mCodeFile.getFile_name();
			mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
		}
	}

	private void pubCommitCodeFile() {
		final String content = mEditContent.getText().toString();
		final String commit_message = mCommitMsg.getText().toString();
		if (content.equals(mCodeFile.getContent())) {
			UIHelper.ToastMessage(mAppContext, "文件内容没有改变");
			return;
		}
		if (StringUtils.isEmpty(commit_message)) {
			UIHelper.ToastMessage(mAppContext, "文件内容没有改变");
			return;
		}
        final AlertDialog pubing = LightProgressDialog.create(this, "正在提交...");


        GitOSCApi.updateRepositoryFiles(mProject.getId(), mBranch, mPath, mBranch, content, commit_message, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String msg = new String(responseBody);
                if (msg != null) {
                    GitViewUtils.showToast("提交成功");
                    CodeFileEditActivity.this.finish();
                } else {
                    GitViewUtils.showToast("提交失败");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                GitViewUtils.showToast("提交失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pubing.dismiss();
            }

            @Override
            public void onStart() {
                super.onStart();
                pubing.show();
            }
        });
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.codefile_edit_pub:
			pubCommitCodeFile();
			break;

		default:
			break;
		}
	}
	
}
