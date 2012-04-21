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

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.task.ColoringPictureTaskView;
import app.tascact.manual.task.CompleteTableTaskView;
import app.tascact.manual.task.ConnectElementsSequenceTaskView;
import app.tascact.manual.task.GroupingElementsTaskView;
import app.tascact.manual.task.LabyrinthTaskView;
import app.tascact.manual.task.SetOperatorsTaskView;
import app.tascact.manual.view.TaskControlView;
import app.tascact.manual.view.TaskView;

public class TaskActivity extends Activity
{
	// View раскладки элементов
	private RelativeLayout mMainLayout = null;
	// View страниц учебника
	private TaskView mTaskView = null;	
	private PopupWindow mTaskControlStart = null;
	private TaskControlView mTaskControl = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		try
		{
			Markup markup = new Markup(this, extras.getString("ManualName"));

			if (extras != null)
			{
				mMainLayout = new RelativeLayout(this);

				switch (extras.getInt("TaskType"))
				{
					case 1:
					{
						// TODO here markup is passes as argument to almost every task.
						// should pe replaced with string of current working directory.
						mTaskView = new ConnectElementsSequenceTaskView(this,
								markup.getTaskResources(
										extras.getInt("PageNumber"),
										extras.getInt("TaskNumber")),
										markup);
						break;
					}
					
					case 2:
					{
						mTaskView = new CompleteTableTaskView(this, markup,
								extras.getInt("PageNumber"),
								extras.getInt("TaskNumber"));
						break;
					}
					
					case 3:
					{
						mTaskView = new SetOperatorsTaskView(this, markup,
								extras.getString("ManualName"),
								extras.getInt("PageNumber"),
								extras.getInt("TaskNumber"));
						break;
					}
					
					case 4:
					{
						mTaskView = new GroupingElementsTaskView(this,
								markup.getTaskResources(
										extras.getInt("PageNumber"),
										extras.getInt("TaskNumber")),
										markup);
						break;
					}
					
					case 5: 
					{
						mTaskView = new ColoringPictureTaskView(this,
								markup.getTaskResources(
										extras.getInt("PageNumber"),
										extras.getInt("TaskNumber")), 
								markup);
						break;
					}
					case 6: 
					{
						mTaskView = new LabyrinthTaskView(this,
								markup.getTaskResources(
										extras.getInt("PageNumber"),
										extras.getInt("TaskNumber")), 
								markup);
						break;
					}
					
					default:
					{
						break;
					}
				}

				Button startTaskControl = new Button(this);
				startTaskControl.setBackgroundResource(R.drawable.taskattr);
				startTaskControl.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{					
						
						mTaskControlStart.setWidth(getWindowManager().getDefaultDisplay().getWidth());
						mTaskControlStart.setHeight(100);
						mTaskControlStart.showAtLocation(mMainLayout, Gravity.LEFT | Gravity.BOTTOM, 0, 0);		
						mMainLayout.postDelayed(new Runnable()
						{							
							@Override
							public void run() 
							{
								if(mTaskControlStart != null)
									mTaskControlStart.dismiss();
							}
						}, 2000);
					}
				});
				
				mTaskControl = new TaskControlView(this);
				// Задаем обработчики касаний
				mTaskControl.setListeners(mCheckTouchListener, mRestartTouchListener);
				mTaskControlStart = new PopupWindow(mTaskControl, 0, 0);
				mTaskControlStart.setAnimationStyle(R.style.ControlsAnimation);

				// Ориентируем View вертикально
				mMainLayout.setBackgroundColor(Color.WHITE);
				// Лочим ориентацию экрана
				//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mMainLayout.addView(mTaskView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				LayoutParams params = new LayoutParams(60, 60);
				params.setMargins(getWindowManager().getDefaultDisplay().getWidth() - 60, 100, 0, 0);
				mMainLayout.addView(startTaskControl, params);

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
				((ImageView) v).setImageResource(R.drawable.checked);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP)
			{
				((ImageView) v).setImageResource(R.drawable.check);
				mTaskView.CheckTask();
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
				((ImageView) v).setImageResource(R.drawable.restarted);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP)
			{
				((ImageView) v).setImageResource(R.drawable.restart);
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
		mTaskView.onStop();
	}
}
