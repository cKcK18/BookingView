package com.ken.bookingview;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomerBookingActivity extends BookingActivity {

	@SuppressWarnings("unused")
	private static final String TAG = CustomerBookingActivity.class.getSimpleName();

	private static final String PHONE_NUMBER = "0985091642";

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_customer_booking;
	}

	@Override
	protected Class<? extends TimesheetAdapter> getTimesheetAdapterClass() {
		return CustomerTimesheetAdapter.class;
	}

	@Override
	protected FormController initializeFormController() {
		return null;
	}

	@Override
	protected void setUpView() {
		final TextView stylishName = (TextView) findViewById(R.id.custommer_booking_stylish_name);
		stylishName.setText("H-Park hair salon, Sophia");

		final ImageButton cancelButton = (ImageButton) findViewById(R.id.customer_booking_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final String callString = getResources().getString(R.string.customer_booking_view_call);
		final Button callButton = (Button) findViewById(R.id.cusomer_booking_call_button);
		callButton.setText(String.format("%s %s", callString, PHONE_NUMBER));
		callButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + PHONE_NUMBER));
				startActivity(intent);
			}
		});

		super.setUpView();
	}
}
