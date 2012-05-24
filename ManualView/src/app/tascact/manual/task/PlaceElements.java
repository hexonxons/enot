package app.tascact.manual.task;

import java.io.File;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import app.tascact.manual.Markup;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.AnswerCheckDialog;

public class PlaceElements extends TaskView
{
	private Element[] mTaskElements = null;
	private Layout mMainLayout = null;
	private final Point mCurrentPosition = new Point();
	private Element mTouchedElement = null;
	private LinearLayout mPlaceLayout = null;
	private String mAnswer = null;
	private AnswerCheckDialog alertDialog;
	
	public PlaceElements(Context context, Node resource, Markup markup)
	{
		super(context);
		//this.setOrientation(LinearLayout.VERTICAL);
		mMainLayout = new Layout(context);
		NodeList taskResources = XMLUtils.evalXpathExprAsNodeList(resource,"./TaskResources/TaskResource");
		mPlaceLayout = new LinearLayout(context);
		mTaskElements = new Element[taskResources.getLength()];
		
		mAnswer = ((Node) XMLUtils.evalXpathExpr(resource, "./TaskAnswer/Answer", XPathConstants.NODE)).getTextContent();
		
		for(int i = 0; i < taskResources.getLength(); ++i)
		{
			Node taskElement = taskResources.item(i);
			String value = ((Node) XMLUtils.evalXpathExpr(taskElement, "./ResourseValue", XPathConstants.NODE)).getTextContent();
			Node imageResource = (Node)XMLUtils.evalXpathExpr(taskElement, "./ResourseName", XPathConstants.NODE);
			String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + imageResource.getTextContent() + ".png";
			Bitmap bg = BitmapFactory.decodeFile(filePath);
			mTaskElements[i] = new Element(context, value);
			mTaskElements[i].setBackgroundDrawable(new BitmapDrawable(bg));
			mMainLayout.addView(mTaskElements[i]);
			mPlaceLayout.addView(new Place(context));
		}
		mMainLayout.setBackgroundColor(Color.WHITE);
		mMainLayout.addView(mPlaceLayout);
		this.addView(mMainLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{	
		int topMargin = 30;
		int leftMargin = 4;
		
		int placeWidth = w / mPlaceLayout.getChildCount();
		
		int placeLayoutHeight = (int) (0.1 * h);
		FrameLayout.LayoutParams placeParams = new FrameLayout.LayoutParams(w, placeLayoutHeight);
		placeParams.topMargin = (int) (0.7 * h);
		mPlaceLayout.setLayoutParams(placeParams);
				
		for(int i = 0; i < mMainLayout.getChildCount() - 1; ++i)
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
			
			
			
			View place = mPlaceLayout.getChildAt(i);
			LayoutParams placeVParams = new LayoutParams(placeWidth - 4, (int) (0.1 * h));
			placeVParams.setMargins(2, 0, 2, 0);
			place.setLayoutParams(placeVParams);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void RestartTask()
	{
		for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
        {
        	Place child = (Place) mPlaceLayout.getChildAt(i);
    		child.setText(null);
        }
		invalidate();
	}

	@Override
	public void CheckTask()
	{
		String answer = "";
		boolean result = false;
		for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
        {
        	Place child = (Place) mPlaceLayout.getChildAt(i);
        	answer += child.getText();
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
            
            for(int i = 0; i < mMainLayout.getChildCount() - 1; ++i)
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

        	for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mPlaceLayout.getChildAt(i);
            	if(mCurrentPosition.x < child.getRight() && mCurrentPosition.x > child.getLeft() && 
            			mCurrentPosition.y > mPlaceLayout.getTop() && mCurrentPosition.y < mPlaceLayout.getBottom())
        		{
            		child.setOn(true);
            		invalidate();
        			return true;
        		}
            }
        	
        	for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mPlaceLayout.getChildAt(i);
        		child.setOn(false);
            }

        	invalidate();
            return true;
        }
        
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
        	if(mTouchedElement == null)
        		return true;
        	
        	mCurrentPosition.x = (int)event.getX();
        	mCurrentPosition.y = (int)event.getY();

        	for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mPlaceLayout.getChildAt(i);
            	if(mCurrentPosition.x < child.getRight() && mCurrentPosition.x > child.getLeft() && 
            			mCurrentPosition.y > mPlaceLayout.getTop() && mCurrentPosition.y < mPlaceLayout.getBottom())
        		{
            		child.setText(mTouchedElement.getValue());
        		}
            }
        	
        	for(int i = 0; i < mPlaceLayout.getChildCount(); ++i)
            {
            	Place child = (Place) mPlaceLayout.getChildAt(i);
        		child.setOn(false);
            }
        	
        	mCurrentPosition.x = -999;
        	mCurrentPosition.y = -999;
        	
        	invalidate();

            return true;
        }
        
        return true;
    }
	
	private class Element extends ImageView
	{
		private String mValue;
		 
		public Element(Context context, String value)
		{
			super(context);
			mValue = value;
		}
		
		public String getValue()
		{
			return mValue;
		}		
	}
	
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
			Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
			fg.setStyle(Style.FILL);
			fg.setTextSize((float) (this.getHeight() * 0.6));
			fg.setTextAlign(Paint.Align.CENTER);
			
			FontMetrics fm = fg.getFontMetrics();
			
			// координаты помещения
			float textY = (this.getHeight()  - fm.ascent - fm.descent) / 2;
			
			if(mValue != null && mValue.length() != 0)
				canvas.drawText(mValue, this.getWidth()  / 2, textY, fg);
			
			super.onDraw(canvas);
		}
	}
	
	private class Layout extends FrameLayout
	{

		public Layout(Context context)
		{
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onDraw(Canvas canvas) 
		{		
			bringToFront();
			if(mTouchedElement != null)
				canvas.drawBitmap(((BitmapDrawable)mTouchedElement.getBackground()).getBitmap(), mCurrentPosition.x, mCurrentPosition.y, null);
			super.onDraw(canvas);
		}
	}

}
