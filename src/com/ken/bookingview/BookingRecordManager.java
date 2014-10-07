package com.ken.bookingview;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class BookingRecordManager {

	private static final String TAG = BookingRecordManager.class.getSimpleName();

	private static final String SERVER_URL = "http://106.187.42.254:3000";
	private static final String SERVER_ADD_URL = SERVER_URL + "/add";
	private static final String SERVER_UPDATE_URL = SERVER_URL + "/update";
	private static final String SERVER_DELETE_URL = SERVER_URL + "/delete";
	@SuppressWarnings("unused")
	private static final String SERVER_QUERY_URL = SERVER_URL + "/query";

	private static BookingRecordManager sManager;

	public interface OnRecordChangedListener {
		void onRecordReady();

		void onRecordChanged();
	}

	private ArrayList<BookingRecord> mRecordList;
	private ArrayList<OnRecordChangedListener> mOnRecordChangedListener;

	public static BookingRecordManager getInstance() {
		if (sManager == null) {
			sManager = new BookingRecordManager();
		}
		return sManager;
	}

	private BookingRecordManager() {
		mOnRecordChangedListener = new ArrayList<OnRecordChangedListener>();

		readBookingRecord();
	}

	private boolean isExist(BookingRecord targetRecord) {
		for (BookingRecord record : mRecordList) {
			if (isTheSameDate(targetRecord, record)) {
				return true;
			}
		}
		return false;
	}

	private void readBookingRecord() {
		new AsyncTask<Void, Void, ArrayList<BookingRecord>>() {
			@Override
			protected ArrayList<BookingRecord> doInBackground(Void... params) {
				final ArrayList<BookingRecord> recordList = new ArrayList<BookingRecord>();

				ArrayList<String> serviceType = new ArrayList<String>();
				serviceType.add("Long Hair");
				serviceType.add("Short Hair");

				BookingRecord record = new BookingRecord("ken chen", "male", 2014, 9, 7, 1, 15, "0985091642", serviceType, 2, 30);
				recordList.add(record);

				serviceType = new ArrayList<String>();
				serviceType.add("Dye");
				serviceType.add("Perm");

				record = new BookingRecord("winnie hsu", "female", 2014, 9, 6, 2, 15, "0985091242", serviceType, 3, 00);
				recordList.add(record);

//				final BookingRecord data = new BookingRecord(id, name, sex, year, month, day, hour, minute, phoneNumber, serviceItemList,
//						requiredHour, requiredMinute);
//				recordList.add(data);
				return recordList;
			}

			@Override
			protected void onPostExecute(ArrayList<BookingRecord> result) {
				Log.d(TAG, String.format("[readBookingRecord] dataList: %d", result != null ? result.size() : 0));
				mRecordList = result;
				for (OnRecordChangedListener listener : mOnRecordChangedListener) {
					listener.onRecordReady();
				}
			}
		}.execute();
	}

	public void addBookingRecord(final BookingRecord newRecord) {
		final boolean exist = isExist(newRecord);
		if (exist) {
			return;
		}
		// add in memory
		mRecordList.add(newRecord);

		// write database
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				RestClient restClient = new RestClient();
				try {
					final Hashtable<String, String> headers = new Hashtable<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Content-Type", "application/json");
					final String stringBody = getPostBody(newRecord.name, newRecord.sex, newRecord.year, newRecord.month, newRecord.day,
							newRecord.hourOfDay, newRecord.minute, newRecord.phoneNumber, newRecord.serviceType, newRecord.requiredHour,
							newRecord.requiredMinute);
					String response = restClient.post(SERVER_ADD_URL, stringBody, headers);
					return response;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, String.format("[addBookingRecord] dataList: %d", mRecordList.size()));
				for (OnRecordChangedListener listener : mOnRecordChangedListener) {
					listener.onRecordChanged();
				}
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	public void updateBookingRecord(final BookingRecord updateRecord) {
		final boolean exist = isExist(updateRecord);
		if (!exist) {
			return;
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
					final String stringBody = getPostBody(updateRecord.name, updateRecord.sex, updateRecord.year, updateRecord.month,
							updateRecord.day, updateRecord.hourOfDay, updateRecord.minute, updateRecord.phoneNumber,
							updateRecord.serviceType, updateRecord.requiredHour, updateRecord.requiredMinute);
					String response = restClient.post(SERVER_UPDATE_URL, stringBody, headers);
					return response;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, String.format("[updateBookingRecord] dataList: %d", mRecordList.size()));
				for (OnRecordChangedListener listener : mOnRecordChangedListener) {
					listener.onRecordChanged();
				}
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	public void deleteBookingRecord(final BookingRecord deleteRecord) {
		// delete in memory
		if (mRecordList != null) {
			mRecordList.remove(deleteRecord);
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
					final String stringBody = getPostBody(deleteRecord.name, deleteRecord.sex, deleteRecord.year, deleteRecord.month,
							deleteRecord.day, deleteRecord.hourOfDay, deleteRecord.minute, deleteRecord.phoneNumber,
							deleteRecord.serviceType, deleteRecord.requiredHour, deleteRecord.requiredMinute);
					String response = restClient.post(SERVER_DELETE_URL, stringBody, headers);
					return response;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, String.format("[writeBookingRecord] dataList: %d", mRecordList.size()));
				for (OnRecordChangedListener listener : mOnRecordChangedListener) {
					listener.onRecordChanged();
				}
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	private String getPostBody(String name, String sex, int year, int month, int day, int hourOfDay, int minute, String phoneNumber,
			ArrayList<String> serviceType, int requiredHour, int requiredMinute) {
		try {
			final String flatten = BookingRecord.flattenServiceType(serviceType);

			JSONObject record = new JSONObject();
			record.put("name", name);
			record.put("sex", sex);
			record.put("year", year);
			record.put("month", month);
			record.put("day", day);
			record.put("hourOfDay", hourOfDay);
			record.put("minute", minute);
			record.put("phoneNumber", phoneNumber);
			record.put("serviceType", flatten);
			record.put("requiredHour", requiredHour);
			record.put("requiredMinute", requiredMinute);

			String result = record.toString();
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

	public void setOnRecordChangedListener(OnRecordChangedListener listener) {
		mOnRecordChangedListener.add(listener);
	}

	public void removeOnRecordChangedListener(OnRecordChangedListener listener) {
		mOnRecordChangedListener.remove(listener);
	}

	public ArrayList<BookingRecord> getRecordListByYear(int year) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mRecordList != null) {
			for (BookingRecord timeSheet : mRecordList) {
				if (year == timeSheet.year) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingRecord> getRecordListByMonth(int year, int month) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mRecordList != null) {
			for (BookingRecord timeSheet : mRecordList) {
				if (year == timeSheet.year && month == timeSheet.month) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public ArrayList<BookingRecord> getRecordListByDate(int year, int month, int date) {
		final ArrayList<BookingRecord> list = new ArrayList<BookingRecord>();
		if (mRecordList != null) {
			for (BookingRecord timeSheet : mRecordList) {
				if (year == timeSheet.year && month == timeSheet.month && date == timeSheet.day) {
					list.add(timeSheet);
				}
			}
		}
		return list;
	}

	public void release() {
		mRecordList.clear();
		mRecordList = null;
		mOnRecordChangedListener.clear();
		mOnRecordChangedListener = null;
	}
}
