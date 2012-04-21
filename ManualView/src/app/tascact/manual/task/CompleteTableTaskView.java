package app.tascact.manual.task;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.FieldView;

public class CompleteTableTaskView extends TaskView
{
	private Table mTables[];
	private String mTaskDescription = "";
	private LinearLayout mMainLayout = null;
	private TableLayout mTableLayout = null;
	private Table mFocusedTable = null;
	private AlertDialog mAlertDialog = null;
	private LogWriter mWriter = null;
	private PlayThread mThread = null;
	
	public CompleteTableTaskView(Context context, Node resource, Markup markup, LogWriter writer)
	{
		super(context);
		
		mAlertDialog = new AlertDialog.Builder(context).create();
		mWriter = writer;
		
		// Getting description of this task
		Node TaskDescription = (Node) XMLUtils.evalXpathExpr(resource, "./TaskDescription", XPathConstants.NODE);
		if (TaskDescription != null) 
		{
			mTaskDescription = TaskDescription.getTextContent();
		}
		
		// Getting resources of this task
		NodeList Tables = (NodeList) XMLUtils.evalXpathExpr(resource, "./Table", XPathConstants.NODESET);
		mTables = new Table[Tables.getLength()];
		for (int i = 0; i < Tables.getLength(); ++i) 
		{
			mTables[i] = new Table(context, Tables.item(i));
		}
		
		mTableLayout = new TableLayout(context);
		mTableLayout.setOrientation(LinearLayout.VERTICAL);
		// задаем белый фон
		mTableLayout.setBackgroundColor(Color.WHITE);
		mMainLayout = new LinearLayout(context);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		if(mTaskDescription != null)
		{
			TextView description = new TextView(context);
			description.setText(mTaskDescription);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mMainLayout.addView(description, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
			
		for(int i = 0; i < mTables.length; ++i)
		{
			mTables[i].setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if(mFocusedTable == null)
					{
						mFocusedTable = (Table) v;
					}
					else
						if(mFocusedTable != (Table) v)
						{
							mFocusedTable.setUnselected();
							mFocusedTable = (Table) v;
						}
					return false;
				}
			});
			mTableLayout.addView(mTables[i], new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		mMainLayout.addView(mTableLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LoadProgress();
	}
	
	private void LoadProgress()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;
		
		NodeList TimeStamps = act.getChildNodes();
		Table activeTable = null;
				
		for(int i = 0; i < TimeStamps.getLength(); ++i)
		{	
			//				   Event  		   LocalTime, Time, event
			Node event = TimeStamps.item(i).getFirstChild().getChildNodes().item(2);
			String eventName = event.getNodeName();
			String eventValue = event.getTextContent();
			
			if(eventName.compareTo("Table") == 0)
			{
				activeTable = (Table) mTableLayout.getChildAt(Integer.parseInt(eventValue));
				continue;
			}
			
			if(eventName.compareTo("Input") == 0)
			{
				activeTable.setInput(Integer.parseInt(eventValue));
				continue;
			}
			
			if(eventName.compareTo("KeyPress") == 0)
			{
				if(activeTable != null)
					activeTable.setKeyPress(eventValue);
				continue;
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
	    private long nextTime = -1;
	    private long timing = 0;
	    private int index = 0;
	    private Table activeTable = null;
		
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
	    			
	    			if(eventName.compareTo("Table") == 0)
	    			{
	    				activeTable = (Table) mTableLayout.getChildAt(Integer.parseInt(eventValue));
	    				continue;
	    			}
	    			
	    			if(eventName.compareTo("Input") == 0)
	    			{
	    				activeTable.setInput(Integer.parseInt(eventValue));
	    				continue;
	    			}
	    			
	    			if(eventName.compareTo("KeyPress") == 0)
	    			{
	    				if(activeTable != null)
	    					activeTable.setKeyPress(eventValue);
	    				continue;
	    			}
	        	}
	        }
	    }
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void RestartTask()
	{
		for(int i = 0; i < mTables.length; ++i)
			mTables[i].RestartTask();
	}

	@Override
	public void CheckTask()
	{
		boolean mAnswer = true;
    	for(int i = 0; i < mTables.length; ++i)
    		if(mTables[i].CheckTask() == false)
    			mAnswer = false;
    	String ans = mAnswer ? "Правильно" : "Неправильно";
		mAlertDialog.setMessage(ans);
		mAlertDialog.show();
		
	}	
	
	public void processKeyEvent(String label)
	{
		if(mFocusedTable != null)
		{
			mWriter.WriteEvent("KeyPress", label);
			mFocusedTable.setKeyPress(label);
		}
	}
	
	private class TableLayout extends LinearLayout
	{

		public TableLayout(Context context) {
			super(context);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			int childWidth = 0;
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				childWidth += this.getChildAt(i).getMeasuredWidth();
			}
			
			if (childWidth < this.getMeasuredWidth())
				this.setOrientation(LinearLayout.HORIZONTAL);
			else
				this.setOrientation(LinearLayout.VERTICAL);
			
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			super.onSizeChanged(w, h, oldw, oldh);
		}
	}
	
