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
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

public class TaskControlView extends RelativeLayout {
	public RelativeLayout mControlLayout = null;
	// Кнопка "Проверить"
	public ImageView mCheckButton = null;
	// Кнопка "Попробовать заново"
	public ImageView mRestartButton = null;

	public TaskControlView(Context context) {
		super(context);

		LayoutParams mParams = null;

		// Создаем RelativeLayout, в котором находятся
		// все кнопки управления
		mControlLayout = new RelativeLayout(context);
		mControlLayout.setBackgroundColor(Color.WHITE);
		mControlLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));

		// Создаем кнопку перехода на
		// предыдущую страницу
		mCheckButton = new ImageView(context);
		mCheckButton.setImageResource(R.drawable.check);
		mCheckButton.setId(0xF342AA);

		// Создаем кнопку перехода к оглавлению
		mRestartButton = new ImageView(context);
		mRestartButton.setImageResource(R.drawable.restart);
		mRestartButton.setId(0xF752A2);

		// Собственно, рисуем
		mParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mParams.setMargins(10, 0, 0, 0);
		mCheckButton.setLayoutParams(mParams);
		mControlLayout.addView(mCheckButton);

		mParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, mCheckButton.getId());
		mParams.setMargins(0, 0, 10, 0);
		mRestartButton.setLayoutParams(mParams);
		mControlLayout.addView(mRestartButton);

		this.addView(mControlLayout);
	}

	// получение размеров экрана
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
}