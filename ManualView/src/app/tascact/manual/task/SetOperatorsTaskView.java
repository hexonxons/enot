/*
 * View задач по решению выражение
 * 
 * 
 * Порядок обработки событий нажатия:
 * 
 */

package app.tascact.manual.task;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.tascact.manual.R;
import app.tascact.manual.XMLResources;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.AnimationView;
import app.tascact.manual.view.TaskView;

public class SetOperatorsTaskView extends TaskView
{
	private String[] mTaskResources = null;
	private String[] mTaskAnswers = null;
	private RelativeLayout mMainLayout = null;
	private KeyboardView mKeyboard = null;
	private ExpressionView mSelectedExpression = null;
	private boolean mAnswer = true;
	private boolean mIsDescSet = false;
	private AlertDialog mAlertDialog = null;
	// время предыдущего касания
	private long mPrevTouchTime = 0;
	private AnimationView mAnswerAnimation = null;
	
	public SetOperatorsTaskView(Context context, XMLResources markup, String ManualName,  int PageNumber, int TaskNumber)
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
		
		mMainLayout = new RelativeLayout(context);
		// клавиатура с размером кнопок 60x60 px
		mKeyboard = new KeyboardView(context, 60, 60);
		// задаем белый фон
		mMainLayout.setBackgroundColor(Color.WHITE);
		mAlertDialog = new AlertDialog.Builder(context).create();
		int verticalOffcet = 75;
		int horizontalOffset = 800;

		if(descr != null)
		{
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			TextView description = new TextView(context);
			description.setText(descr);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mMainLayout.addView(description, params);
			mIsDescSet = true;
		}
		
