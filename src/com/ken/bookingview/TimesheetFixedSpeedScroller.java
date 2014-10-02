package com.ken.bookingview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class TimesheetFixedSpeedScroller extends Scroller {

	private int mDuration;

	public TimesheetFixedSpeedScroller(Context context) {
		this(context, null);
	}

	public TimesheetFixedSpeedScroller(Context context, Interpolator interpolator) {
		this(context, interpolator, false);
	}

	public TimesheetFixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	public void setFixedDuration(int duration) {
		mDuration = duration;
	}
}