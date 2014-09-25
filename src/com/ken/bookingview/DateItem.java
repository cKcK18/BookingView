package com.ken.bookingview;

import java.io.Serializable;
import java.util.Calendar;

public class DateItem implements Serializable {

	private static final long serialVersionUID = 12345678907788L;

	Calendar calendar;

	public DateItem(Calendar calendar) {
		this.calendar = calendar;
	}
}
