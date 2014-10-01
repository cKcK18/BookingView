package com.ken.bookingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

public class StylishBookingActivity extends BookingActivity {

	@SuppressWarnings("unused")
	private static final String TAG = StylishBookingActivity.class.getSimpleName();

	private enum State {
		CALENDAR, DETAIL
	};

	private ImageButton mAddDataButton;
	private View mOverlayView;
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
		mAddDataButton = (ImageButton) findViewById(R.id.stylish_booking_add_button);
		mAddDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mState = State.DETAIL;
				final boolean show = true;
				final boolean animate = true;
				showDetailView(show, animate);
			}
		});

		mOverlayView = (View) findViewById(R.id.stylish_booking_overlay);

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
			showDetailView(show, animate);
		} else {
			super.onBackPressed();
		}
	}

	public void showDetailView(boolean show, boolean animate) {
		mBookingDetailView.show(show, animate);
		overlay(show, animate);
	}

	private void overlay(final boolean show, boolean animate) {
		final float alpha = show ? 1.0f : 0.0f;
		final int duration = animate ? 300 : 0;

		mOverlayView.animate().alpha(alpha).setDuration(duration).setInterpolator(new DecelerateInterpolator())
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
