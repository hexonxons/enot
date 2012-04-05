package app.tascact.manual.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.PopupWindow;
import app.tascact.manual.Markup;

public class PageReaderView extends HorizontalScrollView {
	private static final int CAСHE_SIZE = 5;
	private static final int CACHE_OFFSET = 2;

	private View pages[];
	private Markup markup;
	private FrameLayout innerWrapper;
	private GestureDetector gestureDecoder;
	private int displayXOffset;
	private int displayYOffset;
	private int pageWidth = 0;
	private int pageHeight = 0;
	private int pageToDisplay;
	private int leftCacheBorder;
	private int rightCacheBorder;
	private boolean firstTime = true;
	
	// Manual Controls
	private PopupWindow mLeftControl = null;
	private PopupWindow mRightControl = null;
	// Thread for control disappear of manual controls
	private ControlAliveThread mThread = null;
	
	/**
	 * Number of item (0-based) that is brought to front. This one must be
	 * changed only with changeActiveItem method.
	 */
	private int activeItem;

	/**
	 * @param context
	 * @param markup
	 * @param pageToDisplay
	 *            page to display
	 */
	public PageReaderView(Context context, Markup markup, int pageToDisplay) {
		super(context);

		this.pageToDisplay = pageToDisplay;
		setClickable(true);
		setFocusableInTouchMode(true);
		setFocusable(true);
		requestFocus();

		this.markup = markup;
		FrameLayout outerWrapper = new FrameLayout(context);
		innerWrapper = new FrameLayout(context);
		
		outerWrapper.addView(innerWrapper);
		addView(outerWrapper);
		innerWrapper.setBackgroundColor(Color.WHITE);
		pages = new View[markup.getPageNumber()];

		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);

		gestureDecoder = new GestureDetector(context,
				new SoftScrollOnGestureListener());
		
