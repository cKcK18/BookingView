package com.ken.bookingview;

import java.lang.ref.WeakReference;

import android.app.Application;

public class BookingApplication extends Application {

	private WeakReference<BookingProvider> mBookingProvider;

	@Override
	public void onCreate() {
		super.onCreate();
		BookingRecordManager.init(this);
	}

	void setBookingProvider(BookingProvider provider) {
		mBookingProvider = new WeakReference<BookingProvider>(provider);
	}

	BookingProvider getBookingProvider() {
		return mBookingProvider.get();
	}
}
