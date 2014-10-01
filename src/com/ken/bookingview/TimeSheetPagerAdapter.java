package com.ken.bookingview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 {@link TimeSheetFragment} objects, in sequence.
 */
public class TimeSheetPagerAdapter extends FragmentStatePagerAdapter {

	public TimeSheetPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int pos) {
		return TimeSheetFragment.create(pos);
	}

	@Override
	public int getCount() {
		return DateUtilities.getTotalDays();
	}
}
