package net.oschina.gitapp.photoBrowse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.ImageLoaderUtils;
import net.oschina.gitapp.util.TypefaceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/4/29.
 */
public class PhotoBrowseActivity extends BaseActivity implements View.OnClickListener {

    public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
    private static final String BUNDLE_KEY_INDEX = "bundle_key_index";

    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.tv_photo_index)
    TextView tvPhotoIndex;
    @InjectView(R.id.tv_icon)
    TextView tvDownloadIcon;

    private PhotoAdapter adapter;

    private String[] imageUrls;

    private int index;

    public static void showPhotoBrowse(Context context, String[] images, int index) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoBrowseActivity.class);
        intent.putExtra(BUNDLE_KEY_INDEX, index);
        intent.putExtra(BUNDLE_KEY_IMAGES, images);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browse);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        mActionBar.setDisplayShowTitleEnabled(false);
        adapter = new PhotoAdapter(getSupportFragmentManager());

        viewpager.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            index = intent.getIntExtra(BUNDLE_KEY_INDEX, 1);
            imageUrls = intent.getStringArrayExtra(BUNDLE_KEY_IMAGES);
        }
        setIndex();
        adapter.add(imageUrls);
        viewpager.setCurrentItem(index);
        viewpager.setOffscreenPageLimit(1);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                setIndex();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TypefaceUtils.setSemantic(tvDownloadIcon);
    }

    private void setIndex() {
        tvPhotoIndex.setText((index + 1) + "/" + imageUrls.length);
    }

    @Override
    @OnClick({R.id.ll_download, R.id.tv_photo_index})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download:
            case R.id.tv_photo_index:
                saveImageToGallery();
                break;
            default:
                break;
        }
    }

    class PhotoAdapter extends FragmentStatePagerAdapter {

        private List<PhotoFragment> fragments;

        public PhotoAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @Override
        public PhotoFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void add(String[] imageUrls) {
            for (String url : imageUrls) {
                fragments.add(PhotoFragment.newInstance(url));
            }
            notifyDataSetChanged();
        }
    }

    /***
     * 保存图片到手机中
     *
     * update: 1.2014-05-06 下午 15：35
     * 更新内容: 修改图片下载完成之后再保存到图库中
     *
     */
    public void saveImageToGallery() {
        final Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrls[index], ImageLoaderUtils.getOption());
        ImageLoader.getInstance().loadImage(imageUrls[index], ImageLoaderUtils.getOption(), new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                saveImageToGallery(PhotoBrowseActivity.this, bitmap);
            }
        });
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "/oschina/gitosc");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
