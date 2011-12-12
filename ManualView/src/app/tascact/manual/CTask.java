package app.tascact.manual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class CTask
{
	private int mTaskResId[] = null;
	private int mTaskType = 0;
	
	public Bitmap mTaskResBitmaps[] = null;
	
	private Point mSetCoord[];
	
	CTask(Context context, int _TaskType, int _TaskResId[])
	{
		mTaskType = _TaskType;
		mTaskResId = _TaskResId.clone();
		
		mTaskResBitmaps = new Bitmap[mTaskResId.length];
		mSetCoord = new Point[mTaskResId.length];
		for(int i = 0; i < mTaskResId.length; ++i)
		{
			mSetCoord[i] = new Point();
		}
		
		// получаем массив размещения элементов
		for(int i = 0; i < mTaskResId.length; ++i)
		{
			mTaskResBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), mTaskResId[i]);
		}
	}
	
	public Point[] getSetCoord(int width, int height)
	{		
		int avgW = width / mSetCoord.length;
		int avgH = height / mSetCoord.length;
		
		for(int i = 0; i < mSetCoord.length; ++i)
		{	
			int picWidth = mTaskResBitmaps[i].getWidth();
			int picHeight = mTaskResBitmaps[i].getHeight();
			
			// волшебная формула по вставке картинки посередине блока
			//
			// Heh..
			//
			// Через годы, через реки
			// Тянут-потянут срок смертники-бурлаки
			// С первым криком крылья-веки
			// Открыли для меня вечные сумерки
			
			/*
			 *  	 _________________
			 *  	 |
			 *       |
			 * avgH  |
			 *   	 |
			 *  	 |
			 *  	 |________________
			 * 		 |
			 * 		 |
			 * avgH  |
			 * 		 |
			 * 	 	 ..................
			 * 				________
			 * 				|
			 * picHeight 	|
			 * 				|_______
			 */
			mSetCoord[i].y = avgH * (i + 1) / 2 - picHeight / 2;
			mSetCoord[i].x = (i % 2) == 0 ? 40 : width - picWidth - 40;
		}
		
		return mSetCoord;
	}
}
