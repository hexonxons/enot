/*
 * @author Alex Taran (alex.y.taran@gmail.com)
 */

package app.tascact.manual.task;

//import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import app.tascact.manual.R;
import app.tascact.manual.utils.GraphicsUtils;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;

import org.w3c.dom.*;

// Temporary extends View class

public class ConnectElementsSequenceTaskView extends TaskView {
	private Node inputParams;
	private Resources resources;
	private Bitmap[] taskElements;
	private PointF[] imagePositions;
	private int width, height;
	private final float MARGIN = 0.07f;

	public ConnectElementsSequenceTaskView(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		resources = context.getResources();

		NodeList nodes = null;
		nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./TaskResources/TaskResource");
		int N = nodes.getLength();
		taskElements = new Bitmap[N];
		imagePositions = new PointF[N];

		for (int i = 0; i < N; ++i) {
			int id = resources.getIdentifier(nodes.item(i).getTextContent(),
					"drawable", context.getPackageName());
			taskElements[i] = BitmapFactory.decodeResource(resources, id);
			Log.d("FUCK", taskElements[i] + " " + id);
			imagePositions[i] = new PointF();
		}

		setBackgroundColor(Color.WHITE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		recountPositions();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < taskElements.length; ++i) {
			GraphicsUtils.drawBitmapOnCanvas(canvas, taskElements[i], imagePositions[i]);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	private void recountPositions() {
		float margin = width * MARGIN;
		float currLHeight = 0.0f, currRHeight = 0.0f;
		for (int i = 0; i < imagePositions.length; ++i) {
			if (i % 2 == 0) {
				imagePositions[i].x = margin;
				imagePositions[i].y = currLHeight;
				currLHeight += taskElements[i].getHeight();
			} else {
				imagePositions[i].x = width - taskElements[i].getWidth()
						- margin;
				imagePositions[i].y = currRHeight;
				currRHeight += taskElements[i].getHeight();
			}
		}
	}
}
