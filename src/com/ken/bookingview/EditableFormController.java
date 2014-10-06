package com.ken.bookingview;

import java.util.ArrayList;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditableFormController extends FormController {

	private BookingRecord mReferenceRecord;

	public EditableFormController(ViewGroup formView) {
		super(formView);
	}

	public void setReferenceRecord(BookingRecord referenceRecord) {
		mReferenceRecord = referenceRecord;
	}

	@Override
	public int initilizeYear() {
		return mReferenceRecord.year;
	}

	@Override
	public int initilizeMonth() {
		return mReferenceRecord.month - 1;
	}

	@Override
	public int initilizeDay() {
		return mReferenceRecord.day;
	}

	@Override
	public int initilizeHour() {
		return mReferenceRecord.hourOfDay;
	}

	@Override
	public int initilizeMinute() {
		return mReferenceRecord.minute;
	}

	@Override
	public void initializeServiceType(String[] serviceList, boolean[] checked, boolean[] unconfirmed) {
		final ArrayList<String> serviceType = mReferenceRecord.serviceType;
		for (int i = 0; i < serviceList.length; ++i) {
			boolean check = false;
			for (int j = 0; j < serviceType.size(); ++j) {
				if (serviceList[i].equals(serviceType.get(j))) {
					mServiceType.add(serviceList[i]);
					check = true;
					break;
				}
			}
			checked[i] = unconfirmed[i] = check;
		}
	}

	@Override
	public void initailizeForm() {
		final Resources res = mFormView.getResources();

		final TextView titleText = (TextView) mFormView.findViewById(R.id.stylish_booking_form_title);
		titleText.setText(res.getString(R.string.booking_form_update_title));

		final EditText editName = (EditText) mFormView.findViewById(R.id.stylish_booking_form_edit_name);
		editName.setText(mReferenceRecord.name);

		final String dateString = getDateString(mReferenceRecord);
		final TextView dateText = (TextView) mFormView.findViewById(R.id.stylish_booking_form_date);
		dateText.setText(dateString);

		final EditText editPhoneNumber = (EditText) mFormView.findViewById(R.id.stylish_booking_form_edit_phone_number);
		editPhoneNumber.setText(mReferenceRecord.phoneNumber);

		final String serviceTypeString = BookingRecord.flattenServiceType(mReferenceRecord.serviceType);
		final TextView serviceTypeText = (TextView) mFormView.findViewById(R.id.stylish_booking_form_service_type);
		serviceTypeText.setText(serviceTypeString);

		final String string = res.getString(R.string.booking_form_required_time);
		final float time = mReferenceRecord.requiredHour + (float) mReferenceRecord.requiredMinute / DateUtilities.A_HOUR_IN_MINUTE;
		final String hourString = res.getString(R.string.booking_form_required_hour);
		final String requiredTimeString = String.format("%s: %.1f %s", string, time, hourString);
		final TextView requiredTime = (TextView) mFormView.findViewById(R.id.stylish_booking_form_required_time);
		requiredTime.setText(requiredTimeString);

		final Button deleteButton = (Button) mFormView.findViewById(R.id.stylish_booking_form_delete);
		deleteButton.setVisibility(View.VISIBLE);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteRecord(mReferenceRecord);
			}
		});
	}

	@Override
	void writeBookingRecord(BookingRecord record) {
		BookingRecordManager.getInstance().updateBookingRecord(record);
	}

	private String getDateString(BookingRecord referenceRecord) {
		final Resources res = mFormView.getResources();

		final int year = referenceRecord.year;
		final int month = referenceRecord.month;
		final int day = referenceRecord.day;
		final int hourOfDay = referenceRecord.hourOfDay;
		final int minute = referenceRecord.minute;
		final String yearString = res.getString(R.string.booking_form_year);
		final String monthString = res.getString(R.string.booking_form_month);
		final String dayString = res.getString(R.string.booking_form_day);

		return String.format("%04d%s%2d%s%2d%s %02d:%02d", year, yearString, month, monthString, day, dayString, hourOfDay, minute);
	}

	private void deleteRecord(BookingRecord deleteRecord) {
		final StylishBookingActivity activity = getActivity();
		final boolean show = false;
		activity.showFormView(show);

		BookingRecordManager.getInstance().deleteBookingRecord(deleteRecord);
	}
}