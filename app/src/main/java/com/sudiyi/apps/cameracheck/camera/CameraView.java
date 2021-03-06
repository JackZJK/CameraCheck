package com.sudiyi.apps.cameracheck.camera;

/**
 * Author: Linkin
 * Time：2018/8/29
 * Email：liuzhongjun@novel-supertv.com
 * Blog：https://blog.csdn.net/Android_Technology
 * Desc: TODO
 */

public interface CameraView {

    /**
     * 与生命周期onResume调用
     */
    void onResume();

    /**
     * 与生命周期onPause调用
     */
    void onPause();

    /**
     * 拍照
     */
    void takePicture();

    /**
     * 拍照(有回调)
     */
    void takePicture(TakePictureCallback takePictureCallback);

    /**
     * 设置保存的图片文件
     *
     * @param pictureSavePath 拍摄的图片返回的绝对路径
     */
    void setPictureSavePath(String pictureSavePath);

    /**
     * 切换相机摄像头
     */
    void switchCameraFacing();


    /**
     * 打开摄像头
     *
     * @param openCameraCallback
     */
    void OpenCamera(OpenCameraCallback openCameraCallback);


    interface TakePictureCallback {

        void success(String picturePath);

        void error(final String error);
    }

    /**
     * 摄像头是否存在可用
     */
    interface OpenCameraCallback {

        void success();

        void error(final String error);
    }
}
