/*
 * ManualActivity ����������
 * 
 * ������������ ���������������� ����������������
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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.CResources;
import app.tascact.manual.XMLResources;
import app.tascact.manual.view.ManualControlView;
import app.tascact.manual.view.ManualView;

public class ManualActivity extends Activity
{
	private LinearLayout mMainLayout;
	private ManualView mManualView;
	private ManualControlView mControl;
	private int mPageToDisplay;
	private XMLResources mResources;
	private long mPrevTouchTime;
	private String mManualName;
	private GestureDetector mGestureDetector;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		mManualName = extras.getString("bookName");
		mGestureDetector = new GestureDetector(this, new GestureListener());
		
		try {
			mResources = new XMLResources(this, mManualName);
		} catch (Throwable e) {
			Log.e("XML", e.getMessage());
			finish();
		}
		
		mMainLayout = new LinearLayout(this);
		try {
			mManualView = new ManualView(this, mTouchListener, mClickListener, mManualName);
		} catch (Throwable e) {
			Log.e("XML", "falled in ManualActivity", e);
			finish();
		}
		mControl = new ManualControlView(this);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mMainLayout.setOrientation(1);
		mMainLayout.addView(mManualView, new LayoutParams(LayoutParams.MATCH_PARENT, 1040));
		mMainLayout.addView(mControl, new LayoutParams(LayoutParams.MATCH_PARENT, 167));
		
		mControl.mNextButton.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
			    {
					mPrevTouchTime = event.getEventTime();
					if(mPageToDisplay < mResources.getPageNumber())
						++mPageToDisplay;
					
					SavePreferences();
					mManualView.SetPage(mPageToDisplay);
					//mManualView.invalidate();
			    }
				return true;
			}
		});
		
		mControl.mPrevButton.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
			    {
					mPrevTouchTime = event.getEventTime();
					mPageToDisplay--;
					if(mPageToDisplay < 0)
						mPageToDisplay = 0;
					
					SavePreferences();
					mManualView.SetPage(mPageToDisplay);
			    }
				return true;
			}
		});
		
		mControl.mContentsButton.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getEventTime() - mPrevTouchTime > 250)
			    {
					mPrevTouchTime = event.getEventTime();
					Intent intent = new Intent(v.getContext(), ContentActivity.class);
					intent.putExtra("PageCount", mResources.getPageNumber());
					startActivityForResult(intent, 0);
			    }
				return true;
			}
		});
		
		LoadPreferences();
		mManualView.SetPage(mPageToDisplay);
		setContentView(mMainLayout);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)
		{
			mPageToDisplay = data.getIntExtra("page", -1);
			SavePreferences();
			mManualView.SetPage(mPageToDisplay);
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v)
		{
			if(mResources.getTaskType(mPageToDisplay, v.getId()) != null)
   			{
   				Intent intent = new Intent(v.getContext(), TaskActivity.class);
   				intent.putExtra("ManualNumber", mManualName);
	   			intent.putExtra("PageNumber", mPageToDisplay);
	   			intent.putExtra("TaskNumber", v.getId());
	   			intent.putExtra("TaskType", mResources.getTaskType(mPageToDisplay, v.getId()));
	   			startActivity(intent);
   			}
		}
	};
	
	private OnTouchListener mTouchListener = new OnTouchListener() 
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (mGestureDetector.onTouchEvent(event))
				return true;
	    	
	    	return false;
		}
	};
    
    private class GestureListener extends SimpleOnGestureListener
    {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY)
		{
			if(velocityX < -1500)
			{
				mPageToDisplay++;
				if(mPageToDisplay >= mResources.getPageNumber())
					mPageToDisplay = mResources.getPageNumber() - 1;
				
				SavePreferences();
				mManualView.SetPage(mPageToDisplay);
			}			
			if(velocityX > 1500)
			{
				mPageToDisplay--;
				if(mPageToDisplay < 0)
					mPageToDisplay = 0;
				
				SavePreferences();
				mManualView.SetPage(mPageToDisplay);
			}
			return true;
		}
	}

	@Override
    protected void onStop()
	{
		super.onStop();
		SavePreferences();
    }
	
	private void SavePreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		if(mManualName.equals("/sdcard/text.txt"))
			editor.putInt("page1", mPageToDisplay);
		//if(mManualName == 2)
		//	editor.putInt("page2", mPageToDisplay);
		editor.commit();
	}
	
	private void LoadPreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		if(mManualName.equals("/sdcard/text.txt"))
			mPageToDisplay = settings.getInt("page1", 0);
		//if(mManualName == 2)
		//	mPageToDisplay = settings.getInt("page2", 0);
	}
}