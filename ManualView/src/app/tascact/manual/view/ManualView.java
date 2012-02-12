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
	// Ресурсы для построения учебника
	private CResources mResources = null;
	// Массив ресурсов страницы учебника
	private int mPageRes[] = null;
	// Обработчик клика по задаче
	private OnClickListener mListener = null;
	
    public ManualView(Context context, OnClickListener listener, int PageNumber)
    {
		super(context);		
		mResources = new CResources(PageNumber);
		// ориентируем View вертикально
		this.setOrientation(1);
		
		mListener = listener;
	}
    
    // получение размеров экрана
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    // Задание отображаемой страницы
    public void SetPage(int pageNum)
    {
    	// Получаем ресурсы новой страницы
    	mPageRes = mResources.GetPageResources(pageNum);
    	// Очищаем весь View
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