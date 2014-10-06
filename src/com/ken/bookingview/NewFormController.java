package com.ken.bookingview;

import java.util.Calendar;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public class NewFormController extends FormController {

	public NewFormController(ViewGroup formView) {
		super(formView);
	}

	@Override
	public int initilizeYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	public int initilizeMonth() {
		return Calendar.getInstance().get(Calendar.MONTH);
	}

	@Override
	public int initilizeDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int initilizeHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	@Override
	public int initilizeMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	@Override
	public void initializeServiceType(String[] serviceList, boolean[] checked, boolean[] unconfirmed) {
		// reset the last checked item
		for (int i = 0; i < serviceList.length; ++i) {
			boolean check = false;
			for (int j = 0; j < mServiceType.size(); ++j) {
				if (serviceList[i].equals(mServiceType.get(j))) {
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
		final String choiceString = res.getString(R.string.booking_form_choice);

		mTitleText.setText(res.getString(R.string.booking_form_add_title));

		mEditName.setText(null);
		mEditName.requestFocus();

		mSexSpinner.setSelection(0);

		mDateText.setText(choiceString);

		mEditPhoneNumber.setText(null);

		mServiceType.clear();
		mServiceTypeText.setText(choiceString);

		mDeleteButton.setVisibility(View.GONE);
		mDeleteButton.setOnClickListener(null);
	}

	@Override
	void writeBookingRecord(BookingRecord record) {
		BookingRecordManager.getInstance().addBookingRecord(record);
	}
}