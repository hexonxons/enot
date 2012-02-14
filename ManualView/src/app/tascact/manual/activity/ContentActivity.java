/*
 * ContentActivity �����
 * 
 * ������ �������� ����������
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.activity;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.tascact.manual.R;
import app.tascact.manual.view.ContentView;

public class ContentActivity extends Activity
{
	ContentView mMainView = null;
	private int mPageCount = 0;
	private final String title = "Страница ";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // �������� ���������� �������
        Bundle extras = getIntent().getExtras();
        mPageCount = extras.getInt("PageCount");
        mMainView = new ContentView(this);
        
        RelativeLayout.LayoutParams mParams = null;
        
        int red = 0;
        int green = 0;
        int blue = 0;
        Calendar c = Calendar.getInstance(); 
        int seconds = c.get(Calendar.SECOND);
        Random rand = new Random();
        rand.setSeed(seconds);
        for(int i = 0; i < mPageCount; ++i)
		{
        	// ����� ������ ����������
			RelativeLayout newRow = new RelativeLayout(this);
			// ������� 130 px
			newRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 130));
			// ��� ������
			/*if(i % 2 == 0)
				newRow.setBackgroundColor(Color.rgb(214, 214, 169));
			else
				newRow.setBackgroundColor(Color.rgb(224, 224, 168));*/
			red = (red + rand.nextInt()) % 256;
			green = (green + rand.nextInt()) % 256;
			blue = (blue + rand.nextInt()) % 256;
			newRow.setBackgroundColor(Color.rgb(red, green, blue));
			
			
			// ����� � ��������
			TextView text = new TextView(this);
			ImageView img = new ImageView(this);

			// ������������ ������ - �� ������
			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			// ������� ������� �����, ����� ������ � ������ ������
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX , 60);
			text.setTextColor(Color.BLACK);
			text.setText(title + Integer.toString(i + 1));	
			// ��������� �����
			newRow.addView(text, mParams);

			// ������������ �������� - ������
			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			// ����� �� ������ �������
			mParams.setMargins(0, 0, 10, 0);
			// ���� id-���� - ����� ��������
			img.setId(i);
			// ��������� ����� �� ��������
			img.setOnClickListener(mClickListener);
			// ����������, ���� ��������
			img.setImageResource(R.drawable.contents);
			// ��������� ��������
			newRow.addView(img, mParams);

			// ��������� ������� ����������
			mMainView.addContextElem(newRow);
		}
        setContentView(mMainView);
    }
    
    private OnClickListener mClickListener = new OnClickListener()
   	{
   		//@Override
   		public void onClick(View v)
   		{
   			Intent intent = getIntent();
   			
   			intent.putExtra("page", v.getId());
   			setResult(Activity.RESULT_OK, intent);
       		finish();
   		}
   	};
}