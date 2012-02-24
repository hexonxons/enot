/*
 * @author Alex Taran (alex.y.taran@gmail.com)
 */

package app.tascact.manual.task;

//import android.R;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

// Temporary extends View class

public class ConnectElementsSequenceTaskView extends TaskView {
	private final float MARGIN = 0.07f;
	private final float LINE_WIDTH = 8.0f;
	
	private Node inputParams;
	private Resources resources;
	private int[] elementResourceIds;
	private Bitmap[] taskElements;
	private PointF[] elementPositions;
	private String[] elementNames;
	private Bitmap userBitmap; // user-drawn lines
	private Bitmap oldUserBitmap;
	private Canvas userCanvas;
	private boolean isConsequentnessImportant;
	private boolean isSwapAvailable;
	private AlertDialog alertDialog;
	private int width, height;
	private boolean isLineDrawing;
	private PointF lastTouchedPoint;
	private int firstImageId;
	private Paint emptyPaint;
	private List<Answer> trueAnswers;
	private List<Answer> givenAnswers;

	public ConnectElementsSequenceTaskView(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		resources = context.getResources();

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
			elementResourceIds[i] = resources.getIdentifier(elementNames[i], "drawable",
					context.getPackageName());
			taskElements[i] = BitmapFactory.decodeResource(resources, elementResourceIds[i]);
			elementPositions[i] = new PointF();
		}

		isConsequentnessImportant = Boolean.valueOf(XMLUtils
				.evalXpathExprAsNode(inputParams, "./ConsequentnessImportant")
				.getTextContent());
		isSwapAvailable = Boolean.valueOf(XMLUtils
				.evalXpathExprAsNode(inputParams, "./SwapAvailable")
				.getTextContent());
		
		NodeList answerNodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskAnswer/Answer");
		trueAnswers = new LinkedList<Answer>();
		givenAnswers = new LinkedList<Answer>();
		for (int i = 0; i < answerNodes.getLength(); ++i) {
			NodeList names = XMLUtils.evalXpathExprAsNodeList(
					answerNodes.item(i), "./TaskResource");
			int first = resources.getIdentifier(names.item(0).getTextContent(),
					"drawable", context.getPackageName());
			int second = resources.getIdentifier(
					names.item(1).getTextContent(), "drawable",
					context.getPackageName());
			Answer ans = new Answer(first, second);
			if(isSwapAvailable){
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
		emptyPaint.setARGB(255, 25, 155,25);
		emptyPaint.setStrokeWidth(LINE_WIDTH);

		alertDialog = new AlertDialog.Builder(context).create();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		recountPositions();
		resetUserBitmap();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(userBitmap, 0, 0, emptyPaint);
		for (int i = 0; i < taskElements.length; ++i) {
			canvas.drawBitmap(taskElements[i], elementPositions[i].x,
					elementPositions[i].y, emptyPaint);
		}
	}

	@Override
	public void RestartTask() {
		userBitmap = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		userCanvas = new Canvas(userBitmap);
		givenAnswers = new LinkedList<Answer>();
		invalidate();
	}

	@Override
	public void CheckTask() {
		if(!isConsequentnessImportant){
			Collections.sort(givenAnswers);
		}
		boolean result = true;
		if(trueAnswers.size()!=givenAnswers.size()){
			result = false;
		}else{
			for(int i=0;i<trueAnswers.size();++i){
				if(trueAnswers.get(i).compareTo(givenAnswers.get(i))!=0){
					result = false;
				}
			}
		}

		alertDialog.setMessage(Boolean.toString(result));
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
				oldUserBitmap = userBitmap.copy(Bitmap.Config.ARGB_8888, true);
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

			userCanvas.drawCircle(x, y, LINE_WIDTH * 0.5f, emptyPaint);
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
			if (secondImageId >= 0) {
				userCanvas.drawCircle(x, y, LINE_WIDTH * 0.5f, emptyPaint);
				userCanvas.drawLine(lastTouchedPoint.x, lastTouchedPoint.y, x,
						y, emptyPaint);
				Answer ans = new Answer(elementResourceIds[firstImageId], elementResourceIds[secondImageId]);
				if(isSwapAvailable){
					ans.sort();
				}
				givenAnswers.add(ans);
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
		float margin = width * MARGIN;
		float currLHeight = 0.0f, currRHeight = 0.0f;
		for (int i = 0; i < elementPositions.length; ++i) {
			if (i % 2 == 0) {
				elementPositions[i].x = margin;
				elementPositions[i].y = currLHeight + margin;
				currLHeight += taskElements[i].getHeight();
			} else {
				elementPositions[i].x = width - taskElements[i].getWidth()
						- margin;
				elementPositions[i].y = currRHeight + margin;
				currRHeight += taskElements[i].getHeight();
			}
		}
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
					&& y >= elementPositions[i].y
					&& y <= elementPositions[i].y + taskElements[i].getHeight()) {
				return i;
			}
		}
		return -1;
	}

	private class Answer implements Comparable<Answer> {
		public int first;
		public int second;

		public Answer(int theFirst, int theSecond) {
			first = theFirst;
			second = theSecond;
		}

		@Override
		public int compareTo(Answer another) {
			if (first < another.first)
				return -1;
			if (first > another.first)
				return 1;
			if (second < another.second)
				return -1;
			if (second > another.second)
				return 1;
			return 0;
		}
		
		public void sort(){
			if(first>second){
				int tmp = first;
				first=second;
				second = tmp;
			}
		}
		@Override
		public String toString(){
			return "[ "+Integer.toString(first)+" -> "+Integer.toString(second)+" ]";
		}
	}
}
