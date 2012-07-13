package com.hexonxons.enote.view.taskview;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.w3c.dom.Node;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.hexonxons.enote.utils.Markup;
import com.hexonxons.enote.utils.XMLUtils;
import com.hexonxons.enote.view.TaskView;
import com.hexonxons.enote.view.utils.AnswerCheckDialog;

public class TangramTaskView extends TaskView
{
	// grid step in pixels
	private final static int GRID_STEP = 25;
	private AnswerCheckDialog alertDialog;
	private DrawingLayout mDrawingLayout = null;
	private Bitmap mHintBitmap = null;
	private Bitmap mMainResourceBitmap = null;
	private Shape[] mShapes = null;
	private float mScaleFactor = 1.0f;
	private boolean mIsShapesDecoded = false;
	private ArrayList<TaskElement> mTaskElements = null;
	private TaskElement[] mAnswerTaskElements = null;
	private final Paint mPaint = new Paint();
	private TaskElement mActiveElement = null;
	private Point mLastTouchPoint = new Point();
	private int mWidth = 0;
	private int mHeight = 0;
	public TangramTaskView(Context context, Node resource, Markup markup) 
	{
		super(context);

		Node nTaskHint = XMLUtils.evalXpathExprAsNode(resource,"./TaskHint");
		Node nShapesNumber = XMLUtils.evalXpathExprAsNode(resource,"./ShapesNumber");
		Node nMainResource = XMLUtils.evalXpathExprAsNode(resource,"./TaskAnswer");
		
		int shapesNumber = Integer.parseInt(nShapesNumber.getTextContent());
		String taskMainResourceName = nMainResource.getTextContent();
		String taskHintResourceName = nTaskHint.getTextContent();
		
		mShapes = new Shape[shapesNumber];
		for(int i = 0; i < shapesNumber; ++i)
		{
			mShapes[i] = new Shape();
		}
		mAnswerTaskElements = new TaskElement[shapesNumber];
		
		mTaskElements = new ArrayList<TaskElement>();
		
		mHintBitmap = BitmapFactory.decodeFile(markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + taskHintResourceName + ".png");
		mMainResourceBitmap = BitmapFactory.decodeFile(markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + taskMainResourceName + ".png");

		mDrawingLayout = new DrawingLayout(context);
		mDrawingLayout.setBackgroundColor(Color.TRANSPARENT);
		
		this.setBackgroundColor(Color.WHITE);
		this.addView(mDrawingLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		
		if(w < mHintBitmap.getWidth() + 2 * GRID_STEP || h < mHintBitmap.getHeight() + 2 * GRID_STEP || h < mHintBitmap.getWidth() + 2 * GRID_STEP || w < mHintBitmap.getHeight() + 2 * GRID_STEP)
			mScaleFactor = (float) Math.min(mHintBitmap.getWidth(),  mHintBitmap.getHeight()) / Math.max(w, h) / 1.15f;		
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		//decode shapes
		if(!mIsShapesDecoded)
		{
			mIsShapesDecoded = true;
			mMainResourceBitmap = Bitmap.createScaledBitmap(mMainResourceBitmap, (int)(mMainResourceBitmap.getWidth() * mScaleFactor), (int)(mMainResourceBitmap.getHeight() * mScaleFactor), false);
			try 
			{
				readShapes(mMainResourceBitmap);
			} 
			catch (Throwable e) 
			{
				e.printStackTrace();
			}
			
			mHintBitmap = Bitmap.createScaledBitmap(mHintBitmap, (int)(mHintBitmap.getWidth() * mScaleFactor), (int)(mHintBitmap.getHeight() * mScaleFactor), false);
			
			Random rnd = new Random(System.currentTimeMillis());
			
			for(int i = 0; i < mShapes.length; ++i)
			{
				Bitmap bm = mShapes[i].getBitmap();
				if(bm == null)
					continue;
				
				TaskElement element = new TaskElement(bm);
				mAnswerTaskElements[i] = element;
				element.setPointInit(mShapes[i].getLeft(), mShapes[i].getTop());
				element.setMoveInit(rnd.nextInt(mHeight - element.getHeight()), 
									rnd.nextInt(mWidth - element.getWidth()));
				element.setNodePoint(mShapes[i].getNodePoint());

				mTaskElements.add(element);
			}	
		}
		canvas.drawBitmap(mHintBitmap, (float) ((canvas.getWidth() - mHintBitmap.getWidth()) / 2 / GRID_STEP * GRID_STEP), 0, mPaint);
		
		super.onDraw(canvas);
	}
	
	private void readShapes(Bitmap bm) throws Throwable
	{
		int lastFreeIndex = 0;
		
		// read bitmap and detect bounds of every shape
		for(int i = 0; i < bm.getHeight(); ++i)
		{
			for(int j = 0; j < bm.getWidth(); ++j)
			{
				int pixel = bm.getPixel(j, i);
				boolean flag = false;
				
				if(Color.alpha(pixel) < 20)
					continue;
				
				for(int k = 0; k < mShapes.length; ++k)
				{
					Shape element = mShapes[k];
					
					int redDiff = Color.red(pixel) - Color.red(element.getColor());
					int blueDiff = Color.blue(pixel) - Color.blue(element.getColor());
					int greenDiff = Color.green(pixel) - Color.green(element.getColor());
					
					if(Math.sqrt(redDiff * redDiff + blueDiff * blueDiff + greenDiff * greenDiff) < 100)
					{
						element.checkSize(j, i);
						flag = true;
					}
				}
				
				// false flag means that this color wasnt still checked as a shape
				if(!flag)
				{
					if(lastFreeIndex >= mShapes.length)
						throw new Throwable(new ArrayIndexOutOfBoundsException(lastFreeIndex));
					
					mShapes[lastFreeIndex].setColor(pixel);
					mShapes[lastFreeIndex].checkSize(j, i);
					lastFreeIndex++;
				}
			}
		}
		
		// init width, height and create array for bitmap
		for(int k = 0; k < mShapes.length; ++k)
		{
			mShapes[k].Init();
		}
		
		// fill shape with a color
		for(int i = 0; i < bm.getHeight(); ++i)
		{
			for(int j = 0; j < bm.getWidth(); ++j)
			{
				int pixel = bm.getPixel(j, i);
				
				if(Color.alpha(pixel) < 20)
					continue;
				
				for(int k = 0; k < mShapes.length; ++k)
				{
					Shape element = mShapes[k];
					
					int redDiff = Color.red(pixel) - Color.red(element.getColor());
					int blueDiff = Color.blue(pixel) - Color.blue(element.getColor());
					int greenDiff = Color.green(pixel) - Color.green(element.getColor());
					
					if(Math.sqrt(redDiff * redDiff + blueDiff * blueDiff + greenDiff * greenDiff) < 100)
					{
						element.addPixel(j, i);
					}
				}
			}
		}
		
		for(int k = 0; k < mShapes.length; ++k)
		{
			mShapes[k].createShapeBitmap();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				boolean isActiveElement = false;
				
				mLastTouchPoint.x = (int) event.getX();
				mLastTouchPoint.y = (int) event.getY();
				
				for(int i = mTaskElements.size() - 1; i >= 0 ; --i)
				{
					if(mTaskElements.get(i).isPointInside(event.getX(), event.getY()))
					{
						mActiveElement = mTaskElements.get(i);
						isActiveElement = true;
						
						if(Color.alpha(mActiveElement.getColorByAbsoluteCoords(event.getX(), event.getY())) < 20)
						{
							mActiveElement = null;
						}
						else
						{
							mTaskElements.remove(i);
							mTaskElements.add(mActiveElement);
							break;
						}
					}
				}
				if(!isActiveElement)
					mActiveElement = null;
				break;
			}
			
			case MotionEvent.ACTION_MOVE:
			{
				if(mActiveElement == null)
					break;
				
				mActiveElement.setPosition((int)(mActiveElement.getTop() + event.getY() - mLastTouchPoint.y), (int)(mActiveElement.getLeft() + event.getX() - mLastTouchPoint.x));
				
				mLastTouchPoint.x = (int) event.getX();
				mLastTouchPoint.y = (int) event.getY();

				break;
			}
			
			case MotionEvent.ACTION_UP:
			{
				if(mActiveElement != null)
					mActiveElement.calcPosition();
				mActiveElement = null;
				break;
			}
	
			default:
				break;
		}
		
		mDrawingLayout.invalidate();
		
		return true;
	}

