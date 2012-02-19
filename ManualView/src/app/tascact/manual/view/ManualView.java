/*
 * ManualView ����������
 * 
 * ���������� ������������ �� ������������������
 * 
 * Copyright 2012 hexonxons 
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package app.tascact.manual.view;


import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import app.tascact.manual.CResources;
import app.tascact.manual.XMLResources;

public class ManualView extends LinearLayout
{
	private XMLResources mResources = null;
	private int mPageRes[] = null;
	private OnTouchListener mTouchListener = null;
	private OnClickListener mClickListener = null;
	
    public ManualView(Context context, OnTouchListener touchListener, OnClickListener clickListener, String bookName) throws Throwable
    {
		super(context);		
		mResources = new XMLResources(context, bookName);
		this.setOrientation(1);
		this.setBackgroundColor(Color.WHITE);
		
		mClickListener = clickListener;
		mTouchListener = touchListener;
	}
    
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    public void SetPage(int pageNum)
    {
    	mPageRes = mResources.getPageResources(pageNum);
    	this.removeAllViews();
    	
		for(int i = 0; i < mPageRes.length; ++i)
		{
			ImageView pageElem = new ImageView(this.getContext());
			pageElem.setId(i);
			pageElem.setBackgroundResource(mPageRes[i]);
			pageElem.setOnClickListener(mClickListener);
			pageElem.setOnTouchListener(mTouchListener);
			this.addView(pageElem);
		}
		invalidate();
    }
}