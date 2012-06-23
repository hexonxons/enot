/**
 * Page reader view
 * 
 * Render manual pages.
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package com.hexonxons.enote.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hexonxons.enote.loaders.PageLoaderTask;
import com.hexonxons.enote.utils.Markup;

public class PageReaderView extends HorizontalScrollView 
{
	// Number of pages to load
	private static final int LOADED_PAGES_SIZE = 3;

	// Pages wrapper
	private LinearLayout mWrapper = null;
	private Markup mMarkup = null;
	// Displaying page number
	private int mActivePage = 0;
	// Array of manual pages
	private PageView mPages[];
	// Width of single page
	private int mPageWidth = 0;
	// Height of single page
	private int mPageHeight = 0;
	// "Loading" dialog
	private ProgressDialog mDialog;
	// Left and right indexes of loaded pages
	private int mLeftLoadedIndex = -1;
	private int mRightLoadedIndex = -1;
	// Is it is first time reader load?
	private boolean mInit = true;
	// Flint detector
	private GestureDetector mGestureDetctor = null;
	
	public PageReaderView(Context context, Markup markup, int pageToDisplay, ProgressDialog dialog)
	{
		super(context);
		// Request no scrollbar
		this.setHorizontalScrollBarEnabled(false);
		
		// Init parameters
		mWrapper = new LinearLayout(context);	
		mActivePage = pageToDisplay - 1;
		mMarkup = markup;        
		mDialog = dialog;
		mGestureDetctor = new GestureDetector(context, new SoftScrollOnGestureListener());
		
		this.addView(mWrapper);     
	}

	/**
	 * Load page to display
	 * 
	 * @param activePage number of page to display
	 */
	private void LoadPage(int activePage)
	{		
		if (activePage >= mMarkup.getPageNumber())
		{
			activePage = mMarkup.getPageNumber() - 1;
		}

		if (activePage < 0)
		{
			activePage = 0;
		}

		int newLeftIndex = Math.max(0, activePage - LOADED_PAGES_SIZE / 2);
		int newRightIndex= Math.min(mMarkup.getPageNumber() - 1, activePage + LOADED_PAGES_SIZE / 2);	
		
		// Invalidating old cache
		for (int i = mLeftLoadedIndex; i <= mRightLoadedIndex; ++i)
		{
			if (!(newLeftIndex <= i && i <= newRightIndex) && i != -1)
			{
				mPages[i].deletePageContent();
			}
		}
		
		// Adding new cache
		for (int i = newLeftIndex; i <= newRightIndex; ++i)
		{
			if (!(mLeftLoadedIndex <= i && i <= mRightLoadedIndex))
			{
				PageLoaderTask plt = new PageLoaderTask(mPages[i], mDialog, i + 1);
				plt.execute();
				mPages[i].setLayoutParams(new LinearLayout.LayoutParams(mPageWidth, mPageHeight));
			}
		}

		mLeftLoadedIndex = newLeftIndex;
		mRightLoadedIndex = newRightIndex;
		
		mActivePage = activePage;
		savePreferences();
	}
	
	/**
	 * Save page number
	 */
	private void savePreferences()
	{
		SharedPreferences settings = getContext().getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("last page of " + mMarkup.getManualName(), mActivePage + 1);
		editor.commit();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if(mInit)
		{
			scrollTo(mActivePage * mPageWidth, 0);
			mInit = false;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{		
		mWrapper.setLayoutParams(new LayoutParams(w * mMarkup.getPageNumber(), h));
		
		for(int i = 0; i < mWrapper.getChildCount(); ++i)
		{
			mWrapper.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(w, h));
		}
		// Evaluating new page size
		if (w * mMarkup.getHeight() >= h * mMarkup.getWidth())
		{
			mPageHeight = h;
			mPageWidth = w;
		}
		else 
		{
			mPageWidth = w;
			mPageHeight = h;
		}			
		
		LoadPage(mActivePage);
		
		// Scrolling to the current active element
		scrollTo(mPageWidth * mActivePage, 0);
	}
	
	/**
	 * Delete all loaded pages content
	 */
	public void ClearPage()
	{
		
		for (int i = mLeftLoadedIndex; i <= mRightLoadedIndex; ++i)
		{
			mWrapper.removeAllViews();
			
			mPages[i].deletePageContent();
			mPages[i] = null;
		
			System.gc();
		}
	}
	
	/**
	 * Create all empty page views
	 */
	public void LoadContent()
	{
		mPages = new PageView[mMarkup.getPageNumber()];
		for(int i = 0; i < mMarkup.getPageNumber(); ++i)
		{
			mPages[i] = new PageView(getContext(), mMarkup);
			mWrapper.addView(mPages[i]);
		}
	}
	
	/**
	 * Stabilize current page
	 */
	public void StableCurrPage() 
	{
		smoothScrollTo(mActivePage * mPageWidth, 0);
	}
	
	/**
	 * Load previous page
	 * @return page number
	 */
	public int prevPage() 
	{
		LoadPage(mActivePage - 1);
		smoothScrollTo(mActivePage * mPageWidth, 0);
		return mActivePage - 1;
	}

	/**
	 * Load next page
	 * @return page number
	 */
	public int nextPage() 
	{
		LoadPage(mActivePage + 1);
		smoothScrollTo(mActivePage * mPageWidth, 0);
		return mActivePage + 1;
	}


	/**
	 * Load page
	 * @param pageNumber page to load
	 */
	public void setPage(int pageNumber)
	{
		LoadPage(pageNumber - 1);
		scrollTo(mActivePage * mPageWidth, 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
		if (mGestureDetctor.onTouchEvent(event))
		{
			return true;
		}
		
		
		if (event.getAction() == (MotionEvent.ACTION_UP))
		{
			int newActiveItem = (getScrollX() + mPageWidth / 2) / mPageWidth;
			LoadPage(newActiveItem);
			smoothScrollTo(mActivePage * mPageWidth, 0);
			return true;
		}
		return true;
	}

	private class SoftScrollOnGestureListener extends SimpleOnGestureListener
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
