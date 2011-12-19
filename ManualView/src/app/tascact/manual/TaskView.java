package app.tascact.manual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

public class TaskView extends View
{
	// размеры экрана
	private int mWidth = 0;
	private int mHeight = 0;
	
	private CTask mTask = null;
	private Paint mPaint = null;
	private Canvas mCanvas = null;
	private Bitmap mBitmap = null;
	private Bitmap mPrevBitmap = null;
	private PointF mPrevTouchPoint;
	private boolean isSetLine = false;
	
    public TaskView(Context context, int[] Res)
    {
		super(context);
		mTask = new CTask(context, 0, Res);
		setFocusable(true);
	       
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		
		mPrevTouchPoint = new PointF();
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mWidth = w;
        mHeight = h;
        mCanvas = new Canvas(mBitmap);
    }
    
    // отрисовка страницы
    @Override protected void onDraw(Canvas canvas) 
	{
    	Point[] set = mTask.getSetCoord(mWidth, mHeight);
    	//mCanvas = canvas;
    	
    	canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    	
    	for(int i = 0; i < set.length; ++i)
    	{
    		canvas.drawBitmap(mTask.mTaskResBitmaps[i], set[i].x, set[i].y, mPaint);
    	}
    	
	}
    
    @Override public boolean onTouchEvent(MotionEvent event)
	{
		// получаем action
	    int eventAction = event.getAction(); 
	    
	    // получаем координаты прикасания
	    float X = event.getX(); 
	    float Y = event.getY(); 
		
	    switch (eventAction)
	    { 
	    	// action - нажали на экран
		    case MotionEvent.ACTION_DOWN:
		    {
	    		if (mTask.getTouchedImgId((int)X, (int)Y) != 0)
	    		{
	    			isSetLine = true;
	    			mPrevTouchPoint.set(X, Y);
	    			mPrevBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
	    		}
	    		else
	    		{
	    			isSetLine = false;
	    		}
	    		
	    		invalidate(); 
	    		break; 
		    }
		    
		    // action - ведем палец по экрану
		    case MotionEvent.ACTION_MOVE:
		    {
		    	if(!isSetLine)
		    	{
		    		invalidate(); 
		    		break;
		    	}
		    	
		    	mCanvas.drawCircle(X, Y, 5, mPaint);
		    	DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
		    	mPrevTouchPoint.set(X, Y);
		    	invalidate(); 
		        break; 
		    }
		    
		    // action - убрали палец
		    case MotionEvent.ACTION_UP:
		    {
		    	if(!isSetLine)
		    	{
		    		invalidate();
		    		break;
		    	}
		    	
		    	
	    		if (mTask.getTouchedImgId((int)X, (int)Y) != 0)
	    		{
	    			//mCanvas.drawLine((float)865, (float)565, X, Y, mPaint);
	    			mCanvas.drawCircle(X, Y, 5, mPaint);
	    			DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
	    		}
	    		else
	    		{
	    			mBitmap = mPrevBitmap;//Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
	    			mCanvas = new Canvas(mBitmap);
	    		}
	    		
	    		invalidate(); 
		    	break;
		    }
	    } 
		return true;
	}
    
    private void DrawCurveLine(float firstX, float firstY, float secondX, float secondY, float lineWidth)
	{
		// запоминаем предыдущую ширину линии
		float lPrevWidth = mPaint.getStrokeWidth();
		mPaint.setStrokeWidth(lineWidth);
		mCanvas.drawLine(firstX, firstY, secondX, secondY, mPaint);
		mPaint.setStrokeWidth(lPrevWidth);		
	}
}