	@Override
	public void RestartTask()
	{
		Random rnd = new Random(System.currentTimeMillis());
		for(int i = 0; i < mTaskElements.size(); ++i)
		{	
			TaskElement element = mTaskElements.get(i);
			element.setMoveInit(rnd.nextInt(mHeight - element.getHeight()), 
								rnd.nextInt(mWidth - element.getWidth()));
		}	
		invalidate();
	}

	@Override
	public void CheckTask()
	{
		boolean result = true;
		for(int i = 1; i < mAnswerTaskElements.length; ++i)
		{
			Point moveFirst = new Point(mAnswerTaskElements[i - 1].getLeft(), mAnswerTaskElements[i - 1].getTop());
			Point moveSecond = new Point(mAnswerTaskElements[i].getLeft(), mAnswerTaskElements[i].getTop());

			Point initFirst = mAnswerTaskElements[i - 1].getPointInit();
			Point initSecond = mAnswerTaskElements[i].getPointInit();
			
			if(Math.abs(moveFirst.x - moveSecond.x) != Math.abs(initFirst.x - initSecond.x) || 
			   Math.abs(moveFirst.y - moveSecond.y) != Math.abs(initFirst.y - initSecond.y))
			{
				result = false;
				break;
			}

		}

		alertDialog = new AnswerCheckDialog(getContext());
		alertDialog.setAnswer(result);
		alertDialog.show();
	}
	
