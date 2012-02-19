/*
 * HomeActivity ����������
 * 
 * ������������ ���������������� �������������� ����������������
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import app.tascact.manual.R;

public class HomeActivity extends Activity
{
	private RelativeLayout mMainLayout = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        LayoutParams mParams = null;
        mMainLayout = new RelativeLayout(this);
        mMainLayout.setBackgroundColor(Color.GRAY);
        
        ImageButton mManual_1_1 = new ImageButton(this);
        ImageButton mManual_1_2 = new ImageButton(this);
        
        mManual_1_1.setBackgroundResource(R.drawable.manual_1_1);
        mManual_1_1.setId(1);
        
        mManual_1_2.setBackgroundResource(R.drawable.manual_1_2);
        mManual_1_2.setId(2);
        
        mManual_1_1.setOnClickListener(mClickListener);
        mManual_1_2.setOnClickListener(mClickListener);

		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mParams.setMargins(70, 0, 0, 0);
		mManual_1_1.setLayoutParams(mParams);
		mMainLayout.addView(mManual_1_1);
		
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mParams.setMargins(490, 0, 0, 0);
		mManual_1_2.setLayoutParams(mParams);
		mMainLayout.addView(mManual_1_2);
		

        
        setContentView(mMainLayout);
    }
    
    private OnClickListener mClickListener = new OnClickListener()
   	{
   		@Override
   		public void onClick(View v)
   		{
			Intent intent = new Intent(v.getContext(), ManualActivity.class);
			intent.putExtra("bookName", "book.xml");
   			startActivity(intent);
   		}
   	};
}