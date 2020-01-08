package com.sudiyi.apps.cameracheck;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sudiyi.apps.cameracheck.permission.PermissionUtils;
import com.sudiyi.apps.cameracheck.permission.request.IRequestPermissions;
import com.sudiyi.apps.cameracheck.permission.request.RequestPermissions;
import com.sudiyi.apps.cameracheck.permission.requestresult.IRequestPermissionsResult;
import com.sudiyi.apps.cameracheck.permission.requestresult.RequestPermissionsResultSetApp;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    public static final int OVER_LAYOUT_CODE = 1;
    private IRequestPermissions requestPermissions;
    private IRequestPermissionsResult requestPermissionsResult;
    private AlertDialog mOverlayAskDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.setContext(this);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Config.CAPTURE_ACTION);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);

            }
        });

        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //动态权限请求
        requestPermissions = RequestPermissions.getInstance();
        //动态权限请求结果处理
        requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();

        requestPermissions();

        if (Integer.parseInt(Build.VERSION.SDK) >= 23 && !Settings.canDrawOverlays(MainActivity.this)) {
            overlayPermissionRequest();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if (requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)) {
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            Toast.makeText(MainActivity.this, "授权成功，请重新点击刚才的操作！", Toast.LENGTH_LONG).show();
        } else {
            //输出授权结果
            Toast.makeText(MainActivity.this, "请给APP授权，否则功能无法正常使用！", Toast.LENGTH_LONG).show();
        }
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVER_LAYOUT_CODE) {
            if (PermissionUtils.checkFloatPermission(this)) {
                System.out.println("悬浮窗权限申请成功...");
            } else {
                System.out.println("悬浮窗权限申请失败...");
                overlayPermissionRequest();
            }
        }
    }

    private void overlayPermissionRequest() {
        mOverlayAskDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("悬浮窗权限申请")
                .setMessage("需要申请悬浮窗权限，点击确定进行权限设置")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, OVER_LAYOUT_CODE);
                        }
                        mOverlayAskDialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        mOverlayAskDialog.show();
    }

    private static final int TIME_EXIT = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
            return;
        } else {
            Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();

        }
    }
}