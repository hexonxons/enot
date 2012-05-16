package app.tascact.manual.task;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.MathUtils;
import app.tascact.manual.utils.TouchMoment;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

public class GroupingElementsTaskView extends TaskView {
	private float frictionKoef = 4.9f;
	private Node inputParams;
	private TaskElement[] taskElements;
	private Paint emptyPaint;
	private int width;
	private int height;
	private int touchedElement = -1;
	private PointF lastTouchedPoint;
	private long lastTouchStart;
	private String[][] trueAnswers;
	private AlertDialog alertDialog;
	private float instrictionsTextSize = 0.0f;
	private String[] instructions;
	Markup markup;

	private boolean isActive = true;

	private List<TouchMoment> touchHistory = new LinkedList<TouchMoment>();

	private long updatePhysicsLastTimeLaunched = -1;
	private Runnable updatePhysicsProc = new Runnable() {

		@Override
		public void run() {
			float deltaTime = 0.0f;
			long currentTime = SystemClock.elapsedRealtime();
			if (updatePhysicsLastTimeLaunched >= 0) {
				deltaTime = (currentTime - updatePhysicsLastTimeLaunched) / 1000.0f;
			}
			for (int i = 0; i < taskElements.length; ++i) {
				if (i == touchedElement) {
					continue;
				}
				TaskElement elem = taskElements[i];
				if (elem.position.x <= 0) {
					elem.velocity.x = Math.abs(elem.velocity.x);
				}
				if (elem.position.y <= 0) {
					elem.velocity.y = Math.abs(elem.velocity.y);
				}
				if (elem.position.x >= width - elem.getWidth()) {
					elem.velocity.x = -Math.abs(elem.velocity.x);
				}
				if (elem.position.y >= height - elem.getHeight()) {
					elem.velocity.y = -Math.abs(elem.velocity.y);
				}
				elem.position.x += elem.velocity.x * deltaTime;
				elem.position.y += elem.velocity.y * deltaTime;
				// make it slower
				float len = elem.velocity.length();
				if (len <= frictionKoef * len * deltaTime) {
					// stop it completely
					elem.velocity.x = elem.velocity.y = 0.0f;
				} else {
					elem.velocity.x -= elem.velocity.x * frictionKoef
							* deltaTime;
					elem.velocity.y -= elem.velocity.y * frictionKoef
							* deltaTime;
				}
			}
			updatePhysicsLastTimeLaunched = currentTime;
			invalidate();
			if (isActive) {
				timerRunner.postDelayed(this, 10);
			}
		}
	};

	@Override
	public void onPause() {
		isActive = false;
	}

	@Override
	public void onResume() {
		isActive = true;
		updatePhysicsLastTimeLaunched = -1;
		timerRunner.postDelayed(updatePhysicsProc, 100);
	}

	@Override
	public void onStop() {
		isActive = false;
	}

	private Handler timerRunner = new Handler();

	public GroupingElementsTaskView(Context context, Node theInputParams, Markup markup) {
		super(context);
		this.markup = markup;
		inputParams = theInputParams;
		
		NodeList nodes = null;
		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskResources/TaskResource");

		int N = nodes.getLength();
		taskElements = new TaskElement[N];

		for (int i = 0; i < N; ++i) {
			taskElements[i] = new TaskElement();
			taskElements[i].resourceName = nodes.item(i).getTextContent();
			// taskElements[i].resourceId = resources.getIdentifier(
			//		taskElements[i].resourceName, "drawable",
			//		context.getPackageName());

			String filePath = markup.getMarkupFileDirectory()
					+ File.separator + "img" + File.separator + taskElements[i].resourceName + ".png";			
			taskElements[i].bitmap = BitmapFactory.decodeFile(filePath);
			taskElements[i].position = new PointF();
		}

		trueAnswers = loadTrueAnswers(XMLUtils.evalXpathExprAsNode(inputParams,
				"./TaskAnswer"));

		// count answer groups

		for (int j = 0; j < trueAnswers.length; ++j) {
			for (int k = 0; k < trueAnswers[j].length; ++k) {
				for (int i = 0; i < taskElements.length; ++i) {
					if (trueAnswers[j][k].equals(taskElements[i].resourceName)) {
						taskElements[i].answerGroup = j;
						break;
					}
				}
			}
		}

		// load instructions textsize and text
		instrictionsTextSize = XMLUtils.getFloatProperty(inputParams, "./TaskResources/TextSize", 4.9f);
		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskResources/Text");
		instructions = new String[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); ++i) {
			instructions[i] = nodes.item(i).getTextContent();
		}
		
		frictionKoef = XMLUtils.getFloatProperty(inputParams, "./TaskResources/FrictionKoef", 4.9f);

		setBackgroundColor(Color.WHITE);
		emptyPaint = new Paint();

		alertDialog = new AlertDialog.Builder(context).create();

