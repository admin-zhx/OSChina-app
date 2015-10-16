package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.ImageUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.photoBrowse.PhotoBrowseActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.GitViewUtils;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.widget.ActionSheet;
import net.oschina.gitapp.widget.CircleImageView;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/5/4.
 */
public class MyInfoDetailActivity extends BaseActivity implements View.OnClickListener {

    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/OSChina/Git/Portrait/";

    public static final int LOCAL_PIC_CODE = 0;
    public static final int PHOTO_CODE = 1;

    @InjectView(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_jointime)
    TextView tvJointime;
    @InjectView(R.id.tv_description)
    TextView tvDescription;
    @InjectView(R.id.tv_followers)
    TextView tvFollowers;
    @InjectView(R.id.tv_stared)
    TextView tvStared;
    @InjectView(R.id.tv_following)
    TextView tvFollowing;
    @InjectView(R.id.tv_watched)
    TextView tvWatched;
    @InjectView(R.id.btn_logout)
    Button btnLogout;

    private User mUser;

    private final static int CROP = 400;
    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_detail);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        mUser = AppContext.getInstance().getLoginInfo();
        if (mUser != null) {
            tvName.setText(mUser.getName());
            if (TextUtils.isEmpty(mUser.getWeibo())) {
                tvDescription.setText("暂无填写");
            } else {
                tvDescription.setText(mUser.getBio());
            }

            tvJointime.setText(mUser.getCreated_at().substring(0, 10));

            String portrait = mUser.getPortrait() == null || mUser.getPortrait().equals("null") ? "" : mUser.getPortrait();
            if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
                ivPortrait.setImageResource(R.drawable.mini_avatar);
            } else {
                ImageLoader.getInstance().displayImage(mUser.getNew_portrait(), ivPortrait);
            }

            tvFollowers.setText(mUser.getFollow().getFollowers() + "");
            tvStared.setText(mUser.getFollow().getStarred() + "");
            tvFollowing.setText(mUser.getFollow().getFollowing() + "");
            tvWatched.setText(mUser.getFollow().getWatched() + "");
        }
    }

    @Override
    @OnClick({R.id.iv_portrait, R.id.ll_user_portrait, R.id.ll_user_nickname, R.id.ll_user_jointime,
            R.id.ll_user_description, R.id.ll_followers, R.id.ll_stared, R.id.ll_following,
            R.id.ll_watched, R.id.btn_logout})
    public void onClick(View v) {
        if (mUser == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_portrait:
                PhotoBrowseActivity.showPhotoBrowse(this, new String[]{mUser.getNew_portrait()}, 0);
                break;
            case R.id.ll_user_portrait:
                CharSequence[] items = {getString(R.string.img_from_album),
                        getString(R.string.img_from_camera)};
                imageChooseItem(items);
                break;
            case R.id.btn_logout:
                loginOut();
                break;
            default:
                break;
        }
    }

    private void imageChooseItem(CharSequence[] items) {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet
                .createBuilder(getApplicationContext(),
                        getSupportFragmentManager())
                .setCancelButtonTitle(R.string.cancle)
                .setOtherButtonTitles(getString(R.string.img_from_album), getString(R.string.img_from_camera))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case LOCAL_PIC_CODE: // 本地相册.
                                startImagePick();
                                break;
                            case PHOTO_CODE: // 照相.
                                startActionCamera();
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {
                    }
                }).show();
    }

    /**
     * 选择相册中的图片
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"),
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    /**
     * 相机拍照
     */
    private void startActionCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 拍照保存的绝对路径
    private Uri getCameraTempFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            UIHelper.ToastMessage(AppContext.getInstance(), "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = System.currentTimeMillis() + "";
        // 照片命名
        String cropFileName = "osc_camera_" + timeStamp + ".jpeg";
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);
        cropUri = Uri.fromFile(protraitFile);
        this.origUri = this.cropUri;
        return this.cropUri;
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            UIHelper.ToastMessage(AppContext.getInstance(), "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = System.currentTimeMillis() + "";
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(this, uri);
        }
        String ext = FileUtils.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpeg" : ext;
        // 照片命名
        String cropFileName = "osc_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

        cropUri = Uri.fromFile(protraitFile);
        return this.cropUri;
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     *             裁剪后图片
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(data.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                uploadNewPhoto();// 上传新照片
                break;
        }
    }

    private void uploadNewPhoto() {
        final AlertDialog loading = LightProgressDialog.create(this, "正在上传头像...");
        loading.setCanceledOnTouchOutside(false);
        try {
            GitOSCApi.upLoadFile(protraitFile, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    UpLoadFile upLoadFile = JsonUtils.toBean(UpLoadFile.class, responseBody);
                    if (upLoadFile != null && upLoadFile.isSuccess()) {
                        final String protraitUrl = upLoadFile.getFile().getUrl();
                        GitOSCApi.updateUserProtrait(protraitUrl, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                GitViewUtils.showToast("头像已更新");
                                ivPortrait.setImageBitmap(protraitBitmap);
                                AppContext.getInstance().setProperty(Contanst.PROP_KEY_NEWPORTRAIT, protraitUrl);
                                BroadcastController.sendUserChangeBroadcase(MyInfoDetailActivity.this);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                GitViewUtils.showToast(statusCode + "更新头像失败");
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                loading.dismiss();
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                loading.setMessage("正在更新头像...");
                            }
                        });
                    } else {
                        GitViewUtils.showToast("头像上传失败");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    GitViewUtils.showToast("上传图片失败, 网络错误");
                }

                @Override
                public void onStart() {
                    super.onStart();
                    loading.show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    loading.dismiss();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loginOut() {
        AppContext.getInstance().logout();
        BroadcastController.sendUserChangeBroadcase(AppContext.getInstance());
        this.finish();
    }
}
