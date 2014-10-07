package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StylishTimesheetItemView extends LinearLayout {

	@SuppressWarnings("unused")
	private static final String TAG = StylishTimesheetItemView.class.getSimpleName();

	private TextView mHourView;
	private ViewGroup mServiceType;
	private TextView mRecordInfo;

	public StylishTimesheetItemView(Context context) {
		this(context, null);
	}

	public StylishTimesheetItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StylishTimesheetItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setUpView(context);
	}

	protected void setUpView(Context context) {
	}

	private StylishBookingActivity getActivity() {
		if (getContext() instanceof StylishBookingActivity) {
			return (StylishBookingActivity) getContext();
		}
		return null;
	}

	@Override
	protected void onFinishInflate() {
		mHourView = (TextView) findViewById(R.id.stylish_record_item_hour_of_day);
		mServiceType = (ViewGroup) findViewById(R.id.stylish_record_item_service);
		mRecordInfo = (TextView) findViewById(R.id.stylish_record_item_info);
	}

	@Override
	public void setTag(int key, Object tag) {
		super.setTag(key, tag);

		if (key == R.id.booking_item_count) {
			setHourTime();
		} else if (key == R.id.booking_item_identity) {
			setHourTime();
		} else if (key == R.id.booking_item_info) {
			proceedRecord();
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

	private void proceedRecord() {
		final Object object = getTag(R.id.booking_item_info);
		// check that any record in the time
		mServiceType.removeAllViews();
		if (object == null) {
			mRecordInfo.setText(null);
			setOnClickListener(null);
			return;
		}
		final BookingRecord record = (BookingRecord) object;

		// add service type
		final ArrayList<String> serviceType = record.serviceType;
		for (String type : serviceType) {
			if (mServiceType.getChildCount() == 3) {
				break;
			}
			final TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_stylish_record_item_service_type,
					null, false);
			tv.setText(type);

			final LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			llp.rightMargin = getResources().getDimensionPixelSize(R.dimen.record_item_gap_between_service_type);

			mServiceType.addView(tv, llp);
		}

		// add required time
		final ViewGroup vg = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_stylish_record_item_service_time, null,
				false);
		final TextView hour = (TextView) vg.findViewById(R.id.stylish_record_item_service_hour);
		final String requiredTime;
		if (record.requiredMinute == 0) {
			requiredTime = String.format("%d", record.requiredHour);
		} else {
			final float time = record.requiredHour + (float) record.requiredMinute / DateUtilities.A_HOUR_IN_MINUTE;
			requiredTime = String.format("%.1f", time);
		}
		hour.setText(requiredTime);

		final LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mServiceType.addView(vg, llp);

		// show record information
		final String information = String.format("%02d:%02d  %s  %s", record.hourOfDay, record.minute, record.name, record.phoneNumber);
		mRecordInfo.setText(information);

		// set click listener to clear record
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final StylishBookingActivity activity = getActivity();
				final boolean show = true;
				activity.animateForm(show, record);
			}
		});
	}
}
