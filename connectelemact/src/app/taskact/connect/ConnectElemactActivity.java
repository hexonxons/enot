package app.taskact.connect;

import android.app.Activity;
import android.os.Bundle;

public class ConnectElemactActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new ConnectElemView(this));
    }
}

