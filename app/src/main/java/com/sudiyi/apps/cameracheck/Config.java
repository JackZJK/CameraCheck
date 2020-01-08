package com.sudiyi.apps.cameracheck;

import android.os.Environment;

import java.io.File;

public class Config {
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String CAPTURE_ACTION = "com.sudiyi.checkcamera.capture";
    public static final String REPEAT_CHECK_CAMERA_ACTION = "com.sudiyi.checkcamera.repeat";
    public static final String CAMERA_CHECK_COMPLETE_ACTION = "com.sudiyi.systemapps.devicedetected.CameraComplete";//摄像头检测完毕接收广播
    public static final String MEDIA_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CameraCheck";//存储照片文件夹名
    public static final String MEDIA_CACHE_DIRECTORY = App.getContext().getCacheDir().getPath() + File.separator + "CameraCheck";//存储照片文件夹名
    public static final int START_SET_ALARM_CLOCKS = 1 * 60;//闹钟启动时间

}
