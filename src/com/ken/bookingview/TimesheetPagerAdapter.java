package com.ken.bookingview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 {@link TimesheetFragment} objects, in sequence.
 */
public class TimesheetPagerAdapter extends FragmentStatePagerAdapter {

	public TimesheetPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int pos) {
		return TimesheetFragment.create(pos);
	}

	@Override
	public int getCount() {
		return DateUtilities.getTotalDays();
	}
}
