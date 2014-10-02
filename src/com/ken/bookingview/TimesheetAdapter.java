package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.widget.BaseAdapter;

import com.ken.bookingview.BookingDataManager.OnDateChangedListener;

public abstract class TimesheetAdapter extends BaseAdapter implements OnDateChangedListener {

	@SuppressWarnings("unused")
	private static final String TAG = TimesheetAdapter.class.getSimpleName();

	protected final Context mContext;
	protected final int mYear;
	protected final int mMonth;
	protected final int mDay;
	protected final int mMaxSize;
	protected ArrayList<BookingData> mBookingList;

	protected int mTimeSheetItemViewHeight = -1;

	public TimesheetAdapter(Context context, int year, int month, int day, int maxSize) {
		mContext = context;
		mYear = year;
		mMonth = month;
		mDay = day;
		mMaxSize = maxSize;

		final BookingDataManager manager = BookingDataManager.getInstance();
		mBookingList = manager.getBookingListByDate(year, month, day);

		mTimeSheetItemViewHeight = context.getResources().getDimensionPixelSize(R.dimen.time_sheet_item_view_height);
	}

	@Override
	public int getCount() {
		return mMaxSize;
	}

	@Override
	public Object getItem(int position) {
		return mBookingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onDataReady() {
		mBookingList = BookingDataManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);
	}

	@Override
	public void onDataChanged() {
		mBookingList = BookingDataManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);
	}
}