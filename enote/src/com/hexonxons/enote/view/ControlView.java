package com.hexonxons.enote.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hexonxons.enote.R;

public class ControlView extends RelativeLayout
{
	public final static int FLOAT_TOP = 0;
	public final static int FLOAT_BOTTOM = 1;
	public final static int FLOAT_LEFT = 2;
	public final static int FLOAT_RIGHT = 3;
	
	public final static int ORIENTATION_TOP = 0;
	public final static int ORIENTATION_BOTTOM = 1;
	public final static int ORIENTATION_LEFT = 2;
	public final static int ORIENTATION_RIGHT = 3;
	
	public final static int SIZE_FILL = 0;
	public final static int SIZE_SET = 1;
	public final static int SIZE_UNSPECIFIED = 2;
	
	private int mFloat = 0;
	private int mOrientation = 0;
	private int mSize = 0;
	
	private Map<String, String> mPercentage = new HashMap<String,String>();
	
	public ControlView(Context context)
	{
		super(context);
	}
	
	public void setIconsFloat(int iconsFloat)
	{
		mFloat = iconsFloat;
	}
	
	public void setIconsSize(int iconsSize)
	{
		mSize = iconsSize;
	}
	
	public void setPanelOrientation(int panelOrientation)
	{
		mOrientation = panelOrientation;
	}
	
	/**
	 * Add icon to the panel
	 * 
	 * If IconsSize set as SIZE_SET, this function does nothing.
	 * Use addIcon(ImageView child, int percentage) instead.
	 * 
	 * TODO make throw exeption;
	 * 
	 * @param child ImageView representing an icon.
	 */
	public void addIcon(Drawable icon, Drawable iconPressed, OnTouchListener l)
	{
		if(mSize == SIZE_SET)
		{
			return;
		}
		
		ControlButton child = new ControlButton(this.getContext(), icon, iconPressed);
		child.setOnTouchListener(l);
		this.addView(child);
	}

