package net.oschina.gitapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.AsyncHttpHelp;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Session;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.CyptoUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;
import net.oschina.gitapp.util.JsonUtils;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity
        implements OnClickListener, OnEditorActionListener, TextWatcher {

    @InjectView(R.id.et_account)
    AutoCompleteTextView etAccount;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_login)
    Button btLogin;
    private ProgressDialog mLoginProgressDialog;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        btLogin.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 添加文本变化监听事件
        etAccount.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etPassword.setOnEditorActionListener(this);

        String account = CyptoUtils.decode(Contanst.ACCOUNT_EMAIL, AppContext.getInstance().getProperty(Contanst.ACCOUNT_EMAIL));
        etAccount.setText(account);
        String pwd = CyptoUtils.decode(Contanst.ACCOUNT_PWD, AppContext.getInstance().getProperty(Contanst.ACCOUNT_PWD));
        etPassword.setText(pwd);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //在输入法里点击了“完成”，则去登录
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkLogin();
            //将输入法隐藏
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * 检查登录
     */
    private void checkLogin() {

        String email = etAccount.getText().toString();
        String passwd = etPassword.getText().toString();

        //检查用户输入的参数
        if (StringUtils.isEmpty(email)) {
            UIHelper.ToastMessage(this, getString(R.string.msg_login_email_null));
            return;
        }
        if (!StringUtils.isEmail(email)) {
            UIHelper.ToastMessage(this, getString(R.string.msg_login_email_error));
            return;
        }
        if (StringUtils.isEmpty(passwd)) {
            UIHelper.ToastMessage(this, getString(R.string.msg_login_pwd_null));
            return;
        }


        // 保存用户名和密码
        AppContext.getInstance().saveAccountInfo(CyptoUtils.encode(Contanst.ACCOUNT_EMAIL, email), CyptoUtils.encode(Contanst.ACCOUNT_PWD, passwd));

        login(email, passwd);
    }

    // 登录验证
    private void login(final String account, final String passwd) {
        if (mLoginProgressDialog == null) {
            mLoginProgressDialog = new ProgressDialog(this);
            mLoginProgressDialog.setCancelable(true);
            mLoginProgressDialog.setCanceledOnTouchOutside(false);
            mLoginProgressDialog.setMessage(getString(R.string.login_tips));
        }
        GitOSCApi.login(account, passwd, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Session session = JsonUtils.toBean(Session.class, responseBody);
                if (session != null) {
                    // 保存登录用户的信息
                    AppContext.getInstance().saveLoginInfo(session);
                    // 保存用户的私有token
                    if (session != null && session.get_privateToken() != null) {
                        String token = CyptoUtils.encode(AsyncHttpHelp.GITOSC_PRIVATE_TOKEN, session.get_privateToken());
                        AppContext.getInstance().setProperty(AsyncHttpHelp.PRIVATE_TOKEN, token);
                    }
                    //返回标识，成功登录
                    setResult(RESULT_OK);
                    // 发送用户登录成功的广播
                    BroadcastController.sendUserChangeBroadcase(LoginActivity.this);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401) {
                    GitViewUtils.showToast(R.string.msg_login_error);
                } else {
                    GitViewUtils.showToast("登录失败");
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (mLoginProgressDialog != null) {
                    mLoginProgressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mLoginProgressDialog != null && mLoginProgressDialog.isShowing()) {
                    mLoginProgressDialog.dismiss();
                }
            }
        });

    }

    @Override
    @OnClick(R.id.bt_login)
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
        checkLogin();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // 若密码和帐号都为空，则登录按钮不可操作
        String account = etAccount.getText().toString();
        String pwd = etPassword.getText().toString();
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)) {
            btLogin.setEnabled(false);
        } else {
            btLogin.setEnabled(true);
        }
    }
}
