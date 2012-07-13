package com.hexonxons.enote.view.taskview;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.hexonxons.enote.utils.Markup;
import com.hexonxons.enote.utils.XMLUtils;
import com.hexonxons.enote.view.TaskView;
import com.hexonxons.enote.view.utils.AnswerCheckDialog;

public class SortElements extends TaskView
{
	private TaskElement mTaskElements[] = null;
	private int mTaskElementsWidth = 0;
	private int mTaskElementsHeight = 0;
	private DrawingLayout mDrawingLayout = null;
	private TaskElement mActiveElement = null;
	private int mOrientation = 0;
	// 10 pix offset between images
	private final static int OFFSET = 10;
	private PointF mAvaliablePosition = null;
	private PointF mLastTouchPoint = new PointF();
	private String mTaskAnswers[] = null;
	private AnswerCheckDialog alertDialog;
	private int mActiveElementIndex = 0;
	
	private PointF[] mInitPositions = null;
	
	public SortElements(Context context, Node resource, Markup markup)
	{
		super(context);
		
		// get resources
		NodeList taskResources = XMLUtils.evalXpathExprAsNodeList(resource,"./TaskResources/TaskResource");
		NodeList taskAnswers = XMLUtils.evalXpathExprAsNodeList(resource,"./TaskAnswers/Answer");
		mTaskElements = new TaskElement[taskResources.getLength()];
		mTaskAnswers = new String[taskResources.getLength()];
		mDrawingLayout = new DrawingLayout(context);
		mDrawingLayout.setBackgroundColor(Color.TRANSPARENT);
		mInitPositions = new PointF[taskResources.getLength()];
		for(int i = 0; i < taskResources.getLength(); ++i)
		{
			Node taskElement = taskResources.item(i);
			Node taskAnswer = taskAnswers.item(i);
			mTaskAnswers[i] = taskAnswer.getTextContent();
			// decoding the picture
			String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + taskElement.getTextContent() + ".png";
			Bitmap bm = BitmapFactory.decodeFile(filePath);
			mTaskElements[i] = new TaskElement(bm, taskElement.getTextContent());
			mTaskElementsWidth += mTaskElements[i].getWidth();
			mTaskElementsHeight += mTaskElements[i].getHeight();
		}
		
		this.addView(mDrawingLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// offset for child
		int drawOffset = OFFSET;
		// landscape orientation
		// place elements in a horizontal middle
		// all bitmaps have equal size
		if(w > h)
		{
			mOrientation = 0;
			// horizontal middle line
			int middle = h  / 2;
			// if width of all elements in sum bigger, that width of view
			// we need to scale views
			// there is mTaskElements.length + 1 offsets
			if(mTaskElementsWidth > w)
			{
				int scaledWidthOfElements = (w - (mTaskElements.length + 1) * OFFSET);
				float factor = (float)scaledWidthOfElements / mTaskElementsWidth;
				
				for(int i = 0; i < mTaskElements.length; ++i)
				{
					mTaskElements[i].setScaleFactor(factor);				
				}
			}
			
			for(int i = 0; i < mTaskElements.length; ++i)
			{
				mInitPositions[i] = new PointF(middle - mTaskElements[i].getHeight() / 2, drawOffset);
				drawOffset += mTaskElements[i].getWidth() + OFFSET;					
			}
		}
		else
		{
			mOrientation = 1;
			// vertical middle line
			int middle = w  / 2;
			
			// if height of all elements in sum bigger, that height of view
			// we need to scale views
			// there is mTaskElements.length + 1 offsets
			if(mTaskElementsWidth > w)
			{
				int scaledHeightOfElements = (h - (mTaskElements.length + 1) * OFFSET);
				float factor = (float)scaledHeightOfElements / mTaskElementsHeight;
				
				for(int i = 0; i < mTaskElements.length; ++i)
				{
					mTaskElements[i].setScaleFactor(factor);				
				}
			}
			
			for(int i = 0; i < mTaskElements.length; ++i)
			{
				mInitPositions[i] = new PointF(drawOffset, middle - mTaskElements[i].getWidth() / 2);
				drawOffset += mTaskElements[i].getHeight() + OFFSET;					
			}
		}
		
		ShuffleElements();
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				mLastTouchPoint.x = event.getX();
				mLastTouchPoint.y = event.getY();
				
				for(int i = 0; i < mTaskElements.length; ++i)
				{
					if(mTaskElements[i].isPointInside(event.getX(), event.getY()))
					{
						mActiveElement = mTaskElements[i];
						mActiveElementIndex = i;
						mAvaliablePosition = mTaskElements[i].getInitPosition();
						break;
					}
				}
				break;
			}
			
			case MotionEvent.ACTION_MOVE:
			{
				if(mActiveElement == null)
					break;
				
				mActiveElement.setPosition(mActiveElement.getTop() + event.getY() - mLastTouchPoint.y, mActiveElement.getLeft() + event.getX() - mLastTouchPoint.x);
				
				mLastTouchPoint.x = event.getX();
				mLastTouchPoint.y = event.getY();
				
				if(mOrientation == 0)
				{
					float activeElementLeft = mActiveElement.getLeft();
					
					for(int i = 0; i < mTaskElements.length; ++i)
					{
						float elementLeft = mTaskElements[i].getLeft();
						
						// if active element intersect other element more that in a half
						if(activeElementLeft < elementLeft && activeElementLeft > elementLeft - mTaskElements[i].getWidth() / 2 || 
								activeElementLeft > elementLeft && activeElementLeft < elementLeft + mTaskElements[i].getWidth() / 2)
						{
							PointF elementPosition = mTaskElements[i].getInitPosition();
							mTaskElements[i].setInitPosition(mAvaliablePosition.y, mAvaliablePosition.x);
							mTaskElements[i].setMoveInit(mAvaliablePosition.y, mAvaliablePosition.x);
							mActiveElement.setInitPosition(elementPosition.y, elementPosition.x);
							mAvaliablePosition = elementPosition;
							
							mTaskElements[mActiveElementIndex] = mTaskElements[i];
							mActiveElementIndex = i;
							mTaskElements[mActiveElementIndex] = mActiveElement;
							
						}
					}
				}
				else
				{
					float activeElementTop = mActiveElement.getTop();
					
					for(int i = 0; i < mTaskElements.length; ++i)
					{
						float elementTop = mTaskElements[i].getTop();
						
						// if active element intersect other element more that in a half
						if(activeElementTop < elementTop && activeElementTop > elementTop - mTaskElements[i].getHeight() / 2 || 
								activeElementTop > elementTop && activeElementTop < elementTop + mTaskElements[i].getHeight() / 2)
						{
							PointF elementPosition = mTaskElements[i].getInitPosition();
							mTaskElements[i].setInitPosition(mAvaliablePosition.y, mAvaliablePosition.x);
							mTaskElements[i].setMoveInit(mAvaliablePosition.y, mAvaliablePosition.x);
							mActiveElement.setInitPosition(elementPosition.y, elementPosition.x);
							mAvaliablePosition = elementPosition;
							
							mTaskElements[mActiveElementIndex] = mTaskElements[i];
							mActiveElementIndex = i;
							mTaskElements[mActiveElementIndex] = mActiveElement;
						}
					}
				}
				
					
				break;
			}
			
			case MotionEvent.ACTION_UP:
			{
				if(mActiveElement != null)
					mActiveElement.stabilize();
				
				mActiveElement = null;
				
				break;
			}
	
			default:
				break;
		}
		
