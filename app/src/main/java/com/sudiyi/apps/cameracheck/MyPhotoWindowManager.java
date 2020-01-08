package com.sudiyi.apps.cameracheck;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;


/**
 * 悬浮窗管理
 */
public class MyPhotoWindowManager {
    private static String TAG = MyPhotoWindowManager.class.getCanonicalName();
    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */

    private static WindowManager mWindowManager;

    private static MyPhotoWindowManager mPhotoWindowManager;

    public boolean isAddView() {
        return isAddView;
    }

    public boolean isAddView = false;

    public MyPhotoWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 单例模式的最佳实现。内存占用地，效率高，线程安全，多线程操作原子性。
     *
     * @return
     */
    public static MyPhotoWindowManager getInstance(Context context) {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (mPhotoWindowManager == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (MyPhotoWindowManager.class) {
                //未初始化，则初始instance变量
                if (mPhotoWindowManager == null) {
                    mPhotoWindowManager = new MyPhotoWindowManager(context);
                }
            }
        }
        return mPhotoWindowManager;
    }

    /**
     * 添加悬浮窗
     *
     * @param view
     * @param params
     * @return
     */
    protected boolean addView(View view, WindowManager.LayoutParams params) {
        LogUtils.d(TAG, "addView");
        try {
            mWindowManager.addView(view, params);
            isAddView = true;
            return true;
        } catch (Exception e) {
            mWindowManager.removeView(view);
            mWindowManager.addView(view, params);
            LogUtils.e("manager had add this view !!!");
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param view
     */
    public boolean removeSmallWindow(View view) {
        LogUtils.d(TAG, "removeSmallWindow");

        try {
            mWindowManager.removeView(view);
            isAddView = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新悬浮窗参数
     *
     * @param view
     * @param params
     * @return
     */
    protected boolean updateView(View view, WindowManager.LayoutParams params) {
        LogUtils.d(TAG, "updateView");

        try {
            mWindowManager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static WindowManager getWindowManager() {
        return mWindowManager;
    }
}