package com.hail.myphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.PermissionManager.TPermissionType;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;

public class TakePhotoActivity extends AppCompatActivity implements TakePhoto.TakeResultListener,
        InvokeListener {

    private static final String TAG = "Activity图片";
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TPermissionType type = PermissionManager
                .onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler
                    .of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "takeSuccess：" + result.getImage().getCompressPath());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(com.jph.takephoto.R.string.msg_operation_canceled));
    }

    @Override
    public TPermissionType invoke(InvokeParam invokeParam) {
        TPermissionType type = PermissionManager
                .checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 固定照片的选择一张图片
     */
    public void selectPhotoFixed(boolean isCrop) {
        showTakePhotoDialog(1, isCrop, 204800, 600, 600);
    }

    /**
     * 固定照片的选择多张张图片
     */
    public void selectPhotoFixed(int num, boolean isCrop) {
        showTakePhotoDialog(num, true, 204800, 600, 600);
    }

    private void showTakePhotoDialog(final int num, final boolean isCrop, int maxSize, final int width,
                                     final int height) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View contentView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_select_photo, null);
        TextView btnCamera = (TextView) contentView.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) contentView.findViewById(R.id.btn_select_photo);
        TextView btnCancel = (TextView) contentView.findViewById(R.id.btn_cancel);
        File file = new File(Environment.getExternalStorageDirectory(),
                "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final Uri imageUri = Uri.fromFile(file);
        configTakePhotoOption();
        configCompress(maxSize, width, height);
        btnCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCrop) {
                    if (num == 1) {
                        takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(width, height, true));
                    } else {
                        takePhoto.onPickMultipleWithCrop(num, getCropOptions(width, height, true));
                    }
                } else {
                    if (num == 1) {
                        takePhoto.onPickFromCapture(imageUri);
                    } else {
                        takePhoto.onPickMultiple(num);
                    }
                }
                bottomSheetDialog.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(width, height, true));
                bottomSheetDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(contentView);
        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        behavior.setPeekHeight(contentView.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent
                .getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
        bottomSheetDialog.show();
    }

    private void configTakePhotoOption() {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private void configCompress(int maxSize, int width, int height) {
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, true);
    }

    private CropOptions getCropOptions(int width, int height, boolean isXY) {
        CropOptions.Builder builder = new CropOptions.Builder();
        if (isXY) {
            builder.setAspectX(width).setAspectY(height);
        } else {
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(true);
        return builder.create();
    }
}

