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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.view.PageControlView;
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
							((ImageView)v).setBackgroundResource(R.drawable.next);
							mPrevTouchTime = event.getEventTime();
							mPageToDisplay = mReader.nextPage();
							savePreferences();
							break;
						}
						
						case MotionEvent.ACTION_DOWN:
						{
							((ImageView)v).setBackgroundResource(R.drawable.next_pressed);
							break;
						}
	
						default:
							break;
					}
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
					switch (event.getAction())
					{
					case MotionEvent.ACTION_UP:
					{
						((ImageView)v).setBackgroundResource(R.drawable.prev);
						mPrevTouchTime = event.getEventTime();					
						mPageToDisplay = mReader.prevPage();
						savePreferences();
						break;
					}
					
					case MotionEvent.ACTION_DOWN:
					{
						((ImageView)v).setBackgroundResource(R.drawable.prev_pressed);
						break;
					}

					default:
						break;
					}
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
		
		if(height < width)
		{
			// Setting up page reader.
			mReader = new PageReaderView(this, mMarkup, mPageToDisplay);
			mMainLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			ImageView NextPageButton = new ImageView(this);
			NextPageButton.setBackgroundResource(R.drawable.next);
			NextPageButton.setOnTouchListener(NextPageListener);
			
			ImageView PrevPageButton = new ImageView(this);
			PrevPageButton.setBackgroundResource(R.drawable.prev);
			PrevPageButton.setOnTouchListener(PrevPageListener);
			
			ImageView ContentsButton = new ImageView(this);
			ContentsButton.setBackgroundResource(R.drawable.contents);
			ContentsButton.setOnTouchListener(ContentsListener);
			
			PageControlView LeftControl = new PageControlView(this);
			PageControlView RightControl = new PageControlView(this);
			
			LeftControl.setIconsFloat(PageControlView.FLOAT_BOTTOM);
			RightControl.setIconsFloat(PageControlView.FLOAT_BOTTOM);
			
			LeftControl.setPanelOrientation(PageControlView.ORIENTATION_LEFT);
			RightControl.setPanelOrientation(PageControlView.ORIENTATION_RIGHT);
			
			LeftControl.addIcon(PrevPageButton);
			LeftControl.addIcon(ContentsButton);
			
			RightControl.addIcon(NextPageButton, 50);
			
			mMainLayout.addView(LeftControl, new LayoutParams(width / 10, LayoutParams.MATCH_PARENT));	
			mMainLayout.addView(mReader, new LayoutParams((int) (width * 0.8), LayoutParams.MATCH_PARENT));
			mMainLayout.addView(RightControl, new LayoutParams(width / 10, LayoutParams.MATCH_PARENT));
		}
		else
		{
			// Setting up page reader.
			mReader = new PageReaderView(this, mMarkup, mPageToDisplay);
			mMainLayout.setOrientation(LinearLayout.VERTICAL);
			
			ImageView NextPageButton = new ImageView(this);
			NextPageButton.setBackgroundResource(R.drawable.next);
			NextPageButton.setOnTouchListener(NextPageListener);
			
			ImageView PrevPageButton = new ImageView(this);
			PrevPageButton.setBackgroundResource(R.drawable.prev);
			PrevPageButton.setOnTouchListener(PrevPageListener);
			
			ImageView ContentsButton = new ImageView(this);
			ContentsButton.setBackgroundResource(R.drawable.contents);
			ContentsButton.setOnTouchListener(ContentsListener);
			
			PageControlView BottomControl = new PageControlView(this);
			
			BottomControl.setIconsFloat(PageControlView.FLOAT_RIGHT);
			
			BottomControl.setPanelOrientation(PageControlView.ORIENTATION_BOTTOM);
			
			BottomControl.addIcon(NextPageButton);
			BottomControl.addIcon(ContentsButton);
			BottomControl.addIcon(PrevPageButton);
			
			mMainLayout.addView(mReader, new LayoutParams(LayoutParams.MATCH_PARENT, (int) (height * 0.9)));
			mMainLayout.addView(BottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, height / 10));
		}

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
		savePreferences();
    }
	
	@Override
	protected void onPause()
	{
		super.onPause();
		savePreferences();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
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
