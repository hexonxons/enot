package app.tascact.manual.view.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.widget.ImageView;
import app.tascact.manual.R;

//класс-view поля
public class FieldView extends ImageView
{
	private String mFieldContent = null;
	private boolean isSelectable = false;
	private long mCapacity = 0;
	// является ли выделенной
	private boolean mSelected = false;
	private boolean isChecked = false;
	private int mWidth = 0;
	private int mHeight = 0;
	
	public FieldView(Context context, String key, int capacity)
	{
		super(context);
		mFieldContent = key;
		mCapacity = capacity;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
		fg.setStyle(Style.FILL);
		int textSize = mHeight > mWidth ? mWidth : mHeight;
		fg.setTextSize((float) (textSize * 0.5));
		
		FontMetrics fm = fg.getFontMetrics();
		
		// координаты помещения
		float textY = (mHeight - fm.ascent - fm.descent) / 2;
		
		fg.setTextAlign(Paint.Align.CENTER);
		for(int i = 0; i < mFieldContent.length(); ++i)
		{
			String symb = mFieldContent.substring(i,  i + 1);
			canvas.drawText(symb, (float) ((mWidth / mFieldContent.length() * i) + (mWidth / mFieldContent.length() / 2)), textY, fg);
		}
		if(!isChecked)
			if(mSelected)
				this.setBackgroundResource(R.drawable.selected_field);
			else
				this.setBackgroundResource(R.drawable.input_field);
		
		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mWidth = w;
		mHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void setSelectable(boolean selectable)
	{
		isSelectable = selectable;
	}
	
	public void Check(boolean res)
	{
		isChecked = true;
		if(isSelectable)
			if(res)
				this.setBackgroundResource(R.drawable.keyboard_button_right);
			else
				this.setBackgroundResource(R.drawable.keyboard_button_wrong);
	}

	public void setChecked(boolean checked)
	{
		isChecked = checked;
	}
	
	public String getFieldContent()
	{
		return this.mFieldContent;
	}

	public void setFieldContent(String keyLabel)
	{
		this.mFieldContent = keyLabel;
		postInvalidate();
	}
	
	public void setSelected(boolean selected)
	{
		this.mSelected = selected;
		isChecked = false;
		postInvalidate();
	}
	
	public int addSymb(String symb)
	{
		if(mCapacity > mFieldContent.length() + 1)
		{
			mFieldContent += symb;
			postInvalidate();
			return 0;
		}
		else
			if(mCapacity != mFieldContent.length())
			{
				mFieldContent += symb;
				postInvalidate();
				return 1;
			}
			else
				return 1;
	}
	
	public int delSymb()
	{
		if(mFieldContent.length() > 0)
		{
			mFieldContent = mFieldContent.substring(0, mFieldContent.length() - 1);
			postInvalidate();
			if(mFieldContent.length() == 0)
				return 1;
		}
		
		if(mFieldContent.length() == 0)
			return 1;
		
		return 0;
	}
}