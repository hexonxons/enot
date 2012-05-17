/*
 * @author Alex Taran (alex.y.taran@gmail.com)
 */

package app.tascact.manual.task;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.view.MotionEvent;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

public class ConnectElementsSequenceTaskView extends TaskView {
	private Node inputParams;
	private Resources resources;
	private int[] elementResourceIds;
	private Bitmap[] taskElements;
	private PointF[] elementPositions;
	private String[] elementNames;
	private Bitmap userBitmap; // user-drawn lines
	private Bitmap oldUserBitmap;
	private Canvas userCanvas;
	private AlertDialog alertDialog;
	private int width, height;
	private boolean isLineDrawing;
	private PointF lastTouchedPoint;
	private int firstImageId;
	private Paint emptyPaint;
	private List<Answer> trueAnswers;
	private List<Answer> givenAnswers;
	private float scaleKoeff = 1.0f;
	// properties from XML
	private boolean isConsequentnessImportant;
	private boolean isSwapAvailable;
	private float marginKoef;
	private float lineWidth;

	public ConnectElementsSequenceTaskView(Context context, Node theInputParams, Markup markup) {
		super(context);
		inputParams = theInputParams;
		resources = context.getResources();

		isConsequentnessImportant = XMLUtils.getBooleanProperty(inputParams,
				"./ConsequentnessImportant", false);
		isSwapAvailable = XMLUtils.getBooleanProperty(inputParams,
				"./SwapAvailable", true);
		marginKoef = XMLUtils.getFloatProperty(inputParams, "./Margin", 0.07f);
		lineWidth = XMLUtils.getFloatProperty(inputParams, "./LineWidth", 8.0f);

		NodeList nodes = null;
		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskResources/TaskResource");

		int N = nodes.getLength();
		taskElements = new Bitmap[N];
		elementPositions = new PointF[N];
		elementNames = new String[N];
		elementResourceIds = new int[N];

		for (int i = 0; i < N; ++i) {
			elementNames[i] = nodes.item(i).getTextContent();
			
			String filePath = markup.getMarkupFileDirectory()
					+ File.separator + "img" + File.separator + elementNames[i] + ".png";
			taskElements[i] = BitmapFactory.decodeFile(filePath);
			elementPositions[i] = new PointF();
		}

		NodeList answerNodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskAnswer/Answer");
		trueAnswers = new LinkedList<Answer>();
		givenAnswers = new LinkedList<Answer>();
		for (int i = 0; i < answerNodes.getLength(); ++i) {
			NodeList names = XMLUtils.evalXpathExprAsNodeList(
					answerNodes.item(i), "./TaskResource");
//			int first = resources.getIdentifier(names.item(0).getTextContent(),
//					"drawable", context.getPackageName());
//			int second = resources.getIdentifier(
//					names.item(1).getTextContent(), "drawable",
//					context.getPackageName());
			Answer ans = new Answer(names.item(0).getTextContent(), names.item(1).getTextContent());
			if (isSwapAvailable) {
				ans.sort();
			}
			trueAnswers.add(ans);
		}

		if (!isConsequentnessImportant) {
			Collections.sort(trueAnswers);
		}

		lastTouchedPoint = new PointF();

		setBackgroundColor(Color.WHITE);
		emptyPaint = new Paint(Paint.DITHER_FLAG);
		emptyPaint.setAntiAlias(true);
		emptyPaint.setARGB(255, 25, 155, 25);
		emptyPaint.setStrokeWidth(lineWidth);

		alertDialog = new AlertDialog.Builder(context).create();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		scaleKoeff = countScaleKoeff();
		recountPositions();
		resetUserBitmap();
		drawHint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(userBitmap, 0, 0, emptyPaint);
		Rect src = new Rect();
		RectF dst = new RectF();
		for (int i = 0; i < taskElements.length; ++i) {
			src.left = 0;
			src.top = 0;
			src.right = taskElements[i].getWidth();
			src.bottom = taskElements[i].getHeight();
			dst.left = elementPositions[i].x;
			dst.top = elementPositions[i].y;
			dst.right = elementPositions[i].x + taskElements[i].getWidth()
					* scaleKoeff;
			dst.bottom = elementPositions[i].y + taskElements[i].getHeight()
					* scaleKoeff;
			canvas.drawBitmap(taskElements[i], src, dst, emptyPaint);
		}
	}

	@Override
	public void RestartTask() {
		userBitmap = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		userCanvas = new Canvas(userBitmap);
		drawHint();
		givenAnswers = new LinkedList<Answer>();
		invalidate();
	}

