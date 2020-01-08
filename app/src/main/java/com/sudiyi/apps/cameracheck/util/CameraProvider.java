package com.sudiyi.apps.cameracheck.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;

/**
 * Created by JackZheng on 2017/6/28.
 * 判断当前设备是否有摄像头
 */

public class CameraProvider {

    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }

        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            try {
                Camera.getCameraInfo(i, info);
                if (facing == info.facing) {
                    return true;
                }
            } catch (RuntimeException exception) {
                LogUtils.e("camera error ，have no camera！！！");

            }
        }
        return false;
    }

    /**
     * 检查设备是否有后置摄像头
     *
     * @return
     */
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    /**
     * 检查设备是否有前置摄像头
     *
     * @return
     */
    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    /**
     * 判断当前设备是否有摄像头
     *
     * @param ctx
     * @return
     */
    public static boolean hasCameraDevice(Context ctx) {
        return ctx.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}