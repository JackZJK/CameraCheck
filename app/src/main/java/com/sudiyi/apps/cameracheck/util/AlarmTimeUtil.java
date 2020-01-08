package com.sudiyi.apps.cameracheck.util;

public class AlarmTimeUtil {
    private static int[] defaultHour = new int[]{6, 11, 14, 17, 1};//经验值 错过使用高峰期
    private static int onecloc = 1;//经验值 错过使用高峰期

    /**
     * 根据当前闹钟时间 返回下一个时刻闹钟响起时间
     *
     * @return
     */
    public static int getAlarmTime(int hour) {
        int result = -1;

        for (int currentNum : defaultHour) {
            if (currentNum == hour) {
                continue;
            }
            if (hour < currentNum) {
                return currentNum - hour;
            }
        }
        return result;
    }

    /**
     * 根据当前闹钟时间 返回下一个一点整闹钟响起时间
     *
     * @return
     */
    public static int getOneClocTime(int hour) {
        int result;
        result = 24 - hour + onecloc;
        return result;
    }
}
