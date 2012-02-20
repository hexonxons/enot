/*
 * View ���������� ���� �������������� ������������������
 * 
 * 
 * �������������� ������������������ �������������� ��������������:
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
import app.tascact.manual.view.TaskView;

public class SetOperatorsTaskView extends TaskView
{
	private XMLResources mResources = null;
	private String[] mTaskResources = null;
	private String[] mTaskAnswers = null;
	private RelativeLayout mMainLayout = null;
	private KeyboardView mKeyboard = null;
	private ExpressionView mSelectedExpression = null;
	private boolean mAnswer = true;
	private boolean mIsDescSet = false;
	private AlertDialog mAlertDialog = null;
	// ���������� ���������������������� ��������������
	private long mPrevTouchTime = 0;
	
	private int DELtask = 0;
	public SetOperatorsTaskView(Context context, XMLResources markup, String ManualName,  int PageNumber, int TaskNumber)
	{
		super(context);
		mResources = markup;
		Node res = markup.getTaskResources(PageNumber, TaskNumber);

		// Getting description of this task
		Node taskDescr = (Node) XMLUtils.evalXpathExpr(res,
				"./TaskDescription", XPathConstants.NODE);
		String descr = null;
		if (taskDescr != null) {
			descr = taskDescr.getTextContent();
		}
		
		// Getting resources of this task
		NodeList taskRes = (NodeList) XMLUtils.evalXpathExpr(res,
				"./TaskResources/TaskResource", XPathConstants.NODESET);
		mTaskResources = new String[taskRes.getLength()];
		for (int i = 0; i < taskRes.getLength(); ++i) {
			mTaskResources[i] = taskRes.item(i).getTextContent();
		}
		
		// Getting answer resources
		NodeList taskAns = (NodeList) XMLUtils.evalXpathExpr(res,
				"./TaskAnswer/Answer", XPathConstants.NODESET);
		mTaskAnswers = new String[taskAns.getLength()]; 
		for (int i = 0; i < taskAns.getLength(); ++i) {
			mTaskAnswers[i] = taskAns.item(i).getTextContent();
		}
		
		mMainLayout = new RelativeLayout(context);
		mKeyboard = new KeyboardView(context, 60, 60);
		mMainLayout.setBackgroundColor(Color.WHITE);
		mAlertDialog = new AlertDialog.Builder(context).create();
		int verticalOffcet = 50;
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
		
		if(ManualName == "book2" && PageNumber == 61 && TaskNumber == 2)
		{
			DELtask = 1;
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ImageView first = new ImageView(context);
			first.setBackgroundResource(R.drawable.manual_1_2_pg62_3_task_1);
			ImageView second = new ImageView(context);
			second.setBackgroundResource(R.drawable.manual_1_2_pg62_3_task_2);
			params.setMargins(250, 200, 0, 0);
			mMainLayout.addView(first, params);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(250, 500, 0, 0);
			mMainLayout.addView(second, params);
			
			// �������������� ������������������, �������������� �������� ���������� ������������������
			for(int i = 0; i < mTaskResources.length; ++i)
			{
				ExpressionView expression = new ExpressionView(context, mTaskResources[i], 35, 35);
				// ������������������ ���� ������������ ������������������
				expression.setOnTouchListener(new OnTouchListener()
				{
					// �������������������� ������������ view, �� �������������� ������������������ ��������������
					// ������ �������������������������� ������������ ������������������������ (return true)
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
								// �������� ���������������� ���� ���� ���������� ������������������, ���� 
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
				
				if(i == 0)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(326, 330, 0, 0);
				}
				
				if(i == 1)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(438, 330, 0, 0);
				}
				
				if(i == 2)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(384, 417, 0, 0);
				}
				
				if(i == 3)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(326, 630, 0, 0);
				}
				
				if(i == 4)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(440, 630, 0, 0);
				}
				
				if(i == 5)
				{
					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(384, 714, 0, 0);
				}
				
				// ������������������ ������������������
				mMainLayout.addView(expression, params);
			}
		}
		else
		{
			// �������������� ������������������, �������������� �������� ���������� ������������������
			for(int i = 0; i < mTaskResources.length; ++i)
			{
				ExpressionView expression = new ExpressionView(context, mTaskResources[i], 60, 60);
				// ������������������ ���� ������������ ������������������
				expression.setOnTouchListener(new OnTouchListener()
				{
					// �������������������� ������������ view, �� �������������� ������������������ ��������������
					// ������ �������������������������� ������������ ������������������������ (return true)
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
								// �������� ���������������� ���� ���� ���������� ������������������, ���� 
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
				 * ������������������ ������������������
				 * ------------------
				 * |   Description  |
				 * |(expr1)	(expr2)	|
				 * |(expr3)	(expr4)	|
				 * |(expr5)	(expr6)	|
				 * \/\/\/\/\//\/\/\/
				 */
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				// ���������������� ��������������������
				if(mTaskResources[i].length() * 60 > 400)
				{
					horizontalOffset = (800 - mTaskResources[i].length() * 60)/2;
					if(i != 0)
						verticalOffcet += 75;
				}
				// �������� �������� �������������� �������������������� �� 400px - ���������������� ������������ ������������
				else
				{
					// �������� �������� �������������� ���� ������������������
					if(i != mTaskResources.length - 1)
					{
						// �������� ������������������ �������������� ���� �������������������� �� 400px
						if(mTaskResources[i + 1].length() * 60 > 400)
						{
							// �������� ���������������� ���������������������� ���������������� < 400, ���� �������� �������������� �������������� - 2�� �� ��������������
							// ���� ������������ ���������������������������� ���������������� ���� ���������������� �������������� ���������� �� 400px
							// �� ���� ������������ ������������ ���� ������������������
							if(horizontalOffset < 400)
							{
								horizontalOffset = 400 + (400 - mTaskResources[i].length()* 60) / 2;
							}
						}
						// �������� ������������������ �������������� �������������������� �� 400px
						else
						{
							// �������� ���������������� ���������������������� ���������������� < 400, ���� �������������� �������������� - 2�� �� ��������������
							// ���� ������������ ���������������������������� ���������������� ���� ���������������� �������������� ���������� �� 400px
							// �� ���� ������������ ������������ ���� ������������������
							if(horizontalOffset < 400)
							{
								horizontalOffset = 400 + (400 - mTaskResources[i].length()* 60) / 2;
							}
							// �������� ���������������� ���������������������� ���������������� > 400, ����  �������������� �������������� - 1�� �� ��������������
							// ������������ ���������������������������� ���������������� ���� ���������������� �������������� ���������� �� 400px
							// �� ������������ ���������� ���� ������������������
							else
							{
								horizontalOffset = (400 - mTaskResources[i].length() * 60) / 2;
								verticalOffcet += 75;
							}
						}
					}
					// ������������������ �������������� �������������������� �� 400px
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
				// ������������������ ������������������
				mMainLayout.addView(expression, params);
			}
		}
		
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 830));
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 830);
		params.setMargins(0, 830, 0, 0);
		this.addView(mKeyboard, params);
	}
	
	// ������������������ ����������������
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
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
		if(DELtask == 1)
    	{
    		i += 3;
    		for(; i < mMainLayout.getChildCount(); ++i)
	    	{
	    		((ExpressionView)(mMainLayout.getChildAt(i))).clearValues();
	    	}
    	}
    	else
    	{
			if(mIsDescSet)
				i++;
			for(; i < mMainLayout.getChildCount(); ++i)
	    	{
	    		((ExpressionView)(mMainLayout.getChildAt(i))).clearValues();
	    	}
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
    	if(DELtask == 1)
    	{
    		i += 3;
    		for(; i < mMainLayout.getChildCount(); ++i)
	    	{
	    		if(!((ExpressionView)(mMainLayout.getChildAt(i))).checkValues(mTaskAnswers[j]))
	    			mAnswer = false;
	    		++j;
	    	}
    	}
    	else
    	{
			if(mIsDescSet)
				i++;
	    	for(; i < mMainLayout.getChildCount(); ++i)
	    	{
	    		if(!((ExpressionView)(mMainLayout.getChildAt(i))).checkValues(mTaskAnswers[j]))
	    			mAnswer = false;
	    		++j;
	    	}
    	}
    	
    	mAlertDialog.setMessage(Boolean.toString(mAnswer)) ;
		mAlertDialog.show();
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
			
			// ���������������� ���� �������� ���������������� ������������������
			for(int i = 0; i < expression.length(); ++i)
			{
				// ������������������ i-�� ������������ �� �������������� ������������
				KeyView key = new KeyView(context, expression.substring(i, i + 1), mWidth, mHeight);
				// �������������� ���������������� ������������
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
				
				// ������������ �������������� mWidth x mHeight
				LayoutParams params = new LayoutParams(mWidth, mHeight);
				/*
				 * ���������� ���� ����������������������
				 * -----------------
				 * |(key1)(key2)...|
				 * -----------------
				 */
				params.setMargins(mWidth * i, 0, 0, 0);
				this.addView(key, params);
			}
		}
		
		// �������������� �������������� ���������������� ���������������� ��������
		// ��������������������
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

	// ���������� ��������������������
	private class KeyboardView extends RelativeLayout
	{
		private final String mOperatorsSet = "+-*/><=";
		// ������������������ �������������� ������������ ���� ��������������������
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
					// ������������������ ������������
					// ������ �������������� ���������������������� ���������������� �������������� ������������
					// ������������ �������������������� ������������������ ���������������� (return false)
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
					// ������������������ ������������
					// ������ �������������� ���������������������� ���������������� �������������� ������������
					// ������������ �������������������� ������������������ ���������������� (return false)
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
	
	// ����������-view ������������
	private class KeyView extends ImageView
	{
		private String mKeyLabel = null;
		// ���������������� ���� �������������� ������ ������������ �������������������� ������������
		private boolean mTouchable = true;
		// ���������������� ���� �������������������� ������ �������������� ��������������
		private boolean mSelectable = false;
		// ���������������� ���� ��������������������
		private boolean mSelected = false;
		private boolean focus = false;
		
		// ���������� ���������������������� ��������������
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
			// �������������������� ������������������
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
		
		// ������������������ ���� ������������
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
		
		//������������������ ���� ������������
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
		
		// ���������������� ���� ������������
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
		
		// �������������� �������������� ���� ������������ - �������� ������������ ������������������ - ���������������� ����
		// �� ������������ ������������������ ���������������� (return false)
		// ���������������������������� ������������ �������������������� ������������������������ ��������������������, �� ������������ ������������������ - ������������������
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