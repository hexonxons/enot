/*
 * ContentActivity класс
 * 
 * Запуск процесса оглавления
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.R;
import app.tascact.manual.view.ContentView;

public class ContentActivity extends Activity
{
	ContentView mMainView = null;
	private int mPageCount = 0;
	private int mHeight = 0;
	private final String title = "Страница ";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        mPageCount = extras.getInt("PageCount");
        mMainView = new ContentView(this);
        RelativeLayout.LayoutParams mParams = null;
        
        for(int i = 0; i < mPageCount; ++i)
		{
			RelativeLayout newRow = new RelativeLayout(this);
			newRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 130));
			mHeight += 130;
			if(i % 2 == 0)
				newRow.setBackgroundColor(Color.rgb(214, 214, 169));
			else
				newRow.setBackgroundColor(Color.rgb(224, 224, 168));
			
			TextView text = new TextView(this);
			ImageView img = new ImageView(this);

			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX , 60);
			text.setTextColor(Color.BLACK);
			text.setText(title + Integer.toString(i + 1));	
			newRow.addView(text, mParams);
			
			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			mParams.setMargins(0, 0, 10, 0);
			img.setId(i);
			img.setOnClickListener(mClickListener);
			img.requestFocusFromTouch();
			img.setImageResource(R.drawable.contents);
			newRow.addView(img, mParams);
			
			mMainView.addView(newRow);
		}
        mMainView.setHeight(mHeight);
        setContentView(mMainView);
    }
    
    private OnClickListener mClickListener = new OnClickListener()
   	{
   		@Override
   		public void onClick(View v)
   		{
   			Intent intent = getIntent();
   			
   			intent.putExtra("page", v.getId());
   			setResult(Activity.RESULT_OK, intent);
       		finish();
   		}
   	};
}