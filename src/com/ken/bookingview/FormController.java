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
	}

	public Calendar initilizeBookingDate() {
		Calendar calendar = DateUtilities.getInstance();
		calendar.set(initilizeYear(), initilizeMonth(), initilizeDay(), initilizeHour(), initilizeMinute());
		return calendar;
	}

	public ArrayList<String> initilizeServiceType() {
		final String[] availableServices = mFormView.getResources().getStringArray(R.array.service_arrays);
		final ArrayList<String> chooseServiceType = new ArrayList<String>();
		initializeServiceType(availableServices, chooseServiceType);
		return chooseServiceType;
	}

	abstract void initailizeForm();

	abstract int initilizeYear();

	abstract int initilizeMonth();

	abstract int initilizeDay();

	abstract int initilizeHour();

	abstract int initilizeMinute();

	protected abstract void initializeServiceType(String[] availableServices, ArrayList<String> chooseServiceType);

	abstract void writeBookingRecord(BookingRecord record);

	abstract void animateForm(boolean show);

	abstract String completedString();

	protected final StylishBookingActivity getActivity() {
		if (mFormView.getContext() instanceof StylishBookingActivity) {
			return (StylishBookingActivity) mFormView.getContext();
		}
		return null;
	}
}
