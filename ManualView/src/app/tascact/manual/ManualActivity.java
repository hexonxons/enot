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

package app.tascact.manual;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.activity.ContentActivity;
import app.tascact.manual.view.ControlView;

public class ManualActivity extends Activity
{
	// View раскладки элементов
	private LinearLayout mMainLayout = null;
	// View страниц учебника
	private ManualView mManualView = null;
	// View элемента управления
	private ControlView mControl = null;
	
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
		mControl = new ControlView(this);
		
		// ориентируем View вертикально
		mMainLayout.setOrientation(1);
		
		mMainLayout.addView(mManualView, new LayoutParams(LayoutParams.MATCH_PARENT, 1040));
		mMainLayout.addView(mControl, new LayoutParams(LayoutParams.MATCH_PARENT, 167));
		
		mControl.mNextButton.setOnTouchListener(mNextTouchListener);
		mControl.mPrevButton.setOnTouchListener(mPrevTouchListener);
		mControl.mContentsButton.setOnTouchListener(mContentsTouchListener);
		
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
}