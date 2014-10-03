package com.ken.bookingview;

import java.util.Calendar;

import android.util.Log;

public class DateUtilities {

	/*
	 * we assume the 1970/01/01 is start of the day, it's relative index is 0.
	 */
	private static final int START_YEAR = 1970;
	private static final int START_MONTH = Calendar.JANUARY;
	private static final int START_DATE = 1;

	/*
	 * we assume the 2199/12/31 is end of the day.
	 */
	private static final int END_YEAR = 2199;
	private static final int END_MONTH = Calendar.DECEMBER;
	private static final int END_DATE = 31;

	protected static final int A_MINUTE_IN_SECOND = 60;
	protected static final int A_HOUR_IN_MINUTE = 60;
	protected static final int A_DAY_IN_HOUR = 24;
	protected static final int A_DAY_IN_MINUTE = A_HOUR_IN_MINUTE * A_DAY_IN_HOUR;
	protected static final int A_DAY_IN_SECOND = A_MINUTE_IN_SECOND * A_DAY_IN_MINUTE;
	protected static final long A_DAY_IN_MILLISECOND = A_DAY_IN_SECOND * 1000;

	private static final Calendar sStartDate = Calendar.getInstance();
	private static final Calendar sEndDate = Calendar.getInstance();

	static Calendar sPickedDate;

	static {
		sStartDate.set(START_YEAR, START_MONTH, START_DATE, 0, 0, 0);
		sEndDate.set(END_YEAR, END_MONTH, END_DATE, 0, 0, 0);
		sPickedDate = getInstance();
	}

	private static int diff(long start, long end) {
		final long diff = Math.abs(end - start) / A_DAY_IN_MILLISECOND;
		return (int) diff;
	}

	/*
	 * only retrieve the year, month and date, don't care about the hour, minute and second.
	 */
	public static Calendar getInstance() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);
		return calendar;
	}

	/*
	 * only retrieve the year, month and date by specific calendar you pass, don't care about the hour, minute and second.
	 */
	public static Calendar getInstance(Calendar source) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, source.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, source.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, source.get(Calendar.DATE));
		return calendar;
	}

	/*
	 * the number of the day between 1970/01/01 and 2199/12/31.
	 */
	public synchronized static int getTotalDays() {
		final long start = sStartDate.getTimeInMillis();
		final long end = sEndDate.getTimeInMillis();
		return diff(start, end);
	}

	/*
	 * get index that indicates the number of days from 1970/01/01 to today.
	 */
	public synchronized static int getIndexOfToday() {
		final Calendar calendar = getInstance();
		final long start = sStartDate.getTimeInMillis();
		final long current = calendar.getTimeInMillis();
		return diff(start, current);
	}

	/*
	 * get calendar by passing a specific index that indicates the number of days from 1970/01/01.
	 */
	public static synchronized Calendar getCalendarByIndex(int dateIndex) {
		final long start = sStartDate.getTimeInMillis();
		final long end = start + dateIndex * A_DAY_IN_MILLISECOND;
		final Calendar calendar = getInstance();
		calendar.setTimeInMillis(end);
		// Log.d("kenchen", String.format("[getCalendarByIndex] index: %d date: %d/%d/%d", index,
		// calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
		return calendar;
	}

	/*
	 * get index that indicates the number of days from 1970/01/01 by passing a specific year, month and day
	 */
	public synchronized static int getIndexBySpecificDate(int year, int month, int day) {
		final Calendar calendar = getInstance();
		calendar.set(year, month, day);
		final long start = sStartDate.getTimeInMillis();
		final long specific = calendar.getTimeInMillis();
		return diff(start, specific);
	}

	/*
	 * get index that indicates the number of days from 1970/01/01 by passing a specific calendar
	 */
	public static synchronized int getIndexByCalendar(Calendar calendar) {
		final long start = sStartDate.getTimeInMillis();
		final long specific = calendar.getTimeInMillis();
		return diff(start, specific);
	}

	public static synchronized Calendar getCalendarWithOffsetOfMonthRelativeToPickedDate(int offset) {
		final Calendar calendar = getInstance(sPickedDate);
		calendar.add(Calendar.MONTH, offset);
		calendar.set(Calendar.DATE, 1);
		return calendar;
	}

	public static synchronized int getIndexWithOffsetOfMonthRelativeToPickedDate(int offset) {
		final Calendar calendar = getCalendarWithOffsetOfMonthRelativeToPickedDate(offset);
		final long start = sStartDate.getTimeInMillis();
		final long specific = calendar.getTimeInMillis();
		return diff(start, specific);
	}

	/*
	 * check that the date calculated by index is Saturday or Sunday
	 */
	public static synchronized boolean isSaturdayorSunday(int dateIndex) {
		Calendar calendar = getCalendarByIndex(dateIndex);
		final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public static synchronized boolean within(Calendar start, Calendar end, Calendar target) {
		Log.d("kenchen", String.format("[within] start: %s\nend: %s\n, target: %s", start, end, target));
		Log.d("kenchen", String.format("[within] within: %b", start.before(target) && end.after(target)));
		return start.before(target) && end.after(target);
	}
}
