package com.sudiyi.apps.cameracheck;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;

/**
 * 悬浮窗
 */
public class PhotoWindowSmallView extends LinearLayout {

    private static String TAG = PhotoWindowSmallView.class.getCanonicalName();
    private static PhotoWindowSmallView instance;
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;


    /**
     * 用于更新小悬浮窗的位置
     */
    private MyPhotoWindowManager mWindowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;
    private final View mView;
    private FrameLayout mSurfaceView;
    private int mTouchStartX, mTouchStartY, mParamX, mParamY;//手指按下时坐标
    private Context mContext;


    public static PhotoWindowSmallView getInstance(Context context) {
        if (instance == null) {
            synchronized (PhotoWindowSmallView.class) {
                if (instance == null) {
                    instance = new PhotoWindowSmallView(context);
                }
            }
        }
        return instance;
    }

    /**
     * 構造函數 初始化相关数据
     *
     * @param context
     */
    public PhotoWindowSmallView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = MyPhotoWindowManager.getInstance(context);
        mView = LayoutInflater.from(context).inflate(R.layout.float_window_small, this);

        View view = findViewById(R.id.small_window_layout);
        mSurfaceView = mView.findViewById(R.id.percent);
        mSurfaceView.setOnTouchListener(mOnTouchListener);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        LogUtils.d(TAG, "init PhotoWindowSmallView");

    }

    /**
     * 显示当前view
     */
    public void showView() {
        int screenWidth = MyPhotoWindowManager.getInstance(mContext).getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = MyPhotoWindowManager.getInstance(mContext).getWindowManager().getDefaultDisplay().getHeight();
        mParams = new WindowManager.LayoutParams();
        //窗口类型
        //smallWindowParams.type = LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width = viewWidth;
        mParams.height = viewHeight;
        mParams.x = (screenWidth / 2) - viewWidth / 2;
        mParams.y = (screenHeight / 2) - viewHeight / 2;

        if (mWindowManager.isAddView()) {
            mWindowManager.removeSmallWindow(mView);
        }
        mWindowManager.addView(mView, mParams);
        LogUtils.d(TAG, "show window");
    }

    /**
     * 隐藏当前View
     */
    public void hide() {
        mWindowManager.removeSmallWindow(mView);
        LogUtils.d(TAG, "hide window");

    }

    /**
     * 获取悬乎窗实例
     *
     * @return
     */
    public FrameLayout getParentView() {
        return mSurfaceView;
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtils.d(TAG, "ACTION_DOWN");
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mParamX = mParams.x;
                    mParamY = mParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtils.d(TAG, "ACTION_MOVE");
                    int dx = (int) event.getRawX() - mTouchStartX;
                    int dy = (int) event.getRawY() - mTouchStartY;
                    mParams.x = mParamX + dx;
                    mParams.y = mParamY + dy;
                    mWindowManager.updateView(mView, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtils.d(TAG, "ACTION_MOVE");
                    break;
            }
            return true;
        }
    };
}