	private class Table extends LinearLayout
	{
		private int mRowsNum = 0;
		private int mColumnsNum = 0;
		
		// min dimension of cell
		private static final int MIN_SIZE = 60;
		private static final int MAX_SIZE = 80;
		
		private String[] mVerticalHeaders = null;
		private String[][] mValues;
		private String[][] mAnswers;
		
		private FieldView mPressedKey;
		private LinearLayout mTable = null;
		
		private String mDescription = null;
		
		private List<FieldView> mInputs = new ArrayList<FieldView>();
		
		long mPrevTouchTime = 0;
		
		public Table(Context context, Node table)
		{
			super(context);
			
			mRowsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./RowsNum", XPathConstants.NODE)).getTextContent());
			mColumnsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./ColumnsNum", XPathConstants.NODE)).getTextContent());
			
			Node TableDescription = (Node)XMLUtils.evalXpathExpr(table, "./TableDescription", XPathConstants.NODE);
			NodeList VerticalHeaders = (NodeList) XMLUtils.evalXpathExpr(table, "./VerticalHeaders/Header", XPathConstants.NODESET);
			NodeList Rows = (NodeList) XMLUtils.evalXpathExpr(table, "./Rows/Row", XPathConstants.NODESET);
			NodeList Answers = (NodeList) XMLUtils.evalXpathExpr(table, "./Answers/Row", XPathConstants.NODESET);
			
			if(VerticalHeaders.getLength() != 0)
			{
				mVerticalHeaders = new String[mRowsNum];
				for(int i = 0; i < mRowsNum; ++i)
				{
					mVerticalHeaders[i] = VerticalHeaders.item(i).getTextContent();
				}
			}
			
			mValues = new String[mRowsNum][mColumnsNum];
			for(int i = 0; i < mRowsNum; ++i)
			{
				mValues[i] = Rows.item(i).getTextContent().split("\\,");
			}
			
			mAnswers = new String[mRowsNum][mColumnsNum];
			for(int i = 0; i < mRowsNum; ++i)
			{
				mAnswers[i] = Answers.item(i).getTextContent().split("\\,");
			}
			
			mTable = new LinearLayout(context);
			mTable.setOrientation(LinearLayout.VERTICAL);
			this.setOrientation(LinearLayout.VERTICAL);
			
			if(TableDescription != null)
			{
				mDescription = TableDescription.getTextContent();
				TextView description = new TextView(context);
				description.setText(mDescription);
				description.setGravity(Gravity.CENTER_HORIZONTAL);
				description.setTextSize(30);
				description.setTextColor(Color.BLACK);
				this.addView(description, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}			
			
			// fillin row-by-row
			for(int i = 0; i < mRowsNum; ++i)
			{
				LinearLayout row = new LinearLayout(context);
				
				if(mVerticalHeaders != null)
				{
					row.addView(new FieldView(context, mVerticalHeaders[i], 10), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				}
				
				for(int j = 0; j < mColumnsNum; ++j)
				{
					if(mValues[i][j].charAt(0) == '?')
					{
						FieldView view = new FieldView(context, "", 2);
						view.setSelectable(true);
						mInputs.add(view);
						view.setOnTouchListener(new OnTouchListener()
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
									
									mWriter.WriteEvent("Table", Integer.toString(mTableLayout.indexOfChild((Table)mPressedKey.getParent().getParent().getParent())));
									mWriter.WriteEvent("Input", Integer.toString(mInputs.indexOf(mPressedKey)));
								}
								return false;
							}
						});
						
						row.addView(view);
					}
					else
						row.addView(new FieldView(context, mValues[i][j], 2));
				}
				mTable.addView(row, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 20, 5, 20);
			this.addView(mTable, params);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{	
			int cellSz = 0;

			int descriptionWidth = 0;
			
			if(mVerticalHeaders != null)
			{
				// maximum length of header
				int maxLen = 0;
				for(int i = 0; i < mRowsNum; ++i)
				{
					int len = mVerticalHeaders[i].length();
					maxLen = len > maxLen ? len : maxLen;
				}
				
				descriptionWidth = maxLen * 25;
				cellSz = (w - descriptionWidth) / mColumnsNum;
			}
			else
			{
				cellSz = w / mColumnsNum;
			}
			
			if(cellSz > MAX_SIZE)
				cellSz = MAX_SIZE;
			if(cellSz < MIN_SIZE)
				cellSz = MIN_SIZE;
			
			for(int i = 0; i < mRowsNum; ++i)
			{
				
				LinearLayout row = (LinearLayout) mTable.getChildAt(i);
				
				if(mVerticalHeaders != null)
				{
					row.getChildAt(0).setLayoutParams(new LayoutParams(descriptionWidth, cellSz));
					
					for(int j = 1; j < row.getChildCount(); ++j)
					{
						row.getChildAt(j).setLayoutParams(new LayoutParams(cellSz, cellSz));
					}
				}
				else
				{
					for(int j = 0; j < row.getChildCount(); ++j)
					{
						row.getChildAt(j).setLayoutParams(new LayoutParams(cellSz, cellSz));
						row.getChildAt(j).requestLayout();
					}
				}
			}
			super.onSizeChanged(w, h, oldw, oldh);
		}
		
		public void setKeyPress(String label)
		{
			if(mPressedKey != null)
			{
				if(label.compareTo("Стереть") == 0)
					mPressedKey.delSymb();
				else
					if(label.compareTo("Ввод") == 0)
					{
						mPressedKey.setSelected(false);
						int curIndex = mInputs.indexOf(mPressedKey);
						if(curIndex < mInputs.size() - 1)
						{
							mPressedKey = mInputs.get(curIndex + 1);
							mPressedKey.setSelected(true);
						}
						else
						{
							mPressedKey = mInputs.get(0);
							mPressedKey.setSelected(true);
						}
					}
					else
					{
						mPressedKey.addSymb(label);
					}
			}
		}
		
		public void setInput(int input)
		{
			if(mPressedKey != null)
				mPressedKey.setSelected(false);
			mPressedKey = mInputs.get(input);
			mPressedKey.setSelected(true);
		}
		
		public void setUnselected()
		{
			if(mPressedKey != null)
			{
				mPressedKey.setSelected(false);
				mPressedKey = null;
			}
		}

		public boolean CheckTask()
		{
			boolean answer = true;
			mWriter.WriteEvent("CheckTask", "");
			for(int i = 0; i < mTable.getChildCount(); ++i)
			{
				for(int j = 0; j < mColumnsNum; ++j)
				{
					FieldView field = null;
					
					if(mVerticalHeaders != null)
						field = (FieldView)((LinearLayout)mTable.getChildAt(i)).getChildAt(j + 1);
					else
						field = (FieldView)((LinearLayout)mTable.getChildAt(i)).getChildAt(j);
					
					if(field.getFieldContent().compareTo(mAnswers[i][j]) != 0)
					{
						field.Check(false);
						answer = false;
					}
					else
					{
						field.Check(true);
					}
				}
			}
			return answer;
		}

		public void RestartTask() 
		{
			for(int i = 0; i < mInputs.size(); ++i)
			{
				mInputs.get(i).setFieldContent("");
			}	
		}

	}
}
