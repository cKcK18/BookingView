package com.ken.bookingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

public class StylishBookingActivity extends BookingActivity {

	@SuppressWarnings("unused")
	private static final String TAG = StylishBookingActivity.class.getSimpleName();

	private enum State {
		CALENDAR, DETAIL_VIEW
	};

	private ImageButton mAddRecordButton;
	private View mOverlayView;
	private BookingDetailView mBookingDetailView;

	private State mState = State.CALENDAR;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_stylish_booking;
	}

	@Override
	protected Class<? extends TimesheetAdapter> getTimesheetAdapterClass() {
		return StylishTimesheetAdapter.class;
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
				showDetailView(show);
			}
		});

		mOverlayView = (View) findViewById(R.id.stylish_booking_overlay);
		mOverlayView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		final int tranY = getResources().getDisplayMetrics().heightPixels;
		mBookingDetailView = (BookingDetailView) findViewById(R.id.stylish_booking_detail_view);
		mBookingDetailView.setTranslationY(tranY);

		super.setUpView();
	}

	@Override
	public void onBackPressed() {
		if (mState == State.DETAIL_VIEW) {
			final boolean show = false;
			showDetailView(show);
		} else {
			super.onBackPressed();
		}
	}

	public void showDetailView(boolean show) {
		showDetailView(show, null);
	}

	public void showDetailView(boolean show, BookingRecord updateRecord) {
		if (show) {
			mState = State.DETAIL_VIEW;
		} else {
			mState = State.CALENDAR;
		}
		mBookingDetailView.show(show, updateRecord);
		overlay(show);
	}

	private void overlay(final boolean show) {
		final float alpha = show ? 0.5f : 0.0f;

		mOverlayView.animate().alpha(alpha).setDuration(300).setInterpolator(new DecelerateInterpolator())
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						if (show) {
							mOverlayView.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						if (!show) {
							mOverlayView.setVisibility(View.GONE);
						}
						mOverlayView.setAlpha(alpha);
					}
				}).start();
	}
}
