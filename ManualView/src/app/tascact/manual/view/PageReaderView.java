package app.tascact.manual.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import app.tascact.manual.Markup;

public class PageReaderView extends HorizontalScrollView
{
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
	public PageReaderView(Context context, Markup markup, int pageToDisplay)
	{
		super(context);

		this.pageToDisplay = pageToDisplay;
		setClickable(true);
		setFocusableInTouchMode(true);
		setFocusable(true);
		requestFocus();

		this.markup = markup;
		FrameLayout outerWrapper = new FrameLayout(context);
		innerWrapper = new FrameLayout(context);
		
		outerWrapper.addView(innerWrapper, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(outerWrapper, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setBackgroundColor(Color.WHITE);
		pages = new View[markup.getPageNumber()];

		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);

		gestureDecoder = new GestureDetector(context,
				new SoftScrollOnGestureListener());
				
		setOverScrollMode(OVER_SCROLL_NEVER);
		setDrawingCacheEnabled(true);
		setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
		setPersistentDrawingCache(PERSISTENT_SCROLLING_CACHE);
	}

	public PageReaderView(Context context, Markup markup)
	{
		this(context, markup, 1);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (firstTime)
		{
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
	public int setPage(int pageNumber)
	{
		changeActiveItem(pageNumber - 1);
		scrollTo(activeItem * pageWidth + displayXOffset, 0);
		return activeItem + 1;
	}

	
	/** 
	 * Slides smoothly to the next page.
	 * @return number of page the brought up. 
	 */
	public int nextPage()
	{
		changeActiveItem(activeItem + 1);
		smoothScrollTo(activeItem * pageWidth + displayXOffset, 0);
		return activeItem + 1;
	}

	
	/** 
	 * Slides smoothly to the next page.
	 * @return number of page the brought up. 
	 */
	public int prevPage()
	{
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

		int newLeftCacheBorder = Math.max(0, newActiveItem - CACHE_OFFSET);
		int newRightCacheBorder = Math.min(pages.length, newActiveItem
				+ CAСHE_SIZE - CACHE_OFFSET);	
		
		// Invalidating old cache
		for (int i = leftCacheBorder; i < rightCacheBorder; ++i)
		{
			if (!(newLeftCacheBorder <= i && i < newRightCacheBorder))
			{
				if (pages[i] != null)
				{
					innerWrapper.removeView(pages[i]);
					pages[i] = null;
				}
			}
		}
		
		// Adding new cache
		for (int i = newLeftCacheBorder; i < newRightCacheBorder; ++i)
		{
			if (pages[i] == null)
			{
				// Defining page's left offset
				LayoutParams params = new LayoutParams(pageWidth, pageHeight, Gravity.TOP);
				params.leftMargin = pageWidth * i;
				params.topMargin = displayYOffset;

				// Adding page
				pages[i] = markup.getPageView(i + 1);
				innerWrapper.addView(pages[i], params);
			}
		}

		leftCacheBorder = newLeftCacheBorder;
		rightCacheBorder = newRightCacheBorder;
		
		activeItem = newActiveItem;
		savePreferences();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

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
			pageHeight = h;// w * markup.getHeight() / markup.getWidth();
			displayXOffset = 0;
			displayYOffset = (h - pageHeight) / 2;
		}

		innerWrapper.setLayoutParams(new LayoutParams(markup.getPageNumber()
				* pageWidth, pageHeight));
		
		
		// Scrolling to the current active element
		changeActiveItem(activeItem);
		scrollTo(pageWidth * activeItem, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
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
	
	private void savePreferences()
	{
		SharedPreferences settings = getContext().getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("last page of " + markup.getManualName(), activeItem + 1);
		editor.commit();
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