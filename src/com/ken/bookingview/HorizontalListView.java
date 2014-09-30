/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.ken.bookingview;

import java.util.LinkedList;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class HorizontalListView extends AdapterView<ListAdapter> {

	public static final int VISIBLE_DATE_COUNT = 7;
	public static final int VISIBLE_DATE_IN_CENTER = VISIBLE_DATE_COUNT / 2;

	// simulate fast scrolling through setSelection()
	private static final int FAST_SCROLLING_COUNT = 65;
	private static final int RESERVED_VIEW_COUNT = 20;

	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mInCenterPositionX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	private int mSelectedPosition;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnSelectedItemChangedListener mOnSelectedItemChanged;
	private boolean mDataChanged = false;
	private boolean mSimulateFastScrolling = false;
	private int mSimulateNextX = -1;

	// scroll state
	private boolean mOnScroll = false;
	private boolean mOnFling = false;
	private boolean mActionUp = true;

	public interface OnSelectedItemChangedListener {
		void onSelectedItemChanged(int dateIndex);
	}

	private static class ScrollInterpolator implements Interpolator {
		public ScrollInterpolator() {
		}

		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1;
		}
	}

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mInCenterPositionX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mSelectedPosition = 0;
		mScroller = new Scroller(getContext(), new ScrollInterpolator());
		mGesture = new GestureDetector(getContext(), mOnGesture);
		setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			@Override
			public void onChildViewRemoved(View parent, View child) {
				((DateItemView) child).drawFocus(false);
			}

			@Override
			public void onChildViewAdded(View parent, View child) {
			}
		});
	}

	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	public void setOnScrollChangedListener(OnSelectedItemChangedListener listener) {
		mOnSelectedItemChanged = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (HorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return getChildAt(VISIBLE_DATE_IN_CENTER);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset() {
		initView();
		// setSelection(DateUtilities.getIndexOfToday());
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		Log.d("kenchen", String.format("[setSelection] pos: %d -> %d", mSelectedPosition, position));
		if (position == mSelectedPosition) {
			return;
		}
		// adjust the position and let the position limit in (3, MAX - 3)
		final int totalPosition = mAdapter.getCount();
		final int adjustPosition;
		if (position < VISIBLE_DATE_IN_CENTER) {
			adjustPosition = VISIBLE_DATE_IN_CENTER;
		} else if (position > totalPosition - VISIBLE_DATE_IN_CENTER) {
			adjustPosition = totalPosition - VISIBLE_DATE_IN_CENTER;
		} else {
			adjustPosition = position;
		}

		if (getChildCount() != 0) {
			final int width = getChildAt(0).getWidth();
			final int inCenterPosition = adjustPosition;
			final int leftPosition = inCenterPosition - VISIBLE_DATE_IN_CENTER;
			final int distanceX = width * leftPosition;
			final int diff = inCenterPosition - mSelectedPosition;
			final int reservedX = RESERVED_VIEW_COUNT * width;
			// Log.d("kenchen", String.format("[setSelection] pos: %d -> %d", mCurrentPosition, position));
			// simulate fast scrolling
			if (diff > FAST_SCROLLING_COUNT) {
				mSimulateNextX = distanceX - reservedX;
				mLeftViewIndex = leftPosition - RESERVED_VIEW_COUNT - 2;
				mRightViewIndex = leftPosition - RESERVED_VIEW_COUNT + VISIBLE_DATE_COUNT;
				mSimulateFastScrolling = true;
			} else if (diff < -FAST_SCROLLING_COUNT) {
				// TODO implement
			}
			mScroller.startScroll(mNextX, 0, distanceX - mNextX, 0, 300);
			requestLayout();
		}
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);

		// add a simulate view with bigger width.
		final int widthSpec;
		if (mSimulateFastScrolling) {
			widthSpec = MeasureSpec.makeMeasureSpec(mSimulateNextX, MeasureSpec.EXACTLY);
		} else {
			widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
		}
		child.measure(widthSpec, MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null) {
			return;
		}

		if (mDataChanged) {
			int oldCurrentX = mCurrentX;
			initView();
			// setSelection(DateUtilities.getIndexOfToday());
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if (mScroller.computeScrollOffset()) {
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		if (mNextX < 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if (mNextX > mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		final int childWidth = getChildAt(0).getWidth();
		mCurrentX = mNextX;
		mInCenterPositionX = mCurrentX + VISIBLE_DATE_IN_CENTER * childWidth;
		mSelectedPosition = mInCenterPositionX / childWidth;

		if (!mScroller.isFinished()) {
			post(new Runnable() {
				@Override
				public void run() {
					requestLayout();
					mSimulateFastScrolling = false;
				}
			});
		} else if (mActionUp) {
			if (mOnSelectedItemChanged != null) {
				// draw a overlay rectangle
				for (int i = 0; i < getChildCount(); ++i) {
					final boolean focus = i == VISIBLE_DATE_IN_CENTER;
					((DateItemView) getChildAt(i)).drawFocus(focus);
				}
				mOnSelectedItemChanged.onSelectedItemChanged(mSelectedPosition);
			}
		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}

	private void fillListRight(int rightEdge, final int dx) {
		while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if (mRightViewIndex == mAdapter.getCount() - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}
			mRightViewIndex++;
		}
	}

	private void fillListLeft(int leftEdge, final int dx) {
		while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}

	public synchronized void scrollTo(int x) {
		mScroller.startScroll(mNextX, 0, x - mNextX, 0);
		requestLayout();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final boolean handled = mGesture.onTouchEvent(ev);
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mActionUp = false;
			mOnScroll = mOnFling = false;
			break;
		case MotionEvent.ACTION_UP:
			if (mOnScroll) {
				final int unit = getChildAt(0).getWidth();
				final int finalX = mNextX;
				final int remainer = finalX % unit;
				final int offset = remainer < unit / 2 ? -remainer : unit - remainer;
				mScroller.startScroll(mNextX, 0, offset, 0, 100);
				requestLayout();
			} else if (mOnFling) {
				// nothing to do
			}
			mActionUp = true;
			break;
		}
		return handled;
	}

	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		synchronized (HorizontalListView.this) {
			mOnScroll = false;
			mOnFling = true;

			mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
			final int unit = getChildAt(0).getWidth();
			final int finalX = mScroller.getFinalX();
			final int remainer = finalX % unit;
			final int offset = remainer < unit / 2 ? -remainer : unit - remainer;
			final int adjustFinalX = finalX + offset;
			mScroller.setFinalX(adjustFinalX);
		}
		requestLayout();

		return true;
	}

	protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		synchronized (HorizontalListView.this) {
			mOnScroll = true;
			mNextX += (int) distanceX;
		}
		requestLayout();

		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return HorizontalListView.this.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Rect viewRect = new Rect();
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if (viewRect.contains((int) e.getX(), (int) e.getY())) {
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
			return true;
		}
	};

}
