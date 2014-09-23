/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ken.bookingview;

import java.io.File;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class BookingProvider extends ContentProvider {

	private static final String TAG = BookingProvider.class.getSimpleName();
	private static final boolean LOGD = true;

	private static final String DATABASE_NAME = "booking.db";

	private static final int DATABASE_VERSION = 1;

	static final String AUTHORITY = "com.ken.bookingview";

	static final String TABLE_NAME = "booking";
	static final String PARAMETER_NOTIFY = "notify";

	static final String COLUMN_ID = "_id";
	static final String COLUMN_NAME = "name";
	static final String COLUMN_YEAR = "year";
	static final String COLUMN_MONTH = "month";
	static final String COLUMN_DATE = "date";
	static final String COLUMN_HOUR = "hour";
	static final String COLUMN_MINUTE = "minute";
	static final String COLUMN_PHONE_NUMBER = "phoneNumber";
	static final String COLUMN_SERVICE_ITEMS = "serviceItems";
	static final String COLUMN_REQUIRED_TIME = "requiredTime";
	
	private long mLastID = -1;

	/**
	 * The content:// style URL for this table
	 */
	static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "?" + PARAMETER_NOTIFY
			+ "=true");

	/**
	 * The content:// style URL for this table. When this Uri is used, no notification is sent if the content changes.
	 */
	static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "?"
			+ PARAMETER_NOTIFY + "=false");

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		// open to read and write
		mOpenHelper.getWritableDatabase();
		// set a reference to outer
		((BookingApplication) getContext()).setBookingProvider(this);
		return true;
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments args = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(args.where)) {
			return "vnd.android.cursor.dir/" + args.table;
		} else {
			return "vnd.android.cursor.item/" + args.table;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.table);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
		result.setNotificationUri(getContext().getContentResolver(), uri);

		if (mLastID == -1) {
			mLastID = getLastID(result);
		}

		return result;
	}

	private long getLastID(Cursor cursor) {
		int lastID = -1;
		if (cursor == null || cursor.getCount() == 0) {
			return lastID;
		}
		final boolean succeeded = cursor.moveToLast();
		if (succeeded) {
			final int idIndex = cursor.getColumnIndexOrThrow(BookingProvider.COLUMN_ID);
			lastID = cursor.getInt(idIndex);
		}
		return lastID;
	}

	public long generateNewId() {
		if (mLastID < 0) {
			throw new RuntimeException("Error: last id was not initialized");
		}
		mLastID += 1;
		return mLastID;
	}

	private static long dbInsertAndCheck(DatabaseHelper helper, SQLiteDatabase db, String table, String nullColumnHack,
			ContentValues values) {
		if (!values.containsKey(BaseColumns._ID)) {
			throw new RuntimeException("Error: attempting to add item without specifying an id");
		}
		return db.insert(table, nullColumnHack, values);
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (LOGD) Log.d(TAG, String.format("[insert] uri: %s.   cv: %s", uri.toString(), initialValues));
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = dbInsertAndCheck(mOpenHelper, db, args.table, null, initialValues);
		if (rowId <= 0) return null;

		uri = ContentUris.withAppendedId(uri, rowId);
		sendNotify(uri);

		return uri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		if (LOGD) Log.d(TAG, String.format("[bulkInsert] uri: %s", uri.toString()));
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			int numValues = values.length;
			for (int i = 0; i < numValues; i++) {
				if (dbInsertAndCheck(mOpenHelper, db, args.table, null, values[i]) < 0) {
					return 0;
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		sendNotify(uri);
		return values.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (LOGD) Log.d(TAG, String.format("[delete] uri: %s", uri.toString()));
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.delete(args.table, args.where, args.args);
		if (count > 0) sendNotify(uri);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (LOGD) Log.d(TAG, String.format("[update] uri: %s", uri.toString()));
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.update(args.table, values, args.where, args.args);
		if (count > 0) sendNotify(uri);

		return count;
	}

	private void sendNotify(Uri uri) {
		String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
		if (notify == null || "true".equals(notify)) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	public void deleteDatabase() {
		// Are you sure? (y/n)
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final File dbFile = new File(db.getPath());
		mOpenHelper.close();
		if (dbFile.exists()) {
			SQLiteDatabase.deleteDatabase(dbFile);
		}
		mOpenHelper = new DatabaseHelper(getContext());
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
 
		@Override
		public void onCreate(SQLiteDatabase db) {
			if (LOGD) Log.d(TAG, "creating new booking database");

            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID +  " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_YEAR + " INTEGER NOT NULL," +
                    COLUMN_MONTH + " INTEGER NOT NULL," +
                    COLUMN_DATE + " INTEGER NOT NULL," +
                    COLUMN_HOUR + " INTEGER NOT NULL," +
                    COLUMN_MINUTE + " INTEGER NOT NULL," +
                    COLUMN_PHONE_NUMBER + " TEXT NOT NULL," +
                    COLUMN_SERVICE_ITEMS + " TEXT NOT NULL," +
                    COLUMN_REQUIRED_TIME + " TEXT NOT NULL" +
                    ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (LOGD) Log.d(TAG, "onUpgrade triggered");
		}
	}

	/**
	 * Build a query string that will match any row where the column matches anything in the values list.
	 */
	static String buildOrWhereString(String column, int[] values) {
		StringBuilder selectWhere = new StringBuilder();
		for (int i = values.length - 1; i >= 0; i--) {
			selectWhere.append(column).append("=").append(values[i]);
			if (i > 0) {
				selectWhere.append(" OR ");
			}
		}
		return selectWhere.toString();
	}

	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.table = url.getPathSegments().get(0);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException("WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(0);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				table = url.getPathSegments().get(0);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}
}
