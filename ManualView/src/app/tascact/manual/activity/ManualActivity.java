/*
 * ManualActivity класс
 * 
 * Запуск процесса учебника
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.CResources;
import app.tascact.manual.R;
import app.tascact.manual.TaskActivity;
import app.tascact.manual.view.ManualControlView;
import app.tascact.manual.view.ManualView;

public class ManualActivity extends Activity
{
	// View раскладки элементов
	private LinearLayout mMainLayout = null;
	// View страниц учебника
	private ManualView mManualView = null;
	// View элемента управления
	private ManualControlView mControl = null;
	
	public static final String PREFS_NAME = "ManualPrefs";
	private SharedPreferences mSettings;
	private int mPageToDisplay = 0;
	private int mRequestCode = 0;
	
	private CResources res = new CResources();
	private Context mContext = null;
	
	private long mPrevTouchTime = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		mMainLayout = new LinearLayout(this);
		mManualView = new ManualView(this, res.PageResources);
		mControl = new ManualControlView(this);
		int[] pageres = null;
		try
		{
			pageres = CResources.GetPageResources(0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		// ориентируем View вертикально
		mMainLayout.setOrientation(1);
		
		mMainLayout.addView(mManualView, new LayoutParams(LayoutParams.MATCH_PARENT, 1040));
		mMainLayout.addView(mControl, new LayoutParams(LayoutParams.MATCH_PARENT, 167));
		
		mControl.mNextButton.setOnTouchListener(mNextTouchListener);
		mControl.mPrevButton.setOnTouchListener(mPrevTouchListener);
		mControl.mContentsButton.setOnTouchListener(mContentsTouchListener);
		
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		mPageToDisplay = mSettings.getInt("page", 0);
		mManualView.changePage(mPageToDisplay);
		
		setContentView(mMainLayout);
	}
	
	private OnTouchListener mNextTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(event.getEventTime() - mPrevTouchTime > 250)
		    {
				mPrevTouchTime = event.getEventTime();
				mPageToDisplay++;
				if(mPageToDisplay >= res.PageResources.length)
					mPageToDisplay = res.PageResources.length - 1;
				
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putInt("page", mPageToDisplay);
				editor.commit();
				mManualView.changePage(mPageToDisplay);
				mManualView.invalidate();
		    }
			return true;
		}
	};
	
	private OnTouchListener mPrevTouchListener = new OnTouchListener()
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
				
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putInt("page", mPageToDisplay);
				editor.commit();
				mManualView.changePage(mPageToDisplay);
		    }
			return true;
		}
	};
	
	private OnTouchListener mContentsTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(event.getEventTime() - mPrevTouchTime > 250)
		    {
				mPrevTouchTime = event.getEventTime();
				Intent intent = new Intent(mContext, ContentActivity.class);
				intent.putExtra("PageCount", res.PageResources.length);
				startActivityForResult(intent, mRequestCode);
		    }
			return true;
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)
		{
			mPageToDisplay = data.getIntExtra("page", -1);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putInt("page", mPageToDisplay);
			editor.commit();
			mManualView.changePage(mPageToDisplay);
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getEventTime() - mPrevTouchTime > 250)
	    {
			mPrevTouchTime = event.getEventTime();
			
			mManualView.processEvent(event);
			if(mManualView.mTaskNum >= 0 && res.TaskResources[mManualView.mCurrPageNum][mManualView.mTaskNum][0] != 0)
			{
				Intent intent = new Intent(this, TaskActivity.class);
				intent.putExtra("task", res.TaskResources[mManualView.mCurrPageNum][mManualView.mTaskNum]);
				startActivity(intent);
			}
	    }
		return true;
	}
		
	@Override
    protected void onStop()
	{
		super.onStop();
      
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putInt("page", mPageToDisplay);
		editor.commit();
    }
}