	/**
	 * Add icon to the panel.
	 * 
	 * Setting IconsSize to SIZE_SET.
	 * 
	 * @param child ImageView representing an icon.
	 * @param percentage Long side of button have size = percentage / 100 * long side of panel size.
	 */
	public void addIcon(Drawable icon, Drawable iconPressed, OnTouchListener l, int percentage)
	{
		mSize = SIZE_SET;
		mPercentage.put(Integer.toString(this.getChildCount()), Integer.toString(percentage));
		
		ControlButton child = new ControlButton(this.getContext(), icon, iconPressed);
		child.setOnTouchListener(l);
		this.addView(child);
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{	
		int offset = 0;
		
		/*
		 * Dimension of button in long side and short side
		 */
		int shortSize = w > h ? h : w;
		int longSize = w > h ? w : h;
		
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			ControlButton icon = (ControlButton) this.getChildAt(i);
			LayoutParams params = new LayoutParams(shortSize, shortSize);
			params.addRule(CENTER_IN_PARENT);	
			icon.setLayoutParams(params);
			
			switch (mSize) 
			{
				case SIZE_FILL:
				{
					if(shortSize == w)
					{
						longSize = h / this.getChildCount();
						params = new LayoutParams(shortSize, h / this.getChildCount());
					}
					else
					{
						longSize = w / this.getChildCount();
						params = new LayoutParams(w / this.getChildCount(), shortSize);
					}
					
					break;
				}
				
				case SIZE_SET:
				{
					
					if(shortSize == w)
					{
						longSize = Integer.decode(mPercentage.get(String.valueOf(i))) * h / 100;
						params = new LayoutParams(shortSize, longSize);

					}
					else
					{
						longSize = Integer.decode(mPercentage.get(String.valueOf(i))) * w / 100;
						params = new LayoutParams(longSize, shortSize);
					}
					
					break;
				}
	
				default:
				{
					longSize = shortSize;
					params = new LayoutParams(longSize, longSize);
					break;
				}
			}			
			
			switch (mFloat)
			{
				case FLOAT_TOP:
				{
					params.setMargins(0, offset , 0, 0);
					offset += longSize;
					break;
				}
				
				case FLOAT_BOTTOM:
				{
					offset += longSize;
					params.setMargins(0, h - offset, 0, 0);
					break;
				}
				
				case FLOAT_LEFT:
				{
					params.setMargins(offset, 0 , 0, 0);
					offset += longSize;
					break;
				}
				
				case FLOAT_RIGHT:
				{
					offset += longSize;
					params.setMargins(w - offset, 0 , 0, 0);
					break;
				}
	
				default:
					break;
			}
			
			switch (mOrientation)
			{
				case ORIENTATION_TOP:
				{
					icon.setBackground(R.drawable.button_frame_top, R.drawable.button_frame_top_pushed);
					break;
				}
				
				case ORIENTATION_BOTTOM:
				{
					icon.setBackground(R.drawable.button_frame_bottom, R.drawable.button_frame_bottom_pushed);
					break;
				}
				
				case ORIENTATION_LEFT:
				{
					icon.setBackground(R.drawable.button_frame_left, R.drawable.button_frame_left_pushed);
					break;
				}
				
				case ORIENTATION_RIGHT:
				{
					icon.setBackground(R.drawable.button_frame_right, R.drawable.button_frame_right_pushed);
					break;
				}
				default:
					break;
			}
			
			icon.setLayoutParams(params);
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public class ControlButton extends ImageButton
	{
		private Drawable mPressedIcon = null;
		private Drawable mUnpressedIcon = null;
		
		private Drawable mPressedBackground = null;
		private Drawable mUnpressedBackground = null;
		
		
		public ControlButton(Context context, Drawable unpressed, Drawable pressed)
		{
			super(context);
			
			mUnpressedIcon = unpressed;
			mPressedIcon = pressed;
		}		
		
		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			switch (event.getAction())
			{
				case (MotionEvent.ACTION_DOWN):
				{
					this.setBackgroundDrawable(mPressedIcon);
					break;
				}
				
				case (MotionEvent.ACTION_UP):
				{
					this.setBackgroundDrawable(mUnpressedIcon);
					break;
				}
				default:
					break;
			}
			return super.onTouchEvent(event);
		}
		
		public void setBackground(int unpressedResId, int pressedResId)
		{
			mPressedBackground = getResources().getDrawable(pressedResId);
			mUnpressedBackground = getResources().getDrawable(unpressedResId);
			this.setBackgroundResource(unpressedResId);
		}
		
		private Drawable ScaleAndMergeDrawables(Drawable button, Drawable backgroung, int w, int h)
		{
			double factor = (double) w / button.getMinimumWidth() > (double) h / button.getMinimumHeight() ? 
					(double) h / button.getMinimumHeight() : (double) w / button.getMinimumWidth();
			double scaleWidth = button.getMinimumWidth() * factor;
			double scaleHeight = button.getMinimumHeight() * factor;
			
			BitmapDrawable buttonBitmap = new BitmapDrawable(Bitmap.createScaledBitmap(((BitmapDrawable)button).getBitmap(), (int)scaleWidth, (int)scaleHeight, true));
			BitmapDrawable bgBitmap =  new BitmapDrawable(Bitmap.createScaledBitmap(((BitmapDrawable)backgroung).getBitmap(), w, h, true));
			
			int buttonH = buttonBitmap.getBitmap().getHeight();
			int buttonW = buttonBitmap.getBitmap().getWidth();
			
			int bgH = bgBitmap.getBitmap().getHeight();
			int bgW = bgBitmap.getBitmap().getWidth();
			
			Bitmap result = Bitmap.createBitmap(bgW, bgH, Bitmap.Config.ARGB_8888);
			
		    Canvas comboCanvas = new Canvas(result);

		    comboCanvas.drawBitmap(bgBitmap.getBitmap(), 0, 0, null);
		    comboCanvas.drawBitmap(buttonBitmap.getBitmap(), (bgW - buttonW) / 2, (bgH - buttonH) / 2, null);
		    comboCanvas.save();
		    
		    return new BitmapDrawable(result);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{			
			mPressedIcon = ScaleAndMergeDrawables(mPressedIcon, mPressedBackground, w, h);
			mUnpressedIcon = ScaleAndMergeDrawables(mUnpressedIcon, mUnpressedBackground, w, h);
			
			this.setBackgroundDrawable(mUnpressedIcon);
			
			super.onSizeChanged(w, h, oldw, oldh);
		}
	}
}
