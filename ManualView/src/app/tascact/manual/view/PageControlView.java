package app.tascact.manual.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.tascact.manual.R;

public class PageControlView extends RelativeLayout
{
	public RelativeLayout mTop = null;
	public RelativeLayout mBottom = null;
	private ImageView mTopIcon = null;
	private ImageView mBottomIcon = null;
	
	public PageControlView(Context context, boolean left)
	{
		super(context);
		
		if(left)
		{
			mTop = new RelativeLayout(context);
			mTop.setBackgroundResource(R.drawable.left_panel_top);
			
			mBottom = new RelativeLayout(context);
			mBottom.setBackgroundResource(R.drawable.left_panel_bottom);
			
			mTopIcon = new ImageView(context);
			mTopIcon.setBackgroundResource(R.drawable.contents);
			
			mBottomIcon = new ImageView(context);
			mBottomIcon.setBackgroundResource(R.drawable.prev);
		}
		else
		{
			mTop = new RelativeLayout(context);
			mTop.setBackgroundResource(R.drawable.right_panel_top);
			
			mBottom = new RelativeLayout(context);
			mBottom.setBackgroundResource(R.drawable.right_panel_bottom);
			
			mTopIcon = new ImageView(context);
			mTopIcon.setBackgroundResource(R.drawable.contents);
			
			mBottomIcon = new ImageView(context);
			mBottomIcon.setBackgroundResource(R.drawable.next);
		}
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(CENTER_VERTICAL);
		
		mTop.addView(mTopIcon, params);
		mBottom.addView(mBottomIcon, params);
		
		this.addView(mTop);
		this.addView(mBottom);		
	}
	
	public void setListeners(OnTouchListener Top, OnTouchListener Bottom)
	{
		mTop.setOnTouchListener(Top);
		mBottom.setOnTouchListener(Bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{	
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, h / 2);
		mTop.setLayoutParams(params);
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, h / 2);
		params.setMargins(0, h / 2, 0, 0);
		mBottom.setLayoutParams(params);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
