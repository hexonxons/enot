/*
 * Page reader activity
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.Markup;
import app.tascact.manual.view.PageReaderView;

public class PageReaderActivity extends Activity
{
	// Main layout for all elements, such as PageReaderView, Popup Windows...
	private LinearLayout mMainLayout;
	// View of displayed page
	private PageReaderView mReader;
	// Number of page to display
	private int mPageToDisplay;
	// Markup for gettin' resources of page
	private Markup mMarkup;
	// Name of manual
	private String mManualName;
	// Time of precious touch event
	private long mPrevTouchTime = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// No title display
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Get manual name
		mManualName = getIntent().getExtras().getString("bookName");
			
		try
		{
			mMarkup = new Markup(this, mManualName);
		} 
		catch (Throwable e)
		{
			// Stopping activity on failure.
			// No markup - no pages to read.
			Log.e("XML", "While creating Page Reader", e);
			finish();
			return;
		}
		
		// loading page number
		loadPreferences();
		mMainLayout = new LinearLayout(this);
		//mainLayout.setOrientation(LinearLayout.VERTICAL);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Setting up page reader.
		mReader = new PageReaderView(this, mMarkup, mPageToDisplay);
		mMainLayout.addView(mReader, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mMainLayout.setBackgroundColor(Color.WHITE);
		
		OnTouchListener NextPageListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
				{
					mPrevTouchTime = event.getEventTime();
					mPageToDisplay = mReader.nextPage();
					mReader.updateTimer();
					savePreferences();
			    }
				return true;
			}
		};
		
		OnTouchListener PrevPageListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
				{
					mPrevTouchTime = event.getEventTime();					
					mPageToDisplay = mReader.prevPage();
					mReader.updateTimer();
					savePreferences();
			    }
				return true;
			}
		};
		
		OnTouchListener ContentsListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 1000)
				{
					mPrevTouchTime = event.getEventTime();
					Intent intent = new Intent(v.getContext(), ContentActivity.class);
					intent.putExtra("PageCount", mMarkup.getPageNumber());
					startActivityForResult(intent, 0);
			    }
				return true;
			}
		};
		
		mReader.setListeners(NextPageListener, PrevPageListener, ContentsListener);
		mReader.dismissControls();
		
		// Displaying
		setContentView(mMainLayout);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)
		{
			mPageToDisplay = data.getIntExtra("page", -1);
			savePreferences();
			mReader.setPage(mPageToDisplay);
		}
	}

	@Override
    protected void onStop()
	{
		super.onStop();
		mReader.dismissControls();
		savePreferences();
    }
	
	@Override
	protected void onPause()
	{
		super.onPause();
		mReader.dismissControls();
		savePreferences();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mReader.dismissControls();
		loadPreferences();
	}

	
	private void savePreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("last page of " + mMarkup.getManualName(), mPageToDisplay);
		editor.commit();
	}
	
	private void loadPreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		mPageToDisplay = settings.getInt("last page of " + mMarkup.getManualName(), 1);		
	}
}
