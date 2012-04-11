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
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.task.ColoringPictureTaskView;
import app.tascact.manual.task.CompleteTableTaskView;
import app.tascact.manual.task.ConnectElementsSequenceTaskView;
import app.tascact.manual.task.GroupingElementsTaskView;
import app.tascact.manual.task.SetOperatorsTaskView;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.PageControlView;
import app.tascact.manual.view.TaskControlView;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.KeyboardView;
import app.tascact.manual.view.utils.KeyboardView.OnKeyboardKeyPressListener;

public class TaskActivity extends Activity
{
	// View раскладки элементов
	private LinearLayout mMainLayout = null;
	// View страниц учебника
	private TaskView mTaskView = null;	
	private KeyboardView mKeyboard = null;
	private LinearLayout mContr = null;
	private ScrollView mScroll = null;
	int width = 0;
	int height = 0;
	boolean scrollable = false;
	private LogWriter mWriter = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mMainLayout = new LinearLayout(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMainLayout.setBackgroundColor(Color.WHITE);
		mKeyboard = new KeyboardView(this);
		mContr = new LinearLayout(this);
		mScroll = new ScrollView(this);
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		// ебаный нахуй пиздец...
		width = display.getWidth();
		height = display.getHeight();
		
		try
		{
			Markup markup = new Markup(this, extras.getString("ManualName"));
			Node task = markup.getTaskResources(extras.getInt("PageNumber"), extras.getInt("TaskNumber"));
			scrollable = ((((Node) XMLUtils.evalXpathExpr(task, "./Scrollable", XPathConstants.NODE)).getTextContent()).compareTo("true") == 0);
			
			
			if (extras != null)
			{
				switch (extras.getInt("TaskType"))
				{
					case 1:
					{
						// TODO here markup is passes as argument to almost every task.
						// should pe replaced with string of current working directory.
						mTaskView = new ConnectElementsSequenceTaskView(this, task, markup);
						break;
					}
					
					case 2:
					{
						mTaskView = new CompleteTableTaskView(this, task, markup);
						break;
					}
					
					case 3:
					{
						mWriter = new LogWriter(extras.getString("ManualName"), extras.getInt("PageNumber"), extras.getInt("TaskNumber"));
						mTaskView = new SetOperatorsTaskView(this, task, markup, mWriter);
						mKeyboard.setOnKeyPressedListener(new OnKeyboardKeyPressListener()
						{
							@Override
							public void onKeyboardKeyPress(String label)
							{
								((SetOperatorsTaskView)mTaskView).processKeyEvent(label);
							}
						});
						
						break;
					}
					
					case 4:
					{
						mTaskView = new GroupingElementsTaskView(this, task, markup);
						break;
					}
					
					case 5: 
					{
						mTaskView = new ColoringPictureTaskView(this, task, markup);
						break;
					}
					
					default:
					{
						break;
					}
				}
				
				mMainLayout.setOrientation(LinearLayout.VERTICAL);
				
				ImageView CheckTask = new ImageView(this);
				CheckTask.setBackgroundResource(R.drawable.check);
				CheckTask.setOnTouchListener(mCheckTouchListener);
				
				ImageView RestartTask = new ImageView(this);
				RestartTask.setBackgroundResource(R.drawable.restart);
				RestartTask.setOnTouchListener(mRestartTouchListener);
				
				ImageView Keyboard = new ImageView(this);
				Keyboard.setBackgroundResource(R.drawable.keyboard);
				Keyboard.setOnTouchListener(mKeyboardStartListener);
				
				TaskControlView BottomControl = new TaskControlView(this);	
				BottomControl.setIconsFloat(PageControlView.FLOAT_LEFT);
				BottomControl.setPanelOrientation(PageControlView.ORIENTATION_BOTTOM);
				
				BottomControl.addIcon(CheckTask);
				BottomControl.addIcon(RestartTask);
				BottomControl.addIcon(Keyboard);
				
				if(scrollable)
				{
					mScroll.addView(mTaskView, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mMainLayout.addView(mScroll, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mMainLayout.addView(mContr);
					mMainLayout.addView(BottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, 60));
				}
				else
				{
					mMainLayout.addView(mTaskView, new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
					mMainLayout.addView(mContr);
					mMainLayout.addView(BottomControl, new LayoutParams(LayoutParams.MATCH_PARENT, 60));
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
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_DOWN)
			{
				((ImageView) v).setBackgroundResource(R.drawable.checked);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP)
			{
				((ImageView) v).setBackgroundResource(R.drawable.check);
				mTaskView.CheckTask();
				return true;
			}
			return true;
		}
	};
	
	private OnTouchListener mKeyboardStartListener = new OnTouchListener()
	{
		private boolean flag = true;
		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_DOWN)
			{
				if(flag && scrollable)
				{
					mContr.addView(mKeyboard, new LayoutParams(LayoutParams.MATCH_PARENT, 200));
					mScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height - 260));
					flag = false;
				}
				else
					if(scrollable)
					{
						mContr.removeView(mKeyboard);
						mScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height - 60));
						flag = true;
					}
				return true;
			}
			return true;
		}
	};

	private OnTouchListener mRestartTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_DOWN)
			{
				((ImageView) v).setBackgroundResource(R.drawable.restarted);
				((RelativeLayout) v.getParent()).setBackgroundResource(R.drawable.button_frame_bottom_pushed);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP)
			{
				((ImageView) v).setBackgroundResource(R.drawable.restart);
				((RelativeLayout) v.getParent()).setBackgroundResource(R.drawable.button_frame_bottom);
				mTaskView.RestartTask();
				return true;
			}

			return true;
		}
	};
	
	@Override
	public void onPause()
	{
		super.onPause();
		mTaskView.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mTaskView.onResume();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		if(mWriter != null)
			mWriter.CloseLog();
		mTaskView.onStop();
	}
}
