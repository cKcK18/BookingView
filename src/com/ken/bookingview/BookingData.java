package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingData implements Serializable {

	private static final long serialVersionUID = 12345678905566L;

	final long id; // the same as the database id
	String name;
	String sex;
	int year;
	int month;
	int day;
	int hourOfDay;
	int minute;
	String phoneNumber;
	ArrayList<String> serviceItems;
	String requiredTime;

	public BookingData(long id, String name, String sex, int year, int month, int day, int hourOfDay, int minutes,
			String phoneNumber, ArrayList<String> serviceItem, String requiredTime) {
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hourOfDay = hourOfDay;
		this.minute = minutes;
		this.phoneNumber = phoneNumber;
		this.serviceItems = serviceItem;
		this.requiredTime = requiredTime;
	}

	public void setTimeSheetItem(BookingData timeSheetItem) {
		name = timeSheetItem.name;
		sex = timeSheetItem.sex;
		year = timeSheetItem.year;
		month = timeSheetItem.month;
		day = timeSheetItem.day;
		hourOfDay = timeSheetItem.hourOfDay;
		minute = timeSheetItem.minute;
		phoneNumber = timeSheetItem.phoneNumber;
		serviceItems = timeSheetItem.serviceItems;
		requiredTime = timeSheetItem.requiredTime;
	}

	@Override
	public String toString() {
		return String
				.format("hash: %d, name: %s, sex: %s, date: %04d/%02d/%02d %02d:%02d, phoneNumber: %s, services: %s, requiredTime: %s",
						hashCode(), name, sex, year, month, day, hourOfDay, minute, phoneNumber, serviceItems.toArray()
								.toString(), requiredTime);
	}
}
