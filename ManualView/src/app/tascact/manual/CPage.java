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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CPage
{
	// массив отдельных элементов страницы учебника
	private CPageImage pages[] = null;
	// элемент стрелки перехода на следующую страницу
	private CPageImage mNextArrow = null;
	// элемент стрелки перехода на предыдущую страницу
	private CPageImage mPrevArrow = null;
	// элемент для заливки оставшегося пространства
	private CPageImage mWhite = null;
	
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
		
		mNextArrow = new CPageImage(context, R.drawable.next);
		mPrevArrow = new CPageImage(context, R.drawable.prev);
		mWhite = new CPageImage(context, R.drawable.white);
	}
	
	// получение номера элемента, внутри которого лежат 2 координаты
	public int getTouchedImageNum(int X, int Y)
	{
		for(int i = 0; i < pages.length; ++i)
		{
			if(pages[i].isInto(X, Y))
				return i;
		}
		
		if(mNextArrow.isInto(X, Y))
			return -1;
		
		if(mPrevArrow.isInto(X, Y))
			return -2;
		
		return 0;
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
				top += pages[i - 1].height + 5;
			}
			
			// запоминаем абсолютные координаты левого верхнего угла
			pages[i].left = left;
			pages[i].top = top;
			canvas.drawBitmap(pages[i].img, left, top, paint);
		}
		
		// если осталось свободное место
		if(top < mWindowHeight)
		{
			// заполняем все свободное пространство белым фоном
			// mWindowHeight - top - 10 : высота экрана - top дадут высоту свободного места
			// -10 : просто относительное изменение размера. Сверху и снизу остается 5px
			Bitmap switcher = Bitmap.createScaledBitmap(mWhite.img, mWindowWidth, mWindowHeight - top - 10, true);
			canvas.drawBitmap(switcher, left, top + 5, paint);
			
			// отрисовываем стрелки перехода
			// mWindowWidth - 40 - 90 : отступ от правой границы экрана на 40 px + 90px - размер самой стрелки
			// top + (mWindowHeight - top - 10)/2 - 45 : отступ от верхней границы свободного пространства до его середины
			canvas.drawBitmap(mNextArrow.img, mWindowWidth - 40 - 90, top + (mWindowHeight - top - 10)/2 - 45, paint);
			mNextArrow.left = mWindowWidth - 40 - 90;
			mNextArrow.top = top + (mWindowHeight - top - 10)/2 - 45;
			
			canvas.drawBitmap(mPrevArrow.img, 40, top + (mWindowHeight - top - 10)/2 - 45, paint);
			mPrevArrow.left = 40;
			mPrevArrow.top = top + (mWindowHeight - top - 10)/2 - 45;
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
		mNextArrow = null;
		mPrevArrow = null;
		mWhite = null;
	}
}
