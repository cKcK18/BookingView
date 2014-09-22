package com.ken.bookingview;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ken.bookingview.BookingData.ServiceItems;

public class BookingDataManager {

	private static final String TAG = BookingDataManager.class.getSimpleName();

	private static BookingDataManager sManager;

	public interface OnDateChangedListener {
		void onDataReady(ArrayList<BookingData> dataList);

		void onDataChanged(ArrayList<BookingData> dataList);
	}

	private Context mContext;
	private ArrayList<BookingData> mBookingList;
	private OnDateChangedListener mOnDateChangedListener;

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
		mBookingList = new ArrayList<BookingData>();

		readBookingData();
	}

	private void readBookingData() {
		new AsyncTask<Void, Void, ArrayList<BookingData>>() {
			@Override
			protected ArrayList<BookingData> doInBackground(Void... params) {
				final ContentResolver cr = mContext.getContentResolver();
				final Cursor cursor = cr.query(BookingProvider.CONTENT_URI_NO_NOTIFICATION, null, null, null, null);
				if (cursor == null || cursor.getCount() == 0) {
					return null;
				}

				final int nameIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_NAME);
				final int yearIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_YEAR);
				final int monthIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MONTH);
				final int dateIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_DATE);
				final int hourIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_HOUR);
				final int minuteIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MINUTE);
				final int phoneNumberIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_PHONE_NUMBER);
				final int serviceItemsIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_SERVICE_ITEMS);
				final int requiredTimeIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_REQUIRED_TIME);

				final ArrayList<BookingData> bookingDataList = new ArrayList<BookingData>();
				try {
					while (cursor.moveToNext()) {
						final String name = cursor.getString(nameIndex);
						final int year = cursor.getInt(yearIndex);
						final int month = cursor.getInt(monthIndex);
						final int date = cursor.getInt(dateIndex);
						final int hour = cursor.getInt(hourIndex);
						final int minute = cursor.getInt(minuteIndex);
						final String phoneNumber = cursor.getString(phoneNumberIndex);
						final String serviceItems = cursor.getString(serviceItemsIndex);
						final String requiredTime = cursor.getString(requiredTimeIndex);
						// FIXME
						final BookingData data = new BookingData(name, year, month, date, hour, minute, phoneNumber,
								new ArrayList<ServiceItems>(), requiredTime);
						bookingDataList.add(data);
					}
				} catch (Exception e) {
					bookingDataList.clear();
				} finally {
					cursor.close();
				}
				return bookingDataList;
			}

			@Override
			protected void onPostExecute(ArrayList<BookingData> result) {
				mBookingList = result;
				if (mOnDateChangedListener != null) {
					mOnDateChangedListener.onDataReady(result);
				}
			}
		}.execute();
	}

	public void writeBookingData(final BookingData data) {
		final int uninsertedSize = mBookingList.size();
		mBookingList.add(data);
		final int currentSize = mBookingList.size();
		Log.d(TAG, String.format("[writeBookingData] booking count: %d -> %d", uninsertedSize, currentSize));

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				final ContentValues cv = new ContentValues();
				cv.put(BookingProvider.COLUMN_NAME, data.bookingName);
				cv.put(BookingProvider.COLUMN_YEAR, data.bookingYear);
				cv.put(BookingProvider.COLUMN_MONTH, data.bookingMonth);
				cv.put(BookingProvider.COLUMN_DATE, data.bookingDate);
				cv.put(BookingProvider.COLUMN_HOUR, data.bookingHour);
				cv.put(BookingProvider.COLUMN_MINUTE, data.bookingMinutes);
				cv.put(BookingProvider.COLUMN_PHONE_NUMBER, data.phoneNumber);
				// FIXME
				cv.put(BookingProvider.COLUMN_SERVICE_ITEMS, "");
				cv.put(BookingProvider.COLUMN_REQUIRED_TIME, data.requiredTime);

				final ContentResolver cr = mContext.getContentResolver();
				cr.insert(BookingProvider.CONTENT_URI_NO_NOTIFICATION, cv);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (mOnDateChangedListener != null) {
					mOnDateChangedListener.onDataChanged(mBookingList);
				}
			}
		}.execute();
	}

	public void setOnDataChangedListener(OnDateChangedListener listener) {
		mOnDateChangedListener = listener;
	}

	public ArrayList<BookingData> getBookingListByYear(ArrayList<BookingData> list, int year) {
		if (list == null) {
			list = new ArrayList<BookingData>();
		}
		for (BookingData timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear) {
				list.add(timeSheet);
			}
		}
		return list;
	}

	public ArrayList<BookingData> getBookingListByMonth(ArrayList<BookingData> list, int year, int month) {
		if (list == null) {
			list = new ArrayList<BookingData>();
		}
		for (BookingData timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth) {
				list.add(timeSheet);
			}
		}
		return list;
	}

	public ArrayList<BookingData> getBookingListByDay(ArrayList<BookingData> list, int year, int month, int days) {
		if (list == null) {
			list = new ArrayList<BookingData>();
		}
		for (BookingData timeSheet : mBookingList) {
			if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth && days == timeSheet.bookingDate) {
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
