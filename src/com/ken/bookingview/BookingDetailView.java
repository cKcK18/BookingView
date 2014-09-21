package com.ken.bookingview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BookingDetailView extends LinearLayout {

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
		final TextView dateText = (TextView) findViewById(R.id.booking_detail_date);
		final ImageButton dateButton = (ImageButton) findViewById(R.id.booking_detail_date_arrow);
		final EditText editPhoneNumber = (EditText) findViewById(R.id.booking_detail_edit_phone_number);
		final TextView serviceItemText = (TextView) findViewById(R.id.booking_detail_service_item);
		final Spinner serviceItemSpinner = (Spinner) findViewById(R.id.booking_detail_service_item_arrow);
		final TextView requiredTime = (TextView) findViewById(R.id.booking_detail_required_time);
		final Button confirm = (Button) findViewById(R.id.booking_detail_confirm);
		confirm.setOnClickListener(new OnClickListener() {
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
					Toast.makeText(BookingDetailView.this.getContext(), "invalide service item", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		Button cancel = (Button) findViewById(R.id.booking_detail_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
}
