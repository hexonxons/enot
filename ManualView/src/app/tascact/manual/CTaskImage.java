package app.tascact.manual;

import android.graphics.Bitmap;

public class CTaskImage
{
	public int resId;
	public int finalResId;
	public int height;
	public int width;
	public int left;
	public int top;
	
	CTaskImage(Bitmap bmp, int _resId, int _finalResId)
	{
		resId = _resId;
		finalResId = _finalResId;
		height = bmp.getHeight();
		width = bmp.getWidth();
	}
	
	public boolean isInto(int X, int Y)
	{
		return ((X > left) && (X < left + width) && 
				(Y > top) && (Y < top + height));
	}
	
}