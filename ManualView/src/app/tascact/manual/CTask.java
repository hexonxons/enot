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
	public CTaskImage mTaskImages[] = null;
	
	public Point mSetCoord[];
	
	CTask(Context context, int _TaskType, int _TaskResId[])
	{
		mTaskType = _TaskType;
		mTaskResId = _TaskResId.clone();
		
		mTaskResBitmaps = new Bitmap[mTaskResId.length];
		mTaskImages = new CTaskImage[mTaskResId.length];
		mSetCoord = new Point[mTaskResId.length];
		for(int i = 0; i < mTaskResId.length; ++i)
		{
			mSetCoord[i] = new Point();
		}
		
		// получаем массив размещения элементов
		for(int i = 0; i < mTaskResId.length; ++i)
		{
			mTaskResBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), mTaskResId[i]);
			CTaskImage elem = new CTaskImage(mTaskResBitmaps[i], mTaskResId[i], 0);
			mTaskImages[i] = elem;
		}
	}
	
	public Point[] getSetCoord(int width, int height)
	{		
		int avgW = width / mSetCoord.length;
		int avgH = height / mSetCoord.length;
		
		for(int i = 0; i < mSetCoord.length; ++i)
		{	
			int picWidth = mTaskImages[i].width;
			int picHeight = mTaskImages[i].height;
			
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
			
			mTaskImages[i].top = mSetCoord[i].y;
			mTaskImages[i].left = mSetCoord[i].x;
		}
		
		return mSetCoord;
	}
	
	public int getTouchedImgId(int X, int Y)
	{
		for(int i = 0; i < mTaskImages.length; ++i)
		{
			if(mTaskImages[i].isInto(X, Y))
			{
				return mTaskImages[i].resId;
			}
		}
		
		return 0;
	}
}
