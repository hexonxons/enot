/*
 * TaskView класс
 * 
 * Абстрактный класс для View всех задач
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;

import android.content.Context;
import android.widget.RelativeLayout;
import javax.xml.*;

public abstract class TaskView extends RelativeLayout
{

	public TaskView(Context context)
	{
		super(context);
	}

	// функция перезапуска задачи
	public abstract void RestartTask();
	// функция проверка задачи
	public abstract void CheckTask();
}