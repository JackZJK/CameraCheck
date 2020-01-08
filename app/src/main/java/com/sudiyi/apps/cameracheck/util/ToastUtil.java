package com.sudiyi.apps.cameracheck.util;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast = null;

    /**
     * 子线程也可调用
     *
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        Looper myLooper = Looper.myLooper();
        if (myLooper == null) {
            Looper.prepare();
            myLooper = Looper.myLooper();
        }

        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
        if (myLooper != null) {
            Looper.loop();
            myLooper.quit();
        }
    }
}
