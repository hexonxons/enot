/*
 * ManualView класс
 * 
 * Класс работы с учебником
 * 
 * Copyright 2012 hexonxons 
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import app.tascact.manual.CPage;

public class ManualView extends View
{
	private Context mContext = null;
	// размеры экрана
	private int mWidth = 0;
	private int mHeight = 0;
	private final Paint mPaint = new Paint();
	private CPage mPage = null;
	
	private int mPageRes[][] = null;
	
	public int mCurrPageNum = 0;
	
	public int mTaskNum = -3;
	
    public ManualView(Context context, int _PageRes[][])
    {
		super(context);
		
		mPageRes = _PageRes.clone();
		
		mContext = context;
		mPage = new CPage(mContext, mPageRes[0]);
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPage = new CPage(mContext, mPageRes[mCurrPageNum]);
		mPage.setDims(mWidth, mHeight);
    }
    
    // отрисовка страницы
    @Override protected void onDraw(Canvas canvas) 
	{
		//canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    	mPage = new CPage(mContext, mPageRes[mCurrPageNum]);
		mPage.drawPage(canvas, mPaint);
	}
    
    public void changePage(int pageNum)
    {
    	mCurrPageNum = pageNum;
		mPage = new CPage(mContext, mPageRes[pageNum]);
		mPage.setDims(mWidth, mHeight);
		invalidate();
    }
    
    // обработка касаний
    public boolean processEvent(MotionEvent event)
	{
		// получаем action
	    int eventAction = event.getAction(); 

	    // получаем координаты прикасания
	    float X = event.getX(); 
	    float Y = event.getY(); 

	    switch (eventAction)
	    { 
		    // action - убрали палец
		    case MotionEvent.ACTION_DOWN:
		    {
		    	int val = mPage.getTouchedImageNum((int)X, (int)Y);

		    	mTaskNum = val;
		    	invalidate();
		    }
	    }
		return false;
	}
}