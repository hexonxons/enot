package app.tascact.manual;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class TaskView extends View
{
	// размеры экрана
	private int mWidth = 0;
	private int mHeight = 0;
	
	private CTask mTask = null;
	private final Paint mPaint = new Paint();
	
    public TaskView(Context context, int[] Res)
    {
		super(context);
		mTask = new CTask(context, 0, Res);
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
    
    // отрисовка страницы
    @Override protected void onDraw(Canvas canvas) 
	{
    	Point[] set = mTask.getSetCoord(mWidth, mHeight);
    	for(int i = 0; i < set.length; ++i)
    	{
    		canvas.drawBitmap(mTask.mTaskResBitmaps[i], set[i].x, set[i].y, mPaint);
    	}
	}
}