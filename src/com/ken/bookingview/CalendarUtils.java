package com.ken.bookingview;

import java.util.Calendar;

import android.util.Log;

public class CalendarUtils {

	/*
	 * we assume the 1900/01/01 is start of the day, it's relative index is 0.
	 */
	private static final int START_YEAR = 1900;
	private static final int START_MONTH = 0;
	private static final int START_DATE = 1;

	/*
	 * we assume the 2199/12/31 is end of the day.
	 */
	private static final int END_YEAR = 2199;
	private static final int END_MONTH = 11;
	private static final int END_DATE = 31;

	private static final long A_DAY_IN_MILLISECOND = 60 * 60 * 24 * 1000;

	private static final Calendar sStartate = Calendar.getInstance();
	private static final Calendar sEndDate = Calendar.getInstance();

	static {
		sStartate.set(Calendar.YEAR, START_YEAR);
		sStartate.set(Calendar.MONTH, START_MONTH);
		sStartate.set(Calendar.DATE, START_DATE);
		sEndDate.set(Calendar.YEAR, END_YEAR);
		sEndDate.set(Calendar.MONTH, END_MONTH);
		sEndDate.set(Calendar.DATE, END_DATE);
	}

	private static int diffDay(long start, long end) {
		final long diff = Math.abs(end - start) / A_DAY_IN_MILLISECOND;
		return (int) diff;
	}

	public synchronized static int getTotalDays() {
		final long start = sStartate.getTimeInMillis();
		final long end = sEndDate.getTimeInMillis();
		return diffDay(start, end);
	}

	public synchronized static int getIndexOfCurrentDay() {
		final Calendar calendar = Calendar.getInstance();
		final long current = calendar.getTimeInMillis();
		final long start = sStartate.getTimeInMillis();
//		Log.d("kenchen", "[getIndexOfCurrentDay] diff: " + diffDay(start, current));
		return diffDay(start, current);
	}

	public synchronized static int getIndexBySpecificDate(int year, int month, int day) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		final long start = sStartate.getTimeInMillis();
		final long specific = calendar.getTimeInMillis();
		return diffDay(start, specific);
	}

	public static synchronized Calendar getCalendarByIndex(int index) {
		final long start = sStartate.getTimeInMillis();
		final long end = start + index * A_DAY_IN_MILLISECOND;
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(end);
//		Log.d("kenchen", String.format("[getCalendarByIndex] index: %d date: %d/%d/%d", index,
//				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
		return calendar;
	}
}
