package com.ken.bookingview;

import java.util.ArrayList;
import java.util.HashMap;

import com.ken.bookingview.BookingDataManager.OnDateChangedListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * A simple pager adapter that represents 5 {@link TimeSheetFragment} objects, in sequence.
 */
public class TimeSheetPagerAdapter extends FragmentStatePagerAdapter {

//	private HashMap<Integer, Fragment> mTimeSheetFragmentList;

	public TimeSheetPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
//		mTimeSheetFragmentList = new HashMap<Integer, Fragment>(3);
	}

	@Override
	public Fragment getItem(int pos) {
		// reuse view without creating new one, we keep index of position - 1, position and position + 1
//		Fragment reuseFragment = null;
//		for (int key : mTimeSheetFragmentList.keySet()) {
//			if (key != (pos - 1) || key != pos || key != (pos + 1)) {
//				reuseFragment = mTimeSheetFragmentList.get(key);
//				break;
//			}
//		}
//		if (reuseFragment == null) {
//			reuseFragment = TimeSheetFragment.create(pos);
//			mTimeSheetFragmentList.put(pos, reuseFragment);
//		}
//		return reuseFragment;
		
		Fragment fragment = TimeSheetFragment.create(pos);
		return fragment;
	}

	@Override
	public int getCount() {
		return DateUtilities.getTotalDays();
	}

	@Override
	public void destroyItem(ViewGroup container, int pos, Object object) {
		super.destroyItem(container, pos, object);
		// mTimeSheetFragmentList.remove(pos);
	}

}
