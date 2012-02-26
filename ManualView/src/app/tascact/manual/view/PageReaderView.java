package app.tascact.manual.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import app.tascact.manual.PageView;
import app.tascact.manual.R;
import app.tascact.manual.XMLResources;

public class PageReaderView extends HorizontalScrollView {
	private static final int CAСHE_SIZE = 5;
	private static final int CACHE_OFFSET = 2;

	private PageView pages[];
	private XMLResources markup;
	private FrameLayout innerWrapper;
	private int pageWidth;
	private int pageToDisplay;
	private boolean firstTime = true;
	/** 
	 * Number of item (0-based) that is brought to front.
	 * This one must be changed only with changeActiveItem method.
	 */
	private int activeItem;
	
	/**
	 * @param context
	 * @param markup
	 * @param pageToDisplay page to display
	 */
	public PageReaderView(Context context, XMLResources markup, int pageToDisplay) {
		super(context);
		
		this.pageToDisplay = pageToDisplay;
		setClickable(true);
		setFocusableInTouchMode(true);
		setFocusable(true);
		requestFocus();
		
		this.markup = markup;
		innerWrapper = new FrameLayout(context);	
		pages = new PageView[markup.getPageNumber()];
		addView(innerWrapper);
		
		// Because scroll view doesn't scroll further than the most left picture
		// and the most right picture this shit is needed. I still can't quite
		// figure out why it doesn't work without it (we firstly add elements
		// which we scroll to).
		// TODO research this fucking shit.

		pageWidth = 800;
		
		ImageView beforeEverything = new ImageView(context);
		ImageView afterEverything = new ImageView(context);
		beforeEverything.setBackgroundResource(R.drawable.prev);
		afterEverything.setBackgroundResource(R.drawable.prev);
		LayoutParams p1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
		LayoutParams p2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
		p1.leftMargin = -100;
		p2.leftMargin = 800 * pages.length + 1;
		innerWrapper.addView(beforeEverything, p1);
		innerWrapper.addView(afterEverything, p2);
		
		//setPage(pageToDisplay);		
	}
	
	/**
	 * Create reader ready to show the first page.
	 * @param context
	 * @param markup
	 */
	public PageReaderView(Context context, XMLResources markup) {
		this(context, markup, 1);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (firstTime) {
			setPage(pageToDisplay);
			firstTime = false;
		}
	}
	
	/**
	 * Instantly displays specified page. 
	 * If the item is out of range displays the closest.
	 * @param newActivePage
	 * @return number of page the brought up.
	 */
	public int setPage(int pageNumber) {
		changeActiveItem(pageNumber - 1);
		scrollTo(activeItem * pageWidth, 0);
		smoothScrollTo(activeItem * pageWidth, 0);
		invalidate();
		Log.d("Scroll", "Current scroll x offset is " + Integer.toString(getScrollX()));
		return activeItem + 1;
	}
	
	/** 
	 * Slides smoothly to the next page. 
	 * @return number of page the brought up.
	 */
	public int nextPage() {
	
		changeActiveItem(activeItem + 1);
		smoothScrollTo(activeItem * pageWidth, 0);
		return activeItem + 1;
	}

	/** 
	 * Slides smoothly to the next page. 
	 * @return number of page the brought up.
	 */
	public int prevPage() {
		changeActiveItem(activeItem - 1);
		smoothScrollTo(activeItem * pageWidth, 0);
		return activeItem + 1;
	}
	
	/**
	 * Sets specified item as active and updates cached views.
	 * If item is out of range - sets closest item that is in the
	 * range.
	 * @param newActiveItem base element for cache 0-based.
	 */
	protected void changeActiveItem(int newActiveItem) {
		
		if (newActiveItem >= pages.length)
			newActiveItem = pages.length - 1;
		
		if (newActiveItem < 0)
			newActiveItem = 0;
				
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
				if (pages[i] != null) {
					innerWrapper.removeView(pages[i]);
					pages[i] = null;
				}
			}
		}

		// Adding new cache
		for (int i = newLeftCacheBorder; i < newRightCacheBorder; ++i) {
			if (pages[i] == null) {
				// Defining page's left offset
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
				params.leftMargin = pageWidth * i;

				// Adding page
				pages[i] = new PageView(getContext(), markup, i+1);
				innerWrapper.addView(pages[i], params);
			}
		}
		
		activeItem = newActiveItem;
		
		// DEBUG
		String a = new String();
		for (int i = 0; i < pages.length; ++i) {
			a += (pages[i] == null)? '0' : '1';

		}
		Log.d("Scroll", "Active element is " + Integer.toString(activeItem));
		Log.d("Scroll", "Current scroll x offset is " + Integer.toString(getScrollX()));
		Log.d("Scroll", "Cache before changing is " + a);
		// DEBUG ENDS

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		pageWidth = w;
		
		// Resizing width of all cached pages
		int leftCacheBorder = Math.max(0, activeItem - CACHE_OFFSET);
		int rightCacheBorder = Math.min(pages.length, activeItem
				+ CAСHE_SIZE - CACHE_OFFSET);

		for (int i = leftCacheBorder; i < rightCacheBorder; ++i) {
			if (pages[i] != null) {
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
				params.leftMargin = pageWidth * i;
				pages[i].setLayoutParams(params);
			}
		}
		
		// Scrolling to the current active element
		scrollTo(pageWidth * activeItem, 0);
		Log.d("Scroll", "Current offset is " + Integer.toString(getScrollX()));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int newActiveItem = (getScrollX() + pageWidth / 2) / pageWidth;
		changeActiveItem(newActiveItem);
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			smoothScrollTo(activeItem * pageWidth, 0);
			return true;
		}
		return super.onTouchEvent(event);
	}
}


//class SoftScrollOnGestureListener extends SimpleOnGestureListener {
//@Override
//public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//		float velocityY) {
//	try {
//		// right to left
//		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//			changeActiveItem((activeItem < (pages.length - 1)) ? activeItem + 1
//					: pages.length - 1);
//			smoothScrollTo(width * activeItem, 0);
//			return true;
//		}
//		else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//			changeActiveItem((activeItem > 0) ? activeItem - 1 : 0);
//			smoothScrollTo(width * activeItem, 0);
//
//			return true;
//		}
//	} catch (Exception e) {
//		Log.e("Fling", "Error processing the Fling event", e);
//	}
//	return false;
//}
//}
