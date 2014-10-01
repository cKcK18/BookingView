package com.ken.bookingview;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class StylishBookingActivity extends BookingActivity {

	@SuppressWarnings("unused")
	private static final String TAG = StylishBookingActivity.class.getSimpleName();

	private enum State {
		CALENDAR, DETAIL
	};

	private ImageButton mAddBookingDataButton;
	private BookingDetailView mBookingDetailView;

	private State mState = State.CALENDAR;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_stylish_booking;
	}

	@Override
	protected FragmentStatePagerAdapter getPagerAdapter() {
		return new TimeSheetPagerAdapter(getSupportFragmentManager());
	}

	@Override
	protected void setUpView() {
		final ImageButton backButton = (ImageButton) findViewById(R.id.stylish_booking_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
		mAddBookingDataButton = (ImageButton) findViewById(R.id.stylish_booking_add_button);
		mAddBookingDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mState = State.DETAIL;
				final boolean show = true;
				final boolean animate = true;
				mBookingDetailView.show(show, animate);
			}
		});

		final int tranY = getResources().getDisplayMetrics().heightPixels;
		mBookingDetailView = (BookingDetailView) findViewById(R.id.stylish_booking_detail_view);
		mBookingDetailView.setTranslationY(tranY);

		super.setUpView();
	}

	public void backToBooking() {
		mState = State.CALENDAR;
	}

	@Override
	public void onBackPressed() {
		if (mState == State.DETAIL) {
			final boolean show = false;
			final boolean animate = true;
			mBookingDetailView.show(show, animate);
		} else {
			super.onBackPressed();
		}
	}
}
