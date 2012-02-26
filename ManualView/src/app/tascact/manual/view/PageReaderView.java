package app.tascact.manual.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import app.tascact.manual.PageView;
import app.tascact.manual.XMLResources;

public class PageReaderView extends HorizontalScrollView {
//	private static final int SWIPE_MIN_DISTANCE = 100;
//	private static final int SWIPE_THRESHOLD_VELOCITY = 500;
	private static final int CAСHE_SIZE = 4;
	private static final int CACHE_OFFSET = 2;

	private PageView pages[];
	private XMLResources markup;
	private int width;
	private int activeItem;
//	private GestureDetector gestureDetector;
	private FrameLayout innerWrapper;
	
	public PageReaderView(Context context, XMLResources markup) {
		super(context);
		
		//setClickable(true);
		
		this.markup = markup;

		innerWrapper = new FrameLayout(context);	
//		gestureDetector = new GestureDetector(new SoftScrollOnGestureListener());
		pages = new PageView[markup.getPageNumber()];
		addView(innerWrapper);
		
	
		// TODO Hardcode (((
		width = 800;		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
	}

//	class SoftScrollOnGestureListener extends SimpleOnGestureListener {
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//				float velocityY) {
//			try {
//				// right to left
//				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//					changeActiveItem((activeItem < (pages.length - 1)) ? activeItem + 1
//							: pages.length - 1);
//					smoothScrollTo(width * activeItem, 0);
//					return true;
//				}
//				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//					changeActiveItem((activeItem > 0) ? activeItem - 1 : 0);
//					smoothScrollTo(width * activeItem, 0);
//
//					return true;
//				}
//			} catch (Exception e) {
//				Log.e("Fling", "Error processing the Fling event", e);
//			}
//			return false;
//		}
//	}

	

	/**
	 * Updates cache
	 * @param newActiveItem base element for cache
	 */
	protected void changeActiveItem(int newActiveItem) {
		if (newActiveItem == activeItem)
			return;
		
		// DEBUG
		String a = new String();
		for (int i = 0; i < pages.length; ++i) {
			a += (pages[i] == null)? '0' : '1';

		}
		Log.d("Scroll", "Active element is " + Integer.toString(newActiveItem));
		Log.d("Scroll", a);
		
		// Evaluating cache borders
		int oldLeftCacheBorder = Math.max(0, activeItem - CACHE_OFFSET);
		int newLeftCacheBorder = Math.max(0, newActiveItem - CACHE_OFFSET);
		int oldRightCacheBorder = Math.min(pages.length, activeItem
				+ CAСHE_SIZE - CACHE_OFFSET);
		int newRightCacheBorder = Math.min(pages.length, newActiveItem
				+ CAСHE_SIZE - CACHE_OFFSET);
		
		// Invalidating old cache
		for (int i = oldLeftCacheBorder; i < oldRightCacheBorder; ++i) {
			if (!(newLeftCacheBorder <= i && i < newRightCacheBorder)) {
				innerWrapper.removeView(pages[i]);
				pages[i] = null;
			}
		}

		// Adding new cache
		for (int i = newLeftCacheBorder; i < newRightCacheBorder; ++i) {
			if (pages[i] == null) {
				// Defining page's left offset
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
				params.leftMargin = width * i;

				// Adding page
				pages[i] = new PageView(getContext(), markup, i+1);
				innerWrapper.addView(pages[i], params);
			}
		}

		activeItem = newActiveItem;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int newActiveItem = (getScrollX() + width / 2) / width;
		changeActiveItem(newActiveItem);
		
		// Considering a gesture first
//		if (gestureDetector.onTouchEvent(event)) {
//			return true;
//		}
		// Considering up gesture 
		if (event.getAction() == MotionEvent.ACTION_UP) {
			smoothScrollTo(activeItem * width, 0);
			return true;
		}
		// Default behavior
		return super.onTouchEvent(event);
	}

	public void setPage(int newActivePage) {
		changeActiveItem(newActivePage - 1);
	}

}
