package com.ken.bookingview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateItemView extends LinearLayout {

	private static final Drawable mOverlayDrawable = new ColorDrawable(0x80434575);
	private static final Drawable mTransparentDrawable = new ColorDrawable(Color.TRANSPARENT);

	private TextView mDayOfWeekView;
	private TextView mDayOfMonthView;

	public DateItemView(Context context) {
		this(context, null);
	}

	public DateItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DateItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		mDayOfWeekView = (TextView) findViewById(R.id.date_day_of_week);
		mDayOfMonthView = (TextView) findViewById(R.id.customer_booking_item_status);
	}

	public void setUpView() {
		final Integer dateIndex = (Integer) getTag(R.id.date_item_index);
		final int textColor = DateUtilities.isSaturdayorSunday(dateIndex) ? 0xFFAB9B78 : 0xFFABACCE;
		mDayOfWeekView.setTextColor(textColor);
	}

	public void drawFocus(boolean focus) {
		final Drawable background = focus ? mOverlayDrawable : mTransparentDrawable;
		setBackground(background);

		final int textColor = focus ? 0xFFFFFFFF : 0xFFABACCE;
		mDayOfMonthView.setTextColor(textColor);
	}
}
