package com.sudiyi.apps.cameracheck.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Linkin
 * Time：2018/8/27
 * Email：liuzhongjun@novel-supertv.com
 * Blog：https://blog.csdn.net/Android_Technology
 * Desc: TODO
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 检测外部存储是否存在
     */
    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * 创建一个文件来保存图片或者视频
     */
    public static File getOutputMediaFile(Context mContext, int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera2Examples");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Check save directory of capture pic whether exists
     *
     * @param imgSavePath SD card path
     * @return if the directory exist in SD card or create successfully return true, otherwise return false.
     */
    private static boolean checkSaveDirectory(String imgSavePath) {
        File saveDirectory = new File(imgSavePath);
        if (!saveDirectory.exists()) {
            return saveDirectory.mkdir();
        }
        return true;
    }

    public static File getTimeStampMediaFile(String parentPath, int type) {

        if (checkSaveDirectory(parentPath)) {
            LogUtils.d("check parent save path is exist !");
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(parentPath + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(parentPath + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        if (!mediaFile.exists()) {
            try {
                if (mediaFile.createNewFile()) {
                    return mediaFile;
                }
            } catch (IOException e) {
                LogUtils.d("createFile: " + e.getMessage());
            }
        }

        return mediaFile;
    }


    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }


    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
