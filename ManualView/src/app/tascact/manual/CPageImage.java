/*
 * CPageImage класс
 * 
 * Описание элемента страницы учебника
 * 
 * Copyright 2011 hexonxons 
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CPageImage
{
	// id`шник картинки
	public int imgResId;
	
	// размеры картинки
	public int height;
	public int width;
	
	// положение left и top отрисованной картинки на странице учебника
	public int left;
	public int top;
	
	// Bitmap картинки
	public Bitmap img;
	
	public CTask task = null;
	
	CPageImage(Context context, int _imgResId)
	{
		imgResId = _imgResId;
		img = BitmapFactory.decodeResource(context.getResources(), imgResId);
		height = img.getHeight();
		width = img.getWidth();
	}
	
	public boolean isInto(int X, int Y)
	{
		return ((X > left) && (X < left + width) && 
				(Y > top) && (Y < top + height));
	}
}
