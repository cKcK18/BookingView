package com.ken.bookingview;

import java.util.ArrayList;
import java.util.Calendar;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class FormController {

	protected ViewGroup mFormView;

	protected TextView mTitleText;
	protected EditText mEditName;
	protected Spinner mSexSpinner;
	protected TextView mDateText;
	protected EditText mEditPhoneNumber;
	protected TextView mServiceTypeText;
	protected TextView mRequiredTime;
	protected Button mDeleteButton;

	protected ArrayList<String> mServiceType;

	public FormController(ViewGroup formView) {
		mFormView = formView;
		mTitleText = (TextView) formView.findViewById(R.id.stylish_booking_form_title);
		mEditName = (EditText) formView.findViewById(R.id.stylish_booking_form_edit_name);
		mSexSpinner = (Spinner) formView.findViewById(R.id.stylish_booking_form_sex_spinner);
		mDateText = (TextView) formView.findViewById(R.id.stylish_booking_form_date);
		mEditPhoneNumber = (EditText) formView.findViewById(R.id.stylish_booking_form_edit_phone_number);
		mServiceTypeText = (TextView) formView.findViewById(R.id.stylish_booking_form_service_type);
		mRequiredTime = (TextView) formView.findViewById(R.id.stylish_booking_form_required_time);
		mDeleteButton = (Button) formView.findViewById(R.id.stylish_booking_form_delete);

		mServiceType = new ArrayList<String>();
	}

	public Calendar initilizeBookingDate() {
		Calendar calendar = DateUtilities.getInstance();
		calendar.set(initilizeYear(), initilizeMonth(), initilizeDay(), initilizeHour(), initilizeMinute());
		return calendar;
	}

	abstract int initilizeYear();

	abstract int initilizeMonth();

	abstract int initilizeDay();

	abstract int initilizeHour();

	abstract int initilizeMinute();

	abstract void initializeServiceType(String[] allServices, boolean[] checked, boolean[] unconfirmed);

	public void setServiceType(ArrayList<String> results) {
		if (results != null) {
			mServiceType.clear();
			mServiceType = results;
		} else {
			mServiceType = new ArrayList<String>();
		}
	}

	abstract void initailizeForm();

	abstract void writeBookingRecord(BookingRecord record);

	protected final StylishBookingActivity getActivity() {
		if (mFormView.getContext() instanceof StylishBookingActivity) {
			return (StylishBookingActivity) mFormView.getContext();
		}
		return null;
	}
}
