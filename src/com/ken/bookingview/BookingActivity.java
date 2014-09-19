package com.ken.bookingview;

import java.util.ArrayList;

import com.ken.bookingview.BookingProfileItem.ServiceItems;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

public class BookingActivity extends Activity {

	private final Handler mHandler = new Handler();

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);

		BookingManager.init(this);

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setAdapter(new TimeSheetAdapter(this, 2014, 9, 19));
		mListView.setVelocityScale(0.5f);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// testing
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				TimeSheetAdapter adapter = (TimeSheetAdapter) mListView.getAdapter();
				ArrayList<ServiceItems> serviceList = new ArrayList<ServiceItems>();
				serviceList.add(ServiceItems.洗髮);
				adapter.addOrUpdateTimeSheet(new TimeSheetItem("xxxx", 2014, 9, 19, 3, 25, "0955555555", serviceList, "1h"));
			}
		}, 2000);
	}
}
