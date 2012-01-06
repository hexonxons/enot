package app.tascact.manual;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
	
	private AlertDialog alertDialog = null;
		
	private int mTouchedImageNum = 0;
	
    public TaskView(Context context, int[] Res)
    {
		super(context);
		//TO DO - перенести это в ресурсы
		int[] _rightStarts = {R.drawable.pg5_2_task_5};
		int[] _rightEnds = {R.drawable.pg5_2_task_2};
		TaskMatch type = new TaskMatch(_rightStarts, _rightEnds);
		
		mTask = new CTask(context, type, Res);
		setFocusable(true);
	       
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		
		mPrevTouchPoint = new PointF();
		
		alertDialog = new AlertDialog.Builder(context).create();
		
		
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
    	
    	canvas.drawBitmap(mTask.mCheckBitmap, mTask.mCheck.left, mTask.mCheck.top, mPaint);
    	canvas.drawBitmap(mTask.mRestartBitmap, mTask.mRestart.left, mTask.mRestart.top, mPaint);
    	
	}
    
    @Override public boolean onTouchEvent(MotionEvent event)
	{
		// получаем action
	    int eventAction = event.getAction(); 
	    
	    // получаем координаты прикасания
	    float X = event.getX(); 
	    float Y = event.getY(); 
	    
	    switch (mTask.mTaskType.id)
	    {
	    case 1:
	    {
	    TaskMatch taskCopy = (TaskMatch)mTask.mTaskType;			
	    switch (eventAction)
	    { 
	    	// action - нажали на экран
		    case MotionEvent.ACTION_DOWN:
		    {
		    	mTouchedImageNum = mTask.getTouchedImgId((int)X, (int)Y);
		    	if (mTouchedImageNum > 0)
	    		{	    			
		    		isSetLine = true;
		    		mPrevTouchPoint.set(X, Y);
	    			mPrevBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
	    			taskCopy.currentStarts.add(mTouchedImageNum);
	    		}
	    		else
	    		{
	    			
	    			isSetLine = false;
	    			//если нажали на проверку
	    			if (mTouchedImageNum == -1)
	    			{
	    				/*String b = "";
	    				for (int i = 0; i < a.currentStarts.size(); i++)
	    				{
	    					b += Integer.toString(a.currentStarts.get(i));
	    					b += " ";
	    				}
	    				b += "      ";
	    				for (int i = 0; i < a.rightStarts.length; i++)
	    				{
	    					b += Integer.toString(a.rightStarts[i]);
	    					b += " ";
	    				}
	    				b += "      ";
	    				for (int i = 0; i < a.currentEnds.size(); i++)
	    				{
	    					b += Integer.toString(a.currentEnds.get(i));
	    					b += " ";
	    				}
	    				b += "        ";
	    				for (int i = 0; i < a.rightEnds.length; i++)
	    				{
	    					b += Integer.toString(a.rightEnds[i]);
	    					b += " ";
	    				}
	    				b += "        ";
	    				b += Boolean.toString(mTask.Check());*/
	    				alertDialog.setMessage(Boolean.toString(taskCopy.Check())) ;
	    				alertDialog.show();
	    			}
	    			if (mTouchedImageNum == -2)
	    			{
	    				mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		    			mCanvas = new Canvas(mBitmap);
		    			taskCopy.currentStarts.clear();
		    			taskCopy.currentEnds.clear();
	    				
	    			}
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
		    	
		    	
	    		if (mTask.getTouchedImgId((int)X, (int)Y) > 0)
	    		{
	    			//mCanvas.drawLine((float)865, (float)565, X, Y, mPaint);
	    			mCanvas.drawCircle(X, Y, 5, mPaint);
	    			DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
	    			taskCopy.currentEnds.add(mTask.getTouchedImgId((int)X, (int)Y));
	    		}
	    		else
	    		{
	    			mBitmap = mPrevBitmap;//Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
	    			mCanvas = new Canvas(mBitmap);
	    			taskCopy.currentStarts.remove(taskCopy.currentStarts.size() - 1);
	    		}
	    		
	    		invalidate(); 
		    	break;
		    }
	    } 
	    
		return true;
	    }
	    default: 
	    	return true;
	    }
	    
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