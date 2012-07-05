package com.hexonxons.enote.view.taskview;

import java.io.File;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hexonxons.enote.utils.Markup;
import com.hexonxons.enote.utils.XMLUtils;
import com.hexonxons.enote.view.TaskView;
import com.hexonxons.enote.view.utils.AnswerCheckDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PlaceElements extends TaskView
{
	private Element[] mTaskElements = null;
	private FrameLayout mMainLayout = null;
	private final Point mCurrentPosition = new Point();
	private Element mTouchedElement = null;
	private LinearLayout mSymbolPlaceLayout = null;
	private LinearLayout mExpressionPlaceLayout = null;
	private String mAnswer = null;
	private AnswerCheckDialog alertDialog;
	private final Paint mFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Layout mDrawingLayout = null;
	
	public PlaceElements(Context context, Node resource, Markup markup)
	{
		super(context);

		// get resources
		NodeList taskResources = XMLUtils.evalXpathExprAsNodeList(resource,"./TaskResources/TaskResource");
		// layout for all alements
		mMainLayout = new FrameLayout(context);
		mMainLayout.setBackgroundColor(Color.WHITE);
		// finish place for pictures
		mSymbolPlaceLayout = new LinearLayout(context);
		mExpressionPlaceLayout = new LinearLayout(context);
		// pictures of task
		mTaskElements = new Element[taskResources.getLength()];
		// answers
		mAnswer = ((Node) XMLUtils.evalXpathExpr(resource, "./TaskAnswer/Answer", XPathConstants.NODE)).getTextContent();
		// layout for drawing move events
		mDrawingLayout = new Layout(context);
		
		for(int i = 0; i < taskResources.getLength(); ++i)
		{
			// get description of all task elements, such as picture, expression and symbol on the picture
			Node taskElement = taskResources.item(i);
			String symbol = ((Node) XMLUtils.evalXpathExpr(taskElement, "./ResourseSymb", XPathConstants.NODE)).getTextContent();
			String expression = ((Node) XMLUtils.evalXpathExpr(taskElement, "./ResourseValue", XPathConstants.NODE)).getTextContent();
			Node imageResource = (Node)XMLUtils.evalXpathExpr(taskElement, "./ResourseName", XPathConstants.NODE);
			// decoding the picture
			String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + imageResource.getTextContent() + ".png";
			Bitmap bg = BitmapFactory.decodeFile(filePath);
			// create task element
			mTaskElements[i] = new Element(context, symbol, expression);
			mTaskElements[i].setBackgroundDrawable(new BitmapDrawable(bg));
			mMainLayout.addView(mTaskElements[i]);
			// for every picture we create 2 place: symbol as answer and expression
			mSymbolPlaceLayout.addView(new Place(context));
			mExpressionPlaceLayout.addView(new Place(context));
		}

		mMainLayout.addView(mSymbolPlaceLayout);
		mMainLayout.addView(mExpressionPlaceLayout);
		mMainLayout.addView(mDrawingLayout);
		this.addView(mMainLayout);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{	
		int topMargin = 30;
		int leftMargin = 4;
		
		int placeWidth = w / mSymbolPlaceLayout.getChildCount();
		int placeHeight = (int) (0.1 * h);
		
		FrameLayout.LayoutParams placeLayoutParams = new FrameLayout.LayoutParams(w, placeHeight);
		placeLayoutParams.topMargin = (int) (0.7 * h);
		mSymbolPlaceLayout.setLayoutParams(placeLayoutParams);
		
		placeLayoutParams = new FrameLayout.LayoutParams(w, placeHeight);
		placeLayoutParams.topMargin = (int) (0.8 * h + 10);
		mExpressionPlaceLayout.setLayoutParams(placeLayoutParams);
				
		for(int i = 0; i < mMainLayout.getChildCount() - 3; ++i)
		{
			View child = mMainLayout.getChildAt(i);
			Bitmap bg = ((BitmapDrawable)child.getBackground()).getBitmap();
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bg.getWidth(), bg.getHeight());
			
			params.topMargin = topMargin;
			params.leftMargin = leftMargin;
			leftMargin += params.width + 4;
			
			if(leftMargin >= w)
			{
				topMargin += 2 * params.height;
				leftMargin = 4;
				params.topMargin = topMargin;
				params.leftMargin = leftMargin;
				leftMargin += params.width + 4;
			}
			
			child.setLayoutParams(params);
			
			LayoutParams placeParams = new LayoutParams(placeWidth - 4, placeHeight);
			placeParams.setMargins(2, 0, 2, 0);
			
			mSymbolPlaceLayout.getChildAt(i).setLayoutParams(placeParams);
			mExpressionPlaceLayout.getChildAt(i).setLayoutParams(placeParams);
		}
		
		mDrawingLayout.setLayoutParams(new FrameLayout.LayoutParams(w, h));
		
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void RestartTask()
	{
		// clear all places
		for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
        {
        	((Place) mSymbolPlaceLayout.getChildAt(i)).setText(null);
        	((Place) mExpressionPlaceLayout.getChildAt(i)).setText(null);
        }
		invalidate();
	}

	@Override
	public void CheckTask()
	{
		String answer = "";
		boolean result = false;
		
		for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
        {
        	answer += ((Place) mSymbolPlaceLayout.getChildAt(i)).getText();
        }
		
		if(answer.compareTo(mAnswer) == 0)
		{
			result = true;
		}
		
		alertDialog = new AnswerCheckDialog(getContext());
		alertDialog.setAnswer(result);
		alertDialog.show();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event)
    {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            mCurrentPosition.x = (int)event.getX();
            mCurrentPosition.y = (int)event.getY();
            
            for(int i = 0; i < mMainLayout.getChildCount() - 3; ++i)
            {
            	Element child = (Element) mMainLayout.getChildAt(i);
            	if(mCurrentPosition.x < child.getRight() && mCurrentPosition.x > child.getLeft() && 
            		mCurrentPosition.y > child.getTop() && mCurrentPosition.y < child.getBottom())
        		{
        			mTouchedElement = child;
        			return true;
        		}
            }
            
            mTouchedElement = null;        
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
        	if(mTouchedElement == null)
        		 return true;
        	mCurrentPosition.x = (int)event.getX();
        	mCurrentPosition.y = (int)event.getY();
        	
        	for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mSymbolPlaceLayout.getChildAt(i);
            	if(mCurrentPosition.x < child.getRight() && mCurrentPosition.x > child.getLeft() &&
            		mCurrentPosition.y > mSymbolPlaceLayout.getTop() && mCurrentPosition.y < mSymbolPlaceLayout.getBottom())
        		{
            		child.setOn(true);
            		mDrawingLayout.invalidate();
        			return true;
        		}
            }
        	
        	for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
            {
            	((Place) mSymbolPlaceLayout.getChildAt(i)).setOn(false);
            }      	
        }
        
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
        	if(mTouchedElement == null)
        		return true;
        	
        	mCurrentPosition.x = (int)event.getX();
        	mCurrentPosition.y = (int)event.getY();

        	for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mSymbolPlaceLayout.getChildAt(i);
            	if(mCurrentPosition.x < child.getRight() && mCurrentPosition.x > child.getLeft() && 
            			mCurrentPosition.y > mSymbolPlaceLayout.getTop() && mCurrentPosition.y < mSymbolPlaceLayout.getBottom())
        		{
            		child.setText(mTouchedElement.getSymbol());
            		((Place) mExpressionPlaceLayout.getChildAt(i)).setText(mTouchedElement.getExpression());
            		break;
        		}
            }
        	
        	for(int i = 0; i < mSymbolPlaceLayout.getChildCount(); ++i)
            {
            	((Place) mSymbolPlaceLayout.getChildAt(i)).setOn(false);
            }
        	
        	mTouchedElement = null;
        }
        
        mDrawingLayout.invalidate();
        return true;
    }
	
	/**
	 * Class of task picture element
	 * 
	 * mSymbol - symbol associated with that picture
	 * mExpression - math experssion associated with that picture
	 * 
	 * @author kds
	 *
	 */
	private class Element extends ImageView
	{
		private String mSymbol = null;
		private String mExpression = null;
		 
		public Element(Context context, String value, String expression)
		{
			super(context);
			mSymbol = value;
			mExpression = expression;
		}
		
		public String getSymbol()
		{
			return mSymbol;
		}	
		
		public String getExpression()
		{
			return mExpression;
		}	
	}
	
	/**
	 * Class of final destination of task picture
	 * 
	 * @author kds
	 *
	 */
	private class Place extends ImageView
	{
		private String mValue = null;
		
		public Place(Context context) 
		{
			super(context);
			this.setBackgroundColor(Color.rgb(0x05,0xD7, 0xF2));
		}
		
		public void setText(String value)
		{
			mValue = value;
		}
		
		public String getText()
		{
			return mValue;
		}

		public void setOn(boolean flag)
		{
			if(flag)
				this.setBackgroundColor(Color.BLUE);
			else
				this.setBackgroundColor(Color.rgb(0x05,0xD7, 0xF2));
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			// setting text style
			mFgPaint.setStyle(Style.FILL);
			mFgPaint.setTextSize((float) (this.getHeight() * 0.4));
			mFgPaint.setTextAlign(Paint.Align.CENTER);
			
			FontMetrics fm = mFgPaint.getFontMetrics();
			
			float textY = (this.getHeight()  - fm.ascent - fm.descent) / 2;
			
			if(mValue != null && mValue.length() != 0)
				canvas.drawText(mValue, this.getWidth()  / 2, textY, mFgPaint);
			
			super.onDraw(canvas);
		}
	}
	
	/**
	 * Class for draw moving image in top of layouts
	 * @author kds
	 *
	 */
	private class Layout extends FrameLayout
	{
		public Layout(Context context)
		{
			super(context);
			this.setBackgroundColor(Color.TRANSPARENT);
		}
		
		@Override
		protected void onDraw(Canvas canvas) 
		{		
			if(mTouchedElement != null)
			{
				canvas.drawBitmap(((BitmapDrawable)mTouchedElement.getBackground()).getBitmap(), mCurrentPosition.x, mCurrentPosition.y, null);
			}
		}
	}

}
