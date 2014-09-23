package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingData implements Serializable {

	private static final long serialVersionUID = 12345678905566L;

	public enum ServiceItems {
		cut, shampoo, permanent, color, treatment
	}

	final long id;	// the same as the database id
	String bookingName;
	int bookingYear;
	int bookingMonth;
	int bookingDate;
	int bookingHour;
	int bookingMinutes;
	String phoneNumber;
	ArrayList<ServiceItems> serviceItems;
	String requiredTime;

	public BookingData(long id, String bookingName, int bookingYear, int bookingMonth, int bookingDate,
			int bookingHour, int bookingMinutes, String phoneNumber, ArrayList<ServiceItems> serviceItem,
			String requiredTime) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return String.format(
				"hash: %d, name: %s, date: %04d/%02d/%02d %02d:%02d, phoneNumber: %s, services: %s, requiredTime: %s",
				hashCode(), bookingName, bookingYear, bookingMonth, bookingDate, bookingHour, bookingMinutes,
				phoneNumber, serviceItems.toArray().toString(), requiredTime);
	}
}