	@Override
	public void CheckTask() {
		if (!isConsequentnessImportant) {
			Collections.sort(givenAnswers);
		}
		boolean result = true;
		if (trueAnswers.size() != givenAnswers.size()) {
			result = false;
		} else {
			for (int i = 0; i < trueAnswers.size(); ++i) {
				if (trueAnswers.get(i).compareTo(givenAnswers.get(i)) != 0) {
					result = false;
				}
			}
		}
		String msg = result?"Правильно!":"Неправильно!";
		alertDialog.setMessage(msg);
		alertDialog.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			firstImageId = getImageId(x, y);
			if (firstImageId >= 0) {
				isLineDrawing = true;
				lastTouchedPoint.set(x, y);
				// lastTouchedPoint.x = elementPositions[firstImageId].x +
				// taskElements[firstImageId].getWidth()*0.5f;
				// lastTouchedPoint.y = elementPositions[firstImageId].y +
				// taskElements[firstImageId].getWidth()*0.5f;
				oldUserBitmap = userBitmap.copy(Bitmap.Config.ARGB_8888, true);
				userCanvas.drawCircle(x, y, lineWidth * 0.5f, emptyPaint);
			} else {
				isLineDrawing = false;
			}
			invalidate();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (!isLineDrawing) {
				invalidate();
				break;
			}

			userCanvas.drawCircle(x, y, lineWidth * 0.5f, emptyPaint);
			userCanvas.drawLine(lastTouchedPoint.x, lastTouchedPoint.y, x, y,
					emptyPaint);
			lastTouchedPoint.set(x, y);

			invalidate();
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (!isLineDrawing) {
				invalidate();
				break;
			}
			int secondImageId = getImageId(x, y);
			if (secondImageId >= 0 && firstImageId != secondImageId) {
				userCanvas.drawCircle(x, y, lineWidth * 0.5f, emptyPaint);
				userCanvas.drawLine(lastTouchedPoint.x, lastTouchedPoint.y, x,
						y, emptyPaint);
				Answer ans = new Answer(elementNames[firstImageId],
						elementNames[secondImageId]);
				if (isSwapAvailable) {
					ans.sort();
				}
				boolean alreadyAdded = false;
				for (int i = 0; i < givenAnswers.size(); ++i) {
					if (ans.compareTo(givenAnswers.get(i)) == 0) {
						alreadyAdded = true;
						break;
					}
				}
				if (!alreadyAdded) {
					givenAnswers.add(ans);
				}
			} else {
				userBitmap = oldUserBitmap;
				userCanvas = new Canvas(userBitmap);
			}

			invalidate();
			break;
		}
		}
		return true;
	}

	private void recountPositions() {
		float margin = width * marginKoef * scaleKoeff;
		float currLHeight = 0.0f, currRHeight = 0.0f;
		for (int i = 0; i < elementPositions.length; ++i) {
			if (i % 2 == 0) {
				elementPositions[i].x = margin;
				elementPositions[i].y = currLHeight + margin;
				currLHeight += taskElements[i].getHeight() * scaleKoeff
						+ margin;
			} else {
				elementPositions[i].x = width - taskElements[i].getWidth()
						* scaleKoeff - margin;
				elementPositions[i].y = currRHeight + margin;
				currRHeight += taskElements[i].getHeight() * scaleKoeff
						+ margin;
			}
		}
	}

	private float countScaleKoeff() {
		float margin = width * marginKoef;
		float currLHeight = 0.0f, currRHeight = 0.0f;
		for (int i = 0; i < elementPositions.length; ++i) {
			if (i % 2 == 0) {
				currLHeight += taskElements[i].getHeight() + margin;
			} else {
				currRHeight += taskElements[i].getHeight() + margin;
			}
		}
		float maxHeight = Math.max(currLHeight, currRHeight);
		float maxWidth = 0.0f;
		for (int i = 0; i < elementPositions.length; i += 2) {
			float w = taskElements[i].getWidth() + margin * 2.0f;
			if (i + 1 < elementPositions.length) {
				w += taskElements[i + 1].getWidth() + margin;
			}
			maxWidth = Math.max(maxWidth, w);
		}
		float hk = width / maxWidth;
		float vk = height / maxHeight;
		return Math.min(hk, vk);
	}

	private void resetUserBitmap() {
		userBitmap = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		userCanvas = new Canvas(userBitmap);
	}

	private int getImageId(float x, float y) {
		for (int i = 0; i < elementPositions.length; ++i) {
			if (x >= elementPositions[i].x
					&& x <= elementPositions[i].x + taskElements[i].getWidth()
							* scaleKoeff
					&& y >= elementPositions[i].y
					&& y <= elementPositions[i].y + taskElements[i].getHeight()
							* scaleKoeff) {
				/*
				 * int color = taskElements[i].getPixel( (int) ((x -
				 * elementPositions[i].x) / scaleKoeff), (int) ((y -
				 * elementPositions[i].y) / scaleKoeff)); if (Color.alpha(color)
				 * < 20) { continue; }
				 */
				return i;
			}
		}
		return -1;
	}

	private PointF getCenterByName(String name) {
		for (int i = 0; i < elementNames.length; ++i) {
			if (name.equals(elementNames[i])) {
				PointF p = new PointF(elementPositions[i].x
						+ taskElements[i].getWidth() * scaleKoeff * 0.5f,
						elementPositions[i].y + taskElements[i].getHeight()
								* scaleKoeff * 0.5f);
				return p;
			}
		}
		return null;
	}

	private void drawHint() {
		NodeList nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskHint/TaskResource");
		if (nodes.getLength() < 2) {
			return;
		}
		PointF start = getCenterByName(nodes.item(0).getTextContent());
		PointF finish = getCenterByName(nodes.item(1).getTextContent());
		userCanvas.drawLine(start.x, start.y, finish.x, finish.y, emptyPaint);
	}

	private class Answer implements Comparable<Answer> {
		public String first;
		public String second;

		public Answer(String theFirst, String theSecond) {
			first = theFirst;
			second = theSecond;
		}

		@Override
		public int compareTo(Answer another) {
			int c = first.compareTo(another.first);
			if(c!=0){
				return c;
			}
			return second.compareTo(another.second);
		}

		public void sort() {
			if (first.compareTo(second)<0) {
				String tmp = first;
				first = second;
				second = tmp;
			}
		}

		@Override
		public String toString() {
			return "[ " + first + " -> "
					+ second + " ]";
		}
	}
}
