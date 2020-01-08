package com.sudiyi.apps.cameracheck.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private static BitmapUtil instance;

    public BitmapUtil() {

    }

    public static BitmapUtil getInstance() {
        if (instance == null) {
            synchronized (BitmapUtil.class) {
                if (instance == null) {
                    instance = new BitmapUtil();
                }
            }
        }
        return instance;
    }

    public  byte[] getSmallBitmap(byte[] data, Matrix matrix) {
        byte[] bytes;
        Bitmap bitmap;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
        // Decode bitmap with inSampleSize set
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        int be;
        be = calculateInSampleSize(newOpts, width, height);
        newOpts.inSampleSize = be;//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bytes= Bitmap2Bytes(bitmap);
        if (null != bitmap || !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return bytes;
    }

    /**
     * bitmap 转换为字节
     *
     * @param bm
     * @return
     */
    public  byte[] Bitmap2Bytes(Bitmap bm) {
        byte[] data;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > CURRENT_IMG_MAX_SIZE) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少5
        }

        data = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bm != null || !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
        }
        return data;
    }

    public  final int CURRENT_IMG_MAX_SIZE = 200;//当前照片最大质量为不超过300Kb

    private  int calculateInSampleSize(BitmapFactory.Options newOpts, int width, int height) {
        int inSampleSize = 1;
        if (width >= height && width > MAX_PIXEL) {//缩放比,用高或者宽其中较大的一个数据进行计算
            inSampleSize = (int) (newOpts.outWidth / MAX_PIXEL);
            inSampleSize++;
        } else if (width < height && height > MAX_PIXEL) {
            inSampleSize = (int) (newOpts.outHeight / MAX_PIXEL);
            inSampleSize++;
        }
        return inSampleSize;
    }

    public  final int MAX_PIXEL = 1200;//长或宽不超过的最大像素,单位px

}
