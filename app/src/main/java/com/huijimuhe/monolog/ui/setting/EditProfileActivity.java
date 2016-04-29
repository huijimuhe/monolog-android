package com.huijimuhe.monolog.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.huijimuhe.monolog.R;
import com.huijimuhe.monolog.api.AuthApi;
import com.huijimuhe.monolog.api.StatueApi;
import com.huijimuhe.monolog.bean.UserBean;
import com.huijimuhe.monolog.core.AppContext;
import com.huijimuhe.monolog.ui.base.AbstractActivity;
import com.huijimuhe.monolog.ui.main.MainActivity;
import com.huijimuhe.monolog.utils.FileUtils;
import com.huijimuhe.monolog.utils.ImageUtils;
import com.huijimuhe.monolog.utils.JsonUtils;
import com.huijimuhe.monolog.domain.PrefService;
import com.huijimuhe.monolog.utils.MD5Utils;
import com.huijimuhe.monolog.utils.ToastUtils;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class EditProfileActivity extends AbstractActivity implements ViewSwitcher.OnClickListener {
    public static final int FROM_SIGN_UP = 0;
    public static final int FROM_EDIT = 1;

    public static final int HANDLER_IMAGE_UPLOAD_FAILED = 0x1;
    public static final int HANDLER_IMAGE_UPLOAD_SUCCESS = 0x2;
    public static final int HANDLER_GET_THUMB_SUCCESS = 0x3;
    public static final int HANDLER_GET_THUMB_FAILED = 0x4;
    public static final int HANDLER_EDIT_FAILED =0x5;
    public static final int HANDLER_EDIT_SUCCESS = 0x6;
    public static final int HANDLER_EDIT_PROFILE = 0x7;
    public static final int HANDLER_PUBLISH_IMG = 0x8;
    protected static final int REQUEST_IMAGE = 22;

    private Toolbar toolbar;
    private EditText mEtName;
    private RelativeLayout mBtnAvatar;
    private ImageView mIvAvatar;
    private LinearLayout mLinearLayout;
    private MyHandler handler;

    private int mFlag;
    private String mOSSToken;
    private String mOSSKey;
    private String mSelectPath;
    private String mName;
    private File mThumbFile;
    private boolean bIsUploading=false;
    public static Intent newIntent(int flag) {
        Intent intent = new Intent(AppContext.getInstance(),
                EditProfileActivity.class);
        intent.putExtra("flag", flag);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (savedInstanceState == null) {
            mFlag = getIntent().getIntExtra("flag", FROM_EDIT);
        } else {
            mFlag = savedInstanceState.getInt("flag");
        }
        initUI();
        if (mFlag == FROM_EDIT) {
            bindView();
        }
        handler=new MyHandler(EditProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;
    }

    private void initUI() {
        //Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("编辑资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
        //bind view
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mEtName = (EditText) findViewById(R.id.et_name);
        mBtnAvatar = (RelativeLayout) findViewById(R.id.btn_avatar);
        mLinearLayout = (LinearLayout) findViewById(R.id.layout_background);
        mLinearLayout.setOnClickListener(this);
        mBtnAvatar.setOnClickListener(this);
    }

    private void bindView() {
        UserBean owner= PrefService.getInstance(getApplicationContext()).getUser();
        mEtName.setText(owner.getName());
        AppContext.getInstance().loadImg(mIvAvatar, owner.getAvatar());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("flag", mFlag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                break;
            case R.id.action_save:
                if(!bIsUploading) {
                    if (TextUtils.isEmpty(mSelectPath)) {
                        handler.sendEmptyMessage(HANDLER_EDIT_PROFILE);
                    } else {
                        handler.sendEmptyMessage(HANDLER_PUBLISH_IMG);
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                postEditProfile();
                break;
            case R.id.btn_avatar:
                pickImgClick();
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

    private void postEditProfile() {
        if (isNull(mEtName)) {
            ToastUtils.show(EditProfileActivity.this, getString(R.string.string_please_enter_nick));
            mEtName.requestFocus();
            return;
        }
        if (mEtName.length() > 10) {
            ToastUtils.show(EditProfileActivity.this, "请输入10个字以内的昵称");
            mEtName.requestFocus();
            return;
        }
        mName = mEtName.getText().toString();
        AuthApi.changePorfile(mName, mOSSKey, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HANDLER_EDIT_FAILED);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    UserBean owner = PrefService.getInstance(getApplicationContext()).getUser();
                    owner.setName(mName);
                    if (!TextUtils.isEmpty(mOSSKey)) {
                        owner.setAvatar(String.format("http://7xi4vz.com1.z0.glb.clouddn.com/%s", mOSSKey));
                    }
                    PrefService.getInstance(getApplicationContext()).setUser(owner);

                    if (mFlag == FROM_SIGN_UP) {
                        startActivity(MainActivity.newIntent());
                    }
                    finish();
                } catch (Exception ex) {
                    handler.sendEmptyMessage(HANDLER_EDIT_FAILED);
                }
            }
        });
    }

    private void postAvatar(){
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
                    mOSSToken=  JsonUtils.getString("uptoken", new JSONObject(responseString.trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HANDLER_IMAGE_UPLOAD_FAILED);
                }
                String tempKey="monolog_"+ MD5Utils.md5(String.valueOf(mThumbFile.hashCode()))+".jpg";
                uploadManager.put(mThumbFile, tempKey, mOSSToken,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key,
                                                 com.qiniu.android.http.ResponseInfo info,
                                                 JSONObject response) {
                                try {
                                    if (info.statusCode == 200) {
                                        // 上传图片返回key值
                                        mOSSKey = JsonUtils.getString("key",
                                                response);
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
        Intent intent = new Intent(EditProfileActivity.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private class getThumbImgTask implements Runnable {

        Uri imgUri;

        public getThumbImgTask(Uri uri){
            imgUri=uri;
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
                       // Bitmap b = ImageUtils.loadImgThumbnail(largeFilePath, 200, 200);
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

   private class MyHandler extends Handler {

       WeakReference<EditProfileActivity> mAct;

       public  MyHandler(EditProfileActivity act){
           mAct=new WeakReference<>(act);
       }

       @Override
        public void handleMessage(Message msg) {
           mAct.get().bIsUploading=false;
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_IMAGE_UPLOAD_FAILED:
                    ToastUtils.show(mAct.get(), "图片上传失败");
                    break;
                case HANDLER_IMAGE_UPLOAD_SUCCESS:
                    mAct.get().postEditProfile();
                    break;
                case HANDLER_GET_THUMB_SUCCESS:
                    Uri imageUri = Uri.fromFile(mThumbFile);
                    mAct.get().mIvAvatar.setImageURI(imageUri);
                    break;
                case HANDLER_GET_THUMB_FAILED:
                    ToastUtils.show(mAct.get(), "图片选择失败,请重试");
                    break;
                case HANDLER_EDIT_FAILED:
                    ToastUtils.show(mAct.get(), "修改失败");
                    break;
                case HANDLER_EDIT_SUCCESS:
                    ToastUtils.show(mAct.get(), "修改成功");
                    break;
                case HANDLER_EDIT_PROFILE:
                    ToastUtils.show(mAct.get(), "修改资料中..");
                    mAct.get().bIsUploading=true;
                    mAct.get().postEditProfile();
                    break;
                case HANDLER_PUBLISH_IMG:
                    ToastUtils.show(mAct.get(), "修改资料中..");
                    mAct.get().bIsUploading=true;
                    mAct.get().postAvatar();
                    break;
            }
        }
    }
}
