package com.ken.bookingview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BookingDetailView extends LinearLayout {

	public BookingDetailView(Context context) {
		this(context, null);
	}

	public BookingDetailView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookingDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUpView(context);
	}

	protected void setUpView(Context context) {
	}

	@Override
	protected void onFinishInflate() {
	}

}
