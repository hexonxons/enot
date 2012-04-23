package app.tascact.manual.task;

 import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import app.tascact.manual.R;
import app.tascact.manual.utils.LogWriter;

public class NoteLayout extends ViewGroup
{
	private int CELL_SIZE = 60;
	
	private int mColumnsNum = -1;
	private int mRowsNum = -1;	
	
	private int mTableColumnsNumber = 0;
	private int mMaxRowsNum = 0;
	
	private LogWriter mWriter = null;
	
	private TableActiveArea mActiveArea = null;
	
	public NoteLayout(Context context, LogWriter writer)
	{
		super(context);
		mWriter = writer;
	}
	
	@Override
	public void addView(View child)
	{
		TableActiveArea expr = (TableActiveArea) child;
		child.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(mActiveArea != null && mActiveArea != (TableActiveArea)v)
				{
					mActiveArea.unselect();
					mActiveArea = (TableActiveArea)v;
				}
				else
				{
					mActiveArea = (TableActiveArea)v;
				}
				return false;
			}
		});
		LayoutParams params = new LayoutParams((expr.mWidthCells) * CELL_SIZE, (expr.mHeightCells) * CELL_SIZE);
		mMaxRowsNum += 1 + expr.mHeightCells;
		mTableColumnsNumber = expr.mWidthCells;
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
			if(mMaxRowsNum > mRowsNum)
			{
				sizeHeight = (mMaxRowsNum  / 2) * CELL_SIZE > sizeHeight ? (mMaxRowsNum  / 2) * CELL_SIZE : sizeHeight;
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
				sizeHeight = mMaxRowsNum  * CELL_SIZE > sizeHeight ? mMaxRowsNum * CELL_SIZE : sizeHeight;
			}
			
			if((mColumnsNum - mTableColumnsNumber) % 2 != 0)
			{
				mColumnsNum += 1;
				CELL_SIZE = sizeWidth / mColumnsNum;
			}
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
                child.layout(st.leftMargin, st.topMargin, st.leftMargin + st.width, st.topMargin + st.height);
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

			for(int i = 0; i < mColumnsNum + 1; ++i)
			{
				for(int j = 0; j < mRowsNum + 1; ++j)
				{
					bgCanvas.drawBitmap(cellBg, i * CELL_SIZE, j * CELL_SIZE, null);
				}
			}
		    bgCanvas.save();
		    this.setBackgroundDrawable(new BitmapDrawable(bg));
		}	
		
		for(int areaindex = 0; areaindex < this.getChildCount(); ++areaindex)
		{
			TableActiveArea area = (TableActiveArea) this.getChildAt(areaindex);
			
			int width = area.mWidthCells;
			int height = area.mHeightCells;
			
			if(mColumnsNum / 2 > width)
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
	
	private void LoadProgress()
	{
		Node act = mWriter.GetLastAct();
		if(act == null)
			return;
		
		NodeList TimeStamps = act.getChildNodes();
		TableActiveArea activeTable = null;
				
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
				activeTable = (TableActiveArea) this.getChildAt(Integer.parseInt(eventValue));
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

	private class TableCell extends View
	{
		private String mText = "";
		private boolean mSelect = false;
		private boolean mFocus = false;
		private boolean mActive = false;
		private int mCapacity = 2;
		private int mAnswer = 0;
		
		public TableCell(Context context)
		{
			super(context);
			this.setBackgroundResource(R.drawable.cell_active);
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
			
			fg.setTextAlign(Paint.Align.CENTER);
			
			if(mText.length() != 0)
				canvas.drawText(mText, (float) (CELL_SIZE  / 2), textY, fg);
			if(mActive)
			{
				if(mAnswer == 0)
				{
					if(mSelect)
						this.setBackgroundResource(R.drawable.cell_active_pushed);
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
	
	public class TableActiveArea extends LinearLayout
	{
		public int mWidthCells = -1;
		public int mHeightCells = -1;
		public int mTopCell = -1;
		public int mLeftCell = -1;
		
		private List<TableCell> mActiveCells = new ArrayList<NoteLayout.TableCell>();
		private TableCell mActiveCell = null;
		
		private String[][] mAnswers = null;
		
		private int mCellNumbers = -1;
		
		TableActiveArea(Context context)
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
			if(text.compareTo("Del") != 0)
				mActiveCell.addText(text);
			else
				mActiveCell.delSymb();
		}
		
		public void setRow(String rowData, int index)
		{
			LinearLayout tableRow = (LinearLayout) this.getChildAt(index);
			String values[] = rowData.split("\\,");
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
		
		public void setParameters(int w, int h)
		{
			mWidthCells = w;
			mHeightCells = h;		
			mCellNumbers = w * h;
			
			mAnswers = new String[mHeightCells][];
			
			for(int i = 0; i < mHeightCells; ++i)
			{
				LinearLayout row = new LinearLayout(getContext());
				for(int j = 0; j < mWidthCells; ++j)
				{
					row.addView(new TableCell(getContext()));
				}
				this.addView(row);
			}
		}

		public boolean CheckTask()
		{
			boolean mAnswer = true;
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				LinearLayout tableRow = (LinearLayout) this.getChildAt(i);
				for(int j = 0; j < tableRow.getChildCount(); ++j)
				{
					TableCell cell = (TableCell) tableRow.getChildAt(j);
					if(cell.getText().compareTo(mAnswers[i][j]) == 0)
					{
						cell.setAnswer(1);
					}
					else
					{
						cell.setAnswer(-1);
					}
				}				
			}
			return mAnswer;
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			for(int i = 0; i < this.getChildCount(); ++i)
			{
				LinearLayout row = (LinearLayout) this.getChildAt(i);
				for(int j = 0; j < row.getChildCount(); ++j)
				{
					row.getChildAt(j).setLayoutParams(new LayoutParams(CELL_SIZE, CELL_SIZE));
				}
				
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

		public void setRowAnswer(String rowData, int index)
		{
			mAnswers[index] = rowData.split("\\,");
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

	public void processKeyEvent(String label)
	{
		if(mActiveArea != null)
		{
			mWriter.WriteEvent("KeyPress", label);
			mActiveArea.setText(label);
		}
	}
	
	public void CheckTask()
	{
		mWriter.WriteEvent("CheckTask", "");
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			((TableActiveArea)this.getChildAt(i)).CheckTask();
		}
	}
	
	public void RestartTask()
	{
		mWriter.WriteEvent("RestartTask", "");
		for(int i = 0; i < this.getChildCount(); ++i)
		{
			((TableActiveArea)this.getChildAt(i)).RestartTask();
		}
	}

}
