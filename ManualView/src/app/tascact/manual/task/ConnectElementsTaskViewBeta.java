package app.tascact.manual.task;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import app.tascact.manual.CResources;
import app.tascact.manual.R;
import app.tascact.manual.view.TaskView;

public class ConnectElementsTaskViewBeta extends TaskView
{
	// �������������� ������������
	private int mWidth = 0;
	private int mHeight = 0;
	private CTask mTask = null;
	private Paint mPaint = null;
	private Canvas mCanvas = null;
	private Bitmap mBitmap = null;
	private Bitmap mPrevBitmap = null;
	private PointF mPrevTouchPoint;
	private AlertDialog mAlertDialog = null;
	private int[] mTaskResources = null;
	private boolean mAnswer = true;
	private boolean isSetLine = false;
	private CResources mResources = null;
	private int mTouchedImageId = 0;
	private Point answers[];
	
    public ConnectElementsTaskViewBeta(Context context, int ManualNumber, int PageNumber, int TaskNumber)
    {
		super(context);
		mResources = new CResources(ManualNumber);
		mPrevTouchPoint = new PointF();
		mAlertDialog = new AlertDialog.Builder(context).create();
		mTaskResources = mResources.GetTaskResources(PageNumber, TaskNumber);
		
		answers = new Point[6];
		for (int i = 0; i < 6; ++i)
			answers[i] = new Point();
		
		answers[0].x = R.drawable.manual_1_2_pg62_4_task_2;
		answers[0].y = R.drawable.manual_1_2_pg62_4_task_6;
		
		answers[1].x = R.drawable.manual_1_2_pg62_4_task_4;
		answers[1].y = R.drawable.manual_1_2_pg62_4_task_6;
		
		answers[2].x = R.drawable.manual_1_2_pg62_4_task_4;
		answers[2].y = R.drawable.manual_1_2_pg62_4_task_1;
		
		answers[3].x = R.drawable.manual_1_2_pg62_4_task_1;
		answers[3].y = R.drawable.manual_1_2_pg62_4_task_5;
		
		answers[4].x = R.drawable.manual_1_2_pg62_4_task_3;
		answers[4].y = R.drawable.manual_1_2_pg62_4_task_5;
		
		mTask = new CTask(context, mTaskResources);

		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 100, 25, 55);
		setBackgroundColor(Color.WHITE);
	}
    
    // ������������������ ���������������� ������������
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mWidth = w;
        mHeight = h;
        mCanvas = new Canvas(mBitmap);
    }
    
    // ������������������ ����������������
    @Override protected void onDraw(Canvas canvas) 
	{
    	Point[] set = mTask.getSetCoord(mWidth, mHeight);
    	
    	canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    	DrawCurveLine(400, 450, set[1].x + 100, set[1].y + 100, 10);
    	for(int i = 0; i < set.length; ++i)
    	{
    		canvas.drawBitmap(mTask.mTaskResBitmaps[i], set[i].x, set[i].y, mPaint);
    	}
	}
    
    @Override public boolean onTouchEvent(MotionEvent event)
	{
		// ���������������� action
	    int eventAction = event.getAction(); 
	    
	    // ���������������� �������������������� ��������������������
	    float X = event.getX(); 
	    float Y = event.getY(); 
	    		
	    switch (eventAction)
	    { 
	    	// action - ������������ ���� ����������
		    case MotionEvent.ACTION_DOWN:
		    {
		    	mTouchedImageId = mTask.getTouchedImgId((int)X, (int)Y);
		    	if (mTouchedImageId > 0)
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
		    
		    // action - ���������� ���������� ���� ������������
		    case MotionEvent.ACTION_MOVE:
		    {
		    	if(!isSetLine)
		    	{
		    		invalidate(); 
		    		break;
		    	}
		    	
		    	mCanvas.drawCircle(X, Y, 4, mPaint);
		    	DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
		    	mPrevTouchPoint.set(X, Y);
		    	
		    	invalidate(); 
		        break; 
		    }
		    
		    // action - ������������ ����������
		    case MotionEvent.ACTION_UP:
		    {
		    	if(!isSetLine)
		    	{
		    		invalidate();
		    		break;
		    	}
		    	
		    	
	    		if (mTask.getTouchedImgId((int)X, (int)Y) > 0)
	    		{
	    			mCanvas.drawCircle(X, Y, 5, mPaint);
	    			DrawCurveLine(mPrevTouchPoint.x, mPrevTouchPoint.y, X, Y, 10);
	    			
	    			if(mAnswer)
	    			{
		    			for(int i = 0; i < answers.length; ++i)
		    			{
		    				if((answers[i].x == mTouchedImageId && answers[i].y == mTask.getTouchedImgId((int)X, (int)Y)) ||
	    					   (answers[i].y == mTouchedImageId && answers[i].x == mTask.getTouchedImgId((int)X, (int)Y)))
	    					   {
		    						return true;
	    					   }
		    				
		    			}
		    			mAnswer = false;
	    			}
	    		}
	    		else
	    		{
	    			mBitmap = mPrevBitmap;
	    			mCanvas = new Canvas(mBitmap);
	    		}
	    		
	    		invalidate(); 
		    	break;
		    }
	    } 
    
	    return true;
	}
    
    public void RestartTask()
    {
    	mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mAnswer = true;
		mTouchedImageId = -1;
		invalidate();
    }
    
    public void CheckTask()
    {
    	if(mTouchedImageId == -1)
    		mAnswer = false;
    	mAlertDialog.setMessage(Boolean.toString(mAnswer)) ;
		mAlertDialog.show();
    }
    
    private void DrawCurveLine(float firstX, float firstY, float secondX, float secondY, float lineWidth)
	{
		// �������������������� �������������������� ������������ ����������
		float lPrevWidth = mPaint.getStrokeWidth();
		mPaint.setStrokeWidth(lineWidth);
		mCanvas.drawLine(firstX, firstY, secondX, secondY, mPaint);
		mPaint.setStrokeWidth(lPrevWidth);	
		invalidate();
	}
    
    private class CTask
    {
    	public Bitmap mTaskResBitmaps[] = null;
    	public CTaskResourcesCoordinateSet mTaskSet[] = null;
    	
    	private Point mSetCoord[];
    	
    	public CTask(Context context, int TaskResId[])
    	{    		
    		mTaskResBitmaps = new Bitmap[TaskResId.length];
    		mTaskSet = new CTaskResourcesCoordinateSet[TaskResId.length];
    		
    		mSetCoord = new Point[TaskResId.length];
    		
    		for(int i = 0; i < TaskResId.length; ++i)
    		{
    			mSetCoord[i] = new Point();
    		}
    		
    		// ���������������� ������������ �������������������� ������������������
    		for(int i = 0; i < TaskResId.length; ++i)
    		{
    			mTaskResBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), TaskResId[i]);
    			CTaskResourcesCoordinateSet elem = new CTaskResourcesCoordinateSet(TaskResId[i], mTaskResBitmaps[i].getWidth(), mTaskResBitmaps[i].getHeight());
    			mTaskSet[i] = elem;
    		}
    	}
    	
    	public Point[] getSetCoord(int width, int height)
    	{		
    		//int avgW = width / mSetCoord.length;
    		int avgH = (int)(height * 1.5 / mSetCoord.length);
    		
    		for(int i = 0; i < mSetCoord.length; ++i)
    		{	
    			int picWidth = mTaskSet[i].width;
    			int picHeight = mTaskSet[i].height;
    			
    			// ������������������ �������������� ���� �������������� ���������������� �������������������� ����������
    			
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
    			
    			mTaskSet[i].top = mSetCoord[i].y;
    			mTaskSet[i].left = mSetCoord[i].x;
    			if(i == 6)
    			{
        			mTaskSet[i].top = 300;
        			mTaskSet[i].left = 300;
        			mTaskSet[i].setActive(false);
        			mSetCoord[i].y = 300;
        			mSetCoord[i].x = 300;
    			}
    		}
    		return mSetCoord;
    	}
    	
    	public int getTouchedImgId(int X, int Y)
    	{
    		for(int i = 0; i < mTaskSet.length; ++i)
    		{
    			if(mTaskSet[i].isInto(X, Y))
    			{
    				return mTaskSet[i].resId;
    			}
    		}		
    		return 0;
    	}
    	
    	private class CTaskResourcesCoordinateSet
        {
        	public int resId;
        	public int height;
        	public int width;
        	public int left;
        	public int top;
        	private boolean mActive = true;
        	public void setActive(boolean active)
        	{
        		mActive = active;
        	}
        	
        	CTaskResourcesCoordinateSet(int resourceId, int w, int h)
        	{
        		resId = resourceId;
        		height = h;
        		width = w;
        	}
        	
        	public boolean isInto(int X, int Y)
        	{
        		if(mActive)
        			return ((X > left) && (X < left + width) && 
        					(Y > top) && (Y < top + height));
        		else
        			return false;
        	}
        	
        }
    }  
}