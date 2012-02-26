package app.tascact.manual.view;

//import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public abstract class NormalScroll extends RelativeLayout {
	private boolean horizontalScrollEnabled = true;
	private boolean verticalScrollEnabled = true;
	private int lastTapX;
	private int lastTapY;
	private int scrollXCurrent;
	private int scrollYCurrent;
	private int scrollXDestination;
	private int scrollYDestination;
//	private ObjectAnimator animator;

	public NormalScroll(Context context) {
		this(context, true, true);
	}

	public NormalScroll(Context context, boolean hScroll, boolean vScroll) {
		super(context);
		setHorizontalScrollEnabled(hScroll);
		setVerticalScrollEnabled(vScroll);
		scrollXCurrent = 0;
		scrollYCurrent = 0;
	}

	void setHorizontalScrollEnabled(boolean isEnabled) {
		horizontalScrollEnabled = isEnabled;
	}

	void setVerticalScrollEnabled(boolean isEnabled) {
		verticalScrollEnabled = isEnabled;
	}

	public void setOffsetX(int x) {
		scrollTo(x, scrollYCurrent);
	}

	public synchronized void scrollTo(int x, int y) {
		Log.d("Scroll",
				Integer.toString(scrollXCurrent) + " "
						+ Integer.toString(scrollYCurrent));

		scrollXCurrent = x;
		scrollYCurrent = y;
		super.scrollTo(x, y);
	}

	public void scrollBy(int x, int y) {
		scrollTo(scrollXCurrent + x, scrollYCurrent + y);
	}

	public synchronized void smoothScrollTo(int x) {
		// Stopping previous animations
		//if (animator != null)
		//	animator.end();

		changeScrollDestination(x, scrollYCurrent);
		// Creating new animation/
//		animator = ObjectAnimator.ofInt(this, "offsetX", scrollXCurrent,
//				x);
//		animator.setDuration(200);

		// Starting animation
//		animator.start();
	}

	private synchronized void changeScrollDestination(int x, int y) {
		scrollXDestination = (horizontalScrollEnabled) ? x : 0;
		scrollYDestination = (verticalScrollEnabled) ? y : 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			lastTapX = (int) event.getRawX();
			lastTapY = (int) event.getRawY();
			return true;
		}
		case MotionEvent.ACTION_MOVE: {
			int x2 = (int) event.getRawX();
			int y2 = (int) event.getRawY();
			int translateX = (horizontalScrollEnabled) ? lastTapX - x2 : 0;
			int translateY = (verticalScrollEnabled) ? lastTapY - y2 : 0;

			scrollBy(translateX, translateY);

			lastTapX = x2;
			lastTapY = y2;

			return true;
		}
		}
		return super.onTouchEvent(event);
	}
}
