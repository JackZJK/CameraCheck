package com.sudiyi.apps.cameracheck;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.FrameLayout;

import com.sudiyi.apps.cameracheck.camera.CameraPreview;
import com.sudiyi.apps.cameracheck.util.log.LogUtils;

import static com.sudiyi.apps.cameracheck.Config.CAMERA_CHECK_COMPLETE_ACTION;

/**
 * 拍照服务  am startservice -n com.sudiyi.apps.cameracheck/com.sudiyi.apps.cameracheck.PhotoWindowService -f 32
 */
public class PhotoWindowService extends Service implements CameraPreview.TakePictureCallback {
    private static String TAG = PhotoWindowService.class.getCanonicalName();
    private static final int TAKE_PICTURE_SUCCESS = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int TAKE_PICTURE_TIMEOUT = 3;
    private static final int TAKE_PICTURE_FAIL = -1;
    private CameraPreview mCameraPreview;
    private FrameLayout mParentView;
    private PhotoWindowSmallView mPhotoWindowSmallView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
        setForeground();
        super.onCreate();
    }

    /**
     * 将服务写成前台服务foreground service
     */
    private void setForeground() {
        Notification notification = new Notification(0, null, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
        startForeground(0, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand");
        mPhotoWindowSmallView = PhotoWindowSmallView.getInstance(getApplicationContext());
        mPhotoWindowSmallView.showView();

        mParentView = mPhotoWindowSmallView.getParentView();

        mCameraPreview = new CameraPreview(getApplicationContext());

        if (null != mParentView) {
            if (mParentView.getChildCount() > 0) {
                mParentView.removeAllViews();
            }
            mParentView.addView(mCameraPreview);
        }

        mCameraPreview.setPictureSavePath(Config.MEDIA_DIRECTORY);

        mHandler.sendEmptyMessageDelayed(TAKE_PICTURE, 5 * 1000);
        mHandler.sendEmptyMessageDelayed(TAKE_PICTURE_TIMEOUT, 60 * 1000);

        return super.onStartCommand(intent, flags, START_STICKY);
    }

    private void takePicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCameraPreview.takePicture(PhotoWindowService.this);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TAKE_PICTURE_FAIL:
                    String err = (String) msg.obj;
                    LogUtils.d(TAG, "pic save fail:" + err);
                    sendResultByBroadcast(CameraStatusEnum.CAMERA_EXIST.getValue(), CameraStatusEnum.CAMERA_EXIST.getValue());
                    release();
                    break;
                case TAKE_PICTURE_SUCCESS:
                    String path = (String) msg.obj;
                    LogUtils.d(TAG, "pic save path:" + path);
                    sendResultByBroadcast(CameraStatusEnum.CAMERA_EXIST.getValue(), CameraStatusEnum.CAMERA_EXIST.getValue());
                    release();
                    break;
                case TAKE_PICTURE:
                    takePicture();
                    break;
                case TAKE_PICTURE_TIMEOUT:
                    LogUtils.d(TAG, "tack picture time out !");
                    sendResultByBroadcast(CameraStatusEnum.CAMERA_EXIST.getValue(), CameraStatusEnum.CAMERA_NOT_EXIST.getValue());
                    release();
                    break;

            }
        }
    };

    /**
     * 资源释放
     */
    private void release() {
        if (null != mParentView) {
            if (mParentView.getChildCount() > 0) {
                mParentView.removeAllViews();
            }
            mParentView = null;
            stopForeground(true);
            LogUtils.d(TAG, "release window !");
        }

        if (mPhotoWindowSmallView != null) {
            mPhotoWindowSmallView.hide();
            mPhotoWindowSmallView = null;
        }

        if (mCameraPreview != null) {
            mCameraPreview.releaseCamera();
            mCameraPreview = null;
        }

        if (mPhotoWindowSmallView != null) {
            mPhotoWindowSmallView.hide();
            mPhotoWindowSmallView = null;
        }

        if (mCameraPreview != null) {
            mCameraPreview.onPause();
            mCameraPreview = null;
        }

        if (mHandler != null) {
            mHandler.removeMessages(TAKE_PICTURE_TIMEOUT);
        }
        stopSelf();
    }

    @Override
    public void success(String picturePath) {
        Message message = Message.obtain();
        message.what = TAKE_PICTURE_SUCCESS;
        message.obj = picturePath;
        mHandler.sendMessage(message);
    }

    @Override
    public void error(String error) {
        Message message = Message.obtain();
        message.what = TAKE_PICTURE_FAIL;
        message.obj = error;
        mHandler.sendMessage(message);
    }

    /**
     * 广播当前结果数据
     *
     * @param isExistCamera 是否存在摄像头
     * @param isCanTakePic  是否可以正常拍照
     */
    private void sendResultByBroadcast(String isExistCamera, String isCanTakePic) {
        if (isExistCamera == null || isCanTakePic == null) {
            LogUtils.d("result can not allow null");
            return;
        }
        Intent sendIntentData = new Intent();
        sendIntentData.putExtra("IsExist", isExistCamera);
        sendIntentData.putExtra("IsOK", isCanTakePic);
        sendIntentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntentData.setAction(CAMERA_CHECK_COMPLETE_ACTION);
        this.sendBroadcast(sendIntentData);
    }
}
