package com.ken.bookingview;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class BookingRecordManager {

	private static final String TAG = BookingRecordManager.class.getSimpleName();

	private static final String SERVER_RECORD_URL = "http://106.187.42.254:3000/users";

	static final String SEPARATED_STRING = ", ";

	private static BookingRecordManager sManager;

	public interface OnDateChangedListener {
		void onDataReady();

		void onDataChanged();
	}

	private Context mContext;
	private ArrayList<BookingRecord> mBookingList;
	private ArrayList<OnDateChangedListener> mOnDateChangedListener;

	public static void init(Context context) {
		if (sManager == null) {
			sManager = new BookingRecordManager(context);
		}
	}

	public static BookingRecordManager getInstance() {
		return sManager;
	}

	private BookingRecordManager(Context context) {
		mContext = context;
		mOnDateChangedListener = new ArrayList<OnDateChangedListener>();

		readBookingRecord();
	}

	private void readBookingRecord() {
		new AsyncTask<Void, Void, ArrayList<BookingRecord>>() {
			@Override
			protected ArrayList<BookingRecord> doInBackground(Void... params) {
				final ArrayList<BookingRecord> bookingDataList = new ArrayList<BookingRecord>();

				final ContentResolver cr = mContext.getContentResolver();
				final Cursor cursor = cr.query(BookingProvider.CONTENT_URI_NO_NOTIFICATION, null, null, null, null);
				Log.d(TAG,
						String.format("[readBookingRecord] cursor is null: %b, size: %d ", cursor == null,
								cursor != null ? cursor.getCount() : 0));
				if (cursor == null || cursor.getCount() == 0) {
					return bookingDataList;
				}

				final int idIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_ID);
				final int nameIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_NAME);
				final int sexIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_SEX);
				final int yearIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_YEAR);
				final int monthIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MONTH);
				final int dayIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_DAY);
				final int hourIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_HOUR_OF_DAY);
				final int minuteIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_MINUTE);
				final int phoneNumberIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_PHONE_NUMBER);
				final int serviceTypeIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_SERVICE_TYPE);
				final int requiredHourIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_REQUIRED_HOUR);
				final int requiredMinuteIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_REQUIRED_MINUTE);

				try {
					cursor.moveToFirst();
					while (cursor.moveToNext()) {
						final long id = cursor.getLong(idIndex);
						final String name = cursor.getString(nameIndex);
						final String sex = cursor.getString(sexIndex);
						final int year = cursor.getInt(yearIndex);
						final int month = cursor.getInt(monthIndex);
						final int day = cursor.getInt(dayIndex);
						final int hour = cursor.getInt(hourIndex);
						final int minute = cursor.getInt(minuteIndex);
						final String phoneNumber = cursor.getString(phoneNumberIndex);
						final String serviceItems = cursor.getString(serviceTypeIndex);
						Log.d(TAG, "[read] serviceItems: " + serviceItems);
						// FIXME
						final String[] parts = serviceItems.split(SEPARATED_STRING);
						Log.d(TAG, "[read] parts: " + parts);
						final ArrayList<String> serviceItemList = new ArrayList<String>();
						if (parts != null) {
							for (int i = 0; i < parts.length; ++i) {
								Log.d(TAG, "[read] parts[" + i + "]: " + parts[i]);
								serviceItemList.add(parts[i]);
							}
						}
						Log.d(TAG, "[read] serviceItemsList: " + serviceItemList.size());
						final int requiredHour = cursor.getInt(requiredHourIndex);
						final int requiredMinute = cursor.getInt(requiredMinuteIndex);

						final BookingRecord data = new BookingRecord(id, name, sex, year, month, day, hour, minute, phoneNumber,
								serviceItemList, requiredHour, requiredMinute);
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
			protected void onPostExecute(ArrayList<BookingRecord> result) {
				Log.d(TAG, String.format("[readBookingRecord] dataList: %d", result != null ? result.size() : 0));
				mBookingList = result;
				for (OnDateChangedListener listener : mOnDateChangedListener) {
					listener.onDataReady();
				}
			}
		}.execute();
	}

	public void writeBookingRecord(final BookingRecord updateData) {
		BookingRecord tempData = null;
		for (BookingRecord data : mBookingList) {
			if (isTheSameDate(updateData, data)) {
				tempData = data;
				break;
			}
		}
		// update or add in memory
		final BookingRecord targerData = tempData;
		final boolean add = targerData == null;
		if (add) {
			mBookingList.add(updateData);
		} else {
			targerData.setTimeSheetItem(updateData);
		}
		// write database
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				RestClient restClient = new RestClient();
				try {
					final Hashtable<String, String> headers = new Hashtable<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Content-Type", "application/json");
					final String stringBody = getPostBody(updateData.name, updateData.sex, updateData.year, updateData.month,
							updateData.day, updateData.hourOfDay, updateData.minute, updateData.phoneNumber, updateData.serviceType,
							updateData.requiredHour, updateData.requiredMinute);
					String response = restClient.post(SERVER_RECORD_URL, stringBody, headers);
					return response;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, String.format("[writeBookingRecord] add: %b, dataList: %d", add, mBookingList.size()));
				for (OnDateChangedListener listener : mOnDateChangedListener) {
					listener.onDataChanged();
				}
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	private String getPostBody(String name, String sex, int year, int month, int day, int hourOfDay, int minute, String phoneNumber,
			ArrayList<String> serviceType, int requiredHour, int requiredMinute) {
		try {
			JSONObject record = new JSONObject();
			record.put("name", name);
			record.put("sex", sex);
			record.put("year", year);
			record.put("month", month);
			record.put("day", day);
			record.put("hourOfDay", hourOfDay);
			record.put("minute", minute);
			record.put("phoneNumber", phoneNumber);

			final int serviceTypeSize = serviceType.size();
			Log.d(TAG, "[getPostBody] serviceTypeSize: " + serviceTypeSize);
			final StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < serviceTypeSize; ++i) {
				Log.d(TAG, "[getPostBody] parts[" + i + "]: " + serviceType.get(i));
				final String type = serviceType.get(i);
				// add the separated string ", " if it is first item added into string builder
				if (!"".equals(sb.toString())) {
					sb.append(BookingRecordManager.SEPARATED_STRING);
				}
				sb.append(type);
			}
			Log.d(TAG, "[getPostBody] out: " + sb.toString());
			record.put("serviceType", sb.toString());
			record.put("requiredHour", requiredHour);
			record.put("requiredMinute", requiredMinute);

			JSONObject body = new JSONObject();
			body.put("record", record);

			String result = body.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isTheSameDate(BookingRecord data1, BookingRecord data2) {
		return data1.year == data2.year && data1.month == data2.month && data1.day == data2.day && data1.hourOfDay == data2.hourOfDay
				&& data1.minute == data2.minute;
	}

	public void setOnDataChangedListener(OnDateChangedListener listener) {
		mOnDateChangedListener.add(listener);
	}

	public void removeOnDataChangedListener(OnDateChangedListener listener) {
		mOnDateChangedListener.remove(listener);
	}

	public ArrayList<BookingRecord> getBookingListByYear(int year) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mBookingList != null) {
			for (BookingRecord timeSheet : mBookingList) {
				if (year == timeSheet.year) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingRecord> getBookingListByMonth(int year, int month) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mBookingList != null) {
			for (BookingRecord timeSheet : mBookingList) {
				if (year == timeSheet.year && month == timeSheet.month) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingRecord> getBookingListByDate(int year, int month, int date) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mBookingList != null) {
			for (BookingRecord timeSheet : mBookingList) {
				if (year == timeSheet.year && month == timeSheet.month && date == timeSheet.day) {
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
