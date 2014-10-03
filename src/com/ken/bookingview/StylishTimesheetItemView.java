package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StylishTimesheetItemView extends LinearLayout {

	@SuppressWarnings("unused")
	private static final String TAG = StylishTimesheetItemView.class.getSimpleName();

	private static final int MAX_SERVICE_ITEM = 3;

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
		if (object == null) {
			return;
		}
		final BookingRecord record = (BookingRecord) object;

		// add service type
		final ArrayList<String> serviceType = record.serviceType;
		final int size = serviceType.size();
		if (size > 0) {
			for (String type : serviceType) {
				TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_stylish_record_item_service_type, mServiceType,
						false);
				tv.setText(type);
			}
		}

		// show record information
		final String information = String.format("%02d:%02d  %s  %s", record.hourOfDay, record.minute, record.name, record.phoneNumber);
		mRecordInfo.setText(information);
	}
}
