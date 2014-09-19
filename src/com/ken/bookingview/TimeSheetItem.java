package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;

import com.ken.bookingview.BookingProfileItem.ServiceItems;

public class TimeSheetItem implements Serializable {

	private static final long serialVersionUID = 12345678905566L;

	private final int mIdentity;
	private String mBookingName;
	private String mBookingDate;
	private String mPhoneNumber;
	private ArrayList<ServiceItems> mServiceItems;
	private String mRequiredTime;

	public TimeSheetItem(int identity, String bookingName, String bookingDate, String phoneNumber, ArrayList<ServiceItems> serviceItem,
			String requiredTime) {
		mIdentity = identity;
		mBookingName = bookingName;
		mBookingDate = bookingDate;
		mPhoneNumber = phoneNumber;
		mServiceItems = serviceItem;
		mRequiredTime = requiredTime;
	}

	public void setTimeSheetItem(TimeSheetItem timeSheetItem) {
		mBookingName = timeSheetItem.getBookingName();
		mBookingDate = timeSheetItem.getBookingDate();
		mPhoneNumber = timeSheetItem.getPhoneNumber();
		mServiceItems = timeSheetItem.getServiceItems();
		mRequiredTime = timeSheetItem.getRequiredTime();
	}

	public int getIdetity() {
		return mIdentity;
	}

	public void setBookingName(String bookingName) {
		mBookingName = bookingName;
	}

	public String getBookingName() {
		return mBookingName;
	}

	public void setBookingDate(String bookingDate) {
		mBookingDate = bookingDate;
	}

	public String getBookingDate() {
		return mBookingDate;
	}

	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setServiceItems(ArrayList<ServiceItems> serviceItems) {
		mServiceItems = serviceItems;
	}

	public ArrayList<ServiceItems> getServiceItems() {
		return mServiceItems;
	}

	public void setRequiredTime(String requiredTime) {
		mRequiredTime = requiredTime;
	}

	public String getRequiredTime() {
		return mRequiredTime;
	}
}
