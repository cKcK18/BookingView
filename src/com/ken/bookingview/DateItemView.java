package com.ken.bookingview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class DateItemView extends FrameLayout {

	private static final Drawable mOverlayDrawable = new ColorDrawable(0x64444444);
	private static final Drawable mTransparentDrawable = new ColorDrawable(Color.TRANSPARENT);

	public DateItemView(Context context) {
		this(context, null);
	}

	public DateItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DateItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void drawFocus(boolean focus) {
		Drawable drawable = focus ? mOverlayDrawable : mTransparentDrawable;
		setForeground(drawable);
	}
}
