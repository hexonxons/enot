package app.tascact.manual;

import android.app.Activity;
import android.os.Bundle;

public class TaskActivity extends Activity
{
	Bundle extras = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	int TaskSet[];
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        
        if(extras != null)
		{
        	TaskSet = extras.getIntArray("task");
        	TaskView V = new TaskView(this, TaskSet);
    		setContentView(V);
		}
        else
        	finish();
    }
}