		for(int i = 0; i < mTaskResources.length; ++i)
		{
			ExpressionView expression = new ExpressionView(context, mTaskResources[i], 60, 60);
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
						}
						else
							if(mSelectedExpression == v)
							{
								if(mSelectedExpression.getPressedKey() == null)
									mSelectedExpression = null;
							}
							else
							{
								mSelectedExpression.setSelected(false);
								mSelectedExpression = (ExpressionView) v;
							}
					}
					return true;
				}
			});
			
			
			/*
		 	* параметры раскладки
			 * ------------------
			 * |   Description  |
			 * |(expr1)	(expr2)	|
			 * |(expr3)	(expr4)	|
			 * |(expr5)	(expr6)	|
			 * \/\/\/\/\//\/\/\/
			 */
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			// Топорное размещение
			if(mTaskResources[i].length() * 60 > 400)
			{
				horizontalOffset = (800 - mTaskResources[i].length() * 60)/2;
				if(i != 0)
					verticalOffcet += 75;
			}
			// если этот элемент помещается в 400px - половина ширины экрана
			else
			{
				// если этот элемент не последний
				if(i != mTaskResources.length - 1)
				{
					// если следующий элемент не помещается в 400px
					if(mTaskResources[i + 1].length() * 60 > 400)
					{
						// если смещение предыдущего элемента < 400, то есть текущий элемент - 2й в строчке
						// то задаем горизонтальное смещение на середину второго блока в 400px
						// и не делаем сдвига по вертикали
						if(horizontalOffset < 400)
						{
							horizontalOffset = 400 + (400 - mTaskResources[i].length()* 60) / 2;
						}
					}
					// если следующий элемент помещается в 400px
					else
					{
						// если смещение предыдущего элемента < 400, то текущий элемент - 2й в строчке
						// то задаем горизонтальное смещение на середину второго блока в 400px
						// и не делаем сдвига по вертикали
						if(horizontalOffset < 400)
						{
							horizontalOffset = 400 + (400 - mTaskResources[i].length()* 60) / 2;
						}
						// если смещение предыдущего элемента > 400, то  текущий элемент - 1й в строчке
						// задаем горизонтальное смещение на середину первого блока в 400px
						// и делаем сдвиг по вертикали
						else
						{
							horizontalOffset = (400 - mTaskResources[i].length() * 60) / 2;
							verticalOffcet += 75;
						}
					}
				}
				// последний элемент помещается в 400px
				else
				{
					if(horizontalOffset < 400)
					{
						horizontalOffset = 400 + (400 - mTaskResources[i].length() * 60) / 2;
					}
					else
					{
						horizontalOffset = (400 - mTaskResources[i].length() * 60) / 2;
						verticalOffcet += 75;
					}
				}
			}
		
			if( mTaskResources.length == 1)
				horizontalOffset  = (800 - mTaskResources[i].length() * 60)/2;
			
			params.setMargins(horizontalOffset, verticalOffcet, 0, 0);
			// добавляем выражение
			mMainLayout.addView(expression, params);
		}
		
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 830));
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 830);
		params.setMargins(0, 830, 0, 0);
		this.addView(mKeyboard, params);
		
		mAnswerAnimation = new AnimationView(getContext(), 400, 400, 400, 400, "MarioAnimationResources", 60, -3, true, true);
		mAnswerAnimation.setVisibility(INVISIBLE);
		this.addView(mAnswerAnimation);
	}
	
	// вставляем значение
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		mAnswerAnimation.setVisibility(INVISIBLE);
		if(event.getEventTime() - mPrevTouchTime > 100)
		{
			mPrevTouchTime = event.getEventTime();
			String key = mKeyboard.getPressedKey();
			if(key.equals("invalid") || mSelectedExpression == null)
				return true;
			
			mSelectedExpression.setKeyLabel(key);
		}
		return super.onTouchEvent(event);
	}
	
	public void RestartTask()
    {
		int i = 0;
		if(mIsDescSet)
			i++;
		for(; i < mMainLayout.getChildCount(); ++i)
    	{
    		((ExpressionView)(mMainLayout.getChildAt(i))).clearValues();
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
    	for(; i < mMainLayout.getChildCount(); ++i)
    	{
    		if(!((ExpressionView)(mMainLayout.getChildAt(i))).checkValues(mTaskAnswers[j]))
    			mAnswer = false;
    		++j;
    	}
    	
    	String weee = mAnswer ? "Прально!" : "Непрально!";
    	mAnswerAnimation.setVisibility(VISIBLE);
    	mAnswerAnimation.setTextToDisplay(weee);
    	mAnswerAnimation.Animate();
    }
	
	private class ExpressionView extends RelativeLayout
	{
		private int mWidth;
		private int mHeight;
		private KeyView mInputs[] = null;
		private KeyView mPressedKey = null;
		private long mPrevTouchTime = 0;
		public ExpressionView(Context context, String expression, int KeyWidth, int KeyHeight)
		{
			super(context);
			mWidth = KeyWidth;
			mHeight = KeyHeight;
			int inputSz = 0;
			int inputIndex = 0;
			for(int i = 0; i < expression.length(); ++i)
			{
				if(expression.charAt(i) == '?')
					inputSz++;
			}
			
			mInputs = new KeyView[inputSz];
			
			// проходим по всем символам выражения
			for(int i = 0; i < expression.length(); ++i)
			{
				// извлекаем i-й символ и создаем кнопку
				KeyView key = new KeyView(context, expression.substring(i, i + 1), mWidth, mHeight);
				// убираем свойство кнопки
				key.setTouchable(false);
				if(expression.charAt(i) == '?')
				{
					key.setKeyLabel("");
					key.setSelectable(true);
					mInputs[inputIndex] = key;
					inputIndex++;
				}
				
				key.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						if(event.getEventTime() - mPrevTouchTime > 100)
						{
							mPrevTouchTime = event.getEventTime();
							if(!((KeyView) v).isSelectable())
								return true;
							if(mPressedKey == null)
							{
								mPressedKey = (KeyView) v;
								mPressedKey.setFocus(false);
							}
							else
								if(mPressedKey == v)
									mPressedKey = null;
								else
								{
									mPressedKey.setSelected(false);
									mPressedKey = (KeyView) v;
									mPressedKey.setFocus(false);
								}
						}
						return false;
					}
				});
				
				// задаем размеры mWidth x mHeight
				LayoutParams params = new LayoutParams(mWidth, mHeight);
				/*
				 * сдвиг по горизонтали
				 * -----------------
				 * |(key1)(key2)...|
				 * -----------------
				 */
				params.setMargins(mWidth * i, 0, 0, 0);
				this.addView(key, params);
			}
		}
		
		// функция задания значения нажатого поля
		// переделать
		public void setKeyLabel(String label)
		{
			if(mPressedKey != null)
				//mPressedKey.setKeyLabel(label);
				mPressedKey.addSymb(label);
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
		
		public KeyView getPressedKey()
		{
			return mPressedKey;
		}
		
		public void clearValues()
		{
			for(int i = 0; i < mInputs.length; ++i)
			{
				mInputs[i].setKeyLabel("");
				mInputs[i].setSelectable(true);
				mInputs[i].setSelected(false);
				mPressedKey = null;
				//invalidate();
			}
		}
		
		public boolean checkValues(String answer)
		{
			String data = "";
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				data += ((KeyView)(this.getChildAt(i))).getKeyLabel();
				for(int k = 0; k < mInputs.length; ++k)
					if(mInputs[k].getKeyLabel().length() == 0)
						return false;
			}
			
			if(answer.indexOf(data) < 0)
				return false;

			return true;
		}
	}

	// Класс клавиатуры
	private class KeyboardView extends RelativeLayout
	{
		private final String mOperatorsSet = "+-*/><=";
		// последний нажатый символ на клавиатуре
		private String mPressedKey = "";
		public KeyboardView(Context context, int keyWidth, int keyHeight)
		{
			super(context);
			this.setBackgroundColor(Color.WHITE);
			
			for(int i = 0; i < 10; ++i)
			{
				KeyView key = new KeyView(context, Integer.toString(i), keyWidth, keyHeight);
				LayoutParams params = new LayoutParams(keyWidth, keyHeight);
				int margin = (800 - 10 * keyWidth) / 11;
				params.setMargins(margin + i * (keyWidth + margin), keyHeight / 2, 0, 0);
				key.setOnTouchListener(new OnTouchListener()
				{
					// Слушатели кнопок
					// при нажатии сохраняется значение нажатой кнопки
					// отдаем дальнейшую обработку родителю (return false)
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						mPressedKey = ((KeyView)v).getKeyLabel();
						return false;
					}
				});
				
				this.addView(key, params);
			}
			
			for(int i = 0; i < mOperatorsSet.length(); ++i)
			{
				KeyView key = new KeyView(context, mOperatorsSet.substring(i, i + 1), keyWidth, keyHeight);
				LayoutParams params = new LayoutParams(keyWidth, keyHeight);
				int margin = (800 - mOperatorsSet.length() * keyWidth) / (mOperatorsSet.length() + 1);
				params.setMargins(margin + i * (keyWidth + margin), keyHeight * 2, 0, 0);
				
				key.setOnTouchListener(new OnTouchListener()
				{
					// Слушатели кнопок
					// при нажатии сохраняется значение нажатой кнопки
					// отдаем дальнейшую обработку родителю (return false)
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						mPressedKey = ((KeyView)v).getKeyLabel();
						return false;
					}
				});
				
				this.addView(key, params);
			}
		}
		
		public String getPressedKey()
		{
			String key = mPressedKey.length() != 0 ? mPressedKey : "invalid";
			mPressedKey = "";
			return key;
		}
	}
	
	// класс-view кнопки
	private class KeyView extends ImageView
	{
		private String mKeyLabel = null;
			// является ли кнопкой или просто отображает символ
		private boolean mTouchable = true;
			// является ли выделяемой для вставки символа
		private boolean mSelectable = false;
			// является ли выделенной
		private boolean mSelected = false;
		private boolean focus = false;
		
			// время предыдущего касания
		private long mPrevTouchTime = 0;
		
		private int mWidth = 0;
		private int mHeight = 0;
		
		public KeyView(Context context, String key, int width, int height)
		{
			super(context);
			mKeyLabel = key;
			mWidth = width;
			mHeight = height;
			this.setBackgroundColor(getResources().getColor(R.color.table_light));
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
			fg.setStyle(Style.FILL);
			fg.setTextAlign(Paint.Align.CENTER);
			fg.setTextSize((float) (mHeight * 0.75));
			
			FontMetrics fm = fg.getFontMetrics();
				// координаты помещения
			float textX = mWidth / 2;
			float textY = (mHeight - fm.ascent - fm.descent) / 2;
			canvas.drawText(mKeyLabel, textX, textY, fg);
			
			if(mSelected)
				this.setBackgroundColor(getResources().getColor(R.color.table_selected));
			else
				this.setBackgroundColor(getResources().getColor(R.color.table_light));
			
			super.onDraw(canvas);
		}


		public String getKeyLabel()
		{
			return this.mKeyLabel;
		}

		public void setKeyLabel(String keyLabel)
		{
			this.mKeyLabel = keyLabel;
			invalidate();
		}
		
			// нажимаема ли кнопка
		public boolean isTouchable()
		{
			return mTouchable;
		}
		// setter
		public void setTouchable(boolean touchable)
		{
			this.mTouchable = touchable;
			this.mSelectable = false;
			this.mSelected = false;
		}
		
			//выделяема ли кнопка
		public boolean isSelectable()
		{
			return mSelectable;
		}
		// setter
		public void setSelectable(boolean selectable)
		{
			this.mSelectable = selectable;
			this.mTouchable = false;
		}
		
			// выделена ли кнопка
		public boolean isSelected()
		{
			return mSelected;
		}
		
		// setter
		public void setSelected(boolean selected)
		{
			this.mSelected = selected;
			focus = selected;
			invalidate();
		}
		
		public void setFocus(boolean focus)
		{
			this.focus = focus;
		}
		
		public void addSymb(String symb)
		{
			if(!focus)
				mKeyLabel = symb;
			else
				if(mKeyLabel.length() < 2)
					mKeyLabel += symb;
			focus = true;
			invalidate();
		}
		
			// события нажатия на кнопку - если кнопка выделяема - выделяем ее
			// и отдаем обработку родителю (return false)
			// соответственно кнопки клавиатуры обрабатывает клавиатура, а кнопки выражения - выражение
		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			if(isSelectable())
			{
				int eventAction = event.getAction();
				if (eventAction == MotionEvent.ACTION_DOWN && event.getEventTime() - mPrevTouchTime > 100)
				{
					mPrevTouchTime = event.getEventTime();
					mSelected = mSelected ? false : true;
					invalidate();
				}
				return false;
			}			
			return false;
		}
	}
}
