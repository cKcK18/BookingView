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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class TimeSheetFragment extends Fragment {

	private static final String TAG = TimeSheetFragment.class.getSimpleName();
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	private ListView mListView;
	private TimeSheetAdapter mAdapter;

	private Calendar mCalendar;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static TimeSheetFragment create(int pageNumber) {
		TimeSheetFragment fragment = new TimeSheetFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TimeSheetFragment() {
	} 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPageNumber = getArguments().getInt(ARG_PAGE);
		mCalendar = CalendarUtils.getCalendarByIndex(mPageNumber);
		mAdapter = new TimeSheetAdapter(getActivity(), mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1,
				mCalendar.get(Calendar.DATE));
		BookingDataManager.getInstance().setOnDataChangedListener(mAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		mListView = new ListView(getActivity());
		mListView.setVelocityScale(0.5f);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setAdapter(mAdapter);
		return mListView;
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
