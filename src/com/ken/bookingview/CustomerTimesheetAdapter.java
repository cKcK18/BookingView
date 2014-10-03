package com.ken.bookingview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import com.ken.bookingview.BookingRecordManager.OnRecordChangedListener;

public class CustomerTimesheetAdapter extends TimesheetAdapter implements OnRecordChangedListener {

	private static final String TAG = CustomerTimesheetAdapter.class.getSimpleName();

	public CustomerTimesheetAdapter(Context context, int year, int month, int day, int maxSize) {
		super(context, year, month, day, maxSize);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BookingRecord record = getAvailableRecord(position);

		// reuse or create view
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_customer_booking_item_view, null, false);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mBookingItemHeight));
			convertView.setTag(R.id.booking_item_count, mMaxSize);
		}
		convertView.setTag(R.id.booking_item_identity, position);
		convertView.setTag(R.id.booking_item_info, record);

		return convertView;
	}

	@Override
	public void onRecordReady() {
		super.onRecordReady();

		Log.d(TAG, String.format("[onDataReady] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mRecordList.size()));
		notifyDataSetInvalidated();
	}

	@Override
	public void onRecordChanged() {
		super.onRecordChanged();

		mRecordList = BookingRecordManager.getInstance().getRecordListByDate(mYear, mMonth, mDay);
		Log.d(TAG, String.format("[onDataChanged] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mRecordList.size()));
		notifyDataSetInvalidated();
	}
}