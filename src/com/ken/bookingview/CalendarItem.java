package com.ken.bookingview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import com.ken.bookingview.BookingProfileItem.ServiceItems;

public class CalendarItem implements Serializable {

	private static final long serialVersionUID = 12345678907788L;

	Calendar calendar;

	public CalendarItem(Calendar calendar) {
		this.calendar = calendar;
	}
}
