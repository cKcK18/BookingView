package com.ken.bookingview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import com.ken.bookingview.BookingRecordManager.OnDateChangedListener;

public class CustomerTimesheetAdapter extends TimesheetAdapter implements OnDateChangedListener {

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
	public void onDataReady() {
		super.onDataReady();

		Log.d(TAG, String.format("[onDataReady] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mBookingList.size()));
		notifyDataSetInvalidated();
	}

	@Override
	public void onDataChanged() {
		super.onDataChanged();

		mBookingList = BookingRecordManager.getInstance().getBookingListByDate(mYear, mMonth, mDay);
		Log.d(TAG, String.format("[onDataChanged] date: %04d/%02d/%02d, data size: %d", mYear, mMonth, mDay, mBookingList.size()));
		notifyDataSetInvalidated();
	}
}