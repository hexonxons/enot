/*
 * ContentView класс
 * 
 * View оглавления
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package com.hexonxons.enote.view;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hexonxons.enote.R;

public class ContentView extends LinearLayout 
{	
	private final static String CONTENTS_LABEL = "Оглавление";
	private final static String GOTO_LABEL = "Перейти к";
	private int mPageNumber = 0;
	private OnClickListener listener;
	
	public ContentView(Context context, int pageNumber, OnClickListener l)
	{
		super(context);
		this.setBackgroundColor(Color.WHITE);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.contents_tab_layout, this);
		TabHost th = (TabHost)v.findViewById(R.id.tabhost);
		th.setup();
		
		mPageNumber = pageNumber;
		listener = l;

		th.addTab(th.newTabSpec(GOTO_LABEL).setContent(new TabHost.TabContentFactory()
		{
			@Override
			public View createTabContent(String tag) 
			{
				return new Goto(getContext(), mPageNumber, listener);
			}
		}).setIndicator(GOTO_LABEL));
		
		th.addTab(th.newTabSpec(CONTENTS_LABEL).setContent(new TabHost.TabContentFactory()
		{
			@Override
			public View createTabContent(String tag) 
			{
				return new Content(getContext(), mPageNumber, listener);
			}
		}).setIndicator(CONTENTS_LABEL));
	}
	
	private class Goto extends LinearLayout
	{
		EditText mPageNumber = null;
		Button mGoToButton = null;
		LinearLayout mMainLayout = null;
		TextView mTotalPageCount = null;
		
		public Goto(Context context, int pageNumber, final OnClickListener listener)
		{
			super(context);
			
			this.setOrientation(LinearLayout.VERTICAL);
			
			TextView label = new TextView(context);
			label.setText("Введите номер страницы");
			label.setTextColor(Color.BLACK);
			label.setTextSize(30);
			label.setGravity(Gravity.CENTER);
			
			mTotalPageCount = new TextView(context);
			mTotalPageCount.setText("/" + Integer.toString(pageNumber));
			mTotalPageCount.setTextColor(Color.BLACK);
			mTotalPageCount.setTextSize(30);
			mTotalPageCount.setGravity(Gravity.CENTER);
			
			mPageNumber = new EditText(context);
			mPageNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
			mPageNumber.setGravity(Gravity.CENTER);
			mPageNumber.setOnFocusChangeListener(new OnFocusChangeListener()
			{
				@Override
				public void onFocusChange(View v, boolean hasFocus)
				{
					if(!hasFocus)
					{
						InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mPageNumber.getWindowToken(), 0);
					}
				}
			});
			
			mGoToButton = new Button(context);
			mGoToButton.setText("Перейти");
			mGoToButton.setTextColor(Color.BLACK);
			mGoToButton.setTextSize(30);
			mGoToButton.setGravity(Gravity.CENTER);
			mGoToButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					if(mPageNumber.getText().toString().isEmpty())
						return;
					int page = Integer.parseInt(mPageNumber.getText().toString()) - 1;
					if(page < 0 || page > Integer.parseInt((String) mTotalPageCount.getText().subSequence(1, mTotalPageCount.getText().length())) - 1)
						return;
					v.setId(page);
					listener.onClick(v);
				}
			});
			
			mMainLayout = new LinearLayout(context);
			mMainLayout.setWeightSum(4);
			LayoutParams params = new LayoutParams(60, LayoutParams.MATCH_PARENT);
			params.weight = 2;
			mMainLayout.addView(mPageNumber, params);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			mMainLayout.addView(mTotalPageCount, params);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.weight = 2;
			mMainLayout.addView(mGoToButton, params);
			
			this.addView(label, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			this.addView(mMainLayout);
		}
		
	}
	
	private class Content extends ScrollView
	{
		private LinearLayout mMainTable = null;
		private static final String TITLE = "Страница ";
		private static final int ELEMENT_H = 60;
		
		public Content(Context context, int pageNumber, OnClickListener l)
		{
			super(context);

			mMainTable = new LinearLayout(context);
			this.setBackgroundColor(Color.WHITE);
			mMainTable.setOrientation(1);
			this.addView(mMainTable);
			mPageNumber = pageNumber;
	        
	        for(int i = 0; i < pageNumber; ++i)
			{   
	        	LinearLayout row = new LinearLayout(context);
	        	
	        	ContentElement newElement = new ContentElement(context, i);
	        	newElement.setOnClickListener(l);
	        	row.addView(newElement, new LayoutParams(LayoutParams.MATCH_PARENT, ELEMENT_H));
	        	mMainTable.addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, ELEMENT_H));        		
			}
	        
	        this.setOverScrollMode(OVER_SCROLL_NEVER);
			// Показываем скроллбары
			setVerticalScrollBarEnabled(true);
		}
		
		private class ContentElement extends LinearLayout
	   	{
			private TextView mLabel = null;
			private ImageView mImage = null;
			private LinearLayout mContent = null;
			
			
			public ContentElement(Context context, int pageNumber)
			{
				super(context);

				this.setOrientation(LinearLayout.VERTICAL);
				this.setBackgroundColor(Color.WHITE);
				this.setId(pageNumber);
				
				mContent = new LinearLayout(context);
				mContent.setWeightSum(5);
				mLabel = new TextView(context);
				mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX , ELEMENT_H / 2);
				mLabel.setTextColor(Color.BLACK);
				mLabel.setText(TITLE + Integer.toString(pageNumber + 1));
				mLabel.setGravity(Gravity.CENTER);
				
				mContent.addView(mLabel);
				
				mImage = new ImageView(context);
				mImage.setImageResource(R.drawable.contents);
				mContent.addView(mImage);
				
				this.addView(mContent);
				
				ImageView separator = new ImageView(context);
				separator.setBackgroundResource(R.drawable.separator_contents);
				this.addView(separator, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			
			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh)
			{
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.weight = 1;
				mContent.setLayoutParams(params);
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				params.weight = 4;
				mLabel.setLayoutParams(params);
				params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				params.weight = 1;
				mImage.setLayoutParams(params);
				super.onSizeChanged(w, h, oldw, oldh);
			}
	   	}
	}
}