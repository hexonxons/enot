package app.tascact.manual.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import app.tascact.manual.R;
import app.tascact.manual.XMLResources;
import app.tascact.manual.task.CompleteTableTaskView;
import app.tascact.manual.task.ConnectElementsTaskView;
import app.tascact.manual.task.SetOperatorsTaskView;
import app.tascact.manual.view.TaskControlView;
import app.tascact.manual.view.TaskView;

public class TaskActivity extends Activity {
	// View ������������������ ������������������
	private LinearLayout mMainLayout = null;
	// View �������������� ����������������
	private TaskView mTaskView = null;
	// View ���������������� ��������������������
	private TaskControlView mTaskControl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		XMLResources markup;
		try {
			markup = new XMLResources(this, extras.getString("ManualName"));

			if (extras != null) {
				mMainLayout = new LinearLayout(this);

				switch (extras.getInt("TaskType")) {
				case 1:
					mTaskView = new ConnectElementsTaskView(this, markup,
							extras.getInt("PageNumber"),
							extras.getInt("TaskNumber"));
					break;
				case 2:
					mTaskView = new CompleteTableTaskView(this, markup,
							extras.getInt("PageNumber"),
							extras.getInt("TaskNumber"));
					break;
				case 3:
					 mTaskView = new SetOperatorsTaskView(this, markup,
							 extras.getString("ManualName"),
							 extras.getInt("PageNumber"),
							 extras.getInt("TaskNumber"));
					break;
				case 99:
					// mTaskView = new ConnectElementsTaskViewBeta(this, markup,
					// extras.getInt("PageNumber"),
					// extras.getInt("TaskNumber"));
					break;
				default:
					break;
				}

				mTaskControl = new TaskControlView(this);

				// ������������ ���������������������� ��������������
				mTaskControl.mCheckButton
						.setOnTouchListener(mCheckTouchListener);
				mTaskControl.mRestartButton
						.setOnTouchListener(mRestartTouchListener);

				// ���������������������� View ����������������������
				mMainLayout.setOrientation(1);
				// ���������� �������������������� ������������s
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

				mMainLayout.addView(mTaskView, new LayoutParams(
						LayoutParams.MATCH_PARENT, 1040));
				mMainLayout.addView(mTaskControl, new LayoutParams(
						LayoutParams.MATCH_PARENT, 167));

				setContentView(mMainLayout);
			} else
				finish();
		} catch (Throwable e) {
			Log.e("XML", "Failed to get markup from XML in TaskActivity", e);
			finish();
		}
	}

	private OnTouchListener mCheckTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_DOWN) {
				((ImageView) v).setImageResource(R.drawable.checked);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP) {
				((ImageView) v).setImageResource(R.drawable.check);
				mTaskView.CheckTask();
				return true;
			}

			return true;
		}
	};

	private OnTouchListener mRestartTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int eventAction = event.getAction();

			if (eventAction == MotionEvent.ACTION_DOWN) {
				((ImageView) v).setImageResource(R.drawable.restarted);
				return true;
			}

			if (eventAction == MotionEvent.ACTION_UP) {
				((ImageView) v).setImageResource(R.drawable.restart);
				mTaskView.RestartTask();
				return true;
			}

			return true;
		}
	};
}