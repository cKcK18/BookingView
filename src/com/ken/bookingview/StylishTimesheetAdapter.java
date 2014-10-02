package com.ken.bookingview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

public class StylishTimesheetAdapter extends TimesheetAdapter {

	private static final String TAG = StylishTimesheetAdapter.class.getSimpleName();

	public StylishTimesheetAdapter(Context context, int year, int month, int day, int maxSize) {
		super(context, year, month, day, maxSize);
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
			convertView = new StylishTimesheetItemView(mContext);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mTimeSheetItemViewHeight));
		}
		convertView.setTag(R.id.time_sheet_item_identity, position);
		convertView.setTag(R.id.time_sheet_item_info, timeSheetItem);

		return convertView;
	}

	@Override
	public void onDataReady() {
		super.onDataReady();

		Log.d(TAG, String.format("[onDataReady] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mBookingList.size()));
		notifyDataSetInvalidated();
	}

	@Override
	public void onDataChanged() {
		super.onDataChanged();

		mBookingList = BookingDataManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);
		Log.d(TAG, String.format("[onDataChanged] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mBookingList.size()));
		notifyDataSetInvalidated();
	}
}