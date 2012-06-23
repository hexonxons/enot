package com.hexonxons.enote.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hexonxons.enote.R;
import com.hexonxons.enote.activity.HomeActivity;

public class MainView extends ViewGroup
{
	private ButtonElement mBookButton = null;
	private ButtonElement mCopybookButton = null;
	private ButtonElement mDiaryButton = null;
	private ButtonElement mSettingsButton = null;
	
	public MainView(Context context)
	{
		super(context);
		
		this.setBackgroundColor(Color.WHITE);
		
		mBookButton = new ButtonElement(context, R.drawable.main_book, "Учебники", new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), HomeActivity.class);
	   			getContext().startActivity(intent);			
			}
		});
		mCopybookButton = new ButtonElement(context, R.drawable.main_copybook, "Тетрадки", null);
		mDiaryButton = new ButtonElement(context, R.drawable.main_diary, "Дневник", null);
		mSettingsButton = new ButtonElement(context, R.drawable.main_settings, "Настройки", null);
		
		this.addView(mBookButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
		this.addView(mCopybookButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mDiaryButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mSettingsButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int w = r - l;
		int h = b - t;
		int childCount = this.getChildCount();

		// button icon size is 1x1
		if(w > h)
		{
			// draw child in one row with 2 px offset
			int BTN_W_OFFSET = 2;
			int offset = BTN_W_OFFSET;
			int buttonW = w / 4 - 2 * offset;
			int buttonH = buttonW;
			int topOffset = (h - buttonH) / 2;
			
			for (int i = 0; i < childCount; i++)
	        {
	            View child = getChildAt(i);
	            if (child.getVisibility() != GONE)
	            {
	            	child.layout(offset, topOffset, offset + buttonW, topOffset + buttonH);
	            	offset += buttonW + 2 * BTN_W_OFFSET;
	            }
	        }
		}
		else
		{
			// draw child in 2 rows with 4 px offset
			int BTN_W_OFFSET = 2;
			int offset = BTN_W_OFFSET;
			int buttonW = w / 2 - 2 * offset;
			int buttonH = buttonW;
			int topOffset = (h - 2 * buttonH) / 2;
			
			for (int i = 0; i < childCount; i++)
	        {
	            View child = getChildAt(i);
	            if (child.getVisibility() != GONE)
	            {
	            	child.layout(offset, topOffset, offset + buttonW, topOffset + buttonH);
	            	offset += buttonW + 2 * BTN_W_OFFSET;
	            	
	            	if(i == 1)
	            	{
	            		offset = BTN_W_OFFSET;
	            		topOffset += buttonH + 2 * BTN_W_OFFSET;
	            	}
	            }
	        }
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		measureChildren(sizeWidth, sizeHeight);
		setMeasuredDimension(sizeWidth, sizeHeight);
	}
	
	/**
	 * Main view button element
	 * 
	 * @author hexonxons
	 *
	 */
	private class ButtonElement extends LinearLayout
	{
		public ButtonElement(Context context, int buttonResource, String buttonLabel, OnClickListener l)
		{
			super(context);
			
			this.setOrientation(LinearLayout.VERTICAL);
			this.setOnClickListener(l);
			
			ImageButton button = new ImageButton(context);
			button.setOnClickListener(l);
			button.setBackgroundResource(buttonResource);
			this.addView(button);
			
			TextView text = new TextView(context);
			text.setGravity(Gravity.CENTER);
			text.setText(buttonLabel);
			text.setTextSize(20);
			text.setTextColor(Color.DKGRAY);
			this.addView(text, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			this.getChildAt(0).setLayoutParams(new LayoutParams(w, h - 30));
			super.onSizeChanged(w, h, oldw, oldh);
		}
	}
}
