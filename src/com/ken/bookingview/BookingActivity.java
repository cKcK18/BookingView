package com.ken.bookingview;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ken.bookingview.HorizontalListView.OnScrollChangedListener;

public class BookingActivity extends FragmentActivity implements OnScrollChangedListener {

	private static final int TRIGGER_FROM_ARROW = 0;
	private static final int TRIGGER_FROM_CALENDAR = 1;
	private static final int TRIGGER_FROM_PAGER = 2;

	private TextView mMonthView;
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
				finish();
			}
		});

		Button toadyButton = (Button) findViewById(R.id.booking_today);
		toadyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(DateUilities.getIndexOfToday());
			}
		});

		ImageButton leftArrowButton = (ImageButton) findViewById(R.id.booking_left_arrow);
		leftArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Calendar calendar = DateUilities.getCalendarWithOffsetOfMonthRelativeToPickedDate(-1);
				final int dateIndex = DateUilities.getIndexByCalendar(calendar);
				mPager.setCurrentItem(dateIndex);
				mMonthView.setText(getStringWithYearAndMonth(calendar));
			}
		});

		mMonthView = (TextView) findViewById(R.id.booking_month);
		mMonthView.setText(getStringWithYearAndMonth(Calendar.getInstance()));

		ImageButton rightArrowButton = (ImageButton) findViewById(R.id.booking_right_arrow);
		rightArrowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Calendar calendar = DateUilities.getCalendarWithOffsetOfMonthRelativeToPickedDate(1);
				final int dateIndex = DateUilities.getIndexByCalendar(calendar);
				mPager.setCurrentItem(dateIndex);
				mMonthView.setText(getStringWithYearAndMonth(calendar));
			}
		});

		//
		mCalendarListView = (HorizontalListView) findViewById(R.id.date_list_view);
		mCalendarListView.setAdapter(new DateAdapter(this));
		mCalendarListView.setOnScrollChangedListener(this);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.timesheet_pager);
		mPagerAdapter = new TimeSheetPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int dateIndex) {
				mCalendarListView.setSelection(dateIndex);
			}
		});
	}

	private String getStringWithYearAndMonth(Calendar calendar) {
		return String.format("%d月 %d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPager.setCurrentItem(DateUilities.getIndexOfToday());
			}
		}, 100);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// mHandler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// final BookingApplication app = (BookingApplication) getApplicationContext();
		// final long id = app.getBookingProvider().generateNewId();
		// final BookingData data = new BookingData(id, "winnie hsu", 2014, 9, 27, 1, 20, "0985091242",
		// new ArrayList<ServiceItems>(), "1.5h");
		// BookingDataManager.getInstance().writeBookingData(data);
		// }
		// }, 2000);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onScrollCompleted(int dateIndex) {
	}
}
