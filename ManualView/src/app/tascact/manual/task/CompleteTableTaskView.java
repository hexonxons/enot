package app.tascact.manual.task;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.KeyboardView;
import app.tascact.manual.view.utils.KeyboardView.OnKeyboardKeyPressListener;

public class CompleteTableTaskView extends TaskView
{
	private Table mTables[];
	private String mTaskDescription = "";
	private RelativeLayout mMainLayout = null;
	private KeyboardView mKeyboard = null;
	private Table mFocusedTable = null;
	private AlertDialog mAlertDialog = null;
	
	public CompleteTableTaskView(Context context, Node resource, Markup markup)
	{
		super(context);
		
		mAlertDialog = new AlertDialog.Builder(context).create();
		
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
			mTables[i] = new Table(context, Tables.item(i), 250, 700);
		}
		
		mMainLayout = new RelativeLayout(context);
		// клавиатура с размером кнопок 60x60 px
		mKeyboard = new KeyboardView(context);
		// задаем белый фон
		mMainLayout.setBackgroundColor(Color.WHITE);
		
		if(mTaskDescription != null)
		{
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			TextView description = new TextView(context);
			description.setText(mTaskDescription);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mMainLayout.addView(description, params);
		}
			
		for(int i = 0; i < mTables.length; ++i)
		{
			LayoutParams params = new LayoutParams(700, 300);
			params.setMargins(50, i * 320 + 70, 0, 0);
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
			mMainLayout.addView(mTables[i], params);
		}
		
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 830));
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 830);
		params.setMargins(0, 730, 0, 0);
		mKeyboard.setOnKeyPressedListener(new OnKeyboardKeyPressListener() 
		{
			@Override
			public void onKeyboardKeyPress(String label)
			{
				if(mFocusedTable != null)
				{
					mFocusedTable.setKeyPress(label);
				}
			}
		});
		
		this.addView(mKeyboard, params);
	}
	
	private class Table extends RelativeLayout
	{
		private int mRowsNum = 0;
		private int mColumnsNum = 0;
		
		private String[] mVerticalHeaders = null;
		
		private String[][] mValues;
		private String[][] mAnswers;
		
		private FieldView mPressedKey;
		private String mDescription = null;
		
		private List<FieldView> mInputs = new ArrayList<FieldView>();
		
		long mPrevTouchTime = 0;
		
		public Table(Context context, Node table, int height, int width)
		{
			super(context);
			
			mRowsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./RowsNum", XPathConstants.NODE)).getTextContent());
			mColumnsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./ColumnsNum", XPathConstants.NODE)).getTextContent());
			
			Node TableDescription = (Node)XMLUtils.evalXpathExpr(table, "./TableDescription", XPathConstants.NODE);
			NodeList VerticalHeaders = (NodeList) XMLUtils.evalXpathExpr(table, "./VerticalHeaders/Header", XPathConstants.NODESET);
			NodeList Rows = (NodeList) XMLUtils.evalXpathExpr(table, "./Rows/Row", XPathConstants.NODESET);
			NodeList Answers = (NodeList) XMLUtils.evalXpathExpr(table, "./Answers/Row", XPathConstants.NODESET);
			
			if(TableDescription != null)
			{
				mDescription = TableDescription.getTextContent();
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				TextView description = new TextView(context);
				description.setText(mDescription);
				description.setGravity(Gravity.CENTER_HORIZONTAL);
				description.setTextSize(30);
				description.setTextColor(Color.BLACK);
				this.addView(description, params);
			}
			
			// VerticalHeaders			
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
			
			int RowHeight = height / mRowsNum - 10;
			int ColumnWidth = 0;
			
			if(mVerticalHeaders != null)
			{
				for(int i = 0; i < mRowsNum; ++i)
				{
					LayoutParams params = new LayoutParams(300, RowHeight);
					if(TableDescription != null)
						params.setMargins(0, 60 + i * RowHeight, 0, 0);
					else
						params.setMargins(0, i * RowHeight, 0, 0);
					this.addView(new FieldView(context, mVerticalHeaders[i], 300, RowHeight, 10), params);
				}
				ColumnWidth = (width - 300) / mColumnsNum;
			}
			else
				ColumnWidth = width / mColumnsNum;
			
			
			
			for(int i = 0; i < mRowsNum; ++i)
			{
				for(int j = 0; j < mColumnsNum; ++j)
				{
					LayoutParams params = new LayoutParams(ColumnWidth, RowHeight);
					if(mVerticalHeaders != null)
					{
						if(TableDescription != null)
							params.setMargins(ColumnWidth * j + 300, 60 + i * RowHeight, 0, 0);
						else
							params.setMargins(ColumnWidth * j + 300, i * RowHeight, 0, 0);
					}
					else
					{
						if(TableDescription != null)
							params.setMargins(ColumnWidth * j, 60 + i * RowHeight, 0, 0);
						else
							params.setMargins(ColumnWidth * j, i * RowHeight, 0, 0);
					}
					if(mValues[i][j].charAt(0) == '?')
					{
						FieldView view = new FieldView(context, "", ColumnWidth, RowHeight, 2);
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
								}
								return false;
							}
						});
						
						this.addView(view, params);
					}
					else
						this.addView(new FieldView(context, mValues[i][j], ColumnWidth, RowHeight, 2), params);
				}
			}
		}
		
		public void setKeyPress(String label)
		{
			if(mPressedKey != null)
			{
				if(label == "Стереть")
					mPressedKey.delSymb();
				else
					if(label == "Ввод")
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
		
		public void setUnselected()
		{
			if(mPressedKey != null)
			{
				mPressedKey.setSelected(false);
				mPressedKey = null;
			}
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
			
			public FieldView(Context context, String key, int width, int height, int capacity)
			{
				super(context);
				mFieldContent = key;
				mWidth = width;
				mHeight = height;
				mCapacity = capacity;
			}

			@Override
			protected void onDraw(Canvas canvas)
			{
				Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
				fg.setStyle(Style.FILL);
				fg.setTextSize((float) (mHeight * 0.55));
				
				FontMetrics fm = fg.getFontMetrics();
				
				// координаты помещения
				float textY = (mHeight - fm.ascent - fm.descent) / 2;
				
				fg.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(mFieldContent, mWidth / 2, textY, fg);
				
				if(mSelected)
					this.setBackgroundResource(R.drawable.selected_field);
				else
					this.setBackgroundResource(R.drawable.input_field);
				
				super.onDraw(canvas);
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

		public boolean CheckTask()
		{
			int offset = 0;
			if(mDescription != null)
				offset ++;
			if(mVerticalHeaders != null)
				offset += mVerticalHeaders.length;
			for(int i = 0; i + 3 < this.getChildCount(); ++i)
			{
				if(((FieldView)this.getChildAt(i + offset)).getFieldContent().compareTo(mAnswers[i / mColumnsNum][i % mColumnsNum]) != 0)
				{
					return false;
				}
			}
			return true;
		}

		public void RestartTask() 
		{
			for(int i = 0; i < mInputs.size(); ++i)
			{
				mInputs.get(i).setFieldContent("");
			}	
		}
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
}