		mLeftControl = new PopupWindow(new PageControlView(this.getContext(), true), 0, 0);
		mRightControl = new PopupWindow(new PageControlView(this.getContext(), false), 0, 0);
		mLeftControl.setOutsideTouchable(false);
		mRightControl.setOutsideTouchable(false);
		mThread = new ControlAliveThread(mLeftControl, mRightControl);

		
		setOverScrollMode(OVER_SCROLL_NEVER);
		setDrawingCacheEnabled(true);
		setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
		setPersistentDrawingCache(PERSISTENT_SCROLLING_CACHE);
	}

	public PageReaderView(Context context, Markup markup) {
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
	 * Instantly displays specified page. If the item is out of range displays
	 * the closest.
	 * @param newActivePage
	 * @return number of page the brought up.
	 */
	public int setPage(int pageNumber) {
		changeActiveItem(pageNumber - 1);
		scrollTo(activeItem * pageWidth + displayXOffset, 0);
		return activeItem + 1;
	}

	
	/** 
	 * Slides smoothly to the next page.
	 * @return number of page the brought up. 
	 */
	public int nextPage() {
		changeActiveItem(activeItem + 1);
		smoothScrollTo(activeItem * pageWidth + displayXOffset, 0);
		return activeItem + 1;
	}

	
	/** 
	 * Slides smoothly to the next page.
	 * @return number of page the brought up. 
	 */
	public int prevPage() {
		changeActiveItem(activeItem - 1);
		smoothScrollTo(activeItem * pageWidth + displayXOffset, 0);
		return activeItem + 1;
	}
	
	/**
	 * Stabilize current page
	 */
	
	public void StableCurrPage() 
	{
		smoothScrollTo(activeItem * pageWidth, 0);
	}
	
	public void setListeners(OnTouchListener NextPageListener,
							 OnTouchListener PrevPageListener,
							 OnTouchListener ContentsListener)
	{
		((PageControlView)mLeftControl.getContentView()).setListeners(ContentsListener, PrevPageListener);
		((PageControlView)mRightControl.getContentView()).setListeners(ContentsListener, NextPageListener);
	}

	/** 
	 * Sets specified item as active and updates cached views. If item is out of
	 * range - sets closest item that is in the range.
	 * @param newActiveItem base element for cache 0-based. 
	 */
	protected void changeActiveItem(int newActiveItem) {

		if (newActiveItem >= pages.length)
			newActiveItem = pages.length - 1;

		if (newActiveItem < 0)
			newActiveItem = 0;

		//if (newActiveItem < leftCacheBorder + 1
		//		|| newActiveItem > rightCacheBorder - 1) {

			int newLeftCacheBorder = Math.max(0, newActiveItem - CACHE_OFFSET);
			int newRightCacheBorder = Math.min(pages.length, newActiveItem
					+ CAСHE_SIZE - CACHE_OFFSET);	
			
			// Adding new cache
			for (int i = newLeftCacheBorder; i < newRightCacheBorder; ++i) {
				if (pages[i] == null) {
					// Defining page's left offset
					LayoutParams params = new LayoutParams(pageWidth,
							pageHeight, Gravity.TOP);
					params.leftMargin = pageWidth * i;
					params.topMargin = displayYOffset;

					// Adding page
					pages[i] = markup.getPageView(i + 1);
					innerWrapper.addView(pages[i], params);
				}
			}

			// Invalidating old cache
			for (int i = leftCacheBorder; i < rightCacheBorder; ++i) {
				if (!(newLeftCacheBorder <= i && i < newRightCacheBorder)) {
					if (pages[i] != null) {
						innerWrapper.removeView(pages[i]);
						pages[i] = null;
					}
				}
			}

			//requestLayout();

			leftCacheBorder = newLeftCacheBorder;
			rightCacheBorder = newRightCacheBorder;
		//}
		activeItem = newActiveItem;
	}
	
	public void updateTimer()
	{
		mThread.updateTime();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		dismissControls();
		// Evaluating new page size
		// If screen size is wider
		if (w * markup.getHeight() >= h * markup.getWidth()) 
		{
			pageHeight = w * markup.getHeight() / markup.getWidth() ;
			pageWidth = w;
			displayXOffset = 0;
			displayYOffset = 0;
		}
		// if screen size is thinner
		else 
		{
			pageWidth = w;
			pageHeight = w * markup.getHeight() / markup.getWidth();
			displayXOffset = 0;
			displayYOffset = (h - pageHeight) / 2;
		}

		innerWrapper.setLayoutParams(new LayoutParams(markup.getPageNumber()
				* pageWidth, pageHeight));
				
		mLeftControl.setWidth(80);
		mLeftControl.setHeight(h);
		
		mRightControl.setWidth(80);
		mRightControl.setHeight(h);
		
		// Scrolling to the current active element
		changeActiveItem(activeItem);
		scrollTo(pageWidth * activeItem, 0);
	}

	public class ControlAliveThread extends Thread
	{
	    private boolean mRun = false;
		private long mStartTime;
		private PopupWindow mLeft = null;
		private PopupWindow mRight = null;
		private static final int POPUP_DISMISS_DELAY = 2000;
		
	    public ControlAliveThread(PopupWindow left, PopupWindow right)
	    {
	    	mLeft = left;
	    	mRight = right;
	    }
	    
	    public void setRunning(boolean run)
	    {
	        mRun = run;
	    }
	    
	    public void updateTime()
	    {
	    	mStartTime = System.currentTimeMillis();
	    }
	    
	    @Override
	    public void run() 
	    {  
	        while (mRun) 
	        {
	        	if(System.currentTimeMillis() - mStartTime > POPUP_DISMISS_DELAY)
	        	{
	        		mRun = false;
	        		if(mLeft != null && mRight != null)
	        		{
	        			mLeft.dismiss();
	        			mRight.dismiss();
	        		}
	        	}
	        }
	    }
	}
	
	public void dismissControls()
	{
		if(mLeftControl != null && mLeftControl.isShowing())
			mLeftControl.dismiss();
		if(mRightControl != null && mRightControl.isShowing())
			mRightControl.dismiss();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
		mThread.updateTime();
		
		if(!mLeftControl.isShowing() || !mRightControl.isShowing())
		{
			mLeftControl.showAtLocation(this, Gravity.LEFT | Gravity.TOP, 0, 0);
			mRightControl.showAtLocation(this, Gravity.RIGHT | Gravity.TOP, 0, 0);
			if(mThread.getState().equals(Thread.State.NEW))
				mThread.start();
			if(mThread.getState().equals(Thread.State.TERMINATED))
			{
				mThread = new ControlAliveThread(mLeftControl, mRightControl);
				mThread.updateTime();
				mThread.start();
			}
			mThread.setRunning(true);
		}
		
		if (gestureDecoder.onTouchEvent(event))
		{
			return true;
		}
		
		
		
		if (event.getAction() == (MotionEvent.ACTION_UP))
		{
			int newActiveItem = (getScrollX() + pageWidth / 2) / pageWidth;
			changeActiveItem(newActiveItem);
			smoothScrollTo(activeItem * pageWidth + displayXOffset, 0);
			return true;
		}
		return true;
	}


	class SoftScrollOnGestureListener extends SimpleOnGestureListener
	{
		private final int SWIPE_MIN_SPEED = 500;
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY)
		{
			try 
			{
				if (velocityX < -SWIPE_MIN_SPEED)
				{
					nextPage();
					return true;
				}
				else if (velocityX > SWIPE_MIN_SPEED)
				{
					prevPage();
					return true;
				}
				
				StableCurrPage();
				return true;

			} 
			catch (Exception e) 
			{
				Log.e("Fling", "Error processing the Fling event", e);
			}
			return false;
		}
	}
}