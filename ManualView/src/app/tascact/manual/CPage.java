/*
 * CPage класс
 * 
 * Описание страницы учебника
 * 
 * Copyright 2011 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CPage
{
	// массив отдельных элементов страницы учебника
	private CPageImage pages[] = null;
	
	// ширина и высота экрана
	private int mWindowWidth;
	private int mWindowHeight;
	// конструктор из массива id`шников элементов
	public CPage(Context context, int _pageRes[])
	{
		clear();
		// размер массива
		int len = _pageRes.length;
		
		pages = new CPageImage[len];
		
		for(int i = 0; i < len; ++i)
		{
			pages[i] = new CPageImage(context, _pageRes[i]);
		}
	}
	
	// получение номера элемента, внутри которого лежат 2 координаты
	public int getTouchedImageNum(int X, int Y)
	{
		for(int i = 0; i < pages.length; ++i)
		{
			if(pages[i].isInto(X, Y))
				return i;
		}
		
		return -1;
	}
	
	// метод отрисовки страницы
	public void drawPage(Canvas canvas, Paint paint)
	{
		int left = 0;
		int top = 0;
		
		// каждый элемент рисуем со сдвигом вниз на 5px относительно предыдущего 
		for(int i = 0; i < pages.length; ++i)
		{
			if(i != 0)
			{
				top += pages[i - 1].height;
			}
			
			// запоминаем абсолютные координаты левого верхнего угла
			pages[i].left = left;
			pages[i].top = top;
			canvas.drawBitmap(pages[i].img, left, top, paint);
		}
	}
	
	
	// метод установки размеров экрана
	public void setDims(int _width, int _height)
	{
		mWindowHeight = _height;
		mWindowWidth = _width;
	}
	
	private void clear()
	{
		pages = null;
	}
}
