/*
 * TaskControlView класс
 * 
 * View управляющего элемента для задач
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

public class TaskControlView extends RelativeLayout
{
	// Кнопка "Проверить"
	private ImageView mCheckButton = null;
	// Кнопка "Попробовать заново"
	private ImageView mRestartButton = null;

    public TaskControlView(Context context)
    {
		super(context);

		LayoutParams mParams = null;
		this.setBackgroundResource(R.drawable.taskcontrol);
		// Создаем кнопку перехода на предыдущую страницу
		mCheckButton = new ImageView(context);
		mCheckButton.setImageResource(R.drawable.check);
		mCheckButton.setId(0xF342AA);

		// Создаем кнопку перехода к оглавлению
		mRestartButton = new ImageView(context);
		mRestartButton.setImageResource(R.drawable.restart);
		mRestartButton.setId(0xF752A2);

		// Собственно, рисуем кнопки
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mParams.setMargins(10, 0, 0, 0);
		mCheckButton.setLayoutParams(mParams);
		this.addView(mCheckButton);

		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, mCheckButton.getId());
		mParams.setMargins(0, 0, 10, 0);
		mRestartButton.setLayoutParams(mParams);
		this.addView(mRestartButton);
	}
    
	public void setListeners(OnTouchListener Check, OnTouchListener Restart)
	{
		mCheckButton.setOnTouchListener(Check);
		mRestartButton.setOnTouchListener(Restart);
	}

    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
	}   
}