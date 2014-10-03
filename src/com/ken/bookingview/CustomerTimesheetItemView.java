package com.ken.bookingview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CustomerTimesheetItemView extends FrameLayout {

	@SuppressWarnings("unused")
	private static final String TAG = CustomerTimesheetItemView.class.getSimpleName();

	private TextView mHourView;
	private TextView mStatusView;

	private Drawable mDrawableNew;
	private String mStringNew;
	private Drawable mDrawableFull;
	private String mStringFull;
	private Drawable mDrawableAvailable;
	private String mStringAvailable;

	private BookingRecord mRecord;

	public CustomerTimesheetItemView(Context context) {
		this(context, null);
	}

	public CustomerTimesheetItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomerTimesheetItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setUpView(context);
	}

	protected void setUpView(Context context) {
		mDrawableNew = context.getResources().getDrawable(R.drawable.time_ok);
		mStringNew = context.getResources().getString(R.string.customer_booking_view_ok);
		mDrawableFull = context.getResources().getDrawable(R.drawable.time_full);
		mStringFull = context.getResources().getString(R.string.customer_booking_view_full);
		mDrawableAvailable = context.getResources().getDrawable(R.drawable.time_select);
		mStringAvailable = context.getResources().getString(R.string.customer_booking_view_available);
	}

	@Override
	protected void onFinishInflate() {
		mHourView = (TextView) findViewById(R.id.customer_record_item_hour_of_day);
		mStatusView = (TextView) findViewById(R.id.customer_booking_item_status);
	}

	@Override
	public void setTag(int key, Object tag) {
		super.setTag(key, tag);

		if (key == R.id.booking_item_count) {
			setHourTime();
		} else if (key == R.id.booking_item_identity) {
			setHourTime();
		} else if (key == R.id.booking_item_info) {
			proceedStatusView();
		}
	}

	private String getTimeString(int position, int count) {
		final int unitMinutes = DateUtilities.A_DAY_IN_MINUTE / count;
		final int time = position * unitMinutes;
		final int hour = time / DateUtilities.A_HOUR_IN_MINUTE;
		final int minute = time % DateUtilities.A_HOUR_IN_MINUTE;
		return String.format("%2d:%02d", hour, minute);
	}

	private void setHourTime() {
		if (mHourView != null) {
			final Object object1 = getTag(R.id.booking_item_identity);
			final Object object2 = getTag(R.id.booking_item_count);
			if (object1 == null || object2 == null) {
				return;
			}
			mHourView.setText(getTimeString((Integer) object1, (Integer) object2));
		}
	}

	private void proceedStatusView() {
		if (mStatusView == null) {
			return;
		}
		final Object object = getTag(R.id.booking_item_info);
		// check that any record in the time
		final boolean hasRecord = object != null;
		final BookingRecord record = hasRecord ? (BookingRecord) object : null;
		final boolean newRecord = mRecord == null && record != null;
		mRecord = record;

		final Drawable status;
		if (newRecord) {
			status = mDrawableNew;
		} else if (hasRecord) {
			status = mDrawableFull;
		} else {
			status = mDrawableAvailable;
		}
		mStatusView.setBackground(status);

		final String string;
		if (newRecord) {
			string = String.format("%s: %s", mStringNew, mRecord.serviceType.toArray().toString());
		} else if (hasRecord) {
			string = mStringFull;
		} else {
			string = mStringAvailable;
		}
		mStatusView.setText(string);
	}
}
