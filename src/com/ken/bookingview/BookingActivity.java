package com.ken.bookingview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;

public class BookingActivity extends FragmentActivity {

	private HorizontalListView mCalendarListView;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);

		BookingManager.init(this);

		//
		mCalendarListView = (HorizontalListView) findViewById(R.id.calendar_list_view);
		mCalendarListView.setAdapter(new CalendarAdapter(this));

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.timesheet_pager);
		mPagerAdapter = new TimeSheetPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}
		});
		mPager.setCurrentItem(CalendarUtils.getIndexOfCurrentDay());
	}
}
