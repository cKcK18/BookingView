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
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class BookingDetailView extends LinearLayout {

	private static final String TAG = BookingDetailView.class.getSimpleName();

	private Calendar mBookingDate;
	private ArrayList<String> mServiceItems;

	private TextView mDateText;
	private TextView mServiceItemText;

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
		mServiceItems = new ArrayList<String>();
	}

	@Override
	protected void onFinishInflate() {
		final EditText nameText = (EditText) findViewById(R.id.booking_detail_edit_name);
		nameText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// not work
				if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					InputMethodManager in = (InputMethodManager) getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});

		final Spinner sexSpinner = (Spinner) findViewById(R.id.booking_detail_sex_spinner);
		final String[] sexList = getResources().getStringArray(R.array.sex_array);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_spinner_item, sexList);
		dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
		sexSpinner.setAdapter(dataAdapter);

		mDateText = (TextView) findViewById(R.id.booking_detail_date);
		mDateText.setText("Choose");
		mDateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupDatePickerDialog();
			}
		});

		final ImageButton dateButton = (ImageButton) findViewById(R.id.booking_detail_date_arrow);
		dateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupDatePickerDialog();
			}
		});

		final EditText editPhoneNumber = (EditText) findViewById(R.id.booking_detail_edit_phone_number);

		mServiceItemText = (TextView) findViewById(R.id.booking_detail_service_item);
		mServiceItemText.setText("Choose");
		mServiceItemText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupServicePickerDialog();
			}
		});

		final ImageButton serviceItemButton = (ImageButton) findViewById(R.id.booking_detail_service_item_arrow);
		serviceItemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupServicePickerDialog();
			}
		});

		final TextView requiredTime = (TextView) findViewById(R.id.booking_detail_required_time);
		final String string = getResources().getString(R.string.booking_detail_required_time);
		requiredTime.setText(string + ": 1.5hrs");

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
				final String sex = sexSpinner.getSelectedItem().toString();
				final boolean invalidSex = sex == null || sex.equals("");
				if (invalidSex) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide sex", Toast.LENGTH_SHORT).show();
					return;
				}
				final boolean invalidDate = mBookingDate == null;
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
				final boolean invalidServiceItem = mServiceItems.size() == 0;
				if (invalidServiceItem) {
					Toast.makeText(BookingDetailView.this.getContext(), "invalide service item", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// save the booking and notify data and UI
				final BookingActivity activity = getActivity();
				if (activity != null) {
					final BookingApplication app = (BookingApplication) activity.getApplicationContext();
					final long id = app.getBookingProvider().generateNewId();
					final int year = mBookingDate.get(Calendar.YEAR);
					final int month = mBookingDate.get(Calendar.MONTH);
					final int day = mBookingDate.get(Calendar.DATE);
					final int hour = mBookingDate.get(Calendar.HOUR_OF_DAY);
					final int minute = mBookingDate.get(Calendar.MINUTE);
					// FIXME
					final BookingData date = new BookingData(id, name, sex, year, month, day, hour, minute,
							phoneNumber, mServiceItems, "");
					// notify database
					BookingDataManager.getInstance().writeBookingData(date);

					// notify UI
					final boolean show = false;
					final boolean animate = true;
					show(show, animate);
					Toast.makeText(BookingDetailView.this.getContext(), "add completed", Toast.LENGTH_SHORT).show();

					// clear all
					postDelayed(new Runnable() {
						@Override
						public void run() {
							nameText.setText(null);
							nameText.requestFocus();
							sexSpinner.setSelection(0);
							mDateText.setText("Choose");
							editPhoneNumber.setText(null);
							mServiceItems.clear();
							mServiceItemText.setText("Choose");
						}
					}, 500);
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

	private BookingActivity getActivity() {
		if (getContext() instanceof BookingActivity) {
			return (BookingActivity) getContext();
		}
		return null;
	}

	private void popupDatePickerDialog() {
		final BookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "datePicker");
		}
	}

	private void popupTimePickerDialog(Calendar calendar) {
		final BookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new TimePickerFragment(calendar);
			newFragment.show(activity.getSupportFragmentManager(), "timePicker");
		}
	}

	private void popupServicePickerDialog() {
		final BookingActivity activity = getActivity();
		if (activity != null) {
			DialogFragment newFragment = new ServicePickerFragment();
			newFragment.show(activity.getSupportFragmentManager(), "servicePicker");
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

							// back to activity
							final BookingActivity activity = getActivity();
							if (activity != null) {
								activity.backToBooking();
							}
						}
					}).start();
		}
	}

	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		Calendar calendar = DateUtilities.getInstance();
		boolean positive = false;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar date = Calendar.getInstance();
			int year = date.get(Calendar.YEAR);
			int month = date.get(Calendar.MONTH);
			int day = date.get(Calendar.DAY_OF_MONTH);

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
			// Use the current time as the default values for the picker
			final Calendar date = Calendar.getInstance();
			int hour = date.get(Calendar.HOUR_OF_DAY);
			int minute = date.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			TimePickerDialog timePicker = new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
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
				final int month = calendar.get(Calendar.MONTH) + 1;
				final int day = calendar.get(Calendar.DATE);
				final String yearString = getResources().getString(R.string.booking_detail_year);
				final String monthString = getResources().getString(R.string.booking_detail_month);
				final String dayString = getResources().getString(R.string.booking_detail_day);

				mBookingDate = Calendar.getInstance();
				mBookingDate.set(year, month, day, hourOfDay, minute);
				String string = String.format("%04d%s%2d%s%2d%s %02d:%02d", year, yearString, month, monthString, day,
						dayString, hourOfDay, minute);
				mDateText.setText(string);
				positive = false;

				Log.d(TAG, string);
			}
		}
	}

	public class ServicePickerFragment extends DialogFragment {

		private String[] serviceList;
		private boolean[] checkedItems;
		private boolean[] unconfirmedCheckedItems;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			serviceList = getResources().getStringArray(R.array.service_arrays);
			checkedItems = new boolean[serviceList.length];
			unconfirmedCheckedItems = new boolean[serviceList.length];

			// reset the last checked item
			for (int i = 0; i < serviceList.length; ++i) {
				boolean checked = false;
				for (int j = 0; j < mServiceItems.size(); ++j) {
					if (serviceList[i].equals(mServiceItems.get(j))) {
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
			builder.setTitle(R.string.booking_detail_service_item);
			builder.setMultiChoiceItems(serviceList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int item, boolean isChecked) {
					unconfirmedCheckedItems[item] = isChecked;
				}
			});
			builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final StringBuilder sb = new StringBuilder("");
					mServiceItems.clear();

					// save selected item according to the boolean array
					for (int i = 0; i < unconfirmedCheckedItems.length; ++i) {
						checkedItems[i] = unconfirmedCheckedItems[i];
						if (!unconfirmedCheckedItems[i]) {
							continue;
						}
						final String item = serviceList[i];
						// add the separated string ", " if it is first item added into string builder
						if (!"".equals(sb.toString())) {
							sb.append(BookingDataManager.SEPARATED_STRING);
						}
						sb.append(item);
						mServiceItems.add(item);
					}
					mServiceItemText.setText(sb.toString());
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
}
