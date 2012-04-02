/*
 * View задач по решению выражение
 * 
 * 
 * Порядок обработки событий нажатия:
 * 
 */

package app.tascact.manual.task;

import java.util.Arrays;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.tascact.manual.Markup;
import app.tascact.manual.R;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

public class SetOperatorsTaskView extends TaskView
{
	private String[] mTaskResources = null;
	private String[] mTaskAnswers = null;
	private RelativeLayout mExpressionLayout = null;
	private KeyboardView mKeyboard = null;
	private ExpressionView mSelectedExpression = null;
	// 1 == EditControl
	// 0 == Touch Control
	private int mControl = 1;
	private boolean mAnswer = true;
	private boolean mIsDescSet = false;
	private AlertDialog mAlertDialog = null;
	private int mWidth = 0;
	private int mHeight = 0;
	
	// время предыдущего касания
	private long mPrevTouchTime = 0;
	
	public interface OnKeyPressedListener
	{
		void OnKeyPress(String label);
	}
	
	public SetOperatorsTaskView(Context context, Markup markup, String ManualName,  int PageNumber, int TaskNumber)
	{
		super(context);
		Node res = markup.getTaskResources(PageNumber, TaskNumber);
		
		// Getting description of this task
		Node taskDescr = (Node) XMLUtils.evalXpathExpr(res, "./TaskDescription", XPathConstants.NODE);
		String descr = null;
		if (taskDescr != null) 
		{
			descr = taskDescr.getTextContent();
		}
		
		// Getting description of this task
		Node taskControl = (Node) XMLUtils.evalXpathExpr(res, "./EditMode", XPathConstants.NODE);
		if (taskControl != null) 
		{
			if(taskControl.getTextContent().equals("EditControl"))
				mControl = 1;
			else
				mControl = 0;
		}
		
		// Getting resources of this task
		NodeList taskRes = (NodeList) XMLUtils.evalXpathExpr(res, "./TaskResources/TaskResource", XPathConstants.NODESET);
		mTaskResources = new String[taskRes.getLength()];
		for (int i = 0; i < taskRes.getLength(); ++i) 
		{
			mTaskResources[i] = taskRes.item(i).getTextContent();
		}
		
		// Getting answer resources
		NodeList taskAns = (NodeList) XMLUtils.evalXpathExpr(res, "./TaskAnswer/Answer", XPathConstants.NODESET);
		mTaskAnswers = new String[taskAns.getLength()]; 
		for (int i = 0; i < taskAns.getLength(); ++i) 
		{
			mTaskAnswers[i] = taskAns.item(i).getTextContent();
		}
		
		mExpressionLayout = new RelativeLayout(context);
		// клавиатура
		mKeyboard = new KeyboardView(context);
		// задаем белый фон
		mExpressionLayout.setBackgroundColor(Color.WHITE);
		mAlertDialog = new AlertDialog.Builder(context).create();

		if(descr != null)
		{
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			TextView description = new TextView(context);
			description.setText(descr);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mExpressionLayout.addView(description, params);
			mIsDescSet = true;
		}
		
		for(int i = 0; i < mTaskResources.length; ++i)
		{
			ExpressionView expression = new ExpressionView(context, mTaskResources[i]);
			
			expression.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					if(event.getEventTime() - mPrevTouchTime > 100)
					{
						mPrevTouchTime = event.getEventTime();
						
						if(mSelectedExpression == null)
						{
							mSelectedExpression = (ExpressionView) v;
							mSelectedExpression.setCursor();
						}
						else
						{
							if(mSelectedExpression == v)
							{
								if(mSelectedExpression.getPressedKey() == null)
									mSelectedExpression = null;
							}
							else
							{
								mSelectedExpression.setSelected(false);
								mSelectedExpression = (ExpressionView) v;
								mSelectedExpression.setCursor();
							}
							}
					}
					return true;
				}
			});
			
			// добавляем выражение
			mExpressionLayout.addView(expression);
		}
			
		this.addView(mExpressionLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
		mKeyboard.setOnKeyPressedListener(new OnKeyPressedListener() 
		{
			@Override
			public void OnKeyPress(String label)
			{
				if(mSelectedExpression != null)
				{
					if(label == "Стереть")
						mSelectedExpression.delKeyLabel();
					else
						if(label == "Ввод")
						{
							for(int i = 0; i < mExpressionLayout.getChildCount(); ++i)
							{
								if(mExpressionLayout.getChildAt(i) == mSelectedExpression)
								{
									if(i < mExpressionLayout.getChildCount() - 1)
									{
										mSelectedExpression.setSelected(false);
										mSelectedExpression = (ExpressionView) mExpressionLayout.getChildAt(i + 1);
										mSelectedExpression.setCursor();
										return;
									}
								}
							}
						}
						else
							mSelectedExpression.setKeyLabel(label);
				}
			}
		});
		
		this.addView(mKeyboard);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mWidth = w;
		mHeight = h;
		
		LayoutParams keyboardParams = new LayoutParams(LayoutParams.MATCH_PARENT, 200);
		keyboardParams.setMargins(0, h - 200, 0, 0);
		((KeyboardView)this.getChildAt(1)).setLayoutParams(keyboardParams);
		
		mHeight -= 210;
		int maxHeight = 70;
		
		int expressionCount = mExpressionLayout.getChildCount() - (mIsDescSet ? 1 : 0);
		int offsetW = 10;
		int offsetH = 20;
		
		
		int expressionWidth = (mWidth - offsetW * 3)/2;
		int expressionHeight = mHeight / (expressionCount);

		if(expressionHeight > maxHeight)
			expressionHeight = maxHeight;
		
		if(expressionHeight * expressionCount + 20 * (expressionCount + 1) < mHeight)
		{
			expressionWidth = mWidth - offsetW * 3;
			for(int i = (mIsDescSet ? 1 : 0); i < mExpressionLayout.getChildCount(); ++i)
			{
				ExpressionView expression = (ExpressionView) mExpressionLayout.getChildAt(i);
				LayoutParams params = new LayoutParams(expressionWidth, expressionHeight);
				params.setMargins((mWidth - expressionWidth) / 2, (mIsDescSet ? 1 : 0) * 80 + offsetH + (offsetH + expressionHeight) * (i - (mIsDescSet ? 1 : 0)), 0, 0);
				expression.setLayoutParams(params);
			}
		}
		else
		{
			for(int i = (mIsDescSet ? 1 : 0); i < mExpressionLayout.getChildCount(); ++i)
			{
				ExpressionView expression = (ExpressionView) mExpressionLayout.getChildAt(i);
				LayoutParams params = new LayoutParams(expressionWidth, expressionHeight);
				params.setMargins(offsetW + (offsetW + expressionWidth) * ((i - (mIsDescSet ? 1 : 0)) % 2), (mIsDescSet ? 1 : 0) * 80 + offsetH + (20 + expressionHeight) * ((i - (mIsDescSet ? 1 : 0)) / 2), 0, 0);
				expression.setLayoutParams(params);
			}
		}
			
		super.onSizeChanged(w, h, oldw, oldh);
		}
	
	
	
	public void RestartTask()
    {
		int i = 0;
		if(mIsDescSet)
			i++;
		for(; i < mExpressionLayout.getChildCount(); ++i)
    	{
    		((ExpressionView)(mExpressionLayout.getChildAt(i))).clearValues();
    	}
    	mSelectedExpression = null;
    	mAnswer = true;
		invalidate();
    }
    
    public void CheckTask()
    {
    	mAnswer = true;
    	int i = 0;
    	int j = 0;
		if(mIsDescSet)
			i++;
    	for(; i < mExpressionLayout.getChildCount(); ++i)
    	{
    		if(!((ExpressionView)(mExpressionLayout.getChildAt(i))).checkValues(mTaskAnswers[j]))
    			mAnswer = false;
    		++j;
    	}
    	String ans = mAnswer ? "Правильно" : "Неправильно";
		mAlertDialog.setMessage(ans);
		mAlertDialog.show();
    }
	
	private class ExpressionView extends LinearLayout
	{
		private int mWidth = 0;
		private int mHeight = 0;
		private FieldView mInputs[] = null;
		private FieldView mPressedKey = null;
		private long mPrevTouchTime = 0;
		private String mExpression = null;
		
		public ExpressionView(Context context, String expression)
		{
			super(context);
			mWidth = 60;
			mHeight = 60;
			int inputSz = 0;
			int inputIndex = 0;
			mExpression = expression;
			for(int i = 0; i < expression.length(); ++i)
			{
				if(expression.charAt(i) == '?')
					inputSz++;
			}
			
			mInputs = new FieldView[inputSz];
			int offset = 0;
			
			// проходим по всем символам выражения
			for(int i = 0; i < expression.length();)
			{
				String label = "";
				int capacity = 0;
				
				boolean flag = false;
				
				if(expression.charAt(i) == '?')
				{
					flag = true;
					++i;
					capacity = 2;
				}
				else
				{
					if(expression.charAt(i) == '?')
					{
						flag = true;
						++i;
						capacity = 2;
					}
					else
					{
						while(i < expression.length() && expression.charAt(i) != '?')
						{
							if(i < expression.length() - 2 && (expression.substring(i, i + 2).matches("[0-9]{2}")))
							{
								label += expression.substring(i, i + 2);
								i += 2;
								++capacity;
							}
							else
							{
								label += expression.substring(i, i + 1);
								++i;
								++capacity;
							}
						} 
					}
				}
				
				int fieldWidth = mWidth * label.length();
				if(label.length() == 0)
					fieldWidth = mWidth;
				
				// создаем кнопку
				FieldView field = new FieldView(context, label, capacity);
				// вешаем обработчики только на нажимаемые клавиши
				if(flag)
				{
					mInputs[inputIndex] = field;
					inputIndex++;
					// если контроллим тыкая пальцами
					if(mControl == 0)
					{
						field.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						if(event.getEventTime() - mPrevTouchTime > 100)
						{
							mPrevTouchTime = event.getEventTime();
							if(mPressedKey == null)
							{
										mPressedKey = (FieldView) v;
										mPressedKey.setSelected(true);
							}
							else
								if(mPressedKey == v)
										{
											mPressedKey.setSelected(false);
									mPressedKey = null;
										}
								else
								{
									mPressedKey.setSelected(false);
											mPressedKey = (FieldView) v;
											mPressedKey.setSelected(true);
								}
						}
						return false;
					}
				});
					}
				}
				
				// задаем размеры mWidth x mHeight
				LayoutParams params = new LayoutParams(fieldWidth, mHeight);
				/*
				 * сдвиг по горизонтали
				 * -----------------
				 * |(key1)(key2)...|
				 * -----------------
				 */
				params.setMargins(offset, 0, 0, 0);
				offset += fieldWidth;
				this.addView(field, params);
			}
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			this.mHeight = h;
			this.mWidth = w / mExpression.length();
			
			for(int i = 0; i < this.getChildCount(); ++i)
			{	 
				FieldView field = (FieldView)this.getChildAt(i);
				int width = field.getFieldContent().length() == 0 ? mWidth : mWidth * field.getFieldContent().length();
				LayoutParams params = new LayoutParams(width, this.mHeight);
				field.setLayoutParams(params);
			}
			
			super.onSizeChanged(w, h, oldw, oldh);
		}
		
		public void setCursor()
		{
			if(mControl == 1)
			{
				for(int i = 0; i < mInputs.length; ++i)
				{
					if(mInputs[i].getFieldContent() == "")
					{
						mPressedKey = mInputs[i];
						mPressedKey.setSelected(true);
						return;
					}
				}
				mPressedKey = mInputs[mInputs.length - 1];
				mPressedKey.setSelected(true);
			}
		}
		
		// функция задания значения нажатого поля
		// переделать
		public void setKeyLabel(String label)
		{
			if(mPressedKey != null)
			{
				if(mPressedKey.addSymb(label) == 1)
				{
					int index = Arrays.asList(mInputs).indexOf(mPressedKey);
					if(index != mInputs.length - 1)
					{
						mPressedKey.setSelected(false);
						mPressedKey = mInputs[index + 1];
						mPressedKey.setSelected(true);
					}
				}
			}
		}
		
		public void delKeyLabel()
		{
			if(mPressedKey != null)
			{
				if(mPressedKey.delSymb() == 1)
				{
					int index = Arrays.asList(mInputs).indexOf(mPressedKey);
					if(index > 0)
					{
						mPressedKey.setSelected(false);
						mPressedKey = mInputs[index - 1];
						mPressedKey.setSelected(true);
					}
				}
			}
		}
		
		public void setSelected(boolean selected)
		{
			if(mPressedKey != null)
			{
				mPressedKey.setSelected(selected);
				if(selected == false)
					mPressedKey = null;
			}
		}
		
		public FieldView getPressedKey()
		{
			return mPressedKey;
		}
		
		public void clearValues()
		{
			for(int i = 0; i < mInputs.length; ++i)
			{
				mInputs[i].setFieldContent("");
				mInputs[i].setSelected(false);
				mPressedKey = null;
			}
		}
		
		public boolean checkValues(String answer)
		{
			String data = "";
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				data += ((FieldView)(this.getChildAt(i))).getFieldContent();
				/*for(int k = 0; k < mInputs.length; ++k)
					if(mInputs[k].getKeyLabel().length() == 0)
						return false;*/
			}
			
			if(answer.indexOf(data) < 0 || data.length() == 0 ||
			   ((data.length() < answer.length() && answer.charAt(data.length()) != '&') &&
			   (answer.indexOf(data) + data.length() < answer.length())))
				return false;

			return true;
		}

		// класс-view поля
		private class FieldView extends ImageView
		{
			private String mFieldContent = null;
			private long mCapacity = 0;
			// является ли выделенной
			private boolean mSelected = false;
			
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
				fg.setTextSize((float) (textSize * 0.65));
				
				FontMetrics fm = fg.getFontMetrics();
				
				// координаты помещения
				float textY = (mHeight - fm.ascent - fm.descent) / 2;
				
				fg.setTextAlign(Paint.Align.CENTER);
				for(int i = 0; i < mFieldContent.length(); ++i)
				{
					String symb = mFieldContent.substring(i,  i + 1);
					canvas.drawText(symb, (float) ((mWidth / mFieldContent.length() * i) + (mWidth / mFieldContent.length() / 2)), textY, fg);
				}
				
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

			public String getFieldContent()
			{
				return this.mFieldContent;
			}

			public void setFieldContent(String keyLabel)
			{
				this.mFieldContent = keyLabel;
				invalidate();
			}
			
			// setter
			public void setSelected(boolean selected)
			{
				this.mSelected = selected;
				invalidate();
			}
			
			public int addSymb(String symb)
			{
				if(mCapacity > mFieldContent.length() + 1)
				{
					mFieldContent += symb;
					invalidate();
					return 0;
				}
				else
					if(mCapacity != mFieldContent.length())
					{
						mFieldContent += symb;
						invalidate();
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
					invalidate();
					if(mFieldContent.length() == 0)
						return 1;
				}
				
				if(mFieldContent.length() == 0)
					return 1;
				
				return 0;
			}
		}
	}

	// Класс клавиатуры
	private class KeyboardView extends RelativeLayout implements OnClickListener
	{
		private final String mOperatorsSet = "+-*/><=";
		private final String mBackspace = "Стереть";
		private final String mEnter = "Ввод";
		
		protected OnKeyPressedListener mKeyPressedListener = null;
		
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
			
			Key backspace = new Key(context, mBackspace);
			Key enter = new Key(context, mEnter);
			
			backspace.setOnClickListener(this);
			enter.setOnClickListener(this);
				
			this.addView(backspace);
			this.addView(enter);
		}
		
		public void setOnKeyPressedListener(OnKeyPressedListener l)
		{
			this.mKeyPressedListener = l;
		}
		
		@Override
		public void onClick(View v)
		{
			if(mKeyPressedListener != null)
				mKeyPressedListener.OnKeyPress(((Key)v).getKeyLabel());
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			int keyWidth = w / 12;
			int keyHeight = h / 5;
			
			
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
						int margin = (w - keyWidth * mOperatorsSet.length()) / (mOperatorsSet.length() + 1);
						LayoutParams params = new LayoutParams(keyWidth, keyHeight);
						params.setMargins(margin + (i - 10) * (keyWidth + margin), keyHeight * 2, 0, 0);
						key.setLayoutParams(params);
					}
					else
					{
						int margin = (w - 2 * keyWidth * 4) / 3;
						LayoutParams params = new LayoutParams(keyWidth * 4, keyHeight);
						params.setMargins(margin + (i - 10 - mOperatorsSet.length()) * (keyWidth * 4 + margin), (int)(keyHeight * 3.5), 0, 0);
						key.setLayoutParams(params);
					}
			}
			
			super.onSizeChanged(w, h, oldw, oldh);
		}
		
		// класс-view кнопки
		private class Key extends Button
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
}
