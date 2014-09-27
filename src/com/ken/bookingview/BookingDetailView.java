package com.ken.bookingview;

import java.util.Calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BookingDetailView extends LinearLayout {

	private Calendar mBookingDate;

	private boolean mDatePickerShowing = false;

	public BookingDetailView(Context context) {
		this(context, null);
	}

	public BookingDetailView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookingDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUpView(context);
	}

	protected void setUpView(Context context) {
	}

	@Override
	protected void onFinishInflate() {
		final EditText nameText = (EditText) findViewById(R.id.booking_detail_edit_name);

		final Spinner sexSpinner = (Spinner) findViewById(R.id.booking_detail_sex_spinner);
		final String[] sexList = getResources().getStringArray(R.array.sex_array);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_spinner_item, sexList);
		dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
		sexSpinner.setAdapter(dataAdapter);

		final TextView dateText = (TextView) findViewById(R.id.booking_detail_date);
		dateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		final ImageButton dateButton = (ImageButton) findViewById(R.id.booking_detail_date_arrow);
		dateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		final EditText editPhoneNumber = (EditText) findViewById(R.id.booking_detail_edit_phone_number);

		final TextView serviceItemText = (TextView) findViewById(R.id.booking_detail_service_item);

		final ImageButton serviceItemButton = (ImageButton) findViewById(R.id.booking_detail_service_item_arrow);

		final TextView requiredTime = (TextView) findViewById(R.id.booking_detail_required_time);

		final Button confirmButton = (Button) findViewById(R.id.booking_detail_confirm);
		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = nameText.getText().toString();
				final boolean invalidName = name == null || name.equals("");
				if (invalidName) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide name", Toast.LENGTH_SHORT).show();
					return;
				}
				final boolean invalidSex = true;
				if (invalidSex) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide sex", Toast.LENGTH_SHORT).show();
					return;
				}
				final boolean invalidDate = true;
				if (invalidDate) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide date", Toast.LENGTH_SHORT).show();
					return;
				}
				final String phoneNumber = editPhoneNumber.getText().toString();
				final boolean invalidPhoneNumber = phoneNumber == null || phoneNumber.equals("");
				if (invalidPhoneNumber) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide phone number", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				final boolean invalidServiceItem = true;
				if (invalidServiceItem) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide service item", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.booking_detail_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final boolean show = false;
				final boolean animate = true;
				show(show, animate);
			}
		});
	}

	public void showDatePickerDialog() {
		if (getContext() instanceof BookingActivity) {
			final BookingActivity activity = (BookingActivity) getContext();
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "datePicker");
		}
	}

	public void showTimePickerDialog() {
		if (getContext() instanceof BookingActivity) {
			final BookingActivity activity = (BookingActivity) getContext();
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "timePicker");
		}
	}

	public void show(boolean show, boolean animate) {
		if (show) {
			final int duration = animate ? 300 : 0;
			animate().translationY(0).setDuration(duration).setInterpolator(new DecelerateInterpolator())
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							setTranslationY(0);
						}
					}).start();
		} else {
			final int duration = animate ? 300 : 0;
			final int tranY = getHeight();
			animate().translationY(tranY).setDuration(duration).setInterpolator(new DecelerateInterpolator())
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							setTranslationY(tranY);
						}
					}).start();
		}
	}

	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar date = Calendar.getInstance();
			int year = date.get(Calendar.YEAR);
			int month = date.get(Calendar.MONTH);
			int day = date.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
			datePicker.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					Log.d("kenchen", "onCancel");
				}
			});
			datePicker.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					Log.d("kenchen", "onDismiss");
				}
			});
//			datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					if (which == DialogInterface.BUTTON_NEGATIVE) {
//						dialog.dismiss();
//						Log.d("kenchen", "" + which + " ---- " + " negatif");
//					}
//				}
//			});
//			datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Set time", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Log.d("kenchen", "" + which + " ---- " + " Pozitif");
//				}
//			});
			return datePicker;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			Log.d("kenchen", "[onDateSet]");
			if (mDatePickerShowing) {
				return;
			}
			mDatePickerShowing = true;
			// showTimePickerDialog();
		}
	}

	private class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar date = Calendar.getInstance();
			int hour = date.get(Calendar.HOUR_OF_DAY);
			int minute = date.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mDatePickerShowing = false;
		}
	}
}
