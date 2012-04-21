/*
 * View задач по решению выражения
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */


package app.tascact.manual.task;

import java.util.Arrays;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.FieldView;

public class SetOperatorsTaskView extends TaskView
{
	private String[] mTaskResources = null;
	private String[] mTaskAnswers = null;
	private LinearLayout mMainLayout = null;
	private RelativeLayout mExpressionLayout = null;
	private ExpressionView mSelectedExpression = null;
	private boolean mAnswer = true;
	private AlertDialog mAlertDialog = null;
	private int mWidth = 0;
	private int mHeight = 0;
	private PlayThread mThread = null;
	
	
	// время предыдущего касания
	private long mPrevTouchTime = 0;
	private LogWriter mWriter = null;
	
	public SetOperatorsTaskView(Context context, Node res, Markup markup, LogWriter writer)
	{
		super(context);
		
		mWriter = writer;
		
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
		
		mExpressionLayout = new RelativeLayout(context);
		// задаем белый фон
		mExpressionLayout.setBackgroundColor(Color.WHITE);
		mAlertDialog = new AlertDialog.Builder(context).create();
		mMainLayout = new LinearLayout(context);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		if(descr != null)
		{
			TextView description = new TextView(context);
			description.setText(descr);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mMainLayout.addView(description, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
						mWriter.WriteEvent("Expression", Integer.toString(((RelativeLayout)v.getParent()).indexOfChild(v)));
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
		mMainLayout.addView(mExpressionLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LoadProgress();
	}
	
	private void LoadProgress()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;
		
		NodeList TimeStamps = act.getChildNodes();
		ExpressionView activeExpression = null;
				
		for(int i = 0; i < TimeStamps.getLength(); ++i)
		{	
			//				   Event  		   LocalTime, Time, event
			Node event = TimeStamps.item(i).getFirstChild().getChildNodes().item(2);
			String eventName = event.getNodeName();
			String eventValue = event.getTextContent();
			
			if(eventName.compareTo("Expression") == 0)
			{
				activeExpression = (ExpressionView) mExpressionLayout.getChildAt(Integer.parseInt(eventValue));
				continue;
			}
			
			if(eventName.compareTo("KeyPress") == 0)
			{
				if(eventValue.compareTo("Ввод") == 0)
				{
					int index = mExpressionLayout.indexOfChild(activeExpression) + 1;
					if(index < mExpressionLayout.getChildCount())
						activeExpression = (ExpressionView) mExpressionLayout.getChildAt(index);
					continue;
				}
				
				if(eventValue.compareTo("Стереть") == 0)
				{
					activeExpression.delKeyLabel();
					continue;
				}
				if(activeExpression != null)
					activeExpression.setKeyLabel(eventValue);
			}
		}
	}
	
	public void replay()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;
		
		if( mThread != null && mThread.isAlive())
		{
			mThread.setRunning(false);
			LoadProgress();
		}
		else
		{
			RestartTask();
			mThread = new PlayThread(act);
			mThread.setRunning(true);
			mThread.start();
		}
	}
	
	public void stopReplay()
	{
		Log.d("Thread", "Try Stop");
		if( mThread != null && mThread.isAlive())
		{
			mThread.setRunning(false);
			LoadProgress();
			Log.d("Thread", "Stopped");
		}
	}
	
	private class PlayThread extends Thread 
	{
	    private boolean mRun = false;
	    private NodeList mTimeStamps = null;
	    private long startTime = 0;
	    private long nextTime = 0;
	    private long timing = 0;
	    private int index = 0;
	    private ExpressionView activeExpression = null;
		
	    public PlayThread(Node act)
	    {   	
	    	mTimeStamps = act.getChildNodes();
	    	String time = mTimeStamps.item(0).getFirstChild().getChildNodes().item(1).getTextContent();
	    	if(time != null)		    		
		    	nextTime = Long.parseLong(time);
	    }
	    
	    public void setRunning(boolean run)
	    {
	    	if(nextTime != -1)
	    		mRun = run;
	    }
	    
	    @Override
	    public void run() 
	    {  
	        while (mRun) 
	        {
	        	long currentTime = System.currentTimeMillis();
	        	
	        	if(currentTime - timing > nextTime - startTime)
	        	{
	        		timing = currentTime;
	        		NodeList event = mTimeStamps.item(index).getFirstChild().getChildNodes();
	        		startTime = nextTime;
	        		nextTime = Long.parseLong(event.item(1).getTextContent());
	        		index++;
	        		if(index >= mTimeStamps.getLength())
	        			mRun = false;
	        		String eventName = event.item(2).getNodeName();
	    			String eventValue = event.item(2).getTextContent();
	    			
	    			if(eventName.compareTo("Expression") == 0)
	    			{
	    				if(activeExpression != null)
	    					activeExpression.setSelected(false);
	    				activeExpression = (ExpressionView) mExpressionLayout.getChildAt(Integer.parseInt(eventValue));
	    				activeExpression.setCursor();
	    				continue;
	    			}
	    			
	    			if(eventName.compareTo("KeyPress") == 0)
	    			{
	    				if(eventValue.compareTo("Ввод") == 0)
	    				{
	    					int i = mExpressionLayout.indexOfChild(activeExpression) + 1;
	    					if(i < mExpressionLayout.getChildCount())
	    					{
	    						activeExpression.setSelected(false);
	    						activeExpression = (ExpressionView) mExpressionLayout.getChildAt(i);
	    						activeExpression.setCursor();
	    					}
	    					continue;
	    				}
	    				
	    				if(eventValue.compareTo("Стереть") == 0)
	    				{
	    					activeExpression.delKeyLabel();
	    					continue;
	    				}
	    				
	    				activeExpression.setKeyLabel(eventValue);
	    			}
	        	}
	        }
	    }
	}
	
	public void processKeyEvent(String label)
	{
		mWriter.WriteEvent("KeyPress", label);
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

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mWidth = w;
		mHeight = h;
		
		int maxHeight = 70;
		
		int expressionCount = mExpressionLayout.getChildCount();
		int offsetW = 10;
		int offsetH = 20;
		
		
		int expressionWidth = (mWidth - offsetW * 3)/2;
		int expressionHeight = mHeight / (expressionCount);

		if(expressionHeight > maxHeight)
			expressionHeight = maxHeight;
		
		if(expressionHeight * expressionCount + 20 * (expressionCount + 1) < mHeight)
		{
			expressionWidth = mWidth - offsetW * 3;
			for(int i = 0; i < mExpressionLayout.getChildCount(); ++i)
			{
				ExpressionView expression = (ExpressionView) mExpressionLayout.getChildAt(i);
				LayoutParams params = new LayoutParams(expressionWidth, expressionHeight);
				params.setMargins((mWidth - expressionWidth) / 2, offsetH + (offsetH + expressionHeight) * i, 0, 0);
				expression.setLayoutParams(params);
			}
		}
		else
		{
			for(int i = 0; i < mExpressionLayout.getChildCount(); ++i)
			{
				ExpressionView expression = (ExpressionView) mExpressionLayout.getChildAt(i);
				LayoutParams params = new LayoutParams(expressionWidth, expressionHeight);
				params.setMargins(offsetW + (offsetW + expressionWidth) * (i % 2), offsetH + (20 + expressionHeight) * (i  / 2), 0, 0);
				expression.setLayoutParams(params);
			}
		}
			
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	
	public void RestartTask()
    {
		stopReplay();
		for(int i = 0; i < mExpressionLayout.getChildCount(); ++i)
    	{
    		((ExpressionView)(mExpressionLayout.getChildAt(i))).clearValues();
    	}
    	mSelectedExpression = null;
    	mAnswer = true;
		invalidate();
    }
    
    public void CheckTask()
    {
    	stopReplay();
    	mAnswer = true;
    	
    	int j = 0;
    	for(int i = 0; i < mExpressionLayout.getChildCount(); ++i)
    	{
    		if(!((ExpressionView)(mExpressionLayout.getChildAt(i))).checkValues(mTaskAnswers[j]))
    			mAnswer = false;
    		++j;
    	}
    	mWriter.WriteEvent("TaskCheck", Boolean.toString(mAnswer));
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
					mPressedKey = field;
					mInputs[inputIndex] = field;
					inputIndex++;
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
			for(int i = 0; i < mInputs.length; ++i)
			{
				if(mInputs[i].getFieldContent() == "")
				{
					mPressedKey = mInputs[i];
					mPressedKey.setSelected(true);
					return;
				}
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
				mInputs[i].setChecked(false);
				mPressedKey = null;
			}
		}
		
		public boolean checkValues(String answer)
		{
			String data = "";
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				data += ((FieldView)(this.getChildAt(i))).getFieldContent();
			}
			
			if(answer.indexOf(data) < 0 || data.length() == 0 ||
			   ((data.length() < answer.length() && answer.charAt(data.length()) != '&') &&
			   (answer.indexOf(data) + data.length() < answer.length())))
			{
				for(int i = 0; i < mInputs.length; ++i)
				{
					mInputs[i].Check(false);
				}
				return false;
			}

			for(int i = 0; i < mInputs.length; ++i)
			{
				mInputs[i].Check(true);
			}
			return true;
		}
	}	
}
