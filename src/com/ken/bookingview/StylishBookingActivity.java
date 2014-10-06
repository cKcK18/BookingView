package com.ken.bookingview;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class StylishBookingActivity extends BookingActivity {

	@SuppressWarnings("unused")
	private static final String TAG = StylishBookingActivity.class.getSimpleName();

	private ImageButton mAddRecordButton;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_stylish_booking;
	}

	@Override
	protected Class<? extends TimesheetAdapter> getTimesheetAdapterClass() {
		return StylishTimesheetAdapter.class;
	}

	@Override
	protected FormController initializeFormController() {
		return null;
	}

	@Override
	protected void setUpView() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.stylish_booking_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// finish();
				Intent intent = new Intent(StylishBookingActivity.this, CustomerBookingActivity.class);
				startActivity(intent);
			}
		});

		final Button todayButton = (Button) findViewById(R.id.stylish_booking_today_button);
		todayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_TODAY);
			}
		});

		// setup the button for entering the detail view
		mAddRecordButton = (ImageButton) findViewById(R.id.stylish_booking_add_button);
		mAddRecordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final boolean show = true;
				showFormView(show);
			}
		});

		super.setUpView();
	}
}
