package com.sudiyi.apps.cameracheck.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    /**
     * 对比当前时间，转换时间戳
     *
     * @param cc_time 需要转换的时间戳
     * @return 返回的时间格式：获取时间年份与当前年份一致，返回 MM-dd HH:mm；否则返回yyyy-MM-dd HH:mm
     */
    public static String timeCompareYMDHMinSFigure(long cc_time) {
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        re_StrTime = sdf.format(new Date(cc_time));
        String years = re_StrTime.substring(0, 4);
        if (!year.equals(years)) {
            return re_StrTime;
        } else {
            return re_StrTime.substring(5, re_StrTime.length());
        }
    }

    /**
     * 时间格式转换
     *
     * @param time 需要转换的时间戳
     * @return 返回时间格式  yyyy年MM月dd日
     */
    public static String timeYMDChinese(long time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        re_StrTime = sdf.format(new Date(time));
        return re_StrTime;
    }


    /**
     * 时间格式转换
     *
     * @param time 需要转换的时间戳
     * @return 返回时间格式  yyyy-MM-dd  HH:mm:ss
     */
    public static String timeYMDHMinSFigure(long time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        re_StrTime = sdf.format(new Date(time));
        return re_StrTime;
    }

    /**
     * 时间格式转换
     *
     * @param time 需要转换的时间戳
     * @return 返回时间格式  yyyy-MM-dd
     */
    public static String timeYMDFigure(long time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        re_StrTime = sdf.format(new Date(time));
        return re_StrTime;

    }

    /**
     * 获取当前时间小时转换
     *
     * @param time 需要转换的时间戳
     * @return 返回时间格式  yyyy-MM-dd
     */
    public static int timeHourFigure(long time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        re_StrTime = sdf.format(new Date(time));
        return Integer.parseInt(re_StrTime);
    }

    /**
     * 毫秒 转化为 天 时 分 秒 毫秒
     *
     * @param ms 毫秒数
     * @return dd天HH时mm秒xx毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分钟");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }

    /**
     * 时间比较
     *
     * @param startTime 起始时间
     * @param endTime   终止时间
     * @param format    转换的时间格式，如：yyyy-MM-dd
     * @return 返回两个时间相差多少天
     */
    public static long dateDiff(String startTime, String endTime, String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            // Log.d("TimeUTIL", "时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
            if (day >= 1) {
                return day;
            } else {
                if (day == 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}