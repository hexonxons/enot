/**
 * TaskActivity class
 * 
 * Run task activity
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * :mailto hexonxons@gmail.com
 * 
 */

package app.tascact.manual.activity;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.task.ColoringPictureTaskView;
import app.tascact.manual.task.ConnectElementsSequenceTaskView;
import app.tascact.manual.task.GroupingElementsTaskView;
import app.tascact.manual.task.LabyrinthTaskView;
import app.tascact.manual.task.CompleteTableTaskView;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.ControlView;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.KeyboardView;
import app.tascact.manual.view.utils.KeyboardView.OnKeyboardKeyPressListener;

public class TaskActivity extends Activity
{
	// View раскладки элементов
	private MainLayout mMainLayout = null;
	
	// View страниц учебника
	private TaskView mTaskLayout = null;	
	private KeyboardView mKeyboard = null;
	private LinearLayout mControlLayout = null;
	private ScrollView mScroll = null;
	private TextView mTaskDescription = null;
	ControlView mBottomControl = null;
	int width = 0;
	int height = 0;
	boolean scrollable = false;
	private LogWriter mWriter = null;
	private long mPrevTouchTime = 0;
	int kbh = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mMainLayout = new MainLayout(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMainLayout.setBackgroundColor(Color.WHITE);
		mKeyboard = new KeyboardView(this);
		mControlLayout = new LinearLayout(this);
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		// ебаный нахуй пиздец...
		width = display.getWidth();
		height = display.getHeight();
		
		try
		{
			Markup markup = new Markup(this, extras.getString("ManualName"));
			Node task = markup.getTaskResources(extras.getInt("PageNumber"), extras.getInt("TaskNumber"));
			scrollable = ((((Node) XMLUtils.evalXpathExpr(task, "./Scrollable", XPathConstants.NODE)).getTextContent()).compareTo("true") == 0);
			
			// Getting description of this task
			Node TaskDescription = (Node) XMLUtils.evalXpathExpr(task, "./TaskDescription", XPathConstants.NODE);
			if (TaskDescription != null) 
			{
				mTaskDescription = new TextView(this);
				mTaskDescription.setText(TaskDescription.getTextContent());
				mTaskDescription.setGravity(Gravity.CENTER_HORIZONTAL);
				mTaskDescription.setTextSize(30);
				mTaskDescription.setTextColor(Color.BLACK);
				mMainLayout.addView(mTaskDescription, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			
			if (extras != null)
			{
				switch (extras.getInt("TaskType"))
				{
					case 1:
					{
						// TODO here markup is passes as argument to almost every task.
						// should pe replaced with string of current working directory.
						mTaskLayout = new ConnectElementsSequenceTaskView(this, task, markup);
						break;
					}
					
					case 2:
					{
						mWriter = new LogWriter(this, extras.getString("ManualName"), extras.getInt("PageNumber"), extras.getInt("TaskNumber"));
						mTaskLayout = new CompleteTableTaskView(this, task, markup, mWriter);
						mKeyboard.setOnKeyPressedListener(new OnKeyboardKeyPressListener()
						{
							@Override
							public void onKeyboardKeyPress(String label)
							{
								((CompleteTableTaskView)mTaskLayout).processKeyEvent(label);
							}
						});
						break;
					}

					case 3: 
					{
						mTaskLayout = new LabyrinthTaskView(this,
								markup.getTaskResources(
										extras.getInt("PageNumber"),
										extras.getInt("TaskNumber")), 
								markup);
						break;
					}
					
					case 4:
					{
						mTaskLayout = new GroupingElementsTaskView(this, task, markup);
						break;
					}
					
					case 5: 
					{
						mTaskLayout = new ColoringPictureTaskView(this, task, markup);
						break;
					}
					
					default:
					{
						break;
					}
				}
				
				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				{
					kbh = 160;
				}
				else
					kbh = 200;
				
				mMainLayout.setOrientation(LinearLayout.VERTICAL);
				
				mBottomControl = new ControlView(this);	
				mBottomControl.setIconsFloat(ControlView.FLOAT_LEFT);
				mBottomControl.setPanelOrientation(ControlView.ORIENTATION_BOTTOM);
				
				mBottomControl.addIcon(getResources().getDrawable(R.drawable.play), getResources().getDrawable(R.drawable.play_pressed), mRunReplayTouchListener);
				mBottomControl.addIcon(getResources().getDrawable(R.drawable.button_check), getResources().getDrawable(R.drawable.button_check), mCheckTouchListener);
				mBottomControl.addIcon(getResources().getDrawable(R.drawable.button_delete), getResources().getDrawable(R.drawable.button_delete), mRestartTouchListener);
				mBottomControl.addIcon(getResources().getDrawable(R.drawable.button_type), getResources().getDrawable(R.drawable.button_type), mKeyboardStartListener);
				
				if(scrollable)
				{
					mScroll = new ScrollView(this);
					mScroll.addView(mTaskLayout, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mControlLayout.addView(mKeyboard, new LayoutParams(LayoutParams.MATCH_PARENT, kbh));
					
					mMainLayout.addView(mScroll, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mMainLayout.addView(mControlLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
					mMainLayout.addView(mBottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, 60));
				}
				else
				{
					mMainLayout.addView(mTaskLayout, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mMainLayout.addView(mBottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, 60));
				}
				
				setContentView(mMainLayout);
			}
		} 
		catch (Throwable e)
		{
			Log.e("XML", "Failed to get markup from XML in TaskActivity", e);
			finish();
		}
	}

	private OnTouchListener mCheckTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			mPrevTouchTime = System.currentTimeMillis();

			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				mTaskLayout.CheckTask();
			}
			return false;
		}
	};
	
	private OnTouchListener mRunReplayTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(System.currentTimeMillis() - mPrevTouchTime > 250)
			{
				mPrevTouchTime = System.currentTimeMillis();
				mTaskLayout.replay();
			}
			return false;
		}
	};
	
	private OnTouchListener mKeyboardStartListener = new OnTouchListener()
	{
		private boolean flag = true;
		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(System.currentTimeMillis() - mPrevTouchTime > 250)
			{
				mPrevTouchTime = System.currentTimeMillis();
				int eventAction = event.getAction();
	
				if (eventAction == MotionEvent.ACTION_DOWN)
				{
					if(flag && scrollable)
					{
						mScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mScroll.getMeasuredHeight() - kbh));
						mControlLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, kbh));
						flag = false;
					}
					else
						if(scrollable)
						{
							mScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mScroll.getMeasuredHeight() + kbh));
							mControlLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
							flag = true;
						}
				}
			}
			return false;
		}
	};

	private OnTouchListener mRestartTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			mPrevTouchTime = System.currentTimeMillis();
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_UP)
			{				
				if(mWriter != null)
					mWriter.WriteEvent("TaskRestart", "");
				
				mTaskLayout.RestartTask();
			}

			return false;
		}
	};
	
	@Override
	public void onPause()
	{
		super.onPause();
		mTaskLayout.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mTaskLayout.onResume();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		if(mWriter != null)
			mWriter.CloseLog();
		mTaskLayout.onStop();
	}
	
	private class MainLayout extends LinearLayout
	{

		public MainLayout(Context context)
		{
			super(context);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			if(mScroll != null)
			{
				int height = h - mBottomControl.getMeasuredHeight() - (mTaskDescription == null ? 0 : mTaskDescription.getMeasuredHeight());
				mScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
			}
			else
			{
				int height = h - mBottomControl.getMeasuredHeight() - (mTaskDescription == null ? 0 : mTaskDescription.getMeasuredHeight());
				mTaskLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
			}
			
			super.onSizeChanged(w, h, oldw, oldh);
		}
	}
}
