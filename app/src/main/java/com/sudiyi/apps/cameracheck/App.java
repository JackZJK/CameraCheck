package com.sudiyi.apps.cameracheck;

import android.app.Application;
import android.content.Context;

import com.sudiyi.apps.cameracheck.util.log.LogUtils;

/**
 * Created by JackZheng on 2017/3/30.
 */
public class App extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        mContext = this;
        LogUtils.init(true, true, 'v', "CameraCheck");
        super.onCreate();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
