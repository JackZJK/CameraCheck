package com.sudiyi.apps.cameracheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sudiyi.apps.cameracheck.util.AlarmTimeUtil;
import com.sudiyi.apps.cameracheck.util.PollingUtils;
import com.sudiyi.apps.cameracheck.util.TimeUtil;
import com.sudiyi.apps.cameracheck.util.log.LogUtils;

import static com.sudiyi.apps.cameracheck.App.getContext;


/**
 * Created by JackZheng on 2017/3/29.
 
 * 广播接收
 */
public class BroadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Config.CAPTURE_ACTION)) {
            LogUtils.d("start Service");
            Context appContext = getContext();
            Intent intents = new Intent(appContext, PhotoWindowService.class);
            appContext.startService(intents);
        } else if (intent.getAction().equals(Config.REPEAT_CHECK_CAMERA_ACTION)) {

            int setTime = AlarmTimeUtil.getAlarmTime(TimeUtil.timeHourFigure(System.currentTimeMillis()));

            if (setTime == -1) {
                setTime = AlarmTimeUtil.getOneClocTime(TimeUtil.timeHourFigure(System.currentTimeMillis()));
                PollingUtils.startAlarm(getContext(), setTime * 60 * 60, Config.REPEAT_CHECK_CAMERA_ACTION);
                LogUtils.d("End the day check task ！");
            } else {
                PollingUtils.startAlarm(getContext(), setTime * 60 * 60, Config.REPEAT_CHECK_CAMERA_ACTION);
                LogUtils.d("repeat start service ！" + "设置时间：" + setTime);
            }

            Context appContext = getContext();
            Intent intents = new Intent(appContext, PhotoWindowService.class);
            appContext.startService(intents);

        } else if (intent.getAction().equals(Config.BOOT_COMPLETED)) {
            LogUtils.d("boot and  start service ！");
            PollingUtils.stopPollingService(getContext(), Config.REPEAT_CHECK_CAMERA_ACTION);
            PollingUtils.startAlarm(getContext(), Config.START_SET_ALARM_CLOCKS, Config.REPEAT_CHECK_CAMERA_ACTION);
        }
    }
}
