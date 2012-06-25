package com.hexonxons.enote.view.taskview;

 import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hexonxons.enote.utils.LogWriter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hexonxons.enote.R;

public class NoteLayout extends ViewGroup
{
	private int CELL_SIZE = 60;
	
	private int mColumnsNum = -1;
	private int mRowsNum = -1;	
	
	private int mTableColumnsNumber = 0;
	private int mMaxRowsNum = 1;
	
	private LogWriter mWriter = null;
	
	private NoteView mActiveArea = null;
	
	private PlayThread mThread = null;
	
	private NoteLayout thisLayout = this;

	private Bitmap mTaskImage = null;
	
	private int mDataOffset = 0;
	
	public NoteLayout(Context context, LogWriter writer)
	{
		super(context);
		mWriter = writer;
	}
	
	@Override
	public void addView(View child)
	{
		NoteView expr = (NoteView) child;
		child.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(mActiveArea != null && mActiveArea != (NoteView)v)
				{
					mActiveArea.unselect();
					mActiveArea = (NoteView)v;
				}
				else
				{
					mActiveArea = (NoteView)v;
				}
				return false;
			}
		});
		LayoutParams params = new LayoutParams((expr.getWidthInCells()) * CELL_SIZE, (expr.getHeightInCells()) * CELL_SIZE);
		mMaxRowsNum += expr.getHeightInCells() + 1;
		mTableColumnsNumber = expr.getWidthInCells();
		super.addView(child, params);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		
		mRowsNum = sizeHeight / CELL_SIZE;
		mColumnsNum = sizeWidth / CELL_SIZE;
		
		
		if(mColumnsNum / 2 > mTableColumnsNumber)
		{
			if(mMaxRowsNum / 2 > mRowsNum)
			{
				sizeHeight = (mMaxRowsNum) * CELL_SIZE > sizeHeight ? (mMaxRowsNum) * CELL_SIZE : sizeHeight;
			}
			
			if((mColumnsNum - 2 * mTableColumnsNumber) % 2 != 0)
			{
				mColumnsNum += 1;
				CELL_SIZE = sizeWidth / mColumnsNum;
			}
		}
		else
		{
			if(mMaxRowsNum > mRowsNum)
			{
				sizeHeight = mMaxRowsNum * CELL_SIZE > sizeHeight ? mMaxRowsNum * CELL_SIZE : sizeHeight;
			}
			
			if((mColumnsNum - mTableColumnsNumber) % 2 != 0)
			{
				mColumnsNum += 1;
				CELL_SIZE = sizeWidth / mColumnsNum;
			}
		}
		
		// draw task image
		if(mTaskImage != null)
		{
			mDataOffset = (mTaskImage.getHeight() / CELL_SIZE + 1) * CELL_SIZE;	
			sizeHeight += (mTaskImage.getHeight() / CELL_SIZE + 1) * CELL_SIZE;		
		}
		
		measureChildren(sizeWidth, sizeHeight);
		setMeasuredDimension(sizeWidth, sizeHeight);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE)
            {
            	NoteLayout.LayoutParams st = (NoteLayout.LayoutParams) child.getLayoutParams();
                child.layout(st.leftMargin, st.topMargin + mDataOffset, st.leftMargin + st.width, st.topMargin + st.height + mDataOffset);
            }
        }
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// draw cells bg
		if(h > 0 && w > 0)
		{
			Bitmap bg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Bitmap cellBg = ((BitmapDrawable)getResources().getDrawable(R.drawable.cell)).getBitmap();
			
		    Canvas bgCanvas = new Canvas(bg);

			for(int i = 0; i < w / CELL_SIZE + 1; ++i)
			{
				for(int j = 0; j < h / CELL_SIZE + 1; ++j)
				{
					bgCanvas.drawBitmap(cellBg, i * CELL_SIZE, j * CELL_SIZE, null);
				}
			}
			
			
			// draw task image
			if(mTaskImage != null)
			{
				int imageWidth = mTaskImage.getWidth();
			
				if(imageWidth < w)
					bgCanvas.drawBitmap(mTaskImage, (w - imageWidth) / 2, 5, null);
				else
				{
					double factor = (double) w / mTaskImage.getWidth();
					double scaleWidth = mTaskImage.getWidth() * factor;
					double scaleHeight = mTaskImage.getHeight() * factor;
					
					BitmapDrawable TaskImageBitmap = new BitmapDrawable(Bitmap.createScaledBitmap(mTaskImage, (int)scaleWidth, (int)scaleHeight, true));
					bgCanvas.drawBitmap(TaskImageBitmap.getBitmap(), (float) ((w - scaleWidth) / 2), 5, null);
				}
			}
			
		    bgCanvas.save();
		    this.setBackgroundDrawable(new BitmapDrawable(bg));
		}
		
		for(int areaindex = 0; areaindex < this.getChildCount(); ++areaindex)
		{
			NoteView area = (NoteView) this.getChildAt(areaindex);
			
			int width = area.getWidthInCells();
			int height = area.getHeightInCells();
			
			if(mColumnsNum / 2 > width && this.getChildCount() != 1)
			{
				LayoutParams params = new LayoutParams(width * CELL_SIZE, height * CELL_SIZE);
				int x = (mColumnsNum / 2 - width) / 2 * CELL_SIZE + 1 + (mColumnsNum / 2) * (areaindex % 2) * CELL_SIZE;
				int y = (areaindex / 2) * CELL_SIZE * (height + 1) + 1;
				params.setMargins(x, y, 0, 0);
				area.setLayoutParams(params);
			}
			else
			{
				LayoutParams params = new LayoutParams(width * CELL_SIZE, height * CELL_SIZE);
				int x = (mColumnsNum - width) / 2 * CELL_SIZE + 1;
				int y = areaindex * CELL_SIZE * (height + 1) + 1;
				params.setMargins(x, y, 0, 0);
				area.setLayoutParams(params);
			}
		}
		LoadProgress();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void processKeyEvent(String label)
	{
		stopReplay();
		if(mActiveArea != null)
		{
			mWriter.WriteEvent("KeyPress", label);
			mActiveArea.setText(label);
		}
	}

	public void setTaskImage(Bitmap taskImage) 
	{
		mTaskImage  = taskImage;
	}
	
	public void CheckTask()
	{
		stopReplay();
		mWriter.WriteEvent("EditEvent", "CheckTask");
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			((NoteView)this.getChildAt(i)).CheckTask();
		}
	}
	
	public void RestartTask()
	{
		stopReplay();
		mWriter.WriteEvent("EditEvent", "RestartTask");
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			((NoteView)this.getChildAt(i)).RestartTask();
		}
	}

	private void LoadProgress()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;
		
		NodeList TimeStamps = act.getChildNodes();
		NoteView activeTable = null;
				
		for(int i = 0; i < TimeStamps.getLength(); ++i)
		{	
			//				   Event  		   LocalTime, Time, event
			Node event = TimeStamps.item(i).getFirstChild().getChildNodes().item(2);
			String eventName = event.getNodeName();
			String eventValue = event.getTextContent();
			
			if(eventName.compareTo("Table") == 0)
			{
				if(activeTable != null)
					activeTable.unselect();
				activeTable = (NoteView) this.getChildAt(Integer.parseInt(eventValue));
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
					activeTable.setText(eventValue);
				continue;
			}
		}
		
		if(activeTable != null)
			activeTable.unselect();
	}

	public void replay()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;

		if(mThread != null && mThread.isAlive())
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
		if( mThread != null && mThread.isAlive())
		{
			mThread.setRunning(false);
			LoadProgress();
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
	    private NoteView activeTable = null;

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
	    				if(activeTable != null)
	    					activeTable.unselect();
	    				activeTable = (NoteView) thisLayout.getChildAt(Integer.parseInt(eventValue));
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
	    					activeTable.setText(eventValue);
	    				continue;
	    			}
	        	}
	        }
	    }
	}

	private class TableCell extends View
	{
		private String mText = "";
		private boolean mSelect = false;
		private boolean mFocus = false;
		private boolean mActive = false;
		private int mCapacity = 2;
		private int mAnswer = 0;
		private Align mTextAlign = Paint.Align.CENTER;
		
		public TableCell(Context context)
		{
			super(context);
			this.setBackgroundResource(R.drawable.cell_active);
		}
		
		public void setTextAlign(Align align)
		{
			mTextAlign = align;
		}
		
		public void addText(String text)
		{
			if(!mFocus)
			{
				mText = "";
				mFocus = true;
			}
			
			if(mText.length() < mCapacity)
			{
				mText += text;
				postInvalidate();
			}
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
			fg.setStyle(Style.FILL);
			fg.setTextSize((float) (CELL_SIZE * 0.6));
			
			FontMetrics fm = fg.getFontMetrics();
			
			// координаты помещения
			float textY = (CELL_SIZE - fm.ascent - fm.descent) / 2;
			
			fg.setTextAlign(mTextAlign);
			
			if(mText.length() != 0)
				canvas.drawText(mText, (float) (CELL_SIZE  / 2), textY, fg);
			if(mActive)
			{
				if(mAnswer == 0)
				{
					if(mSelect)
						this.setBackgroundResource(R.drawable.cell_active_pressed);
					else
						this.setBackgroundResource(R.drawable.cell_active);
				}
				else
				{
					if(mAnswer == 1)
						this.setBackgroundResource(R.drawable.cell_true);
					else
						this.setBackgroundResource(R.drawable.cell_false);
				}
				
			}
			else
				this.setBackgroundResource(R.drawable.cell_inactive);
			super.onDraw(canvas);
		}
		
		public void setFocus(boolean focus)
		{
			mFocus = focus;
		}
		
		public void setCapacity(int capacity)
		{
			mCapacity = capacity;
		}

		public void setSelect()
		{
			mSelect = !mSelect;
			postInvalidate();
		}
		
		public void setActive(boolean active)
		{
			mActive = active;
			postInvalidate();
		}
		
		public void setSelect(boolean select)
		{
			mSelect = select;
			postInvalidate();
		}

		public String getText()
		{
			return mText;
		}

		public void setAnswer(int answer)
		{
			mAnswer = answer;
			postInvalidate();
		}

		public void delSymb()
		{
			if(mText.length() != 0)
			{
				mText = mText.substring(0, mText.length() - 1);
				postInvalidate();
			}
		}
	}
	
	private abstract class NoteView extends LinearLayout
	{
		public NoteView(Context context)
		{
			super(context);
		}
		
		abstract void setInput(int index);
		abstract void CheckTask();
		abstract void RestartTask();
		abstract int getWidthInCells();
		abstract int getHeightInCells();
		abstract void unselect();
		abstract void setText(String text);
		
	}
	
	public class EditInput extends NoteView
	{
		private int mWidthCells = -1;
		private int mHeightCells = -1;
		
		private TableCell mActiveCell = null;
		
		private String mAnswer = null;
		
		public EditInput(Context context, int width, int height, int capacity)
		{
			super(context);
			
			mWidthCells = width;
			mHeightCells = height;
			
			TableCell expression = new TableCell(context);
			expression.setCapacity(capacity);
			expression.setTextAlign(Paint.Align.LEFT);
			expression.setActive(true);
			
			expression.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent argevent1)
				{
					if(mActiveCell != null)
					{
						mActiveCell.setFocus(false);
						mActiveCell.setSelect();
						mActiveCell = null;
					}
					else
					{
						mActiveCell = (TableCell)v;
						mActiveCell.setAnswer(0);
						mActiveCell.setSelect();
					}
					
					mWriter.WriteEvent("Table", Integer.toString(((ViewGroup) v.getParent().getParent()).indexOfChild((View) v.getParent())));
					mWriter.WriteEvent("Input", "0");
					
					return false;
				}
			});
			this.addView(expression, new LayoutParams(CELL_SIZE * mWidthCells, CELL_SIZE));
		}
		
		public void setText(String text)
		{
			if(mActiveCell == null)
				return;
			if(text.compareTo("Del") != 0)
				mActiveCell.addText(text);
			else
				mActiveCell.delSymb();
		}
		
		public void setRowAnswer(String rowData)
		{
			mAnswer = rowData;
		}
		
		public void CheckTask()
		{
			if(mActiveCell == null)
				return;
			String data = mActiveCell.getText();
			
			if(mAnswer.indexOf(data) < 0 || data.length() == 0 ||
			   ((data.length() < mAnswer.length() && mAnswer.charAt(data.length()) != '&') &&
			   (mAnswer.indexOf(data) + data.length() < mAnswer.length())))
				mActiveCell.setAnswer(-1);
			else
				mActiveCell.setAnswer(1);
		}

		public void RestartTask()
		{
			TableCell cell = (TableCell) this.getChildAt(0);
			cell.setFocus(false);
			cell.addText("");
		}

		@Override
		int getWidthInCells() 
		{
			return mWidthCells;
		}

		@Override
		int getHeightInCells()
		{
			return mHeightCells;
		}

		@Override
		void unselect()
		{
			if(mActiveCell != null)
			{
				mActiveCell.setSelect(false);
				mActiveCell.setFocus(false);
				mActiveCell = null;
			}
		}

		@Override
		void setInput(int index)
		{
			mActiveCell = (TableCell) this.getChildAt(index);
		}
	}
	
	public class TableInput extends NoteView
	{
		private int mWidthCells = 0;
		private int mHeightCells = 0;
		
		private List<TableCell> mActiveCells = new ArrayList<NoteLayout.TableCell>();
		private TableCell mActiveCell = null;
		
		private String[][] mAnswers = null;
		
		private int mCellNumbers = -1;
		
		private String mTableHeader = null;
		
		TableInput(Context context)
		{
			super(context);
			this.setOrientation(LinearLayout.VERTICAL);
		}
		
		public void setInput(int index)
		{
			mActiveCell = mActiveCells.get(index);			
		}

		public void unselect()
		{
			if(mActiveCell != null)
			{
				mActiveCell.setSelect(false);
				mActiveCell.setFocus(false);
				mActiveCell = null;
			}
		}

		public int getCellNumbers()
		{
			return mCellNumbers;
		}
		
		public void setText(String text)
		{
			if(mActiveCell == null)
				return;
			if(text.compareTo("Del") != 0)
				mActiveCell.addText(text);
			else
				mActiveCell.delSymb();
		}
		
		public void setRow(String rowData, String rowAnswer, int index)
		{
			LinearLayout tableRow = (LinearLayout) this.getChildAt(index + (mTableHeader == null ? 0 : 1));
			String values[] = rowData.split("\\,");
			mAnswers[index] = rowAnswer.split("\\,");
			for(int i = 0; i < mWidthCells; ++i)
			{
				TableCell cell = (TableCell) tableRow.getChildAt(i);
				if(values[i].compareTo("?") != 0)
					cell.addText(values[i]);
				else
				{
					cell.setOnTouchListener(new OnTouchListener()
					{
						@Override
						public boolean onTouch(View v, MotionEvent argevent1)
						{
							if(mActiveCell != null)
							{
								mActiveCell.setFocus(false);
								mActiveCell.setSelect();
							}
							
							mActiveCell = (TableCell)v;
							mActiveCell.setAnswer(0);
							mActiveCell.setSelect();
							
							mWriter.WriteEvent("Table", Integer.toString(((ViewGroup) v.getParent().getParent().getParent()).indexOfChild((View) v.getParent().getParent())));
							mWriter.WriteEvent("Input", Integer.toString(mActiveCells.indexOf(mActiveCell)));
							
							return false;
						}
					});
					cell.addText("");
					cell.setActive(true);
					mActiveCells.add(cell);
				}
			}
		}
		
		public void setTableHeader(String header)
		{
			mTableHeader = header;
			mHeightCells += 1;
			
			LinearLayout headerRow = new LinearLayout(getContext());
			TextView cell = new TextView(getContext());
			cell.setText(header);
			cell.setTextSize((float) (CELL_SIZE * 0.6));
			cell.setTextColor(Color.BLACK);
			cell.setGravity(Gravity.CENTER);
			headerRow.addView(cell);
			this.addView(headerRow);
		}
		
		public void setParameters(int w, int h)
		{
			mWidthCells += w;
			mHeightCells += h;		
			mCellNumbers = w * h;
			
			mAnswers = new String[h][];
			
			for(int i = 0; i < h; ++i)
			{
				LinearLayout row = new LinearLayout(getContext());
				for(int j = 0; j < mWidthCells; ++j)
				{
					row.addView(new TableCell(getContext()));
				}
				this.addView(row);
			}
		}

		public void CheckTask()
		{
			for(int i = 0 + (mTableHeader == null ? 0 : 1); i < this.getChildCount(); ++i)
			{
				LinearLayout tableRow = (LinearLayout) this.getChildAt(i);
				for(int j = 0; j < tableRow.getChildCount(); ++j)
				{
					TableCell cell = (TableCell) tableRow.getChildAt(j);
					if(cell.getText().compareTo(mAnswers[i - (mTableHeader == null ? 0 : 1)][j]) == 0)
					{
						cell.setAnswer(1);
					}
					else
					{
						cell.setAnswer(-1);
					}
				}				
			}
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			int index = (mTableHeader == null ? 0 : 1);
			
			if(index == 1)
			{
				int widthCells = ((LinearLayout) this.getChildAt(index)).getChildCount() * CELL_SIZE;
				((LinearLayout) this.getChildAt(0)).getChildAt(0).setLayoutParams(new LayoutParams(widthCells, CELL_SIZE));
			}
			
			for(; index < this.getChildCount(); ++index)
			{
				LinearLayout row = (LinearLayout) this.getChildAt(index);
				for(int j = 0; j < row.getChildCount(); ++j)
				{
					row.getChildAt(j).setLayoutParams(new LayoutParams(CELL_SIZE, CELL_SIZE));
				}
				row.setLayoutParams(new LayoutParams(CELL_SIZE * row.getChildCount(), CELL_SIZE));
				
			}
			super.onSizeChanged(w, h, oldw, oldh);
		}

		public void RestartTask()
		{
			for(int i = 0; i < mActiveCells.size(); ++i)
			{
				mActiveCells.get(i).setFocus(false);
				mActiveCells.get(i).addText("");
			}
		}

		@Override
		int getWidthInCells()
		{
			return mWidthCells;
		}

		@Override
		int getHeightInCells() 
		{
			return mHeightCells;
		}
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams
	{
		public LayoutParams(int w, int h)
		{
			super(w, h);
		}
		
		public void setMargins(int left, int top, int right, int bottom)
		{
			leftMargin = left;
			topMargin = top;
			rightMargin = right;
			bottomMargin = bottom;
		}
		
	}
}
