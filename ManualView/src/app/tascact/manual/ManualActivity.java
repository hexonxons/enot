package app.tascact.manual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class ManualActivity extends Activity
{
	ManualView V = null;
	int TaskSet[][] = {
			{R.drawable.pg5_2_task_1, R.drawable.pg5_2_task_2, R.drawable.pg5_2_task_3, R.drawable.pg5_2_task_4, R.drawable.pg5_2_task_5, R.drawable.pg5_2_task_6}
	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		int PageRes[][] = {
				{R.drawable.pg5_1, R.drawable.pg5_2, R.drawable.pg5_3, R.drawable.pg5_4, R.drawable.pg5_footer},
				{R.drawable.pg12_1, R.drawable.pg12_2, R.drawable.pg12_footer}
				};
		
		
		
		V = new ManualView(this, PageRes);
		setContentView(V);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		V.onTouchEvent(event);
		if(V.mTaskNum >= 0)
		{
			Intent intent = new Intent(this, TaskActivity.class);
			intent.putExtra("task", TaskSet[0]);
			startActivity(intent);
		}
		return true;
	}
}