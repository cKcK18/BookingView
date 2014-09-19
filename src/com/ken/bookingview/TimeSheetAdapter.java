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
	private ArrayList<TimeSheetItem> mBookingList;

	private int mTimeSheetItemViewHeight = -1;

	public TimeSheetAdapter(Context context, int year, int month, int day) {
		mContext = context;
		mBookingList = new ArrayList<TimeSheetItem>(MAX_ITEM);

		mTimeSheetItemViewHeight = context.getResources().getDimensionPixelSize(R.dimen.time_sheet_item_view_height);

		mBookingList = BookingManager.getInstance().getBookingListByDay(mBookingList, year, month, day);
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
		TimeSheetItem timeSheetItem = null;
		for (TimeSheetItem item : mBookingList) {
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

	public void addOrUpdateTimeSheet(TimeSheetItem updateItem) {
		TimeSheetItem targerItem = null;
		for (TimeSheetItem item : mBookingList) {
			if (item.bookingYear == updateItem.bookingYear && item.bookingDay == updateItem.bookingDay
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