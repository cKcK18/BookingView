package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

public class TimeSheetAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = TimeSheetAdapter.class.getSimpleName();

	private static final int MAX_ITEM = 24;

	private final Context mContext;
	private ArrayList<BookingData> mBookingList;

	private int mTimeSheetItemViewHeight = -1;

	public TimeSheetAdapter(Context context, int year, int month, int day) {
		mContext = context;
		mBookingList = new ArrayList<BookingData>(MAX_ITEM);

		mTimeSheetItemViewHeight = context.getResources().getDimensionPixelSize(R.dimen.time_sheet_item_view_height);

		mBookingList = BookingDataManager.getInstance().getBookingListByDay(mBookingList, year, month, day);
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
			if (position == item.bookingHour) {
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

	public void addOrUpdateTimeSheet(BookingData updateItem) {
		BookingData targerItem = null;
		for (BookingData item : mBookingList) {
			if (item.bookingYear == updateItem.bookingYear && item.bookingDate == updateItem.bookingDate
					&& item.bookingHour == updateItem.bookingHour) {
				targerItem = item;
				break;
			}
		}
		if (targerItem != null) {
			targerItem.setTimeSheetItem(updateItem);
		} else {
			mBookingList.add(updateItem);
		}
		notifyDataSetInvalidated();
	}
}