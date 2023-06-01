package com.zrg.commons.utils;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DateTime Helper
 *
 * @author zrg
 * @date 2021/8/3 22:26
 */
public class DateTimeUtil {
    /**
     * 时间格式化为yyyy年MM月dd日HH时mm分ss秒
     *
     * @param time LocalDateTime
     * @return String
     */
    public static String getChineseTimeFormat(LocalDateTime time) {
        return time.getYear() + "年" + time.getMonthValue() + "月" + time.getDayOfMonth() + "日" + time.getHour() + "时" + time.getMinute() + "分" + time.getSecond() + "秒";
    }

    /**
     * 根据时间长度,时间长度类型，时间尺度，时间精度获取格式化后的字符串
     *
     * @param lengthType 时间长度类型
     * @param length     时间长度
     * @param scale      时间尺度，从哪一级时间开始显示
     * @param resolution 时间精度，到哪一级时间精度为止
     *                   <p>
     *                   例如：参数为132478，ChronoUnit.Minute, ChronoUnit.Days, ChronnoUnit.Minute
     *                   调用结果为  91日23时58分
     *                   参数为132478，ChronoUnit.Minute, ChronoUnit.Month, ChronnoUnit.Second
     *                   调用结果为  3月1日23时58分
     * @return 格式化后的字符串
     */
    public static String getTimeFormat(Long length, ChronoUnit lengthType, ChronoUnit scale, ChronoUnit resolution) {
        @Data
        class TimeScale {
            /**
             * 时间单位
             */
            private ChronoUnit chronoUnit;
            /**
             * 时间单位名字
             */
            private String name;

            private TimeScale(ChronoUnit chronoUnit, String name) {
                this.chronoUnit = chronoUnit;
                this.name = name;
            }
        }

        List<TimeScale> timeScaleList = new ArrayList<>();
        timeScaleList.add(new TimeScale(ChronoUnit.YEARS, "年"));
        timeScaleList.add(new TimeScale(ChronoUnit.MONTHS, "月"));
        timeScaleList.add(new TimeScale(ChronoUnit.DAYS, "天"));
        timeScaleList.add(new TimeScale(ChronoUnit.HOURS, "小时"));
        timeScaleList.add(new TimeScale(ChronoUnit.MINUTES, "分钟"));
        timeScaleList.add(new TimeScale(ChronoUnit.SECONDS, "秒"));

        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plus(length, lengthType);

        boolean underScale = false;
        for (TimeScale timeScale : timeScaleList) {
            if (timeScale.getChronoUnit() == scale) {
                underScale = true;
            }
            if (underScale) {
                long different = timeScale.chronoUnit.between(now, later);
                if (different > 0) {
                    stringBuilder.append(different).append(timeScale.getName());
                    now = now.plus(different, timeScale.getChronoUnit());
                }
            }
            if (timeScale.getChronoUnit() == resolution) {
                return stringBuilder.toString();
            }
        }
        return "时间异常";
    }

    /**
     * 根据起止时间获取两个时间之间的停车分钟差,不足1分钟按1分钟算
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 分钟差
     */
    public static Long getParkTimeDiff(LocalDateTime start, LocalDateTime end) {
        long diff = ChronoUnit.SECONDS.between(start, end);
        return (long) Math.ceil((double) diff / 60);
    }

    /**
     * 时间+多少分钟
     *
     * @param date   日期对象
     * @param minute 增加的时间
     * @return 增加时间以后的时间对象
     */
    public static Date timeAddminutes(Date date, Integer minute) {
        long time = date.getTime();
        time += (minute * 60 * 1000);
        return new Date(time);
    }

    /**
     * 根据传入日期对象获取日期对象当天0点的对象
     *
     * @param date 需要获取当天0点的事件对象
     * @return 当天0点的时间对象
     * @throws ParseException 异常，一般不会抛出
     */
    public static Date getDate(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.parse(simpleDateFormat.format(date));
    }

    /**
     * 根据传入日期对象获取日期对象在系统日期当天的时分秒对象，用于比较时分秒
     *
     * @param date 日期对象
     * @return 系统当天日期下的时分秒对象
     * @throws ParseException 异常，一般不会抛出
     */
    public static Date getTime(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        return simpleDateFormat.parse(simpleDateFormat.format(date));
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        try {
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zone).toInstant();
            return Date.from(instant);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取当前时间(字符串格式)
     *
     * @param format
     */
    public static String getCurrentDateTime(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * 获取昨天时间格式
     *
     * @param format
     */
    public static String getYesterdayByFormat(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().minusDays(1).format(formatter);
    }

    /**
     * 获取从1970年1月1日到现在的秒数
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static Long getSecond(String dateTime, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dtf);
        Long second = localDateTime.toEpochSecond(ZoneOffset.of("+8"));
        return second;
    }

    /**
     * LocalDateTime转String
     *
     * @param localDateTime
     * @param format
     * @return
     */
    public static String getLocalDateTimeToStr(LocalDateTime localDateTime, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(dtf);
    }

    /**
     * 获取从1970年1月1日到现在的秒数转LocalDateTime
     *
     * @param second
     * @return
     */
    public static LocalDateTime getSecondToLocalDateTime(Long second) {
        Instant instant = Instant.ofEpochSecond(second);
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 获取从1970年1月1日到现在的秒数转字符串格式
     *
     * @param second
     * @param format
     * @return
     */
    public static String getSecondToStr(long second, String format) {
        return getLocalDateTimeToStr(getSecondToLocalDateTime(second), format);
    }
}
