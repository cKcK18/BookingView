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
	public int initilizeYear() {
		return mReferenceRecord.year;
	}

	@Override
	public int initilizeMonth() {
		return mReferenceRecord.month;
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
	protected void initializeServiceType(String[] availableServices, ArrayList<String> chooseServiceType) {
		final ArrayList<String> referenceServiceType = mReferenceRecord.serviceType;
		for (int i = 0; i < availableServices.length; ++i) {
			for (int j = 0; j < referenceServiceType.size(); ++j) {
				if (availableServices[i].equals(referenceServiceType.get(j))) {
					chooseServiceType.add(availableServices[i]);
					break;
				}
			}
		}
	}

	@Override
	public void writeBookingRecord(BookingRecord record) {
		mReferenceRecord.updateRecord(record);
		BookingRecordManager.getInstance().updateBookingRecord(mReferenceRecord);
	}

	@Override
	public void animateForm(boolean show) {
		getActivity().animateForm(show, mReferenceRecord);
	}

	@Override
	public String completedString() {
		return "edit completed";
	}

	private String getDateString(BookingRecord referenceRecord) {
		final Resources res = mFormView.getResources();

		final int year = referenceRecord.year;
		final int month = referenceRecord.month + 1;
		final int day = referenceRecord.day;
		final int hourOfDay = referenceRecord.hourOfDay;
		final int minute = referenceRecord.minute;
		final String yearString = res.getString(R.string.booking_form_year);
		final String monthString = res.getString(R.string.booking_form_month);
		final String dayString = res.getString(R.string.booking_form_day);

		return String.format("%04d%s%2d%s%2d%s %02d:%02d", year, yearString, month, monthString, day, dayString, hourOfDay, minute);
	}

	private void deleteRecord(BookingRecord deleteRecord) {
		BookingRecordManager.getInstance().deleteBookingRecord(deleteRecord);

		final boolean show = false;
		getActivity().animateForm(show);
	}
}