package com.ken.bookingview;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ken.bookingview.HorizontalListView.OnSelectedItemChangedListener;

public class BookingActivity extends FragmentActivity implements OnSelectedItemChangedListener {

	private static final int ACTION_TODAY = 0;
	private static final int ACTION_LEFT_ARROW = 1;
	private static final int ACTION_RIGHT_ARROW = 2;
	private static final int ACTION_DATE = 3;
	private static final int ACTION_PAGER = 4;

	private enum State {
		BOOKING, DETAIL
	};

	private TextView mMonthView;
	private HorizontalListView mDateListView;
	private ViewPager mPager;
	private ImageButton mAddBookingDataButton;
	private BookingDetailView mBookingDetailView;

	private State mState = State.BOOKING;

	private int mLastDateIndex = -100;
	private boolean mDateListViewChanged = false;
	private boolean mPagerChanged = false;

	private boolean mLockButtonToChangeDate = false;

	private final Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		setUpView();
	}

	private void setUpView() {
		ImageButton backButton = (ImageButton) findViewById(R.id.booking_back);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button toadyButton = (Button) findViewById(R.id.booking_today);
		toadyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_TODAY);
			}
		});

		ImageButton leftArrowButton = (ImageButton) findViewById(R.id.booking_left_arrow);
		leftArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_LEFT_ARROW);
			}
		});

		mMonthView = (TextView) findViewById(R.id.booking_month);
		mMonthView.setText(getStringWithYearAndMonth(Calendar.getInstance()));

		ImageButton rightArrowButton = (ImageButton) findViewById(R.id.booking_right_arrow);
		rightArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_RIGHT_ARROW);
			}
		});

		//
		mDateListView = (HorizontalListView) findViewById(R.id.date_list_view);
		mDateListView.setAdapter(new DateAdapter(this));
		mDateListView.setOnScrollChangedListener(this);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.timesheet_pager);
		mPager.setAdapter(new TimeSheetPagerAdapter(getSupportFragmentManager()));
		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int dateIndex) {
				if (mDateListViewChanged) {
					return;
				}
				mPagerChanged = true;
				changeDate(ACTION_PAGER, dateIndex);
			}
		});

		//
		mAddBookingDataButton = (ImageButton) findViewById(R.id.add_booking_data_btn);
		mAddBookingDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// final int tranY = getResources().getDisplayMetrics().heightPixels;
				mState = State.DETAIL;
				final boolean show = true;
				final boolean animate = true;
				mBookingDetailView.show(show, animate);
			}
		});

		//
		final int tranY = getResources().getDisplayMetrics().heightPixels;

		mBookingDetailView = (BookingDetailView) findViewById(R.id.booking_detail_view);
		mBookingDetailView.setTranslationY(tranY);
	}

	private void performDateToBeChanged(int action) {
		if (mLockButtonToChangeDate) {
			return;
		}
		mLockButtonToChangeDate = true;
		changeDate(action, -1);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mLockButtonToChangeDate = false;
			}
		}, 500);
	}

	private String getStringWithYearAndMonth(Calendar calendar) {
		return String.format("%d%s %d", calendar.get(Calendar.MONTH) + 1,
				getResources().getString(R.string.booking_view_date), calendar.get(Calendar.YEAR));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				changeDate(ACTION_TODAY, -1);
			}
		}, 100);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void backToBooking() {
		mState = State.BOOKING;
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

	@Override
	public void onSelectedItemChanged(int dateIndex) {
		if (mPagerChanged) {
			return;
		}
		mDateListViewChanged = true;
		changeDate(ACTION_DATE, dateIndex);
	}

	private void changeDate(int action, int dateIndex) {
		Log.d("kenchen", String.format("[changeDate] action: %d, date: %b, pager: %b, index: %d/%d", action,
				mDateListViewChanged, mPagerChanged, mLastDateIndex, dateIndex));
		if (mDateListViewChanged && mPagerChanged && mLastDateIndex == dateIndex) {
			return;
		}
		Log.d("kenchen", String.format("[changeDate] pass !!"));

		final Calendar calendar;
		switch (action) {
		case ACTION_TODAY: {
			final int actualIndex = dateIndex == -1 ? DateUtilities.getIndexOfToday() : dateIndex;
			calendar = DateUtilities.getCalendarByIndex(actualIndex);
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mDateListViewChanged = mPagerChanged = true;
			mLastDateIndex = dateIndex;

			mDateListView.setSelection(actualIndex);
			mPager.setCurrentItem(actualIndex);
			break;
		}
		case ACTION_LEFT_ARROW: {
			calendar = DateUtilities.getCalendarWithOffsetOfMonthRelativeToPickedDate(-1);
			Log.d("kenchen",
					String.format("[LEFT] picked date: %04d/%02d/%02d", calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
			final int actualIndex = dateIndex == -1 ? DateUtilities.getIndexByCalendar(calendar) : dateIndex;
			Log.d("kenchen", String.format("[LEFT] index: %d", actualIndex));
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mDateListViewChanged = mPagerChanged = true;
			mLastDateIndex = dateIndex;

			mDateListView.setSelection(actualIndex);
			mPager.setCurrentItem(actualIndex);
			break;
		}
		case ACTION_RIGHT_ARROW: {
			calendar = DateUtilities.getCalendarWithOffsetOfMonthRelativeToPickedDate(1);
			Log.d("kenchen",
					String.format("[RIGHT] picked date: %04d/%02d/%02d", calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
			final int actualIndex = dateIndex == -1 ? DateUtilities.getIndexByCalendar(calendar) : dateIndex;
			Log.d("kenchen", String.format("[RIGHT] index: %d", actualIndex));
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mDateListViewChanged = mPagerChanged = true;
			mLastDateIndex = dateIndex;

			mDateListView.setSelection(actualIndex);
			mPager.setCurrentItem(actualIndex);
			break;
		}
		case ACTION_DATE: {
			calendar = DateUtilities.getCalendarByIndex(dateIndex);
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mPagerChanged = true;
			mLastDateIndex = dateIndex;

			mPager.setCurrentItem(dateIndex);
			break;
		}
		case ACTION_PAGER: {
			calendar = DateUtilities.getCalendarByIndex(dateIndex);
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mDateListViewChanged = true;
			mLastDateIndex = dateIndex;

			mDateListView.setSelection(dateIndex);
			break;
		}
		default: {
			calendar = DateUtilities.getInstance();
		}
		}
		// reset flag
		mDateListViewChanged = mPagerChanged = false;
		mLastDateIndex = -100;
		final Calendar debug = calendar;
		Log.d("kenchen",
				String.format("picked date: %04d/%02d/%02d", debug.get(Calendar.YEAR), debug.get(Calendar.MONTH),
						debug.get(Calendar.DATE)));
		DateUtilities.sPickedDate = calendar;
	}
}
