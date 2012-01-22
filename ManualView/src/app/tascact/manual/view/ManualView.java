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
import android.widget.ImageView;
import android.widget.LinearLayout;
import app.tascact.manual.CResources;

public class ManualView extends LinearLayout
{
	private CResources mResources = new CResources();
	private int mPageRes[] = null;
	private OnClickListener mListener = null;
	
    public ManualView(Context context, OnClickListener listener)
    {
		super(context);		
		// ориентируем View вертикально
		this.setOrientation(1);
		
		mListener = listener;
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    public void SetPage(int pageNum)
    {
    	mPageRes = mResources.GetPageResources(pageNum);
    	this.removeAllViews();
    	
		for(int i = 0; i < mPageRes.length; ++i)
		{
			ImageView pageElem = new ImageView(this.getContext());
			pageElem.setId(i);
			pageElem.setBackgroundResource(mPageRes[i]);
			pageElem.setOnClickListener(mListener);
			this.addView(pageElem);
		}
		invalidate();
    }
}