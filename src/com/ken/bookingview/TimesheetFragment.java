/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ken.bookingview;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating the page number, along with some dummy
 * text.
 *
 * <p>
 * This class is used by the {@link CardFlipActivity} and {@link ScreenSlideActivity} samples.
 * </p>
 */
public class TimesheetFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = TimesheetFragment.class.getSimpleName();
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	private TimesheetAdapter mAdapter;

	private Calendar mCalendar;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static TimesheetFragment create(int pageNumber) {
		TimesheetFragment fragment = new TimesheetFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TimesheetFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPageNumber = getArguments().getInt(ARG_PAGE);
		mCalendar = DateUtilities.getCalendarByIndex(mPageNumber);
		mAdapter = recognizeAdapter();
		BookingDataManager.getInstance().setOnDataChangedListener(mAdapter);
	}

	private TimesheetAdapter recognizeAdapter() {
		final BookingActivity activity = (BookingActivity) getActivity();
		final Class<? extends TimesheetAdapter> className = activity.getTimesheetAdapterClass();

		if (StylishTimesheetAdapter.class.isAssignableFrom(className)) {
			return new StylishTimesheetAdapter(getActivity(), mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1,
					mCalendar.get(Calendar.DATE), 24);
		} else {
			return new CustomerTimesheetAdapter(getActivity(), mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1,
					mCalendar.get(Calendar.DATE), 48);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ListView listView = new ListView(getActivity());
		listView.setVelocityScale(0.5f);
		listView.setVerticalScrollBarEnabled(false);
		listView.setAdapter(mAdapter);
		return listView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		BookingDataManager.getInstance().removeOnDataChangedListener(mAdapter);
	}

	public Calendar getCalendar() {
		return mCalendar;
	}
}
