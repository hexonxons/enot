package app.tascact.manual.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SoftScrollView extends HorizontalScrollView {
	private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 500;
	private GestureDetector gestureDetector;
	private View[] items;
	private int itemToShow = 0;
	private int width;
	
	public SoftScrollView(Context context) {
		super(context);
	}

	public void setFeatureItems(View[] items) {
		LinearLayout internalWrapper = new LinearLayout(getContext());
		internalWrapper.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(internalWrapper);

		this.items = items;
		for (int i = 0; i < items.length; ++i) {
			internalWrapper.addView(items[i]);
		}

		gestureDetector = new GestureDetector(new SoftScrollOnGestureListener());
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// If the user swipes
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					int scrollX = getScrollX();
					itemToShow = ((scrollX + (width / 2)) / width);
					int scrollTo = itemToShow * width;
					smoothScrollTo(scrollTo, 0);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	public void scrollToItem(int item) {
		if (item >= 0 && item < items.length) {
			smoothScrollTo(width * item, 0);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
	}
	
	class SoftScrollOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				// right to left
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					itemToShow = (itemToShow < (items.length - 1)) ? itemToShow + 1
							: items.length - 1;
					smoothScrollTo(itemToShow * width, 0);
					return true;
				}
				// left to right
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					itemToShow = (itemToShow > 0) ? itemToShow - 1 : 0;
					smoothScrollTo(itemToShow * width, 0);
					return true;
				}
			} catch (Exception e) {
				Log.e("Fling", "Error processing the Fling event", e);
			}
			return false;
		}
	}
}
