package com.shenchen.odds.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils implements Serializable {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * serialVersionUID:（用一句话描述这个变量表示什么）
     *
     * @since Ver 1.1
     */

    private static final long             serialVersionUID = 1L;

    public static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String DateFormatString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String datestr = dateFormat.format(date);
            return datestr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date StringFormatDate(String datestr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(datestr);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date StringFormatDay(String datestr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(datestr);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date endDateFormat(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(5, 1);
            return c.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String diff(Date d1) {
        if (d1 != null) {
            Date d2 = new Date();
            long diff = d1.getTime() - d2.getTime();
            long days = diff / 86400000L;
            long hours = (diff - days * 86400000L) / 3600000L;
            long minutes = (diff - days * 86400000L - hours * 3600000L) / 60000L;
            long ss = (diff - days * 86400000L - hours * 3600000L - minutes * 60000L) / 1000L;
            System.out.println(days + "天" + hours + "小时" + minutes + "分" + ss + "秒");
            return days + " " + hours + ":" + minutes + ":" + ss;
        }
        return "00:00";
    }

    /**
     * 此方法描述的是： 比较两时间差 秒数
     *
     * @author: 19511@etransfar.com
     * @version: 2017年11月15日 下午2:12:29
     */
    public static int dateTimeDiff(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) (Math.abs(diff) / 1000);
    }

    public static Date LongformartDate(Long dateLong) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLong.longValue() * 1000L);
        Date date = calendar.getTime();
        return date;
    }

    public static long DateformartLong(String dateStr) {
        Date date2 = StringFormatDate(dateStr);
        long date = date2.getTime();
        return date;
    }

    public static Date addSecond(Date date, int second) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(13, second);
        date = calendar.getTime();
        return date;
    }

    /**
     * 此方法描述的是： 生成标准时间格式
     *
     * @author: 19511@etransfar.com
     * @version: 2017年11月14日 下午10:34:00
     */
    public static String dateFormatUTC(String dateTimeStr) {
        String STANDARD_DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_DATE_FORMAT_UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateTime = StringFormatDate(dateTimeStr);
        return sdf.format(dateTime);
    }

    public static final String date2Str(String fmt, Date date) {
        return newFormatter(fmt).format(date);
    }

    public static final Date str2Date(String fmt, String dateStr) throws ParseException {
        return newFormatter(fmt).parse(dateStr);
    }

    public static final DateFormat newFormatter(String fmt) {
        return new SimpleDateFormat(fmt);
    }

    public static Date getBeforeHourTime(int ihour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - ihour);
        return calendar.getTime();
    }

    public static Date getBeforeMinuteTime(int iminute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - iminute);
        return calendar.getTime();
    }

    /**
     * 时间格式校验
     *
     * @param str
     * @param dateFormat
     * @return
     */
    public static boolean isValidDate(String str, String dateFormat) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static String formatDateTime(long mss) {
        String DateTimes = null;
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        if (days > 0) {
            DateTimes = days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (hours > 0) {
            DateTimes = hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (minutes > 0) {
            DateTimes = minutes + "分钟" + seconds + "秒";
        } else {
            DateTimes = seconds + "秒";
        }
        return DateTimes;
    }

    public static boolean isGreaterSecond(int seconds, Date dateTime) {
        boolean isGreaterSecond = false;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - seconds);
        if (calendar.getTime().after(dateTime)) {
            isGreaterSecond = true;
        }
        return isGreaterSecond;
    }

    /**
     * 返回当日|dayNum|天之前,格式为timeFormat的日期
     *
     * @param dayNum
     * @return
     * @author chenpengchao 2018年5月2日 上午9:25:17
     */
    public static String getLastDate(int dayNum, String timeFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - Math.abs(dayNum));
        return new SimpleDateFormat(timeFormat).format(calendar.getTime());
    }

    /**
     * 判断两个日期相差天数
     *
     * @param beginDate
     * @param endDate
     * @return
     * @throws ParseException
     * @author chenpengchao 2018年5月2日 上午9:35:02
     */
    public static int getDiffDay(String beginDate, String endDate) throws ParseException {
        Date beginDateFormat = simpleDateFormat.parse(beginDate);
        Date endDateFormat = simpleDateFormat.parse(endDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDateFormat);
        int begin = calendar.get(Calendar.DATE);
        calendar.setTime(endDateFormat);
        int end = calendar.get(Calendar.DATE);
        return Math.abs(begin - end);
    }

    /**
     * 判断两个日期相差天数
     *
     * @param beginDate
     * @param endDate
     * @return
     * @throws ParseException
     * @author chenpengchao 2018年5月2日 上午9:35:02
     */
    public static int getDiffDateDays(String beginDate, String endDate) throws ParseException {
    	long nd = 1000 * 24 * 60 * 60;
        Date beginDateFormat = simpleDateFormat.parse(beginDate);
        Date endDateFormat = simpleDateFormat.parse(endDate);
        long diff = endDateFormat.getTime() - beginDateFormat.getTime();
        long day = diff / nd;
        return Math.abs((int)day);
    }

    /**
     * 判断两个日期相差天数
     *
     * @param beginDate
     * @param endDate
     * @return
     * @throws ParseException
     * @author chenpengchao 2018年5月2日 上午9:35:02
     */
    public static int getDiffUnitDateDay(String beginDate, String endDate) throws ParseException {
    	long nd = 1000 * 24 * 60 * 60;
        Date beginDateFormat = simpleDateFormat.parse(beginDate);
        Date endDateFormat = simpleDateFormat.parse(endDate);
        long diff = endDateFormat.getTime() - beginDateFormat.getTime();
        long day = diff / nd;
        return (int)day;
    }

    public static void main(String[] args) {
        // System.out.println(dateFormatUTC("2017-11-14 22:31:20"));
        // System.out.println(DateUtils.DateFormatString(getBeforeHourTime(4)));
        // System.out.println((((1510931420000l-1510279820000l)/(60*60*1000)))/24d);
        // System.out.println(isValidDate("2017-11-12 22:31:20","yyyy-MM-dd HH:mm:ss"));
        // System.out.println(getBeforeMinuteTime(30));
        // Date dateTime = StringFormatDate("2018-03-01 10:20:10");
        // System.out.println(isGreaterSecond(1800, dateTime));
//        System.out.println(formatDateTime(122222));
    	try {
			System.out.println(getDiffDateDays("2017-12-02 10:20:30","2018-07-22 10:20:30"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
}
