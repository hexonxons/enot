package app.tascact.manual.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Pair;
import android.view.MotionEvent;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

public class GroupingElementsTaskView extends TaskView {
	private Node inputParams;
	private Resources resources;
	private TaskElement[] taskElements;
	private Paint emptyPaint;
	private float scaleKoeff = 1.0f;
	private int width;
	private int height;
	private int touchedElement = -1;
	private PointF lastTouchedPoint;
	private String[][] trueAnswers;
	private AlertDialog alertDialog;

	public GroupingElementsTaskView(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		resources = context.getResources();

		NodeList nodes = null;
		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskResources/TaskResource");

		int N = nodes.getLength();
		taskElements = new TaskElement[N];

		for (int i = 0; i < N; ++i) {
			taskElements[i] = new TaskElement();
			taskElements[i].resourceName = nodes.item(i).getTextContent();
			taskElements[i].resourceId = resources.getIdentifier(
					taskElements[i].resourceName, "drawable",
					context.getPackageName());
			taskElements[i].bitmap = BitmapFactory.decodeResource(resources,
					taskElements[i].resourceId);
			taskElements[i].position = new PointF();
		}

		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskAnswer/Answer");
		trueAnswers = new String[nodes.getLength()][];
		for (int i = 0; i < trueAnswers.length; ++i) {
			NodeList answers = XMLUtils.evalXpathExprAsNodeList(nodes.item(i),
					"./TaskResource");
			trueAnswers[i] = new String[answers.getLength()];
			for (int j = 0; j < trueAnswers[i].length; ++j) {
				trueAnswers[i][j] = answers.item(j).getTextContent();
			}
			Arrays.sort(trueAnswers[i]);
		}

		setBackgroundColor(Color.WHITE);
		emptyPaint = new Paint();

		alertDialog = new AlertDialog.Builder(context).create();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect r = new Rect(0, 0, width, height);
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		canvas.drawRect(r, paint);
		Rect src = new Rect();
		RectF dst = new RectF();
		for (int i = 0; i < taskElements.length; ++i) {
			src.left = 0;
			src.top = 0;
			src.right = taskElements[i].bitmap.getWidth();
			src.bottom = taskElements[i].bitmap.getHeight();
			dst.left = taskElements[i].position.x;
			dst.top = taskElements[i].position.y;
			dst.right = taskElements[i].position.x
					+ taskElements[i].bitmap.getWidth() * scaleKoeff;
			dst.bottom = taskElements[i].position.y
					+ taskElements[i].bitmap.getHeight() * scaleKoeff;
			canvas.drawBitmap(taskElements[i].bitmap, src, dst, emptyPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		scaleKoeff = countScaleKoeff();
		regeneratePositions();
	}

	@Override
	public void RestartTask() {
		regeneratePositions();
		invalidate();
	}

	@Override
	public void CheckTask() {
		boolean res = true;
		for (int i = 0; i < taskElements.length && res; ++i) { // for each elem
			// find its answer-group
			int answerGroup = -1;
			for (int j = 0; j < trueAnswers.length && answerGroup < 0; ++j) {
				for (int k = 0; k < trueAnswers[j].length; ++k) {
					if (trueAnswers[j][k].equals(taskElements[i].resourceName)) {
						answerGroup = j;
						break;
					}
				}
			}
			// number of answers in this group
			int N = trueAnswers[answerGroup].length;
			TaskElement[] elems = new TaskElement[taskElements.length];
			for (int j = 0; j < taskElements.length; ++j) {
				elems[j] = taskElements[j];
			}
			PointF center = new PointF(taskElements[i].position.x
					+ taskElements[i].bitmap.getWidth() * scaleKoeff * 0.5f,
					taskElements[i].position.y
							+ taskElements[i].bitmap.getHeight() * scaleKoeff
							* 0.5f);
			Arrays.sort(elems, new PointComparer(center));
			String[] closest = new String[N];
			for (int j = 0; j < N; ++j) {
				closest[j] = elems[j].resourceName;
			}
			Arrays.sort(closest);
			for (int j = 0; j < N; ++j) {
				if (!closest[j].equals(trueAnswers[answerGroup][j])) {
					res = false;
					break;
				}
			}
		}
		alertDialog.setMessage(Boolean.toString(res));
		alertDialog.show();
	}

	class PointComparer implements Comparator<TaskElement> {
		private PointF center;

		public PointComparer(PointF p) {
			center = p;
		}

		@Override
		public int compare(TaskElement lhs, TaskElement rhs) {
			PointF pl = new PointF(lhs.position.x + lhs.bitmap.getWidth()
					* scaleKoeff * 0.5f, lhs.position.y
					+ lhs.bitmap.getHeight() * scaleKoeff * 0.5f);
			PointF pr = new PointF(rhs.position.x + rhs.bitmap.getWidth()
					* scaleKoeff * 0.5f, rhs.position.y
					+ rhs.bitmap.getHeight() * scaleKoeff * 0.5f);
			float r = (pl.x - center.x) * (pl.x - center.x) + (pl.y - center.y)
					* (pl.y - center.y);
			float l = (pr.x - center.x) * (pr.x - center.x) + (pr.y - center.y)
					* (pr.y - center.y);
			if (l < r)
				return 1;
			if (l > r)
				return -1;
			return 0;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			touchedElement = getElementId(x, y);
			lastTouchedPoint = new PointF(x, y);
			invalidate();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (touchedElement == -1) {
				break;
			}
			TaskElement elem = taskElements[touchedElement];
			PointF pos = elem.position;
			pos.x += x - lastTouchedPoint.x;
			pos.y += y - lastTouchedPoint.y;
			lastTouchedPoint.set(x, y);
			if (pos.x <= 0.0f) {
				pos.x = 0.0f;
			}
			if (pos.y <= 0.0f) {
				pos.y = 0.0f;
			}
			if (pos.x >= width - elem.bitmap.getWidth() * scaleKoeff) {
				pos.x = width - elem.bitmap.getWidth() * scaleKoeff;
			}
			if (pos.y >= height - elem.bitmap.getHeight() * scaleKoeff) {
				pos.y = height - elem.bitmap.getHeight() * scaleKoeff;
			}
			invalidate();
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (touchedElement == -1) {
				break;
			}
			invalidate();
			break;
		}
		}
		return true;
	}

	private float countScaleKoeff() {
		final float AREA_PROPORTION = 0.2f;
		final float MAX_LINEAR_SIZE_PROPORTION = 0.5f;

		if (taskElements.length == 0) {
			return 1.0f;
		}
		float area = width * height;
		float elementsArea = 0.0f;
		for (int i = 0; i < taskElements.length; ++i) {
			elementsArea += taskElements[i].bitmap.getWidth()
					* taskElements[i].bitmap.getHeight();
		}

		// Want: elementsArea*scaleKoeff/area = AREA_PROPORTION
		float result = AREA_PROPORTION * area / elementsArea;
		// Want: w*scaleKoeff<MAX_LINEAR_SIZE_PROPORTION*width,
		// h*scaleKoeff<MAX_LINEAR_SIZE_PROPORTION*height,
		// where w and h - linear sizes of bitmaps of taskElements
		for (int i = 0; i < taskElements.length; ++i) {
			float w = taskElements[i].bitmap.getWidth();
			float h = taskElements[i].bitmap.getHeight();
			result = Math.min(result, MAX_LINEAR_SIZE_PROPORTION * height / h);
			result = Math.min(result, MAX_LINEAR_SIZE_PROPORTION * width / w);
		}
		return result;
	}

	private void regeneratePositions() {
		final int NUM_TRIES = 100;
		Random r = new Random(System.nanoTime());
		for (int i = 0; i < taskElements.length; ++i) {
			for (int j = 0; j < NUM_TRIES; ++j) {
				taskElements[i].position.x = r
						.nextInt((int) (width - scaleKoeff
								* taskElements[i].bitmap.getWidth()));
				taskElements[i].position.y = r
						.nextInt((int) (height - scaleKoeff
								* taskElements[i].bitmap.getHeight()));
				RectF rct = new RectF(taskElements[i].position.x,
						taskElements[i].position.y, taskElements[i].position.x
								+ scaleKoeff
								* taskElements[i].bitmap.getWidth(),
						taskElements[i].position.y + scaleKoeff
								* taskElements[i].bitmap.getHeight());
				boolean intersects = false;
				for (int k = 0; k < i; ++k) {
					if (rct.intersects(taskElements[k].position.x,
							taskElements[k].position.y,
							taskElements[k].position.x + scaleKoeff
									* taskElements[k].bitmap.getWidth(),
							taskElements[k].position.y + scaleKoeff
									* taskElements[k].bitmap.getHeight())) {
						intersects = true;
						break;
					}
				}
				if (!intersects) {
					break;
				}
			}
		}
	}

	private int getElementId(float x, float y) {
		for (int i = taskElements.length - 1; i >= 0; --i) {
			if (x >= taskElements[i].position.x
					&& x <= taskElements[i].position.x
							+ taskElements[i].bitmap.getWidth() * scaleKoeff
					&& y >= taskElements[i].position.y
					&& y <= taskElements[i].position.y
							+ taskElements[i].bitmap.getHeight() * scaleKoeff) {
				int color = taskElements[i].bitmap.getPixel(
						(int) ((x - taskElements[i].position.x) / scaleKoeff),
						(int) ((y - taskElements[i].position.y) / scaleKoeff));
				if(Color.alpha(color)<20){
					continue;
				}
				return i;
			}
		}
		return -1;
	}

	private class TaskElement {
		public Bitmap bitmap;
		public PointF position;
		public int resourceId;
		public String resourceName;
	}
}
