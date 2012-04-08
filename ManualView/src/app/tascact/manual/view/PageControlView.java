package app.tascact.manual.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

public class PageControlView extends RelativeLayout
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
	
	public PageControlView(Context context)
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
	public void addIcon(ImageView child)
	{
		if(mSize == SIZE_SET)
		{
			return;
		}

		RelativeLayout frame = new RelativeLayout(this.getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(CENTER_IN_PARENT);	
		frame.addView(child, params);
		super.addView(frame);
	}
	
	/**
	 * Add icon to the panel.
	 * 
	 * Setting IconsSize to SIZE_SET.
	 * 
	 * @param child ImageView representing an icon.
	 * @param percentage Long side of button have size = percentage / 100 * long side of panel size.
	 */
	public void addIcon(ImageView child, int percentage)
	{
		mSize = SIZE_SET;
		mPercentage.put(Integer.toString(this.getChildCount()), Integer.toString(percentage));
		
		RelativeLayout frame = new RelativeLayout(this.getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(CENTER_IN_PARENT);	
		frame.addView(child, params);
		super.addView(frame);
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
			View icon = this.getChildAt(i);
			LayoutParams params = null;
			
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
					icon.setBackgroundResource(R.drawable.button_frame_top);
					break;
				}
				
				case ORIENTATION_BOTTOM:
				{
					icon.setBackgroundResource(R.drawable.button_frame_bottom);
					break;
				}
				
				case ORIENTATION_LEFT:
				{
					icon.setBackgroundResource(R.drawable.button_frame_left);
					break;
				}
				
				case ORIENTATION_RIGHT:
				{
					icon.setBackgroundResource(R.drawable.button_frame_right);
					break;
				}
				default:
					break;
			}
			
			
			icon.setLayoutParams(params);
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
