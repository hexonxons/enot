package app.taskact.connect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

public class ConnectElemView extends View
{
	// картинка начальной позиции
	private Bitmap ball;
	// картинка конечной позиции
	private Bitmap ballpos;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
    
    private boolean isSetLine = false;
    
    private int height;
    private int width;
 
    private PointF mPrevTouchPoint;
    
	public ConnectElemView(Context context)
	{
		super(context);
		setFocusable(true);
		
		// создаем картинку
		ball = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
		ballpos = BitmapFactory.decodeResource(context.getResources(), R.drawable.ballpos);
	       
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		
		mPrevTouchPoint = new PointF();
		
	}
   
	@Override protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		canvas.drawBitmap(ball, 100, 500, mPaint);		
		canvas.drawBitmap(ballpos, 800, 500, mPaint);
	}
	
	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        width = w;
        height = h;
        mCanvas = new Canvas(mBitmap);
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
	    		// получаем координаты центра перемещаемого шара
	    		int centerX = 100 + 65;
	    		int centerY = 500 + 65;

	    		double radCircle  = Math.sqrt( (double) (((centerX - X)*(centerX - X)) + (centerY - Y)*(centerY - Y)));
	    		
	    		if (radCircle < 65)
	    		{
	    			isSetLine = true;
	    			mPrevTouchPoint.set(X, Y);
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
		    	
		    	// получаем координаты центра перемещаемого шара
	    		int centerX = 800 + 65;
	    		int centerY = 500 + 65;

	    		double radCircle  = Math.sqrt( (double) (((centerX - X)*(centerX - X)) + (centerY - Y)*(centerY - Y)));
		    	
	    		if (radCircle < 65)
	    		{
	    			//mCanvas.drawLine((float)865, (float)565, X, Y, mPaint);
	    			mCanvas.drawCircle(X, Y, 5, mPaint);
	    			DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
	    		}
	    		else
	    		{
	    			mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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