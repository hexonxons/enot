/**
 * Control View class
 * 
 * Class of view to control pages or tasks
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
import app.tascact.manual.R;

public class ControlView extends LinearLayout
{	
	public final int FLOAT_TOP = 0;
	public final int FLOAT_BOTTOM = 1;
	public final int FLOAT_LEFT = 2;
	public final int FLOAT_RIGHT = 3;
	
	public final int VERTICAL = 0;
	public final int HORIZONTAL = 1;
	
	private int mFloat = 0;
	private int mOrientation = 0;
	
	public ControlView(Context context)
	{
		super(context);	
	}
	
	/**
	 * Set transparancy for this view
	 * 
	 * @param alpha
	 */
	public void setAlpha(float alpha)
	{
		//setBackgroundColor(Color.TRANSPARENT);
	}
	
	public void setIconsFloat(int iconsfloat)
	{
		this.mFloat = iconsfloat;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{	
		int minDimension = w > h ? h : w;
		
		this.setLayoutParams(new LayoutParams(w, h));
		
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			ImageView child = (ImageView)this.getChildAt(i);
			child.setLayoutParams(new LayoutParams(minDimension, minDimension));
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