		mDrawingLayout.invalidate();
		
		return true;
	}

	private void ShuffleElements()
	{
		Collections.shuffle(Arrays.asList(mTaskElements));
		
		for(int i = 0; i < mTaskElements.length; ++i)
		{
			mTaskElements[i].setInitPosition(mInitPositions[i].x, mInitPositions[i].y);
			mTaskElements[i].setMoveInit(mInitPositions[i].x, mInitPositions[i].y);
		}
	}
	
	@Override
	public void RestartTask()
	{
		ShuffleElements();
		mDrawingLayout.invalidate();
	}

	@Override
	public void CheckTask() 
	{
		boolean result = true;
		
		for(int i = 0; i < mTaskAnswers.length - 1; ++i)
		{
			if(mTaskElements[i].getResourceName().compareTo(mTaskAnswers[i]) != 0)
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
		private Bitmap mScaledBitmap = null;
		private PointF mPoint = null;
		private PointF mMovePoint = null;
		private float mScaleFactor = 1;
		private String mResourcename = null;
		
		public TaskElement(Bitmap bm, String resourceName) 
		{
			mBitmap = bm;
			mScaledBitmap = bm;
			mResourcename = resourceName;
		}
		
		public String getResourceName()
		{
			return mResourcename;
		}
		
		public void stabilize() 
		{
			// landscape
			if(mOrientation == 0)
			{
				mMovePoint.y = mPoint.y;
				mMovePoint.x = OFFSET + (int)((getLeft() + getWidth() / 2)  / (getWidth() + OFFSET)) * (int)(getWidth() + OFFSET);
			}
			else
			{
				mMovePoint.y = OFFSET + (int)((getTop() + getHeight() / 2) / (getHeight() + OFFSET)) * (int)(getHeight() + OFFSET);
				mMovePoint.x = mPoint.x;
			}
		}
		
		public Bitmap getScaledBitmap()
		{
			return mScaledBitmap;
		}

		public void setMoveInit(float top, float left)
		{
			mMovePoint = new PointF(left, top);
		}
		
		public void setInitPosition(float top, float left)
		{
			mPoint = new PointF(left, top);
		}
		
		public PointF getInitPosition()
		{
			return mPoint;
		}
		
		public void setPosition(float top, float left)
		{
			mMovePoint.x = left;
			mMovePoint.y = top;
		}
		
		public void setScaleFactor(float factor)
		{
			mScaleFactor = factor;
			mScaledBitmap = new BitmapDrawable(Bitmap.createScaledBitmap(mBitmap, (int)getWidth(), (int)getHeight(), true)).getBitmap();
		}
		
		public float getWidth() 
		{
			return mBitmap.getWidth() * mScaleFactor;
		}

		public float getHeight() 
		{
			return mBitmap.getHeight() * mScaleFactor;
		}

		public float getLeft()
		{
			return mMovePoint.x;
		}

		public float getRight() 
		{
			return mMovePoint.x + getWidth();
		}

		public float getTop()
		{
			return mMovePoint.y;
		}

		public float getBottom() 
		{
			return mMovePoint.y + getHeight();
		}

		public boolean isPointInside(float x, float y)
		{
			return x >= getLeft() && x <= getRight() &&
					y >= getTop() && y <= getBottom();
		}
	}
	
	private class DrawingLayout extends FrameLayout
	{
		private final Paint mPaint = new Paint();

		public DrawingLayout(Context context)
		{
			super(context);
			mPaint.setAntiAlias(true);
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			for(int i = 0; i < mTaskElements.length; ++i)
			{
				canvas.drawBitmap(mTaskElements[i].getScaledBitmap(), mTaskElements[i].getLeft(), mTaskElements[i].getTop(), mPaint);
			}
		}
	}
}
