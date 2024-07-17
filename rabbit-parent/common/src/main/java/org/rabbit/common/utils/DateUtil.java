package org.rabbit.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

	public static String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String DATEFORMAT = "yyyy-MM-dd";
	public static String DATEFORMAT2 = "yyyy/MM/dd";
	public static String DATEFORMAT3 = "yyyyMMdd";
	public static String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
	public static String MINUTE_FORMAT2 = "yyyy/MM/dd HH:mm";
	public static String SECOND_FORMAT3 = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static String SECOND_FORMAT4 = "yyyy-MM-dd'T'HH:mm";
	public static String SECOND_FORMAT5 = "yyyy-MM-dd'T'HH:mm:ss";
	public static String NAXUEFORMAT3 = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
	public static String NAXUEFORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static String NAXUEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static String SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String SECOND_FORMAT2 = "yyyy/MM/dd HH:mm:ss";

	public static String MYSQL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static String[] PARSE_PATTERNS = {
			TIMEFORMAT, DATEFORMAT, "yyyy年MM月dd日",
			DATEFORMAT2, DATEFORMAT3,
			SECOND_FORMAT, SECOND_FORMAT2, SECOND_FORMAT3,
			MINUTE_FORMAT, MINUTE_FORMAT2, SECOND_FORMAT4, SECOND_FORMAT5,
			NAXUEFORMAT, NAXUEFORMAT2, NAXUEFORMAT3,
			"yyyy-MM-dd HH:mm:ss.SSSSSS", "yyyy/MM/dd HH:mm:ss.SSSSSS", "yyyyMMdd HH:mm:ss.SSSSSS"
	};

	public static Date formatDate(String eventDate) {
		if (StringUtils.isNumeric(eventDate)) {
			Timestamp tms = new Timestamp(Long.parseLong(eventDate));
			Date date = new Date(tms.getTime());
			return DateUtil.convertSqlDate(date);
		} else {
			try {
				return DateUtils.parseDate(eventDate, PARSE_PATTERNS);
			} catch (ParseException e) {
				System.out.println("Failed to parse Log Event Date:: " + eventDate);
			}
		}
		return null;
	}

	/**
	 * 把日期字符串格式化成日期类型
	 *
	 * @param dateStr
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date convert2Date(String dateStr, String format) throws ParseException {
		SimpleDateFormat simple = new SimpleDateFormat(format);
		simple.setLenient(false);
		return simple.parse(dateStr);
	}

	/**
	 * 把日期类型格式化成字符串
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String convert2String(Date date, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		return formater.format(date);
	}

	/**
	 * 获取当前日期
	 *
	 * @return Date
	 */
	public static Date currentDate() {
		return new Date();
	}

	/**
	 * 转sql的time格式
	 *
	 * @param date
	 * @return
	 */
	public static Timestamp convertSqlTime(Date date) {
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}

	/**
	 * 转sql的日期格式
	 *
	 * @param date
	 * @return
	 */
	public static java.sql.Date convertSqlDate(Date date) {
		java.sql.Date Datetamp = new java.sql.Date(date.getTime());
		return Datetamp;
	}

	/**
	 * 获取当前日期
	 *
	 * @param format
	 * @return
	 */
	public static String getCurrentDate(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 获取当前日期
	 *
	 * @param
	 * @return
	 */
	public static String getCurrentDate() {
		return new SimpleDateFormat(TIMEFORMAT).format(new Date());
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(getTimestamp());
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Long getTimestampSecond() {
		return System.currentTimeMillis() / 1000l;
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Integer getTimestampStandard() {
		Long timestamp = System.currentTimeMillis() / 1000l;
		return timestamp.intValue();
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Integer getTimestampInt(Timestamp timestamp) {
		Long time = timestamp.getTime() / 1000l;
		return time.intValue();
	}

	/**
	 * 获取时间戳
	 *
	 * @return
	 */
	public static Integer getTimestampInt() {
		return getTimestampSecond().intValue();
	}

	/**
	 * 获取月份的天数
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期的年
	 *
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取日期的月
	 *
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期的日
	 *
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 获取日期的时
	 *
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR);
	}

	/**
	 * 获取日期的分种
	 *
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒
	 *
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * 获取星期几
	 *
	 * @param date
	 * @return
	 */
	public static int getWeekDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek - 1;
	}


	private static Date add(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(calendarField, amount);
			return c.getTime();
		}
	}

	/*
	 * 1则代表的是对年份操作， 2是对月份操作， 3是对星期操作， 5是对日期操作， 11是对小时操作， 12是对分钟操作， 13是对秒操作， 14是对毫秒操作
	 */

	/**
	 * 增加年
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addYears(Date date, int amount) {
		return add(date, 1, amount);
	}

	/**
	 * 增加月
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addMonths(Date date, int amount) {
		return add(date, 2, amount);
	}

	/**
	 * 增加周
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addWeeks(Date date, int amount) {
		return add(date, 3, amount);
	}

	/**
	 * 增加天
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addDays(Date date, int amount) {
		return add(date, 5, amount);
	}

	/**
	 * 增加时
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addHours(Date date, int amount) {
		return add(date, 11, amount);
	}

	/**
	 * 增加分
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addMinutes(Date date, int amount) {
		return add(date, 12, amount);
	}

	/**
	 * 增加秒
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addSeconds(Date date, int amount) {
		return add(date, 13, amount);
	}

	/**
	 * 增加毫秒
	 *
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addMilliseconds(Date date, int amount) {
		return add(date, 14, amount);
	}


	/**
	 * 秒差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static long diffSecond(Date before, Date after) {
		return (after.getTime() - before.getTime()) / 1000;
	}

	/**
	 * 分种差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffMinute(Date before, Date after) {
		return (int) ((after.getTime() - before.getTime()) / 1000l / 60);
	}

	/**
	 * 时差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffHour(Date before, Date after) {
		return (int) ((after.getTime() - before.getTime()) / 1000L / 60 / 60);
	}

	/**
	 * 天数差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffDay(Date before, Date after) {
		return Integer.parseInt(String.valueOf(((after.getTime() - before.getTime()) / 86400000)));
	}

	/**
	 * 月差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffMonth(Date before, Date after) {
		int monthAll = 0;
		int yearsX = diffYear(before, after);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(before);
		c2.setTime(after);
		int monthsX = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		monthAll = yearsX * 12 + monthsX;
		int daysX = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
		if (daysX > 0) {
			monthAll = monthAll + 1;
		}
		return monthAll;
	}

	/**
	 * 年差
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffYear(Date before, Date after) {
		return getYear(after) - getYear(before);
	}

	/**
	 * 设置23:59:59
	 *
	 * @param date
	 * @return
	 */
	public static Date setEndDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 设置00:00:00
	 *
	 * @param date
	 * @return
	 */
	public static Date setStartDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		return calendar.getTime();
	}

	/**
	 * 取得上周的星期一
	 *
	 * @param date
	 * @return
	 */
	public static Date LastWeekMonday(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int n = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (n == 0) {
			n = 7;
		}
		cal.add(Calendar.DATE, -(7 + (n - 1)));
		Date monday = cal.getTime();
		return monday;
	}

	/**
	 * 取得某天所在周的最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		return c.getTime();
	}
	public static Date firstDayOfLastMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH,1);//设置为1号
		return c.getTime();
	}
	public static Date lastDayOfLastMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH,0);//设置为1号
		return c.getTime();
	}


	public static Date firstDayOfLastYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int currentYear = c.get(Calendar.YEAR);
		c.clear();
		c.set(Calendar.YEAR, currentYear-1);
		return c.getTime();
	}
	public static Date lastDayOfLastYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int currentYear = c.get(Calendar.YEAR);
		c.clear();
		c.set(Calendar.YEAR, currentYear);
		c.set(Calendar.DAY_OF_YEAR,-1);
		return c.getTime();
	}
	public static Map<String, String> getMondayToSunday() throws ParseException {
		Date date = convert2Date("2021-01-01 00:00:00",TIMEFORMAT);
		Map<String, String> map = new HashMap<String, String>();
		Date monday = LastWeekMonday(date);
		map.put("monday", convert2String(setStartDay(monday),TIMEFORMAT));
		Date sunday = getLastDayOfWeek(monday);
		map.put("sunday", convert2String(setEndDay(sunday),TIMEFORMAT));
		return map;
	}

	public static void main(String[] args) throws ParseException {
		Map<String,String> map = getMondayToSunday();
		Date date = DateUtil.convert2Date(map.get("monday"),TIMEFORMAT);

	}

}
