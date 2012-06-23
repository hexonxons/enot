package com.hexonxons.enote.activity;

import com.hexonxons.enote.view.MainView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity 
{
	private MainView mMainView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mMainView = new MainView(this);
		this.setContentView(mMainView);
	}
}
