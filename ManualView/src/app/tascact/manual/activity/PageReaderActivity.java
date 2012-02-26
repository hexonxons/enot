package app.tascact.manual.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.XMLResources;
import app.tascact.manual.view.ManualControlView;
import app.tascact.manual.view.PageReaderView;

public class PageReaderActivity extends Activity {
	private LinearLayout mMainLayout;
	private PageReaderView mManualView;
	private ManualControlView mControl;
	private int pageToDisplay;
	private XMLResources markup;
	private long mPrevTouchTime;
	private String mManualName;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);


		Bundle extras = getIntent().getExtras();
		mManualName = extras.getString("bookName");
		try {
			markup = new XMLResources(this, mManualName);
		} catch (Throwable e) {
			Log.e("XML", e.getMessage());
			finish();
		}

		loadPreferences();

		mMainLayout = new LinearLayout(this);
		mManualView = new PageReaderView(this, markup);
		// TODO hardcode
		mManualView.setPage(4);
		//mManualView.setOnClickListener(mClickListener);
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
					if(pageToDisplay < markup.getPageNumber())
						++pageToDisplay;
					
					savePreferences();
					mManualView.setPage(pageToDisplay);
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
					if(pageToDisplay > 1)
						--pageToDisplay;
					
					savePreferences();
					mManualView.setPage(pageToDisplay);
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
					intent.putExtra("PageCount", markup.getPageNumber());
					startActivityForResult(intent, 0);
			    }
				return true;
			}
		});
		
		setContentView(mMainLayout);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)
		{
			pageToDisplay = data.getIntExtra("page", -1);
			savePreferences();
			mManualView.setPage(pageToDisplay);
		}
	}
	
	@Override
    protected void onStop()
	{
		super.onStop();
		savePreferences();
    }
	
	private void savePreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		if(mManualName.equals("book1"))
			editor.putInt("page1", pageToDisplay);
		//if(mManualName.equals("book2"))
		//	editor.putInt("page2", mPageToDisplay);
		editor.commit();
	}
	
	private void loadPreferences()
	{
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		if(mManualName.equals("book1"))
			pageToDisplay = settings.getInt("page1", 1);
		//if(mManualName.equals("book2"))
		//	mPageToDisplay = settings.getInt("page2", 0);
		
	}
}
