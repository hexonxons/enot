/**
 * TaskView class
 * 
 * Abstract class for all tasks
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * :mailto hexonxons@gmail.com
 * 
 */

package com.hexonxons.enote.view;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class TaskView extends LinearLayout
{
	public TaskView(Context context)
	{
		super(context);
	}

	// function for restart task
	public abstract void RestartTask();
	// function for check task
	public abstract void CheckTask();
	
	// These 3 procs are needed for tasks with a "game-loop"
	// make abstract and revrite tasks
	public void onPause(){}
	
	public void onResume(){}
	
	public void onStop(){}
	
	public void WriteLog(){}
	public void ReadLog(){}

	public void replay() {}
}