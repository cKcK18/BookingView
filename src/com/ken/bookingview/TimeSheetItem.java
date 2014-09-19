package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;

import com.ken.bookingview.BookingProfileItem.ServiceItems;

public class TimeSheetItem implements Serializable {

	private static final long serialVersionUID = 12345678905566L;

	String bookingName;
	int bookingYear;
	int bookingMonth;
	int bookingDay;
	int bookingHour;
	int bookingMinutes;
	String phoneNumber;
	ArrayList<ServiceItems> serviceItems;
	String requiredTime;

	public TimeSheetItem(String bookingName, int bookingYear, int bookingMonth, int bookingDay, int bookingHour, int bookingMinutes,
			String phoneNumber, ArrayList<ServiceItems> serviceItem, String requiredTime) {
		this.bookingName = bookingName;
		this.bookingYear = bookingYear;
		this.bookingMonth = bookingMonth;
		this.bookingDay = bookingDay;
		this.bookingHour = bookingHour;
		this.bookingMinutes = bookingMinutes;
		this.phoneNumber = phoneNumber;
		this.serviceItems = serviceItem;
		this.requiredTime = requiredTime;
	}

	public void setTimeSheetItem(TimeSheetItem timeSheetItem) {
		bookingName = timeSheetItem.bookingName;
		bookingYear = timeSheetItem.bookingYear;
		bookingMonth = timeSheetItem.bookingMonth;
		bookingDay = timeSheetItem.bookingDay;
		bookingHour = timeSheetItem.bookingHour;
		bookingMinutes = timeSheetItem.bookingMinutes;
		phoneNumber = timeSheetItem.phoneNumber;
		serviceItems = timeSheetItem.serviceItems;
		requiredTime = timeSheetItem.requiredTime;
	}
}
