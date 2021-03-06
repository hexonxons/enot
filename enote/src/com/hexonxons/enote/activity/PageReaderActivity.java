/**
 * Page reader activity
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package com.hexonxons.enote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.hexonxons.enote.R;
import com.hexonxons.enote.utils.Markup;
import com.hexonxons.enote.view.ControlView;
import com.hexonxons.enote.view.PageReaderView;

public class PageReaderActivity extends Activity
{
	// Main layout for all elements, such as PageReaderView
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
	// Load progress dialog
	public AlertDialog mProgressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// No title display
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Get manual name
		mProgressDialog = new Builder(this).create();
		mProgressDialog.setView(getLayoutInflater().inflate(R.layout.dialog_loading_layout, mMainLayout), 0, 0, 0, 0);
		mProgressDialog.setCancelable(false);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		// ебаный нахуй пиздец...
		int width = display.getWidth();
		int height = display.getHeight();
		
		// loading page number
		loadPreferences();
		mMainLayout = new LinearLayout(this);
		mMainLayout.setBackgroundColor(Color.WHITE);
		
		OnTouchListener NextPageListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
				{
					switch (event.getAction())
					{
						case MotionEvent.ACTION_UP:
						{
							mPrevTouchTime = event.getEventTime();
							mPageToDisplay = mReader.nextPage();
							break;
						}
	
						default:
							break;
					}
			    }
				return false;
			}
		};
		
		OnTouchListener PrevPageListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				
				if(event.getEventTime() - mPrevTouchTime > 250)
				{
					switch (event.getAction())
					{
						case MotionEvent.ACTION_UP:
						{
							mPrevTouchTime = event.getEventTime();				
							mPageToDisplay = mReader.prevPage();
							break;
						}
	
						default:
							break;
					}
			    }
				return false;
			}
		};
		
		OnTouchListener ContentsListener = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 1000)
				{
					switch (event.getAction())
					{
						case MotionEvent.ACTION_UP:
						{
							mPrevTouchTime = event.getEventTime();
							//Intent intent = new Intent(v.getContext(), ContentActivity.class);
							//intent.putExtra("PageCount", mMarkup.getPageNumber());
							//startActivityForResult(intent, 0);
							mReader.LoadPagePickerDialog();
							break;
						}
	
						default:
							break;
					}
					
					
			    }
				return false;
			}
		};
		
		if(height < width)
		{
			// Setting up page reader.
			mReader = new PageReaderView(this, mMarkup, mPageToDisplay, mProgressDialog);
			mMainLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			ControlView LeftControl = new ControlView(this);
			ControlView RightControl = new ControlView(this);
			
			LeftControl.setIconsFloat(ControlView.FLOAT_BOTTOM);
			RightControl.setIconsFloat(ControlView.FLOAT_BOTTOM);
			
			LeftControl.setPanelOrientation(ControlView.ORIENTATION_LEFT);
			RightControl.setPanelOrientation(ControlView.ORIENTATION_RIGHT);
			
			LeftControl.addIcon(getResources().getDrawable(R.drawable.prev), getResources().getDrawable(R.drawable.prev_pressed), PrevPageListener);
			LeftControl.addIcon(getResources().getDrawable(R.drawable.contents), getResources().getDrawable(R.drawable.contents_pressed), ContentsListener);
			
			RightControl.addIcon(getResources().getDrawable(R.drawable.next), getResources().getDrawable(R.drawable.next_pressed), NextPageListener, 50);
			//RightControl.addIcon(50);
			
			mMainLayout.addView(LeftControl, new LayoutParams(width / 10, LayoutParams.MATCH_PARENT));	
			mMainLayout.addView(mReader, new LayoutParams((int) (width * 0.8), LayoutParams.MATCH_PARENT));
			mMainLayout.addView(RightControl, new LayoutParams(width / 10, LayoutParams.MATCH_PARENT));
		}
		else
		{
			// Setting up page reader.
			mReader = new PageReaderView(this, mMarkup, mPageToDisplay, mProgressDialog);
			mMainLayout.setOrientation(LinearLayout.VERTICAL);
			
			ControlView BottomControl = new ControlView(this);
			
			BottomControl.setIconsFloat(ControlView.FLOAT_RIGHT);
			
			BottomControl.setPanelOrientation(ControlView.ORIENTATION_BOTTOM);
			
			BottomControl.addIcon(getResources().getDrawable(R.drawable.next), getResources().getDrawable(R.drawable.next_pressed), NextPageListener);
			BottomControl.addIcon(getResources().getDrawable(R.drawable.contents), getResources().getDrawable(R.drawable.contents_pressed), ContentsListener);
			BottomControl.addIcon(getResources().getDrawable(R.drawable.prev), getResources().getDrawable(R.drawable.prev_pressed), PrevPageListener);
			
			mMainLayout.addView(mReader, new LayoutParams(LayoutParams.MATCH_PARENT, (int) (height * 0.9)));
			mMainLayout.addView(BottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, height / 10));
		}

		// Displaying
		setContentView(mMainLayout);
		mReader.LoadPages();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)
		{
			mPageToDisplay = data.getIntExtra("page", -1);
			mReader.setPage(mPageToDisplay);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    setContentView(mMainLayout);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		loadPreferences();
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mMainLayout.removeAllViews();
		mReader.ClearPages();
	}
	
	private void loadPreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		mPageToDisplay = settings.getInt("last page of " + mMarkup.getManualName(), 1);		
	}

}
