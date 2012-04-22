package app.tascact.manual.view.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

//Класс клавиатуры
public class KeyboardView extends RelativeLayout implements OnClickListener
{
	private final String mOperatorsSet = "+-*/><=";
	
	public interface OnKeyboardKeyPressListener
	{
		void onKeyboardKeyPress(String label);
	}
	
	protected OnKeyboardKeyPressListener mKeyPressedListener = null;
	
	public KeyboardView(Context context)
	{
		super(context);
		this.setBackgroundColor(0xFFF2F2F2);
		
		for(int i = 0; i < 10; ++i)
		{
			Key key = new Key(context, Integer.toString(i));				
			key.setOnClickListener(this);
			this.addView(key);
		}
		
		for(int i = 0; i < mOperatorsSet.length(); ++i)
		{
			Key key = new Key(context, mOperatorsSet.substring(i, i + 1));				
			key.setOnClickListener(this);
			
			this.addView(key);
		}
		
		Key key = new Key(context, "Del");				
		key.setOnClickListener(this);
		
		this.addView(key);
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
		int keyWidth = w / 12;
		int keyHeight = h / 4;
		
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			Key key = (Key)this.getChildAt(i);
			if(i < 10)
			{
				int margin = (w - keyWidth * 10) / 11;
				LayoutParams params = new LayoutParams(keyWidth, keyHeight);
				params.setMargins(margin + i * (keyWidth + margin), keyHeight / 2, 0, 0);
				key.setLayoutParams(params);
			}
			else
				if(i < 10 + mOperatorsSet.length())
				{
					int margin = (w - keyWidth * (mOperatorsSet.length() + 1)) / (mOperatorsSet.length() + 2);
					LayoutParams params = new LayoutParams(keyWidth, keyHeight);
					params.setMargins(margin + (i - 10) * (keyWidth + margin), keyHeight * 2, 0, 0);
					key.setLayoutParams(params);
				}
				else
				{
					int margin = (w - keyWidth * (mOperatorsSet.length() + 1)) / (mOperatorsSet.length() + 2);
					LayoutParams params = new LayoutParams(keyWidth, keyHeight);
					params.setMargins(margin + (i - 10) * (keyWidth + margin), keyHeight * 2, 0, 0);
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