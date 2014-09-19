package com.ken.bookingview;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.ken.bookingview.BookingProfileItem.ServiceItems;

public class TimeSheetAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = TimeSheetAdapter.class.getSimpleName();

	private static final int MAX_ITEM = 24;

	private final Context mContext;
	private HashMap<Integer, TimeSheetItem> mBookingList;

	private int mTimeSheetItemViewHeight = -1;

	public TimeSheetAdapter(Context context) {
		mContext = context;
		mBookingList = new HashMap<Integer, TimeSheetItem>(MAX_ITEM);

		mTimeSheetItemViewHeight = context.getResources().getDimensionPixelSize(R.dimen.time_sheet_item_view_height);

		generateTestingData();
	}

	private void generateTestingData() {
		// testing data
		ArrayList<ServiceItems> serviceList = new ArrayList<ServiceItems>();
		serviceList.add(ServiceItems.剪髮);
		serviceList.add(ServiceItems.染髮);
		TimeSheetItem timeSheetItemForHash = new TimeSheetItem(1, "陳先生", "01:15", "0985091642", serviceList, "1h");
		mBookingList.put(1, timeSheetItemForHash);

		serviceList = new ArrayList<ServiceItems>();
		serviceList.add(ServiceItems.剪髮);
		timeSheetItemForHash = new TimeSheetItem(5, "林先生", "05:30", "0919183483", serviceList, "1h");
		mBookingList.put(5, timeSheetItemForHash);
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final TimeSheetItem timeSheetItem = mBookingList.get(position);
		if (convertView == null) {
			convertView = new TimeSheetItemView(mContext);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mTimeSheetItemViewHeight));
		}
		convertView.setTag(R.id.time_sheet_item_identity, position);
		convertView.setTag(R.id.time_sheet_item_info, timeSheetItem);
		// convertView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// }
		// });
		return convertView;
	}

	public void bookOrUpdateTimeSheet(int identity, TimeSheetItem timeSheetItem) {
		TimeSheetItem currentTimeSheetItem = mBookingList.get(identity);
		if (currentTimeSheetItem != null) {
			currentTimeSheetItem.setTimeSheetItem(timeSheetItem);
		} else {
			mBookingList.put(identity, timeSheetItem);
		}
		notifyDataSetInvalidated();
	}
}