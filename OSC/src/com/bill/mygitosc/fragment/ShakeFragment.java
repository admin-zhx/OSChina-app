package com.bill.mygitosc.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Project;
import com.bill.mygitosc.common.BitmapCache;
import com.bill.mygitosc.ui.ViewProjectInfoActivity;
import com.bill.mygitosc.utils.OscApiUtils;
import com.bill.mygitosc.common.ShakeListener;
import com.bill.mygitosc.common.TypefaceUtils;
import com.bill.mygitosc.utils.Utils;
import com.bill.mygitosc.gson.GsonRequest;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ShakeFragment extends Fragment {
    @InjectView(R.id.shakeImgUp)
    RelativeLayout mImgUp;

    @InjectView(R.id.shakeImgDown)
    RelativeLayout mImgDn;

    @InjectView(R.id.shake_loading)
    LinearLayout mLoaging;

    @InjectView(R.id.shakeres_paroject)
    RelativeLayout mShakeResProject;// 摇到项目

    @InjectView(R.id.iv_portrait)
    ImageView mProjectFace;
    @InjectView(R.id.tv_title)
    TextView mProjectTitle;
    @InjectView(R.id.tv_description)
    TextView mProjectDescription;
    @InjectView(R.id.tv_watch)
    TextView mProjectLanguage;
    @InjectView(R.id.tv_star)
    TextView mProjectWatchNums;
    @InjectView(R.id.tv_fork)
    TextView mProjectStarNums;
    @InjectView(R.id.tv_language)
    TextView mProjectForkNums;

    private final int DURATION_TIME = 600;

    private ShakeListener mShakeListener = null;
    private Vibrator mVibrator;
    private Project mProject;
    private SoundPool sndPool;
    private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
    private Bitmap mBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mVibrator = (Vibrator) getActivity().getApplication().getSystemService(
                getActivity().VIBRATOR_SERVICE);

        loadSound();
        mShakeListener = new ShakeListener(getActivity());
        // 监听到手机摇动
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                startAnim();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shake, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    private void loadSound() {

        sndPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        new Thread() {
            public void run() {
                try {
                    soundPoolMap.put(
                            0,
                            sndPool.load(
                                    getActivity().getAssets().openFd(
                                            "sound/shake_sound_male.mp3"), 1));

                    soundPoolMap.put(1, sndPool.load(
                            getActivity().getAssets().openFd("sound/shake_match.mp3"), 1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void startAnim() {
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                -0.5f);
        mytranslateanimup0.setDuration(DURATION_TIME);
        TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                +0.5f);
        mytranslateanimup1.setDuration(DURATION_TIME);
        mytranslateanimup1.setStartOffset(DURATION_TIME);
        animup.addAnimation(mytranslateanimup0);
        animup.addAnimation(mytranslateanimup1);
        mImgUp.startAnimation(animup);

        AnimationSet animdn = new AnimationSet(true);
        TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                +0.5f);
        mytranslateanimdn0.setDuration(DURATION_TIME);
        TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                -0.5f);
        mytranslateanimdn1.setDuration(DURATION_TIME);
        mytranslateanimdn1.setStartOffset(DURATION_TIME);
        animdn.addAnimation(mytranslateanimdn0);
        animdn.addAnimation(mytranslateanimdn1);
        mImgDn.startAnimation(animdn);

        // 动画监听，开始时显示加载状态，
        mytranslateanimdn0.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mShakeResProject.setVisibility(View.GONE);
                mShakeListener.stop();
                sndPool.play(soundPoolMap.get(0), (float) 0.2, (float) 0.2, 0, 0,
                        (float) 0.6);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadProject();
            }
        });
    }

    private void loadProject() {
        beforeLoading();
        final RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        GsonRequest<Project> gsonRequest = new GsonRequest<Project>(OscApiUtils.getRomProjectURL(), Project.class,
                new Response.Listener<Project>() {
                    @Override
                    public void onResponse(Project response) {
                        mProject = response;
                        if (mProject != null) {
                            mShakeResProject.setVisibility(View.VISIBLE);
                            mProjectTitle.setText(mProject.getOwner().getName() + " / " + mProject.getName());
                            String description = mProject.getDescription();
                            if (TextUtils.isEmpty(description)) {
                                description = getString(R.string.no_description_hint);
                            }
                            mProjectDescription.setText(description);

                            TypefaceUtils.setIconText(mProjectStarNums, getString(R.string.sem_watch) + " " + mProject.getWatches_count());
                            TypefaceUtils.setIconText(mProjectForkNums, getString(R.string.sem_star) + " " + mProject.getStars_count());
                            TypefaceUtils.setIconText(mProjectWatchNums, getString(R.string.sem_fork) + " " + mProject.getForks_count());

                            String language = mProject.getLanguage();
                            if (TextUtils.isEmpty(language)) {
                                mProjectLanguage.setVisibility(View.GONE);
                            } else {
                                TypefaceUtils.setIconText(mProjectLanguage, getString(R.string.sem_tag) + " " + mProject.getLanguage());
                            }
                            if (!Utils.checkNoPicMode(getActivity())) {
                                ImageLoader.ImageListener listener = ImageLoader.getImageListener(mProjectFace,
                                        R.drawable.mini_avatar, R.drawable.mini_avatar);
                                ImageLoader mImageLoader = new ImageLoader(mQueue, new BitmapCache());
                                mImageLoader.get(mProject.getOwner().getNew_portrait(), listener);
                            }
                        }
                        afterLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.no_random_project_find), Toast.LENGTH_SHORT).show();
                afterLoading();
            }
        });
        mQueue.add(gsonRequest);
    }

    private void beforeLoading() {
        mLoaging.setVisibility(View.VISIBLE);
        mShakeResProject.setVisibility(View.GONE);
    }

    private void afterLoading() {
        mLoaging.setVisibility(View.GONE);
        sndPool.play(soundPoolMap.get(1), (float) 0.2, (float) 0.2,
                0, 0, (float) 0.6);
        mVibrator.cancel();
        mShakeListener.start();
    }

    @OnClick(R.id.shakeres_paroject)
    public void showProjectInfo() {
        if (mProject != null) {
            Intent intent = new Intent(getActivity(), ViewProjectInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ViewProjectInfoActivity.VIEW_PROJECT_INFO, mProject);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shake_fragment, menu);
        menu.findItem(R.id.action_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_remind).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void startVibrato() {
        mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mShakeListener != null) {
            mShakeListener.stop();
        }
    }
}
