package com.bill.mygitosc.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Session;
import com.bill.mygitosc.common.AppContext;
import com.bill.mygitosc.common.DoubleClickExitHelper;
import com.bill.mygitosc.fragment.FindInfoTabFragment;
import com.bill.mygitosc.fragment.LanguageCardFragment;
import com.bill.mygitosc.fragment.MySelfInfoTabFragment;
import com.bill.mygitosc.fragment.ShakeFragment;
import com.bill.mygitosc.gson.GsonRequest;
import com.bill.mygitosc.utils.CryptUtils;
import com.bill.mygitosc.utils.OscApiUtils;
import com.bill.mygitosc.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity {

    private String CURRENT_NAV_VIEW_MENU_ITEM = "current_nav_view_menu_item";

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.nv_menu)
    NavigationView mNavigationView;

    @InjectView(R.id.id_username)
    TextView userNameTextView;

    @InjectView(R.id.my_portrait)
    CircleImageView myPortrait;

    private int currentNavViewMenuItem;
    private boolean rightHandOn;

    private DoubleClickExitHelper doubleClickExitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentNavViewMenuItem = savedInstanceState.getInt(CURRENT_NAV_VIEW_MENU_ITEM);
        } else {
            currentNavViewMenuItem = R.id.menu_item_explore;
        }
        doubleClickExitHelper = new DoubleClickExitHelper(this);

        setupDrawerContent(mNavigationView);
        initMainContent(currentNavViewMenuItem);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (rightHandOn != sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false)) {
            rightHandOn = !rightHandOn;
            if (rightHandOn) {
                setNavigationViewGravity(Gravity.END);
            } else {
                setNavigationViewGravity(Gravity.START);
            }
        }
        if (AppContext.getInstance().getSession() != null) {
            Session session = AppContext.getInstance().getSession();
            userNameTextView.setText(session.getName());
            Utils.getPorTraitFormURL(this, myPortrait, session.getNew_portrait());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_NAV_VIEW_MENU_ITEM, currentNavViewMenuItem);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        MenuItem currentMenuItem = menu.findItem(currentNavViewMenuItem);
        currentMenuItem.setChecked(true);

        toolbar.setTitle(currentMenuItem.getTitle());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        rightHandOn = sharedPreferences.getBoolean(getString(R.string.right_hand_mode_key), false);
        if (rightHandOn) {
            setNavigationViewGravity(Gravity.END);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int menuItemId = menuItem.getItemId();
                        if (menuItemId == R.id.menu_item_settings) {
                            Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(intent3);
                            return true;
                        } else if (menuItemId == R.id.menu_item_changeTheme) {
                            changeTheme();
                            return true;
                        }

                        mDrawerLayout.closeDrawers();
                        if (currentNavViewMenuItem != menuItemId) {
                            if (menuItemId == R.id.menu_item_myself && AppContext.getInstance().getSession() == null) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                return true;
                            }
                            toolbar.setTitle(menuItem.getTitle());
                            menuItem.setChecked(true);
                            initMainContent(menuItemId);
                            currentNavViewMenuItem = menuItem.getItemId();
                        }
                        return true;
                    }
                });

        autoLogin();
    }

    private void initMainContent(int navViewMenuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (navViewMenuItem == R.id.menu_item_explore) {
            fragmentTransaction.replace(R.id.main_content_layout, new FindInfoTabFragment(), null).commit();
        } else if (navViewMenuItem == R.id.menu_item_myself) {
            fragmentTransaction.replace(R.id.main_content_layout, MySelfInfoTabFragment.newInstance(AppContext.getInstance().getSession().getId()), null)
                    .commit();
        } else if (navViewMenuItem == R.id.menu_item_language) {
            fragmentTransaction.replace(R.id.main_content_layout, new LanguageCardFragment(), null).commit();
        } else if (navViewMenuItem == R.id.menu_item_shake) {
            fragmentTransaction.replace(R.id.main_content_layout, new ShakeFragment(), null).commit();
        } else {
        }
    }

    private void autoLogin() {
        String username = sharedPreferences.getString(getString(R.string.login_username), "");
        String pwdBeforeDecode = sharedPreferences.getString(getString(R.string.login_pwd), "");
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwdBeforeDecode)) {
            userNameTextView.setText(getString(R.string.drawer_logining_hint));
            myPortrait.setClickable(false);
            String pwd = CryptUtils.decode(CryptUtils.ACCOUNT_PWD, pwdBeforeDecode);

            RequestQueue mQueue = Volley.newRequestQueue(this);
            Map<String, String> map = new HashMap<String, String>();
            map.put("email", username);
            map.put("password", pwd);

            GsonRequest<Session> gsonRequest = new GsonRequest<Session>(map, OscApiUtils.getLoginURL(), Session.class,
                    new Response.Listener<Session>() {
                        @Override
                        public void onResponse(Session response) {
                            AppContext.getInstance().setSession(response);
                            userNameTextView.setText(response.getName());
                            Utils.getPorTraitFormURL(MainActivity.this, myPortrait, response.getNew_portrait());
                            myPortrait.setClickable(true);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userNameTextView.setText(getString(R.string.need_ano_login_hint));
                    myPortrait.setClickable(true);
                }
            });
            mQueue.add(gsonRequest);
        }
    }

    private void setNavigationViewGravity(int gravity) {
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        layoutParams.gravity = gravity;
        mNavigationView.setLayoutParams(layoutParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder builder = generateAlterDialog();
                builder.setTitle(getString(R.string.about_dialog_title)).setMessage(R.string.about_dialog_message)
                        .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return true;
            case R.id.action_remind:
                if (AppContext.getInstance().getSession() == null) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, NotificationActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawers();
                return true;
            }
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeTheme() {
        AppContext application = AppContext.getInstance();
        if (application.getCurrentTheme() == R.style.GreenAppBaseTheme) {
            application.setDefaultTheme();
        } else {
            application.setGreenTheme();
        }
        recreate();
    }

    @OnClick(R.id.my_portrait)
    public void onClickPortrait() {
        if (AppContext.getInstance().getSession() != null) {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivityForResult(intent, AppContext.MAIN_START_EVNET);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, AppContext.MAIN_START_EVNET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppContext.MAIN_START_EVNET)
            if (resultCode == AppContext.LOGIN_SUCCESS_EVNET) {
                Session session = AppContext.getInstance().getSession();
                userNameTextView.setText(session.getName());
                Utils.getPorTraitFormURL(this, myPortrait, session.getNew_portrait());
            } else if (resultCode == AppContext.LOGOUT_SUCCESS_EVNET) {
                userNameTextView.setText(getString(R.string.need_ano_login_hint));
                myPortrait.setImageResource(R.drawable.mini_avatar);
            }
    }
}