		timerRunner.postDelayed(updatePhysicsProc, 100);
	}

	private String[][] loadTrueAnswers(Node rootAnswerNode) {
		NodeList nodes = XMLUtils.evalXpathExprAsNodeList(rootAnswerNode,
				"./Answer");
		String[][] result;
		result = new String[nodes.getLength()][];
		for (int i = 0; i < result.length; ++i) {
			NodeList answers = XMLUtils.evalXpathExprAsNodeList(nodes.item(i),
					"./TaskResource");
			result[i] = new String[answers.getLength()];
			for (int j = 0; j < result[i].length; ++j) {
				result[i][j] = answers.item(j).getTextContent();
			}
			Arrays.sort(result[i]);
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect r = new Rect(0, 0, width, height);
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		canvas.drawRect(r, paint);
		Rect src = new Rect();
		src.left = 0;
		src.top = 0;
		for (int i = 0; i < taskElements.length; ++i) {
			src.right = taskElements[i].bitmap.getWidth();
			src.bottom = taskElements[i].bitmap.getHeight();
			canvas.drawBitmap(taskElements[i].bitmap, src,
					taskElements[i].getRectF(), emptyPaint);
		}
		emptyPaint.setTextSize(width * instrictionsTextSize);
		emptyPaint.setTextAlign(Align.CENTER);
		for (int i = 0; i < instructions.length; ++i) {
			canvas.drawText(instructions[i], width * 0.5f, width
					* instrictionsTextSize * (i+1), emptyPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		float sc = countScaleKoeff();
		for (int i = 0; i < taskElements.length; ++i) {
			taskElements[i].scaleKoeff = sc;
		}
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
			int answerGroup = taskElements[i].answerGroup;
			// number of answers in this group
			int N = trueAnswers[answerGroup].length;
			TaskElement[] elems = new TaskElement[taskElements.length];
			for (int j = 0; j < taskElements.length; ++j) {
				elems[j] = taskElements[j];
			}
			PointF center = new PointF(taskElements[i].position.x
					+ taskElements[i].getWidth() * 0.5f,
					taskElements[i].position.y + taskElements[i].getHeight()
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
		for (TaskElement elem : taskElements) {
			elem.velocity.x = elem.velocity.y = 0.0f;
		}
		String msg=res?"Правильно!":"Неправильно!";
		alertDialog.setMessage(msg);
		alertDialog.show();
	}

	class PointComparer implements Comparator<TaskElement> {
		private PointF center;

		public PointComparer(PointF p) {
			center = p;
		}

		@Override
		public int compare(TaskElement lhs, TaskElement rhs) {
			PointF pl = new PointF(lhs.position.x + lhs.getWidth() * 0.5f,
					lhs.position.y + lhs.getHeight() * 0.5f);
			PointF pr = new PointF(rhs.position.x + rhs.getWidth() * 0.5f,
					rhs.position.y + rhs.getHeight() * 0.5f);
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
			touchHistory.clear();
			lastTouchStart = SystemClock.elapsedRealtime();
			touchHistory.add(new TouchMoment(x, y, 0.0f));
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
			if (pos.x >= width - elem.getWidth()) {
				pos.x = width - elem.getWidth();
			}
			if (pos.y >= height - elem.getHeight()) {
				pos.y = height - elem.getHeight();
			}
			float deltaTime = (SystemClock.elapsedRealtime() - lastTouchStart) / 1000.0f;
			touchHistory.add(new TouchMoment(x, y, deltaTime));
			cleanOldTouchHistory(deltaTime, 0.1f);
			invalidate();
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (touchedElement == -1) {
				break;
			}
			float deltaTime = (SystemClock.elapsedRealtime() - lastTouchStart) / 1000.0f;
			touchHistory.add(new TouchMoment(x, y, deltaTime));
			cleanOldTouchHistory(deltaTime, 0.1f);
			taskElements[touchedElement].velocity = MathUtils
					.getAverageVelocity(touchHistory);
			touchedElement = -1;
			invalidate();
			break;
		}
		}
		return true;
	}

	private void cleanOldTouchHistory(float curr, float time) {
		while (!touchHistory.isEmpty()) {
			TouchMoment m = touchHistory.get(0);
			if (curr - m.t >= time) {
				touchHistory.remove(0);
			} else {
				break;
			}
		}
	}

	private float countScaleKoeff() {
		final float AREA_PROPORTION = 0.4f;
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
		float result = (float) Math.sqrt(AREA_PROPORTION * area / elementsArea);
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

	/*
	 * regenerates all positions and stops all velocities
	 */
	private void regeneratePositions() {
		final int NUM_TRIES = 100;
		Random r = new Random(System.nanoTime());
		for (int i = 0; i < taskElements.length; ++i) {
			for (int j = 0; j < NUM_TRIES; ++j) {
				taskElements[i].position.x = r
						.nextInt((int) (width - taskElements[i].getWidth()));
				taskElements[i].position.y = r
						.nextInt((int) (height - taskElements[i].getHeight()));
				RectF rct = taskElements[i].getRectF();
				boolean intersects = false;
				for (int k = 0; k < i; ++k) {
					if (RectF.intersects(rct, taskElements[k].getRectF())) {
						intersects = true;
						break;
					}
				}
				if (!intersects) {
					break;
				}
			}
			taskElements[i].velocity = new PointF(0.0f, 0.0f);
		}
	}

	private int getElementId(float x, float y) {
		for (int i = taskElements.length - 1; i >= 0; --i) {
			if (taskElements[i].isPointInside(x, y)) {
				int color = taskElements[i].getColorByAbsoluteCoords(x, y);
				if (Color.alpha(color) < 20) {
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
		public PointF velocity;
		public String resourceName;
		public float scaleKoeff = 1.0f;
		public int answerGroup = -1;

		public float getWidth() {
			return bitmap.getWidth() * scaleKoeff;
		}

		public float getHeight() {
			return bitmap.getHeight() * scaleKoeff;
		}

		public float getLeft() {
			return position.x;
		}

		public float getRight() {
			return position.x + getWidth();
		}

		public float getTop() {
			return position.y;
		}

		public float getBottom() {
			return position.y + getHeight();
		}

		public boolean isPointInside(float x, float y) {
			return x >= getLeft() && x <= getRight() && y >= getTop()
					&& y <= getBottom();
		}

		public int getColorByAbsoluteCoords(float x, float y) {
			return bitmap.getPixel((int) ((x - position.x) / scaleKoeff),
					(int) ((y - position.y) / scaleKoeff));
		}

		public RectF getRectF() {
			return new RectF(getLeft(), getTop(), getRight(), getBottom());
		}
	}
}
