package com.sudiyi.apps.cameracheck.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.sudiyi.apps.cameracheck.base.AspectRatio;
import com.sudiyi.apps.cameracheck.base.Constants;
import com.sudiyi.apps.cameracheck.base.Size;
import com.sudiyi.apps.cameracheck.base.SizeMap;
import com.sudiyi.apps.cameracheck.util.BitmapUtil;
import com.sudiyi.apps.cameracheck.util.FileUtils;
import com.sudiyi.apps.cameracheck.util.log.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraPreview extends SurfaceView implements CameraView, SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getCanonicalName();
    private static CameraPreview instance;
    private Camera mCamera;
    private Context mContext;
    private int mCameraCount;
    private int mCurrentCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;

    private SurfaceHolder mSurfaceHolder;

    /**
     * 标识相机是否正在拍照过程中
     */
    private AtomicBoolean isPictureCaptureInProgress;


    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public static CameraPreview getInstance(Context context) {
        if (instance == null) {
            synchronized (CameraPreview.class) {
                if (instance == null) {
                    instance = new CameraPreview(context);
                }
            }
        }
        return instance;
    }

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initCamera();
        isPictureCaptureInProgress = new AtomicBoolean(false);
    }


    private void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera == null) {
            LogUtils.e(TAG, "initCamera fail !");
            return;
        }

        //得到摄像头数量
        mCameraCount = Camera.getNumberOfCameras();
        mSurfaceHolder = getHolder();
        // 设置SurfaceHolder.Callback回调，这样我们可以在创建或销毁Surface时处理相应的逻辑
        mSurfaceHolder.addCallback(this);
        //设置屏幕常亮
        mSurfaceHolder.setKeepScreenOn(true);
        //自动对焦
        try {
            mCamera.autoFocus(null);
        } catch (Exception e) {
            LogUtils.d(TAG, "autoFocus fail : " + e.getMessage());
        }

        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    try {
                        mCamera.autoFocus(null);
                    } catch (Exception e) {
                        LogUtils.d(TAG, "autoFocus fail : " + e.getMessage());
                    }
                }
            }
        });*/

        LogUtils.d(TAG, "initCamera success !");

    }


    @Override
    public void onResume() {
        LogUtils.d(TAG, "onResume");

        if (mCamera == null)
            mCamera = getCameraInstance();
    }


    @Override
    public void onPause() {
        LogUtils.d(TAG, "onPause");
        releaseCamera();
    }

    /**
     * 拍照方法(无回调，默认保存到文件中)
     */
    @Override
    public void takePicture() {
        if (mCamera == null) {
            LogUtils.e(TAG, "Camera is not ready. Call start() before takePicture().");
            return;
        }
        takePictureInternal();
    }

    /**
     * 拍照方法(有回调)
     *
     * @param callback
     */
    @Override
    public void takePicture(TakePictureCallback callback) {
        takePictureCallback = callback;
        if (mCamera == null) {
            if (takePictureCallback != null) {
                takePictureCallback.error("Camera is not ready. Call start() before takePicture().");
            }
            LogUtils.e(TAG, "Camera is not ready. Call start() before takePicture().");
            return;
        }
        takePictureInternal();
        LogUtils.d(TAG, "takePicture");
    }

    private void takePictureInternal() {
        //如果正在拍照处理中，则不能调用takePicture方法，否则应用会崩溃
        if (!isPictureCaptureInProgress.get()) {
            isPictureCaptureInProgress.set(true);
            if (mCamera != null) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        } else {
            if (takePictureCallback != null) {
                takePictureCallback.error("picture capture in progress !");
            }
            LogUtils.d(TAG, "picture capture in progress !");
        }
        LogUtils.d(TAG, "takePictureInternal");
    }


    /**
     * 设置图片的保存路径
     *
     * @param pictureSavePath
     */
    @Override
    public void setPictureSavePath(String pictureSavePath) {
        mPictureSaveDir = pictureSavePath;
    }

    /***
     * 切换相机摄像头
     */
    @Override
    public void switchCameraFacing() {
        if (mCameraCount > 1) {
            mCurrentCameraFacing = (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                    Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
            releaseCamera();
            startPreview(mSurfaceHolder);
        } else {
            //手机不支持前置摄像头
        }
    }

    @Override
    public void OpenCamera(OpenCameraCallback openCameraCallback) {

    }


    /**
     * 设置此视图的宽高比。
     * 视图的大小将基于从参数计算的比率来测量。
     * 请注意，参数的实际大小并不重要，也就是说，setAspectRatio（2, 3）setAspectRatio（4, 6）会得到相同的结果。
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }


    /**
     * 获取相机实例
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            // 获取相机实例, 注意：某些设备厂商可能需要用 Camera.open() 方法才能打开相机。
            camera = Camera.open(mCurrentCameraFacing);
        } catch (Exception e) {
            // 相机不可用或不存在
            LogUtils.e(TAG, "error open(" + mCurrentCameraFacing + ") camera : " + e.getMessage());
        }

        try {
            if (null == camera)
                camera = Camera.open();
        } catch (Exception e) {
            LogUtils.e(TAG, "error open camera（） : " + e.getMessage());
        }

        return camera;
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();        // release the camera1 for other applications
            mCamera = null;
            LogUtils.d(TAG, "releaseCamera");
        }
        if (instance != null) {
            instance = null;
        }

        if (isPictureCaptureInProgress != null) {
            isPictureCaptureInProgress = null;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.d(TAG, "surfaceCreated");
        // Surface创建完成, 现在即可设置相机预览
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        LogUtils.d(TAG, "surfaceChanged   format" + format + ", width =" + width + " | height=" + height);
        // surface在改变大小或旋转时触发此时间
        // 确保在调整或重新格式化之前停止视频预览
        if (mSurfaceHolder.getSurface() == null) {
            // 预览Surface不存在
            return;
        }
        // 改变前先停止预览
        try {
            mCamera.stopPreview();
            startPreview(holder);
            setCameraParameters(width, height);
        } catch (Exception e) {
            LogUtils.e(TAG, "error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.d(TAG, "surfaceDestroyed");
        //释放相机
        releaseCamera();
    }


    private void startPreview(SurfaceHolder holder) {
        if (mCamera == null)
            mCamera = getCameraInstance();

        if (mCamera == null) {
            LogUtils.e(TAG, "startPreview fail !");
            return;
        }
        try {
            mCamera.setPreviewDisplay(holder);
            //设置预览的旋转角度
            mCamera.setDisplayOrientation(calcDisplayOrientation(mCurrentCameraFacing));
            mCamera.startPreview();
        } catch (IOException e) {
            LogUtils.e(TAG, "error setting camera1 preview:" + e.getMessage());
        }
    }


    private int calcDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        mDisplayOrientation = degrees;
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {                            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    private Camera.Parameters mCameraParameters;
    private final SizeMap mPreviewSizes = new SizeMap();
    private final SizeMap mPictureSizes = new SizeMap();
    private AspectRatio mAspectRatio;

    private int mDisplayOrientation;

    private AspectRatio chooseAspectRatio() {
        AspectRatio r = null;
        for (AspectRatio ratio : mPreviewSizes.ratios()) {
            r = ratio;
            if (ratio.equals(Constants.DEFAULT_ASPECT_RATIO)) {
                return ratio;
            }
        }
        return r;
    }

    private Size chooseOptimalSize(SortedSet<Size> sizes, int surfaceWidth, int surfaceHeight) {

        int desiredWidth;
        int desiredHeight;
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        } else {
            desiredWidth = surfaceWidth;
            desiredHeight = surfaceHeight;
        }
        Size result = null;
        for (Size size : sizes) { // Iterate from small to large
            if (desiredWidth <= size.getWidth() && desiredHeight <= size.getHeight()) {
                return size;

            }
            result = size;
        }
        return result;
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Constants.LANDSCAPE_90 ||
                orientationDegrees == Constants.LANDSCAPE_270);
    }

    /***
     * 设置相机参数
     *
     * @param width
     * @param height
     */
    private void setCameraParameters(int width, int height) {
        if (mCamera == null)
            return;

        mCameraParameters = mCamera.getParameters();


        // 相机预览支持的大小
        mPreviewSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
            mPreviewSizes.add(new Size(size.width, size.height));
        }
        // 相机照片支持的大小
        mPictureSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPictureSizes()) {
            mPictureSizes.add(new Size(size.width, size.height));
        }
        // AspectRatio  默认长宽比为4：3
        if (mAspectRatio == null) {
            mAspectRatio = Constants.DEFAULT_ASPECT_RATIO;
        }

        SortedSet<Size> sizes = mPreviewSizes.sizes(mAspectRatio);
        if (sizes == null) { // Not supported
            mAspectRatio = chooseAspectRatio();
            sizes = mPreviewSizes.sizes(mAspectRatio);
        }
        Size previewSize = chooseOptimalSize(sizes, width, height);

        // Always re-apply camera1 parameters
        // Largest picture size in this ratio
        final Size pictureSize = mPictureSizes.sizes(mAspectRatio).last();

        mCameraParameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        mCameraParameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());


        mCameraParameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式
        mCameraParameters.setJpegQuality(100); // 设置照片质量
        mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//自动对焦
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
        //camera1.cancelAutoFocus();//如果要实现连续的自动对焦，这一句必须加上

        mCamera.setParameters(mCameraParameters);


        //根据我们选中的预览相机大小的宽高比调整View的大小
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
        } else {
            setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
        }

    }


    private String mPictureSaveDir;
    private TakePictureCallback takePictureCallback;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            LogUtils.d(TAG, "onPictureTaken start timestemp :" + System.currentTimeMillis());
            savePictureToSDCard(data);
            startPreview(mSurfaceHolder);
            isPictureCaptureInProgress.set(false);
            LogUtils.d(TAG, "onPictureTaken end timestemp :" + System.currentTimeMillis());
        }
    };


    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     */

    private void savePictureToSDCard(final byte[] data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File pictureFile;
                FileOutputStream mOutputStream;
                //检测外部存储是否存在
                if (FileUtils.checkSDCard()) {
                    if (mPictureSaveDir == null) {
                        pictureFile = FileUtils.getOutputMediaFile(mContext, FileUtils.MEDIA_TYPE_IMAGE);
                    } else {
                        pictureFile = FileUtils.getTimeStampMediaFile(mPictureSaveDir, FileUtils.MEDIA_TYPE_IMAGE);
                    }
                    if (pictureFile == null) {
                        LogUtils.e(TAG, "error creating media file, check storage permissions");
                        if (takePictureCallback != null) {
                            takePictureCallback.error("error creating media file, check storage permissions");
                        }
                        return;
                    }
                } else {
                    pictureFile = FileUtils.getOutputMediaFile(mContext, FileUtils.MEDIA_TYPE_IMAGE);
                }

                try {
                    mOutputStream = new FileOutputStream(pictureFile);
                    //由于在预览的时候，我们调整了预览的方向，所以在保存的时候我们要旋转回来，不然保存的图片方向是不正确的
                    Matrix matrix = new Matrix();
                    if (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        matrix.setRotate(90);
                    } else {
                        matrix.setRotate(-90);
                    }
                    mOutputStream.write(BitmapUtil.getInstance().getSmallBitmap(data, matrix));
                    mOutputStream.close();
                } catch (Exception e) {
                    LogUtils.e(TAG, "savePictureToSDCard error :" + e.getMessage());
                    if (takePictureCallback != null) {
                        takePictureCallback.error(e.getMessage());
                    }
                } finally {
                    if (takePictureCallback != null)
                        takePictureCallback.success(pictureFile.getAbsolutePath());

                    mOutputStream = null;
                    pictureFile = null;
                }

            }
        }).start();
    }
}
