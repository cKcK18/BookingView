package com.ken.bookingview;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ken.bookingview.BookingData.ServiceItems;

public class BookingActivity extends FragmentActivity {

	public static int VISIBLE_DATE_COUNT = 7;
	public static int VISIBLE_DATE_IN_CENTER = VISIBLE_DATE_COUNT / 2;

	private TextView mDateView;
	private HorizontalListView mCalendarListView;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

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
			}
		});

		ImageButton leftArrowButton = (ImageButton) findViewById(R.id.booking_left_arrow);
		leftArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int dateIndex = CalendarUtils.getIndexWithLastMonth();
				mPager.setCurrentItem(dateIndex);
				mDateView.setText(CalendarUtils.getStringOfYearAndLastMonth());
			}
		});

		mDateView = (TextView) findViewById(R.id.booking_date);
		mDateView.setText(CalendarUtils.getStringOfYearAndMonth());

		ImageButton rightArrowButton = (ImageButton) findViewById(R.id.booking_right_arrow);
		rightArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int dateIndex = CalendarUtils.getIndexWithNextMonth();
				mPager.setCurrentItem(dateIndex);
				mDateView.setText(CalendarUtils.getStringOfYearAndNextMonth());
			}
		});

		//
		mCalendarListView = (HorizontalListView) findViewById(R.id.calendar_list_view);
		mCalendarListView.setAdapter(new CalendarAdapter(this));

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.timesheet_pager);
		mPagerAdapter = new TimeSheetPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(final int position) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						final int fixInCenter = position >= VISIBLE_DATE_IN_CENTER ? position - VISIBLE_DATE_IN_CENTER
								: 0;
						mCalendarListView.setSelection(fixInCenter);
					}
				}, 100);
			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mPager.setCurrentItem(CalendarUtils.getIndexOfCurrentDay());
	}

	@Override
	protected void onResume() {
		super.onResume();

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final BookingApplication app = (BookingApplication) getApplicationContext();
				final long id = app.getBookingProvider().generateNewId();
				final BookingData data = new BookingData(id, "winnie hsu", 2014, 9, 23, 1, 20, "0985091242",
						new ArrayList<ServiceItems>(), "1.5h");
				BookingDataManager.getInstance().writeBookingData(data);
			}
		}, 2000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final BookingApplication app = (BookingApplication) getApplicationContext();
				final long id = app.getBookingProvider().generateNewId();
				final BookingData data = new BookingData(id, "ken chen", 2014, 9, 23, 1, 20, "0985091642",
						new ArrayList<ServiceItems>(), "1.5h");
				BookingDataManager.getInstance().writeBookingData(data);
			}
		}, 4000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final BookingApplication app = (BookingApplication) getApplicationContext();
				final long id = app.getBookingProvider().generateNewId();
				final BookingData data = new BookingData(id, "winnie hsu", 2014, 9, 23, 2, 20, "0985091242",
						new ArrayList<ServiceItems>(), "1.5h");
				BookingDataManager.getInstance().writeBookingData(data);
			}
		}, 6000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final BookingApplication app = (BookingApplication) getApplicationContext();
				final long id = app.getBookingProvider().generateNewId();
				final BookingData data = new BookingData(id, "ken chen", 2014, 9, 23, 4, 20, "0985091242",
						new ArrayList<ServiceItems>(), "1.5h");
				BookingDataManager.getInstance().writeBookingData(data);
			}
		}, 8000);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