	private class TaskElement
	{
		private Bitmap mBitmap = null;
		private Point mMovePoint = null;
		private Point mNodePoint = null;		
		private Point mInit = new Point();
		
		public TaskElement(Bitmap bm) 
		{
			mBitmap = bm;
		}
		
		public void setPointInit(int top, int left)
		{
			mInit = new Point(top, left);
		}
		
		public Point getPointInit()
		{
			return mInit;
		}

		public void calcPosition()
		{
			int npx = (int) (mNodePoint.x + getLeft());
			int npy = (int) (mNodePoint.y + getTop());
			
			mMovePoint.x = mMovePoint.x + (npx % GRID_STEP > GRID_STEP / 2 ? GRID_STEP - npx % GRID_STEP : - npx % GRID_STEP);
			mMovePoint.y = mMovePoint.y + (npy % GRID_STEP > GRID_STEP / 2 ? GRID_STEP - npy % GRID_STEP : - npy % GRID_STEP);
		}

		public void setNodePoint(Point nodePoint)
		{
			mNodePoint = nodePoint;
		}

		public Bitmap getBitmap()
		{
			return mBitmap;
		}
		
		public void setMoveInit(int top, int left)
		{
			mMovePoint = new Point(left, top);
		}
		
		public void setPosition(int top, int left)
		{
			mMovePoint.x = left;
			mMovePoint.y = top;
		}
		
		public int getWidth() 
		{
			return mBitmap.getWidth();
		}

		public int getHeight() 
		{
			return mBitmap.getHeight();
		}

		public int getLeft()
		{
			return mMovePoint.x;
		}

		public float getRight() 
		{
			return mMovePoint.x + getWidth();
		}

		public int getTop()
		{
			return mMovePoint.y;
		}

		public float getBottom() 
		{
			return mMovePoint.y + getHeight();
		}
		
		public boolean isPointInside(float x, float y)
		{
			return x >= getLeft() && x <= getRight() && y >= getTop() && y <= getBottom();
		}
		
		public int getColorByAbsoluteCoords(float x, float y)
		{
			return mBitmap.getPixel((int) ((x - mMovePoint.x)), (int) ((y - mMovePoint.y)));
		}
	}
	
	private class Shape
	{
		// shape color
		private int mColor;
		// shape rectangle
		private int left = Integer.MAX_VALUE;
		private int right = 0;
		private int top = Integer.MAX_VALUE;
		private int bottom = 0;
		// width and height
		private int width = 0;
		private int height = 0;
		// source for new bitmap
		private int[] src;
		
		private Point nodePoint = null;
		private boolean isNodeSettled = false;
		
		private Bitmap mBitmap = null;

		/**
		 * Check bounds of shape
		 * @param x
		 * @param y
		 */
		public void checkSize(int x, int y)
		{
			if(left > x)
				left = x;
			if(right < x)
				right = x;
			if(top > y)
				top = y;
			if(bottom < y)
				bottom = y;
		}

		public Point getNodePoint()
		{
			return nodePoint;
		}

		/**
		 * Init width, height values and allocation memory for picture colors array
		 */
		public void Init()
		{
			width = right - left + 1;
			height = bottom - top + 1;
			
			src = new int[width * height + 1];
			Arrays.fill(src, (byte) 0);
		}
		
		/**
		 * Add pixel to final bitmap
		 * @param x
		 * @param y
		 */
		public void addPixel(int x, int y)
		{
			try 
			{
				if(!isNodeSettled && x % GRID_STEP == 0 && y % GRID_STEP == 0)
				{
					isNodeSettled = true;
					nodePoint = new Point(x - left, y - top);
				}
				src[(y - top) * width + x - left] = mColor;
			} 
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			
		}
		
		public int getTop()
		{
			return top;
		}
		
		public int getLeft()
		{
			return left;
		}

				
		/**
		 * Get bitmap from src[] array.
		 * src = null after calling this function
		 * @return created shape bitmap
		 */
		public void createShapeBitmap()
		{						
			if(width > 0 && height > 0)
				mBitmap = Bitmap.createBitmap(src, width, height, Config.ARGB_8888);
			src = null;
			System.gc();
			//mBitmap = Bitmap.createScaledBitmap(mBitmap, (int)(mBitmap.getWidth() * mScaleFactor), (int)(mBitmap.getHeight() * mScaleFactor), false);

			
			//nodePoint.x += left - left * mScaleFactor;
			///nodePoint.y += top - top * mScaleFactor;
			//left *= mScaleFactor;
			//top *= mScaleFactor;
		}
		
		public Bitmap getBitmap()
		{
			return mBitmap;
		}
		
		public int getColor()
		{
			return mColor;
		}
		
		public void setColor(int color)
		{
			mColor = color;
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
			super.onDraw(canvas);
			for(int i = 0; i < mTaskElements.size(); ++i)
			{
				TaskElement element = mTaskElements.get(i);
				canvas.drawBitmap(element.getBitmap(), element.getLeft(), element.getTop(), mPaint);
			}
		}
	}

}
