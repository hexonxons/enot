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
	private final String title = "Страница ";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // получаем количество страниц
        Bundle extras = getIntent().getExtras();
        mPageCount = extras.getInt("PageCount");
        mMainView = new ContentView(this);
        
        RelativeLayout.LayoutParams mParams = null;
        
        for(int i = 0; i < mPageCount; ++i)
		{
        	// новая строка оглавления
			RelativeLayout newRow = new RelativeLayout(this);
			// высотой 130 px
			newRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 130));
			// фон строки
			if(i % 2 == 0)
				newRow.setBackgroundColor(Color.rgb(214, 214, 169));
			else
				newRow.setBackgroundColor(Color.rgb(224, 224, 168));
			
			// текст и картинка
			TextView text = new TextView(this);
			ImageView img = new ImageView(this);
			
			// расположение текста - по центру
			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			// задание размера буквы, цвета текста и самого текста
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX , 60);
			text.setTextColor(Color.BLACK);
			text.setText(title + Integer.toString(i + 1));	
			// вставляем текст
			newRow.addView(text, mParams);
			
			// расположение картинки - справа
			mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			// сдвиг от правой границы
			mParams.setMargins(0, 0, 10, 0);
			// даем id-шник - номер страницы
			img.setId(i);
			// обработка клика по картинке
			img.setOnClickListener(mClickListener);
			// собственно, сама картинка
			img.setImageResource(R.drawable.contents);
			// вставляем картинку
			newRow.addView(img, mParams);
			
			// вставляем строчку оглавления
			mMainView.addContextElem(newRow);
		}
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