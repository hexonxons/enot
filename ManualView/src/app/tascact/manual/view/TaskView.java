package app.tascact.manual.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import javax.xml.*;

public class TaskView extends RelativeLayout {

	public TaskView(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void RestartTask() {
	}

	public void CheckTask() {
	}
}