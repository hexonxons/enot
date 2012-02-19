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
import android.util.Log;
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
	
    public ManualView(Context context, OnTouchListener touchListener, OnClickListener clickListener, XMLResources markup)
    {
		super(context);		
		mResources = markup;
		this.setOrientation(1);
		this.setBackgroundColor(Color.WHITE);
		
		mClickListener = clickListener;
		mTouchListener = touchListener;
	}
    
    public void SetPage(int pageNum)
    {
    	mPageRes = mResources.getPageResources(pageNum);
    	
    	String dbg = new String();
    	for (int i = 0; i < mPageRes.length; ++i) {
    		dbg += Integer.toString(mPageRes[i]) + " ";
    	}
    	Log.d("XML", dbg);
    	
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