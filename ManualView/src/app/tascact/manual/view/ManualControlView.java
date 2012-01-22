/*
 * ControlView класс
 * 
 * View управляющего элемента для страниц
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

public class ManualControlView extends RelativeLayout
{
	public RelativeLayout mControlLayout = null;
	// Кнопка "Вперед"
	public ImageView mNextButton = null;
	// Кнопка "Назад"
	public ImageView mPrevButton = null;
	// Кнопка оглавления
	public ImageView mContentsButton = null;
	
    public ManualControlView(Context context)
    {
		super(context);

		LayoutParams mParams = null;
		
		// Создаем RelativeLayout, в котором находятся все кнопки управления
		mControlLayout = new RelativeLayout(context);
		mControlLayout.setBackgroundColor(Color.WHITE);
		mControlLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// Создаем кнопку перехода на предыдущую страницу
		mPrevButton = new ImageView(context);
		mPrevButton.setImageResource(R.drawable.prev);
		mPrevButton.setId(0xFF12AA);
		
		// Создаем кнопку перехода к оглавлению
		mContentsButton = new ImageView(context);
		mContentsButton.setImageResource(R.drawable.contents);
		mContentsButton.setId(0xFF12A2);
		
		// Создаем кнопку перехода на следующую страницу
		mNextButton = new ImageView(context);
		mNextButton.setImageResource(R.drawable.next);
		mNextButton.setId(0xFF12A8);
		
		// Собственно, рисуем
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mParams.setMargins(10, 0, 0, 0);
		mPrevButton.setLayoutParams(mParams);
		mControlLayout.addView(mPrevButton);
		
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.LEFT_OF, mNextButton.getId());
		mParams.setMargins(0, 0, 120, 0);
		mContentsButton.setLayoutParams(mParams);
		mControlLayout.addView(mContentsButton);
		
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		mParams.setMargins(0, 0, 10, 0);
		mNextButton.setLayoutParams(mParams);
		mControlLayout.addView(mNextButton);
		
		this.addView(mControlLayout);
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);        
    }
}