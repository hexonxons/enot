/**
 * Single manual page class
 * 
 * Copyright 2012 hexonxons
 * 
 * @author losik, hexonxons
 * @mailto killgamesh666@gmail.com
 * 
 */


package com.hexonxons.enote.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hexonxons.enote.activity.TaskReaderActivity;
import com.hexonxons.enote.utils.Markup;

public class PageView extends LinearLayout
{
	private int mPageNumber;
	private ImageView mPageElements[] = null;
	private int mHeight = 0;
	private int mWidth = 0;
	private Markup mMarkup = null;
	private Uri mResources[] = null;
	private Bitmap mPageBitmaps[] = null;
	private float mScaleFactor = 1.0f;
	
	/** 
	 * Create empty page. Use LoadPageImages for load page content
	 *  
	 * @param context
	 * @param markup
	 */
	public PageView(Context context, Markup markup)
	{
		super(context);
		
		this.setOrientation(LinearLayout.VERTICAL);
		
		mMarkup = markup;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		//if(mScaleFactor != 1.0f)
		//	Log.d("onDraw",	"Scale with factor = " + Float.toString(mScaleFactor));
		//canvas.save();
		//canvas.scale(mScaleFactor, mScaleFactor);
		//canvas.restore();
	}
	
	/**
	 * Load page content.
	 * Only create all elements. Use loadPageContent to display.
	 * 
	 * @param pageNumber page number
	 */
	public void createPageContent(int pageNumber)
	{
		// Init zero height
		mHeight = 0;
		// Page number
		mPageNumber = pageNumber;
		// Page resources
		mResources = mMarkup.getPageElementsUri(mPageNumber);
		// Bitmaps of page elements
		mPageBitmaps = new Bitmap[mResources.length];
		// Views of page elements
		mPageElements = new ImageView[mResources.length];
		// Load all page elements		
		for(int i = 0; i < mResources.length; ++i)
		{
			// Create views
			mPageElements[i] = new ImageView(getContext());
			try 
			{
				// Page element's image
				Drawable bg = Drawable.createFromStream(new FileInputStream(new File(mResources[i].getPath())), null);
				mPageBitmaps[i] = ((BitmapDrawable) bg).getBitmap();
				mWidth = mPageBitmaps[i].getWidth();
				mHeight += mPageBitmaps[i].getHeight();
			} 
			catch (FileNotFoundException e) 
			{
				Log.e("LoadPageImages", "FileNotFoundException while creating bitmaps from stream", e);
				Log.e("LoadPageImages", mResources[i].getPath());
			}
			catch (NullPointerException e)
			{
				Log.e("LoadPageImages", "NullPointerException while creating bitmaps from stream", e);
				Log.e("LoadPageImages", mResources[i].getPath());
			}
		}
	}
	
	/**
	 * Delete all page content, recycle bitmaps and remove all created views in the page.
	 */
	public void deletePageContent()
	{
		if(mPageBitmaps != null)
		{
			for(int i = 0; i < mPageBitmaps.length; ++i)
			{
				try
				{
					mPageBitmaps[i].recycle();
				}
				catch (NullPointerException e) 
				{
					
					Log.e("UnloadPageImages", "NullPointerException while recycling bitmaps", e);
				}
			}
		}
		
		// Remove all page elements
		this.removeAllViews();
		
		mPageElements = null;
		mPageBitmaps = null;
	}
	
	/**
	 * Display page content. Content should be created by createPageContent before 
	 */
	public void loadPageContent()
	{
		if(mPageElements != null && mPageBitmaps != null)
		{
			for(int i = 0; i < mResources.length; ++i)
			{
				mPageElements[i].setImageBitmap(mPageBitmaps[i]);
				mPageElements[i].setId(i + 1);
				mPageElements[i].setOnClickListener(taskLauncher);
				this.addView(mPageElements[i]);
			}
		}
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		// Dont layout, if no child
		if(this.getChildCount() == 0)
			return;
		
		// Bitmap scale factor
		double scaleFactor = Math.min(((double) (b - t) / mHeight), ((double) (r - l) / mWidth));
		
		int top = 0;
		int bottom = 0;
		
		for(int i = 0; i < mPageElements.length; ++i)
		{
			try
			{
				Bitmap bg = ((BitmapDrawable) mPageElements[i].getDrawable()).getBitmap();
				int imageWidth = (int) (bg.getWidth() * scaleFactor);
				int imageHeight = (int) (bg.getHeight() * scaleFactor);
				int offset = (r - l - imageWidth) / 2;
				bottom += imageHeight;
				
				mPageElements[i].layout(offset, top, imageWidth + offset, bottom);
				top = bottom;
			}
			catch (NullPointerException e)
			{
				Log.e("onLayout", "NullPointerException while getting bitmap from mPageElements", e);
				Log.e("onLayout", "Index: " + i);
			}
		}
	}
	
	private OnClickListener taskLauncher = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			// Indexes are 1-based
			if(mMarkup.getTaskType(mPageNumber, v.getId()) != null)
			{
   				Intent intent = new Intent(v.getContext(), TaskReaderActivity.class);
   				intent.putExtra("ManualName", mMarkup.getManualName());
	   			intent.putExtra("PageNumber", mPageNumber);
	   			intent.putExtra("TaskNumber", v.getId());
	   			intent.putExtra("TaskType", mMarkup.getTaskType(mPageNumber, v.getId()));
	   			getContext().startActivity(intent);
   			}
		}
	};

	public void setScale(float scaleFactor)
	{
		mScaleFactor = scaleFactor;
		for(int i = 0; i < mPageElements.length; ++i)
		{
			mPageElements[i].invalidate();
		}
	}
}