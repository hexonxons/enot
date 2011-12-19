package app.tascact.manual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class ManualActivity extends Activity
{
	private ManualView V = null;
	
	private CResources res = new CResources();
	private long mPrevTouchTime = 0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		V = new ManualView(this, res.PageResources);
		setContentView(V);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getEventTime() - mPrevTouchTime > 250)
	    {
			mPrevTouchTime = event.getEventTime();
			
			V.processEvent(event);
			if(V.mTaskNum >= 0 && res.TaskResources[V.mCurrPageNum][V.mTaskNum][0] != 0)
			{
				Intent intent = new Intent(this, TaskActivity.class);
				intent.putExtra("task", res.TaskResources[V.mCurrPageNum][V.mTaskNum]);
				startActivity(intent);
			}
	    }
		return true;
	}
}