package com.ken.bookingview;

import java.util.ArrayList;

import android.util.Log;

public class BookingRecord {

	private static final String TAG = BookingRecord.class.getSimpleName();

	public static final String SEPARATED_STRING = ", ";

	String name;
	String sex;
	int year;
	int month;
	int day;
	int hourOfDay;
	int minute;
	String phoneNumber;
	ArrayList<String> serviceType;
	int requiredHour;
	int requiredMinute;

	public BookingRecord(String name, String sex, int year, int month, int day, int hourOfDay, int minutes, String phoneNumber,
			ArrayList<String> serviceType, int requiredHour, int requiredMinute) {
		this.name = name;
		this.sex = sex;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hourOfDay = hourOfDay;
		this.minute = minutes;
		this.phoneNumber = phoneNumber;
		this.serviceType = serviceType;
		this.requiredHour = requiredHour;
		this.requiredMinute = requiredMinute;
	}

	public void updateRecord(BookingRecord updateRecord) {
		this.name = updateRecord.name;
		this.sex = updateRecord.sex;
		this.year = updateRecord.year;
		this.month = updateRecord.month;
		this.day = updateRecord.day;
		this.hourOfDay = updateRecord.hourOfDay;
		this.minute = updateRecord.minute;
		this.phoneNumber = updateRecord.phoneNumber;
		this.serviceType = updateRecord.serviceType;
		this.requiredHour = updateRecord.requiredHour;
		this.requiredMinute = updateRecord.requiredMinute;
	}

	public static String flattenServiceType(ArrayList<String> serviceType) {
		Log.d(TAG, "[flattenServiceType] service type size: " + serviceType.size());
		final int serviceTypeSize = serviceType.size();
		final StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < serviceTypeSize; ++i) {
			final String type = serviceType.get(i);
			// add the separated string ", " if it is first item added into string builder
			if (!"".equals(sb.toString())) {
				sb.append(BookingRecord.SEPARATED_STRING);
			}
			sb.append(type);
		}
		Log.d(TAG, "[flattenServiceType] flatten: " + sb.toString());
		return sb.toString();
	}

	public static ArrayList<String> unflattenServiceType(String flatten) {
		Log.d(TAG, "[unflattenServiceType] flatten: " + flatten);
		// FIXME
		final String[] parts = flatten.split(SEPARATED_STRING);
		Log.d(TAG, "[unflattenServiceType] parts: " + parts);
		final ArrayList<String> serviceType = new ArrayList<String>();
		if (parts != null) {
			for (int i = 0; i < parts.length; ++i) {
				Log.d(TAG, "[unflattenServiceType] parts[" + i + "]: " + parts[i]);
				serviceType.add(parts[i]);
			}
		}
		return serviceType;
	}

	@Override
	public String toString() {
		return String.format(
				"hash: %d, name: %s, sex: %s, date: %04d/%02d/%02d %02d:%02d, phoneNumber: %s, services: %s, requiredTime: %2d:%02d",
				hashCode(), name, sex, year, month, day, hourOfDay, minute, phoneNumber, serviceType.toArray().toString(), requiredHour,
				requiredMinute);
	}
}
