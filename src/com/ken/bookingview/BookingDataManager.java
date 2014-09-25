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
		void onDataReady();

		void onDataChanged();
	}

	private Context mContext;
	private ArrayList<BookingData> mBookingList;
	private ArrayList<OnDateChangedListener> mOnDateChangedListener;

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
		mOnDateChangedListener = new ArrayList<OnDateChangedListener>();

		readBookingData();
	}

	private void readBookingData() {
		new AsyncTask<Void, Void, ArrayList<BookingData>>() {
			@Override
			protected ArrayList<BookingData> doInBackground(Void... params) {
				final ArrayList<BookingData> bookingDataList = new ArrayList<BookingData>();

				final ContentResolver cr = mContext.getContentResolver();
				final Cursor cursor = cr.query(BookingProvider.CONTENT_URI_NO_NOTIFICATION, null, null, null, null);
				Log.d(TAG, String.format("[readBookingData] cursor is null: %b, size: %d ", cursor == null,
						cursor != null ? cursor.getCount() : 0));
				if (cursor == null || cursor.getCount() == 0) {
					return bookingDataList;
				}

				final int idIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_ID);
				final int nameIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_NAME);
				final int yearIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_YEAR);
				final int monthIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MONTH);
				final int dateIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_DATE);
				final int hourIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_HOUR);
				final int minuteIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MINUTE);
				final int phoneNumberIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_PHONE_NUMBER);
				final int serviceItemsIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_SERVICE_ITEMS);
				final int requiredTimeIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_REQUIRED_TIME);

				try {
					cursor.moveToFirst();
					while (cursor.moveToNext()) {
						final long id = cursor.getLong(idIndex);
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
						final ServiceItems[] serviceItemValues = ServiceItems.values();
						final String[] parts = serviceItems.split(",");
						final ArrayList<ServiceItems> serviceItemList = new ArrayList<ServiceItems>();
						if (parts != null) {
							for (int i = 0; i < parts.length; ++i) {
								for (int j = 0; j < serviceItemValues.length; ++j) {
									if (serviceItemValues[j].equals(parts[i])) {
										serviceItemList.add(serviceItemValues[j]);
									}
								}
							}
						}
						final BookingData data = new BookingData(id, name, year, month, date, hour, minute,
								phoneNumber, serviceItemList, requiredTime);
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
				Log.d(TAG, String.format("[readBookingData] dataList: %d", result != null ? result.size() : 0));
				mBookingList = result;
				for (OnDateChangedListener listener : mOnDateChangedListener) {
					listener.onDataReady();
				}
			}
		}.execute();
	}

	public void writeBookingData(final BookingData updateData) {
		BookingData tempData = null;
		for (BookingData data : mBookingList) {
			if (isTheSameDate(updateData, data)) {
				tempData = data;
				break;
			}
		}
		// update or add in memory
		final BookingData targerData = tempData;
		final boolean add = targerData == null;
		if (add) {
			mBookingList.add(updateData);
		} else {
			targerData.setTimeSheetItem(updateData);
		}
		// write database
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				final ContentValues cv = new ContentValues();
				if (add) {
					cv.put(BookingProvider.COLUMN_ID, updateData.id);
				}
				cv.put(BookingProvider.COLUMN_NAME, updateData.bookingName);
				cv.put(BookingProvider.COLUMN_YEAR, updateData.bookingYear);
				cv.put(BookingProvider.COLUMN_MONTH, updateData.bookingMonth);
				cv.put(BookingProvider.COLUMN_DATE, updateData.bookingDate);
				cv.put(BookingProvider.COLUMN_HOUR, updateData.bookingHour);
				cv.put(BookingProvider.COLUMN_MINUTE, updateData.bookingMinutes);
				cv.put(BookingProvider.COLUMN_PHONE_NUMBER, updateData.phoneNumber);
				// FIXME
				final int serviceItemSize = updateData.serviceItems.size();
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < serviceItemSize; ++i) {
					sb.append(updateData.serviceItems.get(i).toString());
					if (i != serviceItemSize - 1) {
						sb.append(",");
					}
				}
				cv.put(BookingProvider.COLUMN_SERVICE_ITEMS, sb.toString());
				cv.put(BookingProvider.COLUMN_REQUIRED_TIME, updateData.requiredTime);

				// update or add in database
				final ContentResolver cr = mContext.getContentResolver();
				if (add) {
					cr.insert(BookingProvider.CONTENT_URI_NO_NOTIFICATION, cv);
				} else {
					cr.update(BookingProvider.CONTENT_URI_NO_NOTIFICATION, cv, null, null);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.d(TAG, String.format("[writeBookingData] add: %b, dataList: %d", add, mBookingList.size()));
				for (OnDateChangedListener listener : mOnDateChangedListener) {
					listener.onDataChanged();
				}
			}
		}.execute();
	}

	private boolean isTheSameDate(BookingData data1, BookingData data2) {
		return data1.bookingYear == data2.bookingYear && data1.bookingMonth == data2.bookingMonth
				&& data1.bookingDate == data2.bookingDate && data1.bookingHour == data2.bookingHour
				&& data1.bookingMinutes == data2.bookingMinutes;
	}

	public void setOnDataChangedListener(OnDateChangedListener listener) {
		mOnDateChangedListener.add(listener);
	}

	public void removeOnDataChangedListener(OnDateChangedListener listener) {
		mOnDateChangedListener.remove(listener);
	}

	public ArrayList<BookingData> getBookingListByYear(int year) {
		final ArrayList<BookingData> list = new ArrayList<BookingData>();
		if (mBookingList != null) {
			for (BookingData timeSheet : mBookingList) {
				if (year == timeSheet.bookingYear) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingData> getBookingListByMonth(int year, int month) {
		final ArrayList<BookingData> list = new ArrayList<BookingData>();
		if (mBookingList != null) {
			for (BookingData timeSheet : mBookingList) {
				if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingData> getBookingListByDate(int year, int month, int date) {
		final ArrayList<BookingData> list = new ArrayList<BookingData>();
		if (mBookingList != null) {
			for (BookingData timeSheet : mBookingList) {
				if (year == timeSheet.bookingYear && month == timeSheet.bookingMonth && date == timeSheet.bookingDate) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public void release() {
		mContext = null;
		mBookingList.clear();
		mBookingList = null;
		mOnDateChangedListener.clear();
		mOnDateChangedListener = null;
	}
}
