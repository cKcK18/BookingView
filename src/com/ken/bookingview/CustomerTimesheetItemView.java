package com.ken.bookingview;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class CustomerTimesheetItemView extends View {

	@SuppressWarnings("unused")
	private static final String TAG = CustomerTimesheetItemView.class.getSimpleName();

	private static final int MAX_SERVICE_ITEM = 3;

	private int mIdentityWidth = -1;
	private int mDivisionWidth = -1;
	private int mGapBetweenDivisionAndContent = -1;
	private int mServiceItemWidth = -1;
	private int mServiceItemHeight = -1;
	private int mGapBetweenServiceItem = -1;
	private int mGapBetweenServiceItemAndContent = -1;

	// here is for drawing paint
	private Paint mIdentityPaint;
	private Paint mDivisionPaint;
	private Paint mServiceItemsBackgroundPaint;
	private Paint mServiceItemsPaint;
	private Paint mContentPaint;
	private Paint mFlagTextBackgroundPaint;
	private Paint mFlagTextPaint;

	// here is for each component position
	private Point mIdentityPoint;
	private Rect mDivisionRect;
	private ArrayList<Rect> mServiceListBackgroundRect;
	private ArrayList<Point> mServiceListPoint;
	private Point mContentPoint;
	private Path mFlagTextBackgroundPath;
	private Point mFlagTextPoint;

	// / here is for flag text position
	private int mFlagTextLeft;
	private int mFlagTextRight;
	private int mFlagTextTop;
	private int mFlagTextBottom;
	private int mFlagTextCenter;

	public CustomerTimesheetItemView(Context context) {
		this(context, null);
	}

	public CustomerTimesheetItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomerTimesheetItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setUpView(context);
	}

	protected void setUpView(Context context) {
		setUpDimension(context.getResources());
		setUpPaint();
	}

	private void setUpDimension(Resources resources) {
		mIdentityWidth = resources.getDimensionPixelSize(R.dimen.identity_width);
		mDivisionWidth = resources.getDimensionPixelSize(R.dimen.division_width);
		mGapBetweenDivisionAndContent = resources.getDimensionPixelSize(R.dimen.gap_between_division_and_content);
		mServiceItemWidth = resources.getDimensionPixelSize(R.dimen.service_item_width);
		mServiceItemHeight = resources.getDimensionPixelSize(R.dimen.service_item_height);
		mGapBetweenServiceItem = resources.getDimensionPixelSize(R.dimen.gap_between_service_item);
		mGapBetweenServiceItemAndContent = resources
				.getDimensionPixelSize(R.dimen.gap_between_service_item_and_content);

		mFlagTextLeft = resources.getDimensionPixelSize(R.dimen.flag_text_left);
		mFlagTextRight = resources.getDimensionPixelSize(R.dimen.flag_text_right);
		mFlagTextTop = resources.getDimensionPixelSize(R.dimen.flag_text_top);
		mFlagTextBottom = resources.getDimensionPixelSize(R.dimen.flag_text_bottom);
		mFlagTextCenter = resources.getDimensionPixelSize(R.dimen.flag_text_center);
	}

	private void setUpPaint() {
		mIdentityPaint = new Paint();
		mIdentityPaint.setAntiAlias(true);
		mIdentityPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mIdentityPaint.setTextSize(45.0f);
		mIdentityPaint.setColor(0xFF7A7A7A);
		mIdentityPaint.setTextAlign(Paint.Align.CENTER);

		mDivisionPaint = new Paint();
		mDivisionPaint.setColor(0x4CB2B2B2);

		mServiceItemsBackgroundPaint = new Paint();
		mServiceItemsBackgroundPaint.setStyle(Style.FILL);
		mServiceItemsBackgroundPaint.setColor(0xFF6063A8);

		mServiceItemsPaint = new Paint();
		mServiceItemsPaint.setAntiAlias(true);
		mServiceItemsPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mServiceItemsPaint.setTextSize(35.0f);
		mServiceItemsPaint.setColor(Color.WHITE);
		mServiceItemsPaint.setTextAlign(Paint.Align.CENTER);

		mContentPaint = new Paint();
		mContentPaint.setAntiAlias(true);
		mContentPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mContentPaint.setTextSize(45.0f);
		mContentPaint.setColor(0xFF6063A8);
		mContentPaint.setTextAlign(Paint.Align.LEFT);

		mFlagTextBackgroundPaint = new Paint();
		mFlagTextBackgroundPaint.setStyle(Style.FILL);
		mFlagTextBackgroundPaint.setColor(0xFFFFA042);

		mFlagTextPaint = new Paint();
		mFlagTextPaint.setAntiAlias(true);
		mFlagTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mFlagTextPaint.setTextSize(35.0f);
		mFlagTextPaint.setColor(Color.WHITE);
		mFlagTextPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		final int wholeHeight = getHeight();

		// calculate the point of the identity string and let it in the center.
		if (mIdentityPoint == null) {
			mIdentityPoint = new Point();
		}
		estimatePositionInCenter(mIdentityPoint, 0, 0, mIdentityWidth, wholeHeight, mIdentityPaint);

		// calculate the rectangle of the division
		if (mDivisionRect == null) {
			mDivisionRect = new Rect();
		}
		mDivisionRect.set(mIdentityWidth, 0, mIdentityWidth + mDivisionWidth, wholeHeight);

		// calculate the rectangles of the service list and the point of the brief contents
		final int paddingLeftForContent = mDivisionRect.right + mGapBetweenDivisionAndContent;

		if (mServiceListBackgroundRect == null) {
			mServiceListBackgroundRect = new ArrayList<Rect>(MAX_SERVICE_ITEM);
		}
		if (mServiceListPoint == null) {
			mServiceListPoint = new ArrayList<Point>(MAX_SERVICE_ITEM);
		}
		int serviceItemLeft = paddingLeftForContent;
		for (int i = 0; i < MAX_SERVICE_ITEM; ++i) {
			final Rect rect = new Rect(serviceItemLeft, mServiceItemHeight, serviceItemLeft + mServiceItemWidth,
					mServiceItemHeight * 2);
			mServiceListBackgroundRect.add(rect);

			final Point point = new Point();
			estimatePositionInCenter(point, rect.left, rect.top, mServiceItemWidth, mServiceItemHeight,
					mServiceItemsPaint);
			mServiceListPoint.add(point);

			serviceItemLeft += mServiceItemWidth + mGapBetweenServiceItem;
		}

		// calculate the point of the brief booking
		if (mContentPoint == null) {
			mContentPoint = new Point();
		}
		estimatePositionInCenter(mContentPoint, mDivisionRect.right + mGapBetweenDivisionAndContent, mServiceItemHeight
				* 2 + mGapBetweenServiceItemAndContent, 0, mServiceItemHeight, mContentPaint);

		// prepare the NEW tag
		if (mFlagTextBackgroundPath == null) {
			mFlagTextBackgroundPath = new Path();
		}
		mFlagTextBackgroundPath.moveTo(mFlagTextLeft, mFlagTextTop);
		mFlagTextBackgroundPath.lineTo(mFlagTextLeft, mFlagTextBottom);
		mFlagTextBackgroundPath.lineTo((mFlagTextLeft + mFlagTextRight) / 2, mFlagTextCenter);
		mFlagTextBackgroundPath.lineTo(mFlagTextRight, mFlagTextBottom);
		mFlagTextBackgroundPath.lineTo(mFlagTextRight, mFlagTextTop);
		mFlagTextBackgroundPath.lineTo(mFlagTextLeft, mFlagTextTop);
		mFlagTextBackgroundPath.close();

		if (mFlagTextPoint == null) {
			mFlagTextPoint = new Point();
		}
		estimatePositionInCenter(mFlagTextPoint, mFlagTextLeft, mFlagTextTop, mFlagTextRight - mFlagTextLeft,
				mFlagTextCenter - mFlagTextTop, mFlagTextPaint);
	}

	/*
	 * this is for text only estimating it's position in center
	 */
	private Point estimatePositionInCenter(Point point, int startX, int startY, int width, int height, Paint paint) {
		Paint.FontMetrics fm = paint.getFontMetrics();
		final int offsetX = (int) (width / 2);
		final int offsetY = (int) ((height - fm.ascent) / 2);
		point.set(startX + offsetX, startY + offsetY);
		return point;
	}

	private String identityToTime(int identity) {
		// just like the identity is 9, it will transform into _9:00
		return String.format("%2d:00", identity);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final Integer identity = (Integer) getTag(R.id.time_sheet_item_identity);
		final BookingData data = (BookingData) getTag(R.id.time_sheet_item_info);

		canvas.drawText(identityToTime(identity), mIdentityPoint.x, mIdentityPoint.y, mIdentityPaint);
		canvas.drawRect(mDivisionRect, mDivisionPaint);

		// check any booking by booking name
		final boolean hasBooking = data != null;
		if (hasBooking) {
			final ArrayList<String> serviceList = data.serviceItems;
			final int serviceItemCount = serviceList != null ? serviceList.size() : 0;
			// draw each service item
			for (int i = 0; i < serviceItemCount; ++i) {
				final String item = serviceList.get(i);

				final Rect rect = mServiceListBackgroundRect.get(i);
				canvas.drawRect(rect, mServiceItemsBackgroundPaint);

				final Point point = mServiceListPoint.get(i);
				canvas.drawText(item, point.x, point.y, mServiceItemsPaint);
			}

			// draw contents
			final String content = String.format("%02d:%02d  %s  %s", data.hourOfDay, data.minute, data.name,
					data.phoneNumber);
			canvas.drawText(content, mContentPoint.x, mContentPoint.y, mContentPaint);

			// draw NEW
			canvas.drawPath(mFlagTextBackgroundPath, mFlagTextBackgroundPaint);
			canvas.drawText(getResources().getString(R.string.stylish_booking_view_new), mFlagTextPoint.x, mFlagTextPoint.y,
					mFlagTextPaint);
		}

		// draw end line
		canvas.drawRect(getRight() - 5, 0, getRight(), getBottom(), mDivisionPaint);
	}
}
