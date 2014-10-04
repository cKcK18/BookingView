package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class EditableBookingDetailView extends BookingDetailView {

	private static final String TAG = EditableBookingDetailView.class.getSimpleName();

	private BookingRecord mReferenceRecord;

	public EditableBookingDetailView(Context context) {
		this(context, null);
	}

	public EditableBookingDetailView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EditableBookingDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int initilizeYear() {
		return mReferenceRecord.year;
	}

	@Override
	protected int initilizeMonth() {
		return mReferenceRecord.month;
	}

	@Override
	protected int initilizeDay() {
		return mReferenceRecord.day;
	}

	@Override
	protected int initilizeHour() {
		return mReferenceRecord.hourOfDay;
	}

	@Override
	protected int initilizeMinute() {
		return mReferenceRecord.minute;
	}

	@Override
	protected void initializeServiceType(String[] serviceList, boolean[] checked, boolean[] unconfirmed) {
		final ArrayList<String> serviceType = mReferenceRecord.serviceType;
		for (int i = 0; i < serviceList.length; ++i) {
			boolean check = false;
			for (int j = 0; j < serviceType.size(); ++j) {
				if (serviceList[i].equals(serviceType.get(j))) {
					check = true;
					break;
				}
			}
			checked[i] = unconfirmed[i] = check;
		}
	}

	@Override
	protected void initailizeForm() {
		mTitleText.setText(getResources().getString(R.string.booking_detail_update_title));

		mEditName.setText(mReferenceRecord.name);

		final String dateString = getDateString(mReferenceRecord);
		mDateText.setText(dateString);

		mEditPhoneNumber.setText(mReferenceRecord.phoneNumber);

		final String serviceTypeString = BookingRecord.flattenServiceType(mReferenceRecord.serviceType);
		mServiceTypeText.setText(serviceTypeString);

		final String string = getResources().getString(R.string.booking_detail_required_time);
		final float time = mReferenceRecord.requiredHour + (float) mReferenceRecord.requiredMinute / DateUtilities.A_HOUR_IN_MINUTE;
		final String hourString = getResources().getString(R.string.booking_detail_required_hour);
		final String requiredTimeString = String.format("%s: %.1f %s", string, time, hourString);
		mRequiredTime.setText(requiredTimeString);

		mDeleteButton.setVisibility(VISIBLE);
		mDeleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteRecord(mReferenceRecord);
			}
		});
	}

	@Override
	public void show(final boolean show, final BookingRecord referenceRecord) {
		mReferenceRecord = referenceRecord;
		super.show(show, referenceRecord);
	}

	private void deleteRecord(BookingRecord deleteRecord) {
		final StylishBookingActivity activity = getActivity();
		final boolean show = false;
		activity.showDetailView(show);

		BookingRecordManager.getInstance().deleteBookingRecord(deleteRecord);
	}

	private String getDateString(BookingRecord referenceRecord) {
		final int year = referenceRecord.year;
		final int month = referenceRecord.month;
		final int day = referenceRecord.day;
		final int hourOfDay = referenceRecord.hourOfDay;
		final int minute = referenceRecord.minute;
		final String yearString = getResources().getString(R.string.booking_detail_year);
		final String monthString = getResources().getString(R.string.booking_detail_month);
		final String dayString = getResources().getString(R.string.booking_detail_day);

		return String.format("%04d%s%2d%s%2d%s %02d:%02d", year, yearString, month, monthString, day, dayString, hourOfDay, minute);
	}
}
