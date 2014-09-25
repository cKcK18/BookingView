package com.ken.bookingview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = DateAdapter.class.getSimpleName();

	private static class ViewHolder {
		TextView dayOfWeekView;
		TextView dayView;
	}

	private final Context mContext;
	private int mCalendarViewWidth = -1;

	public DateAdapter(Context context) {
		mContext = context;
		mCalendarViewWidth = context.getResources().getDisplayMetrics().widthPixels
				/ HorizontalListView.VISIBLE_DATE_COUNT;
	}

	@Override
	public int getCount() {
		return DateUilities.getTotalDays();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// reuse or create view
		ViewHolder holder;
		if (convertView == null) {
			final LayoutParams lp = new LayoutParams(mCalendarViewWidth, LayoutParams.WRAP_CONTENT);
			convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_date_view, parent, false);
			convertView.setLayoutParams(lp);

			holder = new ViewHolder();
			final LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(mCalendarViewWidth,
					LayoutParams.WRAP_CONTENT);
			holder.dayOfWeekView = (TextView) convertView.findViewById(R.id.calendar_day_of_week);
			holder.dayOfWeekView.setLayoutParams(llp);
			holder.dayView = (TextView) convertView.findViewById(R.id.calendar_day);
			holder.dayView.setLayoutParams(llp);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Calendar calendar = DateUilities.getCalendarByIndex(position);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.US);
		final String shortString = dateFormat.format(calendar.getTime()).toUpperCase();
		holder.dayOfWeekView.setText(shortString);
		holder.dayView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		// DEBUG
//		holder.dayView.setText(String.valueOf(position % 100));
		// normally, we don't have to call the invalidate function because of horizontal list view bug.
		 convertView.invalidate();

		return convertView;
	}
}