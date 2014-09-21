package com.ken.bookingview;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;

import com.ken.bookingview.BookingProfileItem.ServiceItems;
import com.ken.bookingview.ExecutorWorker.ExecutorTask;

public class BookingDataManager {

	private static final String TAG = BookingDataManager.class.getSimpleName();

	private static BookingDataManager sManager;

	private Context mContext;
	private ArrayList<TimeSheetItem> mBookingList;

	public static void init(Context context) {
		if (sManager == null) {
			sManager = new BookingDataManager(context);
		}
	}

	public static BookingDataManager getInstance() {
		return sManager;
	}

	private BookingDataManager(Context context) {
		mContext = context;
		mBookingList = new ArrayList<TimeSheetItem>();

		generateTestingData();
		// final Callable<Void> callable = new Callable<Void>() {
		// @Override
		// public Void call() throws Exception {
		//
		// return null;
		// }
		// };
		// ExecutorWorker.execute(new ExecutorTask(callable, "initialBookingManager"));
	}

	private void generateTestingData() {
		Log.d(TAG, "[generateTestingData]");
		// testing data
		ArrayList<ServiceItems> serviceList = new ArrayList<ServiceItems>();
		serviceList.add(ServiceItems.剪髮);
		serviceList.add(ServiceItems.洗髮);
		TimeSheetItem timeSheetItemForHash = new TimeSheetItem("ken chen", 2014, 9, 19, 1, 15, "0985091642", serviceList, "1h");
		mBookingList.add(timeSheetItemForHash);

		serviceList = new ArrayList<ServiceItems>();
		serviceList.add(ServiceItems.燙髮);
		timeSheetItemForHash = new TimeSheetItem("yomin lin", 2014, 9, 19, 5, 30, "0919183483", serviceList, "1h");
		mBookingList.add(timeSheetItemForHash);
	}

	public void putBookingData(TimeSheetItem timeSheet) {
		final int uninsertedSize = mBookingList.size();
		mBookingList.add(timeSheet);
		final int currentSize = mBookingList.size();
		Log.d(TAG, String.format("[putBookingData] booking count: %d -> %d", uninsertedSize, currentSize));

		writeTimeSheetIntoDatabase();
	}

	private void writeTimeSheetIntoDatabase() {
		final Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}
		};
		ExecutorWorker.execute(new ExecutorTask(callable, "writeTimeSheetIntoDatabase"));
	}

	public ArrayList<TimeSheetItem> getBookingListByYear(ArrayList<TimeSheetItem> list, int year) {
		if (list == null) {
			list = new ArrayList<TimeSheetItem>();
		}
		for (TimeSheetItem timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear) {
				list.add(timeSheet);
			}
		}
		return list;
	}

	public ArrayList<TimeSheetItem> getBookingListByMonth(ArrayList<TimeSheetItem> list, int year, int month) {
		if (list == null) {
			list = new ArrayList<TimeSheetItem>();
		}
		for (TimeSheetItem timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth) {
				list.add(timeSheet);
			}
		}
		return list;
	}

	public ArrayList<TimeSheetItem> getBookingListByDay(ArrayList<TimeSheetItem> list, int year, int month, int days) {
		if (list == null) {
			list = new ArrayList<TimeSheetItem>();
		}
		for (TimeSheetItem timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth && days == timeSheet.bookingDay) {
				list.add(timeSheet);
			}
		}
		return list;
	}

	public void release() {
		mContext = null;
		mBookingList.clear();
		mBookingList = null;
	}
}
