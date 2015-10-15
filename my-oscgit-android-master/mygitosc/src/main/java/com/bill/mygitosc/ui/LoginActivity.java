package com.bill.mygitosc.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Session;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.gson.GsonRequest;
import com.bill.mygitosc.utils.CryptUtils;
import com.bill.mygitosc.utils.OscApiUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener {
    @InjectView(R.id.username)
    EditText userName;

    @InjectView(R.id.password)
    EditText password;

    @InjectView(R.id.bt_login)
    Button loginButton;


    private InputMethodManager inputMethodManager;
    private ProgressDialog mLoginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setTitle(getString(R.string.login_tool_title));
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String account = userName.getText().toString();
            String pwd = password.getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
                loginButton.setEnabled(false);
            } else {
                loginButton.setEnabled(true);
            }
        }
    };

    private void initView() {
        String existUsername = sharedPreferences.getString(getString(R.string.login_username), "");
        if (!TextUtils.isEmpty(existUsername)) {
            userName.setText(existUsername);
        }
        String existPwd = sharedPreferences.getString(getString(R.string.login_pwd), "");
        if (!TextUtils.isEmpty(existPwd)) {
            password.setText(CryptUtils.decode(CryptUtils.ACCOUNT_PWD, existPwd));
        }

        if (!TextUtils.isEmpty(existUsername) && !TextUtils.isEmpty(existPwd)) {
            loginButton.setEnabled(true);
        }
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 添加文本变化监听事件
        userName.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        password.setOnEditorActionListener(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_login;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelLoginOrNot();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            cancelLoginOrNot();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //dialog有问题，不能随主题变化而变化
    private void cancelLoginOrNot() {
        if (!TextUtils.isEmpty(userName.getText().toString()) &&
                !TextUtils.isEmpty(password.getText().toString())) {
            AlertDialog.Builder builder = generateAlterDialog();
            builder.setTitle(getString(R.string.login_leave_dialog_title)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } else {
            finish();
        }
    }

    @OnClick(R.id.bt_login)
    public void onClick() {
        inputMethodManager.hideSoftInputFromWindow(password.getWindowToken(), 0);
        checkLogin();
    }

    private void checkLogin() {
        String username = userName.getText().toString();
        String passwd = password.getText().toString();

        //检查用户输入的参数
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getString(R.string.msg_login_username_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            Toast.makeText(this, getString(R.string.msg_login_passwork_null), Toast.LENGTH_SHORT).show();
            return;
        }
        Login(username, passwd);
    }

    private void Login(final String username, final String passwd) {
        if (mLoginProgressDialog == null) {
            mLoginProgressDialog = new ProgressDialog(this);
            mLoginProgressDialog.setCancelable(true);
            mLoginProgressDialog.setCanceledOnTouchOutside(false);
            mLoginProgressDialog.setMessage(getString(R.string.login_tips));
            mLoginProgressDialog.show();
        }

        RequestQueue mQueue = Volley.newRequestQueue(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("email", username);
        map.put("password", passwd);

        GsonRequest<Session> gsonRequest = new GsonRequest<Session>(map, OscApiUtils.getLoginURL(), Session.class,
                new Response.Listener<Session>() {
                    @Override
                    public void onResponse(Session response) {
                        AppContext.getInstance().setSession(response);
                        editor.putString(getString(R.string.login_username), username).commit();
                        editor.putString(getString(R.string.login_pwd), CryptUtils.encode(CryptUtils.ACCOUNT_PWD, passwd)).commit();
                        mLoginProgressDialog.dismiss();
                        setResult(AppContext.LOGIN_SUCCESS_EVNET);
                        LoginActivity.this.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoginProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, getString(R.string.login_fail_hint), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(gsonRequest);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //在输入法里点击了“完成”，则去登录
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkLogin();
            inputMethodManager.hideSoftInputFromWindow(password.getWindowToken(), 0);
            return true;
        }
        return false;
    }

}
