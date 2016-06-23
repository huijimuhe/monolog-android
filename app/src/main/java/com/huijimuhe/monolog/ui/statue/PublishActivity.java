package com.huijimuhe.monolog.ui.statue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.network.StatueApi;
import com.huijimuhe.monolog.data.account.Account;
import com.huijimuhe.monolog.AppContext;
import com.huijimuhe.monolog.domain.BaiduService;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.domain.OnBaiduLBSListener;
import com.huijimuhe.monolog.ui.main.MainActivity;
import com.huijimuhe.monolog.utils.FileUtils;
import com.huijimuhe.monolog.utils.ImageUtils;
import com.huijimuhe.monolog.utils.JsonUtils;
import com.huijimuhe.monolog.domain.PrefManager;
import com.huijimuhe.monolog.utils.MD5Utils;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class PublishActivity extends AbstractActivity implements ImageView.OnClickListener {

    public static final int HANDLER_IMAGE_UPLOAD_FAILED = 0x0;
    public static final int HANDLER_IMAGE_UPLOAD_SUCCESS = 0x1;
    public static final int HANDLER_GET_THUMB_SUCCESS = 0x2;
    public static final int HANDLER_GET_THUMB_FAILED = 0x3;
    public static final int HANDLER_PUBLISH_FAILED = 0x4;
    public static final int HANDLER_PUBLISH_SUCCESS = 0x5;
    public static final int HANDLER_PUBLISH = 0x6;

    public static final int REQUEST_IMAGE = 21;
    public static final int REQUEST_PUBLISH = 21;

    public static final int RESULT_OK = 30;

    private MyHandler handler;
    private EditText mEtText;
    private TextView mTvLocation;
    private ImageView mIvImg;
    private Toolbar toolbar;
    private LinearLayout mLinearLayout;
    private double mLng = 0;
    private double mLat = 0;
    private boolean bIsLocateFailed = false;
    private String mOSSToken;
    private String mOSSKey;
    private String mSelectPath;
    private File mThumbFile;
    private boolean bIsUploading = false;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                PublishActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initUI();
        BaiduService.getInstance().StartLocation(baiduLBSListener);
        handler = new MyHandler(PublishActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduService.getInstance().StopLocation();
        baiduLBSListener = null;
        handler = null;
    }

    private void initUI() {
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("写独白");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);

        //bind view
        mLinearLayout = (LinearLayout) findViewById(R.id.layout_background);
        mEtText = (EditText) findViewById(R.id.et_text);
        mIvImg = (ImageView) findViewById(R.id.iv_img);
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        mIvImg.setOnClickListener(this);
        mTvLocation.setOnClickListener(this);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_text:
                break;
            case R.id.iv_img:
                pickImgClick();
                break;
            case R.id.tv_location:
                if (bIsLocateFailed) {
                    bIsLocateFailed = false;
                    BaiduService.getInstance().StartLocation(baiduLBSListener);
                }
                break;
            case R.id.layout_background:
                hiddenKeyBoard(v);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE) {
            ArrayList<String> pathes = result.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mSelectPath = pathes.get(0);
            // 建立缩略图
            new Thread(new getThumbImgTask(Uri.parse(mSelectPath))).start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_publish) {
            if (!bIsUploading) {
                if (validInput()) {
                    ToastUtils.show(PublishActivity.this, "开始上传");
                    //上传图片，然后发布消息
                    handler.sendEmptyMessage(HANDLER_PUBLISH);
                    item.setEnabled(false);
                }
            } else {
                ToastUtils.show(PublishActivity.this, "上传中");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void post() {
        StatueApi.postCreateStatue(mEtText.getText().toString(), mOSSKey, String.valueOf(mLng), String.valueOf(mLat), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HANDLER_PUBLISH_FAILED);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.sendEmptyMessage(HANDLER_PUBLISH_SUCCESS);
            }
        });
    }

    private void getUploadToken() {
        final UploadManager uploadManager = new UploadManager();
        mOSSKey = "";
        StatueApi.getOssToken(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    mOSSToken = JsonUtils.getString("uptoken", new JSONObject(responseString.trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                }
                String tempKey = "monolog_" + MD5Utils.md5(String.valueOf(mThumbFile.hashCode())) + ".jpg";
                uploadManager.put(mThumbFile, tempKey, mOSSToken,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key,
                                                 ResponseInfo info,
                                                 JSONObject response) {
                                try {
                                    if (info.statusCode == 200) {
                                        // 上传图片返回key值
                                        mOSSKey = JsonUtils.getString("key", response);
                                        handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_SUCCESS);
                                    } else {
                                        handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                                }
                            }
                        }, null);
            }
        });
    }

    private void pickImgClick() {
        Intent intent = new Intent(PublishActivity.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private class getThumbImgTask implements Runnable {

        Uri imgUri;

        public getThumbImgTask(Uri uri) {
            imgUri = uri;
        }

        @Override
        public void run() {
            String largeFilePath = imgUri.getPath();
            String largeFileName = FileUtils.getFileName(largeFilePath);
            String thumbFilePath;

            if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                // direct save thumbnail
                thumbFilePath = largeFilePath;
                mThumbFile = new File(thumbFilePath);
            } else {
                // save thumbnail
                String thumbFileName = "thumb_" + largeFileName;
                thumbFilePath = ImageUtils.IMG_SAVEPATH + thumbFileName;
                if (new File(thumbFilePath).exists()) {
                    // check if thumbnail exist
                    mThumbFile = new File(thumbFilePath);
                } else {
                    try {
                        // create thumbnail
                        //  Bitmap b = ImageUtils.loadImgThumbnail(largeFilePath, 200, 200);
                        ImageUtils.createImageThumbnail(AppContext.getInstance(), largeFilePath, thumbFilePath, 800, 80);
                        mThumbFile = new File(thumbFilePath);
                        if (!mThumbFile.exists()) {
                            handler.sendEmptyMessage(HANDLER_GET_THUMB_FAILED);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(HANDLER_GET_THUMB_FAILED);
                    }
                }
            }
            handler.sendEmptyMessage(HANDLER_GET_THUMB_SUCCESS);
        }
    }


    private class uploadImgTask implements Runnable {
        private UploadManager uploadManager;

        private uploadImgTask() {
            uploadManager = new UploadManager();
            mOSSKey = "";
        }

        @Override
        public void run() {
            StatueApi.getOssToken(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    try {
                        mOSSToken = JsonUtils.getString("uptoken", new JSONObject(responseString.trim()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                    }

                    uploadManager.put(mThumbFile, null, mOSSToken,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key,
                                                     ResponseInfo info,
                                                     JSONObject response) {
                                    try {
                                        if (info.statusCode == 200) {
                                            // 上传图片返回key值
                                            mOSSKey = JsonUtils.getString("key", response);
                                            handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_SUCCESS);
                                        } else {
                                            handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                                    }
                                }
                            }, null);
                }
            });
        }
    }

    private class MyHandler extends Handler {
        WeakReference<PublishActivity> mAct;

        public MyHandler(PublishActivity act) {
            mAct = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_IMAGE_UPLOAD_FAILED:
                    ToastUtils.show(mAct.get(), "图片上传失败,请重试");
                    mAct.get().bIsUploading = false;
                    break;
                case HANDLER_IMAGE_UPLOAD_SUCCESS:
                    mAct.get().post();
                    break;
                case HANDLER_GET_THUMB_SUCCESS:
                    Uri imageUri = Uri.fromFile(mThumbFile);
                    mAct.get().mIvImg.setImageURI(imageUri);
                    break;
                case HANDLER_GET_THUMB_FAILED:
                    ToastUtils.show(mAct.get(), "图片选择失败,请重试");
                    mAct.get().bIsUploading = false;
                    break;
                case HANDLER_PUBLISH_FAILED:
                    ToastUtils.show(mAct.get(), "网络错误请重试");
                    mAct.get().bIsUploading = false;
                    break;
                case HANDLER_PUBLISH_SUCCESS:
                    ToastUtils.show(mAct.get(), "发布成功");
                    mAct.get().bIsUploading = false;

                    //计数
                    Account owner = PrefManager.getInstance().getUser();
                    owner.setStatue_count(owner.getStatue_count() + 1);
                    PrefManager.getInstance().setUser(owner);
                    mAct.get().setResult(MainActivity.RESULT_OK);

                    finish();
                    break;
                case HANDLER_PUBLISH:
                    mAct.get().bIsUploading = true;
                    mAct.get().getUploadToken();//new Thread(new uploadImgTask()).start();
                    break;
            }
        }
    }

    ;

    private OnBaiduLBSListener baiduLBSListener = new OnBaiduLBSListener() {
        @Override
        public void onStart() {
            mTvLocation.setText("定位中");
        }

        @Override
        public void onFailed(int code) {
            bIsLocateFailed = true;
            mTvLocation.setText("定位失败");
        }

        @Override
        public void onComplete(int code, double lng, double lat, String addr) {
            mLng = lng;
            mLat = lat;
            mTvLocation.setText("定位成功");
        }
    };

    private boolean validInput() {
        //validate input
        if (isNullOrOverLength(mEtText)) {
            ToastUtils.show(PublishActivity.this, "请输入120字以内的独白");
            mEtText.requestFocus();
            return false;
        }
        if (mLat == 0 && mLng == 0) {
            ToastUtils.show(PublishActivity.this, "请定位");
            return false;
        }

        if (TextUtils.isEmpty(mSelectPath)) {
            ToastUtils.show(PublishActivity.this, "请选择图片");
            return false;
        }
        return true;
    }

}
