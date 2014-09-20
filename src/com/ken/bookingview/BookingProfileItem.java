package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class BookingProfileItem implements Serializable {

	public static enum ServiceItems {
		°Å¾v, ¬~¾v, ¿S¾v, ¬V¾v
	};

	private String mBookingName;
	private String mBookingDate;
	private String mPhoneNumber;
	private ArrayList<ServiceItems> mServiceItems;
	private String mRequiredTime;

	public BookingProfileItem(String bookingName, String bookingDate, String phoneNumber, ArrayList<ServiceItems> serviceItem,
			String requiredTime) {
		mBookingName = bookingName;
		mBookingDate = bookingDate;
		mPhoneNumber = phoneNumber;
		mServiceItems = serviceItem;
		mRequiredTime = requiredTime;
	}

	public String getBookingName() {
		return mBookingName;
	}

	public String getBookingDate() {
		return mBookingDate;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public ArrayList<ServiceItems> getServiceItems() {
		return mServiceItems;
	}

	public String getRequiredTime() {
		return mRequiredTime;
	}
}
