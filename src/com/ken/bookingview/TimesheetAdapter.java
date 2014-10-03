package com.ken.bookingview;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.widget.BaseAdapter;

import com.ken.bookingview.BookingRecordManager.OnRecordChangedListener;

public abstract class TimesheetAdapter extends BaseAdapter implements OnRecordChangedListener {

	@SuppressWarnings("unused")
	private static final String TAG = TimesheetAdapter.class.getSimpleName();

	protected final Context mContext;
	protected final int mYear;
	protected final int mMonth;
	protected final int mDay;
	protected final int mMaxSize;
	protected ArrayList<BookingRecord> mRecordList;

	protected int mRecordItemHeight = -1;

	public TimesheetAdapter(Context context, int year, int month, int day, int maxSize) {
		mContext = context;
		mYear = year;
		mMonth = month;
		mDay = day;
		mMaxSize = maxSize;

		final BookingRecordManager manager = BookingRecordManager.getInstance();
		mRecordList = manager.getRecordListByDate(year, month, day);

		mRecordItemHeight = context.getResources().getDimensionPixelSize(R.dimen.booking_item_height);
	}

	@Override
	public int getCount() {
		return mMaxSize;
	}

	@Override
	public Object getItem(int position) {
		return mRecordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onRecordReady() {
		mRecordList = BookingRecordManager.getInstance().getRecordListByDate(mYear, mMonth, mDay);
	}

	@Override
	public void onRecordChanged() {
		mRecordList = BookingRecordManager.getInstance().getRecordListByDate(mYear, mMonth, mDay);
	}

	protected final BookingRecord getAvailableRecord(int position) {
		// transform position into specific time
		final int unitMinutes = DateUtilities.A_DAY_IN_MINUTE / mMaxSize;
		final int time = position * unitMinutes;
		final int hour = time / DateUtilities.A_HOUR_IN_MINUTE;
		final int minute = time % DateUtilities.A_HOUR_IN_MINUTE;

		// find record first
		BookingRecord timeSheetItem = null;
		for (BookingRecord record : mRecordList) {
			Calendar start = Calendar.getInstance();
			start.set(mYear, mMonth, mDay, record.hourOfDay, record.minute, 0);

			Calendar end = Calendar.getInstance();
			end.set(mYear, mMonth, mDay, record.hourOfDay, record.minute, 0);
			end.add(Calendar.HOUR_OF_DAY, record.requiredHour);
			end.add(Calendar.MINUTE, record.requiredMinute);

			Calendar target = Calendar.getInstance();
			target.set(mYear, mMonth, mDay, hour, minute, 0);

			if (DateUtilities.within(start, end, target)) {
				timeSheetItem = record;
				break;
			}
		}
		return timeSheetItem;
	}
}