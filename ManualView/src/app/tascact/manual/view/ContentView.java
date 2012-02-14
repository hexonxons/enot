/*
 * ContentView �����
 * 
 * View ����������
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ContentView extends ScrollView {
	private GestureDetector mGestureDetector = null;
	private Scroller mScroller;
	private int mHeight = 0;
	private LinearLayout mMainTable = null;

	public ContentView(Context context) {
		super(context);

		// ���������� ������
		mGestureDetector = new GestureDetector(context, new GestureListener());
		mScroller = new Scroller(context);
		// this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		mMainTable = new LinearLayout(context);
		mMainTable.setOrientation(1);
		this.addView(mMainTable);
		// ���������� ����������
		setVerticalScrollBarEnabled(true);
	}

	// ��������� �������� ������
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ������ �������/������ ��� ������� ��
		// �����
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
		}
		// ��������� �����
		if (mGestureDetector.onTouchEvent(event))
			return true;

		return true;
	}

	@Override
	protected int computeVerticalScrollRange() {
		// ������ ������ ����� View
		mHeight = mMainTable.getBottom() - mMainTable.getTop();
		return mHeight;
	}

	public void addContextElem(android.view.View child) {
		// ��������� �������� ����������
		mMainTable.addView(child);
	};

	private class GestureListener extends SimpleOnGestureListener {
		// ���������� �������
		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2,
				float distanceX, float distanceY) {
			int newScrollY = getScrollY();

			if (getScrollY() < 0) {
				newScrollY = 0;
				distanceY = 0;
			} else if (getScrollY() > mHeight - getHeight())
				newScrollY = mHeight - getHeight();

			// ����������, �� �������
			// ������������
			int offset = newScrollY + (int) distanceY >= mHeight - getHeight() ? 0
					: (int) distanceY;
			// ������ ���������
			mScroller.startScroll(0, getScrollY(), 0, offset, 60);
			// ���������� ����������
			awakenScrollBars(mScroller.getDuration());

			// scrollBy(0, (int)distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			mScroller.fling(0, getScrollY(), 0, -(int) velocityY, 0, 0, 0,
					mHeight - getHeight());
			awakenScrollBars(mScroller.getDuration());
			return true;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int oldY = getScrollY();
			int y = mScroller.getCurrY();
			scrollTo(0, y);

			if (oldY != getScrollY()) {
				onScrollChanged(0, getScrollY(), 0, oldY);
			}

			postInvalidate();
		}
	}
}