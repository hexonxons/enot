package app.tascact.manual.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import app.tascact.manual.view.ManualControlView;
import app.tascact.manual.view.PageReaderView;

public class PageReaderActivity extends Activity {
	private LinearLayout mainLayout;
	private PageReaderView reader;
	private ManualControlView controlPanel;
	private int pageToDisplay;
	private Markup markup;
	private long mPrevTouchTime;
	private String mManualName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle extras = getIntent().getExtras();
		mManualName = extras.getString("bookName");
		try {
			markup = new Markup(this, mManualName);
		} catch (Throwable e) {
			// Stopping activity on failure.
			// No markup - no pages to read.
			Log.e("XML", "While creating Page Reader", e);
			finish();
			return;
		}

		loadPreferences();

		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Setting up page reader.
		reader = new PageReaderView(this, markup, pageToDisplay);
		int h = getWindowManager().getDefaultDisplay().getHeight();
		mainLayout.addView(reader, new LayoutParams(
				LayoutParams.MATCH_PARENT, h-150));
		//mainLayout.addView(reader, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		mainLayout.setBackgroundColor(Color.WHITE);
		// Setting up control panel.
		controlPanel = new ManualControlView(this);		

		controlPanel.mNextButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getEventTime() - mPrevTouchTime > 250) {
					mPrevTouchTime = event.getEventTime();
					pageToDisplay = reader.nextPage();
					savePreferences();
			    }
				return true;
			}
		});

		controlPanel.mPrevButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getEventTime() - mPrevTouchTime > 250) {
					mPrevTouchTime = event.getEventTime();					
					pageToDisplay = reader.prevPage();
					savePreferences();
			    }
				return true;
			}
		});

		controlPanel.mContentsButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getEventTime() - mPrevTouchTime > 250) {
					mPrevTouchTime = event.getEventTime();
					Intent intent = new Intent(v.getContext(), ContentActivity.class);
					intent.putExtra("PageCount", markup.getPageNumber());
					startActivityForResult(intent, 0);
			    }
				return true;
			}
		});

		mainLayout.addView(controlPanel, new LayoutParams(LayoutParams.MATCH_PARENT, 167));

		// Displaying
		setContentView(mainLayout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 0)	{
			pageToDisplay = data.getIntExtra("page", -1);
			savePreferences();
			reader.setPage(pageToDisplay);
		}
	}

	@Override
    protected void onStop() {
		super.onStop();
		savePreferences();
    }

	
	private void savePreferences() {
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("last page of " + markup.getManualName(), pageToDisplay);
		editor.commit();
	}
	
	private void loadPreferences() {
		SharedPreferences settings = getSharedPreferences("ManualPrefs", 0);
		pageToDisplay = settings.getInt("last page of " + markup.getManualName(), 1);		
	}
}
