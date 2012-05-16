package app.tascact.manual.view.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import app.tascact.manual.R;

//Класс клавиатуры
public class KeyboardView extends LinearLayout implements OnClickListener
{
	private static final String[] DigitalRow = {"0", "1","2","3","4","5","6","7","8","9"};
	private static final String[] OperatorsRow = {"+","-","*","/",">","<","=", "Del"};
	
	private LinearLayout[] mKeyboardRows = null;
	
	public interface OnKeyboardKeyPressListener
	{
		void onKeyboardKeyPress(String label);
	}
	
	protected OnKeyboardKeyPressListener mKeyPressedListener = null;
	
	public KeyboardView(Context context)
	{
		super(context);
		this.setBackgroundColor(0xFFF2F2F2);
		this.setOrientation(LinearLayout.VERTICAL);
		
		// if width < height
		if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			mKeyboardRows = new LinearLayout[3];
			
			for(int i = 0; i < mKeyboardRows.length; ++i)
			{
				mKeyboardRows[i] = new LinearLayout(context);
				this.addView(mKeyboardRows[i]);
			}	
			
			// 5 digits in a row 
			for(int i = 0; i < DigitalRow.length; ++i)
			{
				Key key = new Key(context, DigitalRow[i]);
				key.setOnClickListener(this);
				mKeyboardRows[i / 5].addView(key);
			}
			
			for(int i = 0; i < OperatorsRow.length; ++i)
			{
				Key key = new Key(context, OperatorsRow[i]);
				key.setOnClickListener(this);
				mKeyboardRows[2].addView(key);
			}
		}
		else
		{
			mKeyboardRows = new LinearLayout[2];
			
			for(int i = 0; i < mKeyboardRows.length; ++i)
			{
				mKeyboardRows[i] = new LinearLayout(context);
				this.addView(mKeyboardRows[i]);
			}	
			
			// 10 digits in a row 
			for(int i = 0; i < DigitalRow.length; ++i)
			{
				Key key = new Key(context, DigitalRow[i]);
				key.setOnClickListener(this);
				mKeyboardRows[0].addView(key);
			}
			
			for(int i = 0; i < OperatorsRow.length; ++i)
			{
				Key key = new Key(context, OperatorsRow[i]);
				key.setOnClickListener(this);
				mKeyboardRows[1].addView(key);
			}
		}
	}
	
	public void setOnKeyPressedListener(OnKeyboardKeyPressListener l)
	{
		this.mKeyPressedListener = l;
	}
	
	@Override
	public void onClick(View v)
	{
		if(mKeyPressedListener != null)
			mKeyPressedListener.onKeyboardKeyPress(((Key)v).getKeyLabel());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// distance between rows and keys
		final int MARGIN = 5;
		final int ROW_COUNT = mKeyboardRows.length;
		final int BUTTON_HEIGHT = (h - (ROW_COUNT + 1) * MARGIN) / ROW_COUNT;
		
		for(int i = 0; i < mKeyboardRows.length; ++i)
		{
			int BUTTON_COUNT = mKeyboardRows[i].getChildCount();
			int BUTTON_WIDTH = (w - MARGIN * (BUTTON_COUNT + 1)) / BUTTON_COUNT;
			
			LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, BUTTON_HEIGHT);
			rowParams.setMargins(0, MARGIN, 0, 0);
			mKeyboardRows[i].setLayoutParams(rowParams);
				
			for(int j = 0; j < mKeyboardRows[i].getChildCount(); ++j)
			{
				Key key = (Key)mKeyboardRows[i].getChildAt(j);
				LayoutParams params = new LayoutParams(BUTTON_WIDTH, BUTTON_HEIGHT);
				params.setMargins(MARGIN, 0, 0, 0);
				key.setLayoutParams(params);
			}
		}	
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	// класс-view кнопки
	private class Key extends ImageButton
	{
		private String mKeyLabel = null;
		
		private int mWidth = 0;
		private int mHeight = 0;
		
		public Key(Context context, String key)
		{
			super(context);
			mKeyLabel = key;
			this.setBackgroundResource(R.drawable.keyboard_key);
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
			fg.setStyle(Style.FILL);
			fg.setTextAlign(Paint.Align.CENTER);
			int textSize = mHeight > mWidth ? mWidth : mHeight;
			fg.setTextSize((float) (textSize * 0.65));
			
			FontMetrics fm = fg.getFontMetrics();
			// координаты помещения
			float textX = mWidth / 2;
			float textY = (mHeight - fm.ascent - fm.descent) / 2;
			canvas.drawText(mKeyLabel, textX, textY, fg);
			
			super.onDraw(canvas);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			mWidth = w;
			mHeight = h;
			super.onSizeChanged(w, h, oldw, oldh);
		}

		public String getKeyLabel()
		{
			return this.mKeyLabel;
		}
	}
}