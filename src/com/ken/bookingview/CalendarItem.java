package com.ken.bookingview;

import java.io.Serializable;
import java.util.Calendar;

public class CalendarItem implements Serializable {

	private static final long serialVersionUID = 12345678907788L;

	Calendar calendar;

	public CalendarItem(Calendar calendar) {
		this.calendar = calendar;
	}
}
