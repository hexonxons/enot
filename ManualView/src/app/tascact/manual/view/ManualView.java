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
import android.graphics.Color;
import app.tascact.manual.PageView;
import app.tascact.manual.XMLResources;

public class ManualView extends SoftScrollView {
	private PageView[] pages;	
	
	public ManualView(Context context, XMLResources markup, OnClickListener clickListener) {
		super(context);
		
		pages = new PageView[markup.getPageNumber()];
		for (int i = 0; i < markup.getPageNumber(); ++i) {
			pages[i] = new PageView(context, markup.getPageResources(i+1), clickListener);
		}
		
		setBackgroundColor(Color.WHITE);
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setClickable(true);
		setFeatureItems(pages);
	}
	
	/**
	 * @param pageNum 1-based page number
	 */
	public void setPage(int pageNum) {
		scrollToItem(pageNum-1);
	}	
}