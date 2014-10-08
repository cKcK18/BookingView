package com.ken.bookingview;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ken.bookingview.HorizontalListView.OnSelectedItemChangedListener;

abstract public class BookingActivity extends FragmentActivity implements OnSelectedItemChangedListener {

	private static final String TAG = BookingActivity.class.getSimpleName();

	protected static final int ACTION_TODAY = 0;
	protected static final int ACTION_LEFT_ARROW = 1;
	protected static final int ACTION_RIGHT_ARROW = 2;
	protected static final int ACTION_DATE = 3;
	protected static final int ACTION_PAGER = 4;

	protected enum State {
		CALENDAR, FORM_VIEW
	};

	protected TextView mMonthView;
	protected HorizontalListView mDateListView;
	protected ViewPager mPager;

	protected State mState = State.CALENDAR;

	private int mLastDateIndex = -100;
	private boolean mDateListViewChanged = false;
	private boolean mPagerChanged = false;
	private boolean mLockButtonToChangeDate = false;

	protected final Handler mHandler = new Handler();

	abstract protected int getLayoutResource();

	abstract protected Class<? extends TimesheetAdapter> getTimesheetAdapterClass();

	abstract protected FormController initializeFormController();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final int layoutResource = getLayoutResource();
		setContentView(layoutResource);
		setUpView();
	}

	protected void setUpView() {
		final ImageButton leftArrowButton = (ImageButton) findViewById(R.id.calendar_left_arrow);
		leftArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_LEFT_ARROW);
			}
		});

		mMonthView = (TextView) findViewById(R.id.calendar_month);
		mMonthView.setText(getStringWithYearAndMonth(Calendar.getInstance()));

		final ImageButton rightArrowButton = (ImageButton) findViewById(R.id.calendar_right_arrow);
		rightArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performDateToBeChanged(ACTION_RIGHT_ARROW);
			}
		});

		mDateListView = (HorizontalListView) findViewById(R.id.calendar_date_listview);
		mDateListView.setAdapter(new DateAdapter(this));
		mDateListView.setOnScrollChangedListener(this);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.calendar_timesheet_pager);
		mPager.setAdapter(new TimesheetPagerAdapter(getSupportFragmentManager()));
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

		// move to today when queue idle
		Looper.myQueue().addIdleHandler(new IdleHandler() {
			@Override
			public boolean queueIdle() {
				changeDate(ACTION_TODAY, -1);
				return false;
			}
		});
	}

	protected final void performDateToBeChanged(int action) {
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
		return String.format("%d%s %d", calendar.get(Calendar.MONTH) + 1, getResources().getString(R.string.booking_view_date),
				calendar.get(Calendar.YEAR));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onSelectedItemChanged(int dateIndex) {
		if (mPagerChanged) {
			return;
		}
		mDateListViewChanged = true;
		changeDate(ACTION_DATE, dateIndex);
	}

	protected final void changeDate(int action, int dateIndex) {
		Log.d(TAG, String.format("[changeDate] action: %d, date: %b, pager: %b, index: %d/%d", action, mDateListViewChanged, mPagerChanged,
				mLastDateIndex, dateIndex));
		if (mDateListViewChanged && mPagerChanged && mLastDateIndex == dateIndex) {
			return;
		}

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
			final int actualIndex = dateIndex == -1 ? DateUtilities.getIndexByCalendar(calendar) : dateIndex;
			mMonthView.setText(getStringWithYearAndMonth(calendar));

			mDateListViewChanged = mPagerChanged = true;
			mLastDateIndex = dateIndex;

			mDateListView.setSelection(actualIndex);
			mPager.setCurrentItem(actualIndex);
			break;
		}
		case ACTION_RIGHT_ARROW: {
			calendar = DateUtilities.getCalendarWithOffsetOfMonthRelativeToPickedDate(1);
			final int actualIndex = dateIndex == -1 ? DateUtilities.getIndexByCalendar(calendar) : dateIndex;
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

		DateUtilities.sPickedDate = calendar;
	}

	@Override
	public void onBackPressed() {
		if (mState == State.FORM_VIEW) {
			final boolean show = false;
			animateForm(show);
		} else {
			super.onBackPressed();
		}
	}

	public final void animateForm(boolean show) {
		animateForm(show, null);
	}

	protected void animateForm(boolean show, BookingRecord updateRecord) {
	}
}
