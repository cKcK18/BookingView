package com.ken.bookingview;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

public class TimeSheetViewPager extends ViewPager {
	
	public TimeSheetViewPager(Context context) {
		this(context, null);
	}

	public TimeSheetViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDuration(300);
	}

	private void setDuration(int duration) {
		try {
			Field fieldScroller;
			fieldScroller = ViewPager.class.getDeclaredField("mScroller");
			fieldScroller.setAccessible(true);
			TimeSheetFixedSpeedScroller scroller = new TimeSheetFixedSpeedScroller(getContext(),
					new DecelerateInterpolator(2.0f));
			scroller.setFixedDuration(duration);
			fieldScroller.set(this, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}
}