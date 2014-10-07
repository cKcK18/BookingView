package com.ken.bookingview;

import java.util.ArrayList;
import java.util.Calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class BookingFormView extends LinearLayout {

	private static final String TAG = BookingFormView.class.getSimpleName();

	protected TextView mTitleText;
	protected EditText mEditName;
	protected Spinner mSexSpinner;
	protected TextView mDateText;
	protected EditText mEditPhoneNumber;
	protected TextView mServiceTypeText;
	protected TextView mRequiredTime;
	protected Button mDeleteButton;

	protected FormController mFormController;

	private Calendar mBookingDate;
	private ArrayList<String> mServiceType;

	private boolean mDatePickerShowing = false;

	public BookingFormView(Context context) {
		this(context, null);
	}

	public BookingFormView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookingFormView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUpView(context);
	}

	protected void setUpView(Context context) {
		mServiceType = new ArrayList<String>();
	}

	public void setController(FormController controller) {
		mFormController = controller;
		mBookingDate = mFormController.initilizeBookingDate();
		mServiceType.clear();
		mServiceType = mFormController.initilizeServiceType();
	}

	@Override
	protected void onFinishInflate() {
		mTitleText = (TextView) findViewById(R.id.stylish_booking_form_title);

		mEditName = (EditText) findViewById(R.id.stylish_booking_form_edit_name);

		final String[] sexList = getResources().getStringArray(R.array.sex_array);
		mSexSpinner = (Spinner) findViewById(R.id.stylish_booking_form_sex_spinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_spinner_item, sexList);
		dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
		mSexSpinner.setAdapter(dataAdapter);

		final String choiceString = getResources().getString(R.string.booking_form_choice);
		mDateText = (TextView) findViewById(R.id.stylish_booking_form_date);
		mDateText.setText(choiceString);
		mDateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupDatePickerDialog();
			}
		});

		final ImageButton dateButton = (ImageButton) findViewById(R.id.stylish_booking_form_date_arrow);
		dateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupDatePickerDialog();
			}
		});

		mEditPhoneNumber = (EditText) findViewById(R.id.stylish_booking_form_edit_phone_number);

		mServiceTypeText = (TextView) findViewById(R.id.stylish_booking_form_service_type);
		mServiceTypeText.setText(choiceString);
		mServiceTypeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupServicePickerDialog();
			}
		});

		final ImageButton serviceTypeButton = (ImageButton) findViewById(R.id.stylish_booking_form_service_type_arrow);
		serviceTypeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupServicePickerDialog();
			}
		});

		final String string = getResources().getString(R.string.booking_form_required_time);
		final String hour = getResources().getString(R.string.booking_form_required_hour);
		mRequiredTime = (TextView) findViewById(R.id.stylish_booking_form_required_time);
		// FIXME
		mRequiredTime.setText(string + ": 1.5 " + hour);

		final Button confirmButton = (Button) findViewById(R.id.stylish_booking_form_confirm);
		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = mEditName.getText().toString();
				final boolean invalidName = name == null || name.equals("");
				if (invalidName) {
					Toast.makeText(BookingFormView.this.getContext(), "invalide name", Toast.LENGTH_SHORT).show();
					return;
				}
				final String sex = mSexSpinner.getSelectedItem().toString();
				final boolean invalidSex = sex == null || sex.equals("");
				if (invalidSex) {
					Toast.makeText(BookingFormView.this.getContext(), "invalide sex", Toast.LENGTH_SHORT).show();
					return;
				}
				final boolean invalidDate = mBookingDate == null;
				if (invalidDate) {
					Toast.makeText(BookingFormView.this.getContext(), "invalide date", Toast.LENGTH_SHORT).show();
					return;
				}
				final String phoneNumber = mEditPhoneNumber.getText().toString();
				final boolean invalidPhoneNumber = phoneNumber == null || phoneNumber.equals("");
				if (invalidPhoneNumber) {
					Toast.makeText(BookingFormView.this.getContext(), "invalide phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				final boolean invalidServiceType = mServiceType.size() == 0;
				if (invalidServiceType) {
					Toast.makeText(BookingFormView.this.getContext(), "invalide service type", Toast.LENGTH_SHORT).show();
					return;
				}

				// save the booking and notify data and UI
				final StylishBookingActivity activity = getActivity();
				if (activity != null) {
					final int year = mBookingDate.get(Calendar.YEAR);
					final int month = mBookingDate.get(Calendar.MONTH);
					final int day = mBookingDate.get(Calendar.DATE);
					final int hour = mBookingDate.get(Calendar.HOUR_OF_DAY);
					final int minute = mBookingDate.get(Calendar.MINUTE);
					final ArrayList<String> serviceType = new ArrayList<String>(mServiceType);
					// FIXME
					final BookingRecord record = new BookingRecord(name, sex, year, month, day, hour, minute, phoneNumber, serviceType, 1,
							30);
					// notify database
					mFormController.writeBookingRecord(record);

					// notify UI
					postDelayed(new Runnable() {
						@Override
						public void run() {
							Log.d("kenchen", "[onClick] controller: " + mFormController);
							Toast.makeText(BookingFormView.this.getContext(), mFormController.completedString(), Toast.LENGTH_SHORT).show();
							final boolean show = false;
							mFormController.animateForm(show);
						}
					}, 500);
				}
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.stylish_booking_form_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final boolean show = false;
				final StylishBookingActivity activity = getActivity();
				if (activity != null) {
					activity.animateForm(show);
				}
			}
		});

		mDeleteButton = (Button) findViewById(R.id.stylish_booking_form_delete);
		mDeleteButton.setOnClickListener(null);
	}

	private StylishBookingActivity getActivity() {
		if (getContext() instanceof StylishBookingActivity) {
			return (StylishBookingActivity) getContext();
		}
		return null;
	}

	private void popupDatePickerDialog() {
		final StylishBookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "datePicker");
		}
	}

	private void popupTimePickerDialog(Calendar calendar) {
		final StylishBookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new TimePickerFragment(calendar);
			newFragment.show(activity.getSupportFragmentManager(), "timePicker");
		}
	}

	private void popupServicePickerDialog() {
		final StylishBookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new ServicePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "servicePicker");
		}
	}

	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		Calendar calendar = DateUtilities.getInstance();
		boolean positive = false;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the updated record or the current date as the default date in the picker
			final int year = mFormController.initilizeYear();
			final int month = mFormController.initilizeMonth();
			final int day = mFormController.initilizeDay();

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
			datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_NEGATIVE) {
						dialog.dismiss();
					}
				}
			});
			datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Set time", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positive = true;
				}
			});
			return datePicker;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (mDatePickerShowing) {
				mDatePickerShowing = false;
				return;
			}
			mDatePickerShowing = true;
			if (positive) {
				calendar.set(year, month, day);
				popupTimePickerDialog(calendar);

				positive = false;
			}
		}
	}

	private class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		Calendar calendar;
		boolean positive = false;

		TimePickerFragment(Calendar calendar) {
			this.calendar = calendar;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the updated record or the current time as the default values for the picker
			final int hour = mFormController.initilizeHour();
			final int minute = mFormController.initilizeMinute();

			// Create a new instance of TimePickerDialog and return it
			TimePickerDialog timePicker = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
			timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_NEGATIVE) {
						dialog.dismiss();
					}
				}
			});
			timePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					positive = true;
				}
			});
			return timePicker;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mDatePickerShowing = false;
			if (positive) {
				// keep the calendar to outer class
				final int year = calendar.get(Calendar.YEAR);
				final int month = calendar.get(Calendar.MONTH);
				final int day = calendar.get(Calendar.DATE);

				mBookingDate.set(year, month, day, hourOfDay, minute, 0);

				final String string = getDateString(mBookingDate);
				mDateText.setText(string);
				positive = false;

				Log.d(TAG, string);
			}
		}
	}

	private class ServicePickerFragment extends DialogFragment {

		private String[] availableService;
		private boolean[] checkedItems;
		private boolean[] unconfirmedCheckedItems;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			availableService = getResources().getStringArray(R.array.service_arrays);
			checkedItems = new boolean[availableService.length];
			unconfirmedCheckedItems = new boolean[availableService.length];

			// set the last checked item
			for (int i = 0; i < availableService.length; ++i) {
				boolean checked = false;
				for (int j = 0; j < mServiceType.size(); ++j) {
					if (availableService[i].equals(mServiceType.get(j))) {
						checked = true;
						break;
					}
				}
				checkedItems[i] = unconfirmedCheckedItems[i] = checked;
			}
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.booking_form_service_item);
			builder.setMultiChoiceItems(availableService, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int item, boolean isChecked) {
					unconfirmedCheckedItems[item] = isChecked;
				}
			});
			builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final StringBuilder sb = new StringBuilder("");
					mServiceType.clear();

					// save selected item according to the boolean array
					for (int i = 0; i < unconfirmedCheckedItems.length; ++i) {
						checkedItems[i] = unconfirmedCheckedItems[i];
						if (!unconfirmedCheckedItems[i]) {
							continue;
						}
						final String item = availableService[i];
						// add the separated string ", " if it is first item added into string builder
						if (!"".equals(sb.toString())) {
							sb.append(BookingRecord.SEPARATED_STRING);
						}
						sb.append(item);
						mServiceType.add(item);
					}
					mServiceTypeText.setText(sb.toString());
				}
			});
			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// reset the unconfirmed checked item
					for (int i = 0; i < checkedItems.length; ++i) {
						unconfirmedCheckedItems[i] = checkedItems[i];
					}
				}
			});
			return builder.create();
		}
	}

	public void show(final boolean show) {
		final float tranY = show ? 0 : getHeight();

		// switch title and setup clear button when add or update
		mFormController.initailizeForm();

		animate().translationY(tranY).setDuration(300).setInterpolator(new DecelerateInterpolator())
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						if (show) {
							setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						if (!show) {
							setVisibility(View.GONE);
						}
						setTranslationY(tranY);
					}
				}).start();
	}

	private String getDateString(Calendar calendar) {
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int day = calendar.get(Calendar.DATE);
		final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final String yearString = getResources().getString(R.string.booking_form_year);
		final String monthString = getResources().getString(R.string.booking_form_month);
		final String dayString = getResources().getString(R.string.booking_form_day);

		return String.format("%04d%s%2d%s%2d%s %02d:%02d", year, yearString, month, monthString, day, dayString, hourOfDay, minute);
	}
}
