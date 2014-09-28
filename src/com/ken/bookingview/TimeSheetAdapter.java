package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.ken.bookingview.BookingDataManager.OnDateChangedListener;

public class TimeSheetAdapter extends BaseAdapter implements OnDateChangedListener {

	private static final String TAG = TimeSheetAdapter.class.getSimpleName();

	private static final int MAX_ITEM = 24;

	private final Context mContext;
	private final int mYear;
	private final int mMonth;
	private final int mDay;
	private ArrayList<BookingData> mBookingList;

	private int mTimeSheetItemViewHeight = -1;

	public TimeSheetAdapter(Context context, int year, int month, int day) {
		mContext = context;
		mYear = year;
		mMonth = month;
		mDay = day;

		final BookingDataManager manager = BookingDataManager.getInstance();
		mBookingList = manager.getBookingListByDate(year, month, day);

		mTimeSheetItemViewHeight = context.getResources().getDimensionPixelSize(R.dimen.time_sheet_item_view_height);
	}

	@Override
	public int getCount() {
		return MAX_ITEM;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// find time sheet item first
		BookingData timeSheetItem = null;
		for (BookingData item : mBookingList) {
			if (position == item.hourOfDay) {
				timeSheetItem = item;
				break;
			}
		}
		// reuse or create view
		if (convertView == null) {
			convertView = new TimeSheetItemView(mContext);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mTimeSheetItemViewHeight));
		}
		convertView.setTag(R.id.time_sheet_item_identity, position);
		convertView.setTag(R.id.time_sheet_item_info, timeSheetItem);

		return convertView;
	}

	@Override
	public void onDataReady() {
		mBookingList = BookingDataManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);
		Log.d(TAG, String.format("[onDataReady] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay,
						mBookingList.size()));
		notifyDataSetInvalidated();
	}

	@Override
	public void onDataChanged() {
		mBookingList = BookingDataManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);;
		Log.d(TAG, String.format("[onDataChanged] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay,
				mBookingList.size()));
		notifyDataSetInvalidated();
	}
}