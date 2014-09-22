package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingData implements Serializable {

	private static final long serialVersionUID = 12345678905566L;

	public enum ServiceItems {
		cut, shampoo, permanent, color, treatment
	}

	String bookingName;
	int bookingYear;
	int bookingMonth;
	int bookingDate;
	int bookingHour;
	int bookingMinutes;
	String phoneNumber;
	ArrayList<ServiceItems> serviceItems;
	String requiredTime;

	public BookingData(String bookingName, int bookingYear, int bookingMonth, int bookingDate, int bookingHour,
			int bookingMinutes, String phoneNumber, ArrayList<ServiceItems> serviceItem, String requiredTime) {
		this.bookingName = bookingName;
		this.bookingYear = bookingYear;
		this.bookingMonth = bookingMonth;
		this.bookingDate = bookingDate;
		this.bookingHour = bookingHour;
		this.bookingMinutes = bookingMinutes;
		this.phoneNumber = phoneNumber;
		this.serviceItems = serviceItem;
		this.requiredTime = requiredTime;
	}

	public void setTimeSheetItem(BookingData timeSheetItem) {
		bookingName = timeSheetItem.bookingName;
		bookingYear = timeSheetItem.bookingYear;
		bookingMonth = timeSheetItem.bookingMonth;
		bookingDate = timeSheetItem.bookingDate;
		bookingHour = timeSheetItem.bookingHour;
		bookingMinutes = timeSheetItem.bookingMinutes;
		phoneNumber = timeSheetItem.phoneNumber;
		serviceItems = timeSheetItem.serviceItems;
		requiredTime = timeSheetItem.requiredTime;
	}
}
