package com.hexonxons.enote.view.taskview;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hexonxons.enote.R;
import com.hexonxons.enote.utils.Markup;
import com.hexonxons.enote.utils.XMLUtils;
import com.hexonxons.enote.view.TaskView;

public class ShapesCuttingTaskView extends TaskView
{
	// offset from bounds
	private final static int BOUNDS_OFFSET = 50;
	private int mRowsNumber = 0;
	private int mColumnsNumber = 0;
	private DrawingLayout mDrawingLayout = null;
	private Map<String, Bitmap> mShapes = null;
	private FrameLayout mMainLayout = null; 
	private LinearLayout mTableLayout = null;
	private Pair<Point, Point> mCutLine = null;
	private CellNode mNodes[][] = null;
	private Paint mPaint = new Paint();
	private Bitmap mNodePoint = null;
	private Bitmap mNodePointPressed = null;
	
	public ShapesCuttingTaskView(Context context, Node resource, Markup markup)
	{
		super(context);
		
		mCutLine = new Pair<Point, Point>(new Point(), new Point());
		mNodePoint = ((BitmapDrawable)getResources().getDrawable(R.drawable.cell_node)).getBitmap();
		mNodePointPressed = ((BitmapDrawable)getResources().getDrawable(R.drawable.cell_node_pressed)).getBitmap();
		NodeList Shapes = XMLUtils.evalXpathExprAsNodeList(resource, "./TaskShapes/Shape");
		Node Table = XMLUtils.evalXpathExprAsNode(resource, "./Table");
		NodeList Rows = XMLUtils.evalXpathExprAsNodeList(Table, "./Row");
		
		mRowsNumber = Integer.parseInt(((Attr)Table.getAttributes().getNamedItem("rows")).getValue());
		mColumnsNumber = Integer.parseInt(((Attr)Table.getAttributes().getNamedItem("columns")).getValue());
		
		mShapes = new HashMap<String, Bitmap>();
		for(int i = 0; i < Shapes.getLength(); ++i)
		{
			String shapename = Shapes.item(i).getTextContent();
			mShapes.put(shapename, BitmapFactory.decodeFile(markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + Shapes.item(i).getTextContent() + ".png"));
		}
		
		mNodes = new CellNode[mRowsNumber + 1][];
		for(int i = 0; i < mRowsNumber + 1; ++i)
			mNodes[i] = new CellNode[mColumnsNumber + 1];
		
		mMainLayout = new FrameLayout(context);
		mTableLayout = new LinearLayout(context);
		mTableLayout.setOrientation(LinearLayout.VERTICAL);
		mDrawingLayout = new DrawingLayout(context);
		mDrawingLayout.setBackgroundColor(Color.TRANSPARENT);
		
		for(int i = 0; i < mRowsNumber; ++i)
		{
			LinearLayout row = new LinearLayout(context);
			String[] cellShapes = Rows.item(i).getTextContent().split(",");
			
			for(int j = 0; j < mColumnsNumber; ++j)
			{
				Cell cell = new Cell(context);
				String shapeName = cellShapes[j];
				if(shapeName.compareTo("null") != 0)
				{
					cell.setImageBitmap(mShapes.get(cellShapes[j]));
					
					// fill mNodes
					for(int k = i; k < i + 2; ++k)
					{
						for(int l = j; l < j + 2; ++l)
						{
							if(mNodes[k][l] == null)
							{
								mNodes[k][l] = new CellNode();
							}
						}
					}
				}
				row.addView(cell);
			}
			
			
			
			mTableLayout.addView(row);
		}
		
		mMainLayout.addView(mTableLayout);
		mMainLayout.addView(mDrawingLayout, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		int width = w - 2 * BOUNDS_OFFSET;
		int height = h - 2 * BOUNDS_OFFSET;
		
		int cellsz = Math.min(width / (mColumnsNumber + 1), height / (mRowsNumber + 1));
		
		int leftOffset = (w - cellsz * mColumnsNumber) / 2;
		int topOffset = (h - cellsz * mRowsNumber) / 4;
		
		// lal, 2 times called shit(
		if(oldw == 0 && oldh == 0)
		{
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellsz * mColumnsNumber, cellsz * mRowsNumber);
			params.setMargins(leftOffset, topOffset, 0, 0);
			mTableLayout.setLayoutParams(params);
			
			for(int i = 0; i < mRowsNumber + 1; ++i)
			{
				for(int j = 0; j < mColumnsNumber + 1; ++j)
				{
					if(mNodes[i][j] != null)
					{
						int x = leftOffset + j * cellsz - mNodePoint.getWidth() / 2;
						int y = topOffset + i * cellsz - mNodePoint.getHeight() / 2;
					
						mNodes[i][j].setPoint(x, y);
					}
				}
			}
			
			for(int i = 0; i < mRowsNumber; ++i)
			{
				LinearLayout row = (LinearLayout) mTableLayout.getChildAt(i);
				for(int j = 0; j < row.getChildCount(); ++j)
				{
					row.getChildAt(j).setLayoutParams(new LayoutParams(cellsz, cellsz));
				}
			}
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		return super.onTouchEvent(event);
	}
	
	@Override
	public void RestartTask()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void CheckTask()
	{
		// TODO Auto-generated method stub

	}

	private class Cell extends ImageView
	{

		public Cell(Context context)
		{
			super(context);
		}
		
	}
	
	private class CellNode
	{
		int x;
		int y;
		
		public void setPoint(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public Point getPoint()
		{
			return new Point(x, y);
		}
		
	}
	
	private class DrawingLayout extends FrameLayout
	{
		
		public DrawingLayout(Context context)
		{
			super(context);
			
			
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			
			for(int i = 0; i < mRowsNumber + 1; ++i)
			{
				for(int j = 0; j < mColumnsNumber + 1; ++j)
				{					
					if(mNodes[i][j] != null)
					{
						Point p = mNodes[i][j].getPoint();
						canvas.drawBitmap(mNodePoint, p.x, p.y, mPaint);
					}
				}
			}
			super.onDraw(canvas);
		}
	}
}
