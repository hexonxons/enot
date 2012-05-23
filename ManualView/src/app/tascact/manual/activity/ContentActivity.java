/*
 * ContentActivity класс
 * 
 * Запуск процесса выбора отображаемой страницы
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.tascact.manual.R;
import app.tascact.manual.view.ContentView;

public class ContentActivity extends Activity
{
	ContentView mMainView = null;
	private int mPageCount = 0;
	private static final String TITLE = "Страница ";
	private int mRotation = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // No title display
     	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
     	mRotation = getWindowManager().getDefaultDisplay().getRotation();
        Bundle extras = getIntent().getExtras();
        mPageCount = extras.getInt("PageCount");
        mMainView = new ContentView(this);
        ContentRow row = null;
        
        for(int i = 0; i < mPageCount; ++i)
		{   
        	if(mRotation % 2 == 0)
        	{
        		if(i % 2 == 0 || i == mPageCount - 1)
        			row = new ContentRow(this);
        	}
        	else
        		row = new ContentRow(this);
        	
        	ContentElement newElement = new ContentElement(this, i);
        	newElement.setOnClickListener(mClickListener);
        	row.addView(newElement);
        	
        	if(mRotation % 2 == 0)
        	{
        		if(i % 2 != 0 || i == mPageCount - 1)
        			mMainView.addContextElem(row);
    		}
        	else
        		mMainView.addContextElem(row);
        		
		}
        setContentView(mMainView);
    }
    
    private OnClickListener mClickListener = new OnClickListener()
   	{
		@Override
		public void onClick(View v)
		{
   			Intent intent = getIntent();
   			v.setBackgroundColor(Color.rgb(220, 220, 220));
   			intent.putExtra("page", v.getId()+1);
   			setResult(Activity.RESULT_OK, intent);
       		finish();
		}
   	};
   	
   	private class ContentElement extends LinearLayout
   	{
		public ContentElement(Context context, int pageNumber)
		{
			super(context);

			this.setOrientation(LinearLayout.VERTICAL);
			this.setBackgroundColor(Color.WHITE);
			this.setId(pageNumber);
			
			LinearLayout content = new LinearLayout(context);
			
			TextView label = new TextView(context);
			label.setTextSize(TypedValue.COMPLEX_UNIT_PX , 60);
			label.setTextColor(Color.BLACK);
			label.setText(TITLE + Integer.toString(pageNumber + 1));
			label.setGravity(Gravity.CENTER);
			content.addView(label);
			
			ImageView image = new ImageView(context);
			image.setImageResource(R.drawable.contents);
			content.addView(image);
			
			this.addView(content);
			
			ImageView separator = new ImageView(context);
			separator.setBackgroundResource(R.drawable.separator_contents);
			this.addView(separator);
		}
   		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh )
		{
			super.onSizeChanged(w, h, oldw, oldh);
			LinearLayout child = (LinearLayout) this.getChildAt(0);
			child.getChildAt(0).setLayoutParams(new LayoutParams((int) (w * 0.8), (int) (h * 0.4)));
			child.getChildAt(1).setLayoutParams(new LayoutParams((int) (w * 0.2), (int) (h * 0.4)));
		}
   	}
   	
   	private class ContentRow extends LinearLayout
   	{
		public ContentRow(Context context)
		{
			super(context);
		}
   		
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b)
		{
	        for (int i = 0; i < getChildCount(); i++)
	        {
	            View child = getChildAt(i);
	            if (child.getVisibility() != GONE)
	            {
	            	if(mRotation % 2 == 0)
	            		child.layout((i % 2) * (r - l) / 2, 0, indexOfChild(child) == 0 ? (r - l) / 2 : (r - l), b - t);
	            	else
	            		child.layout(0, 0, r, b - t);
	            }
	        }
		}

   	}
}