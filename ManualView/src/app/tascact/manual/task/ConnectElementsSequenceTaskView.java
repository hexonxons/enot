/*
 * @author Alex Taran (alex.y.taran@gmail.com)
 */

package app.tascact.manual.task;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import app.tascact.manual.view.TaskView;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;

import org.w3c.dom.*;

// Temporary extends View class

public class ConnectElementsSequenceTaskView extends View {
	private Node inputParams;
	private Resources resources;
	private Bitmap[] taskElements;

	public ConnectElementsSequenceTaskView(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		resources = context.getResources();
		XPathFactory fact = XPathFactory.newInstance();
		XPath xpath = fact.newXPath();
		NodeList nodes = null;
		try {
			XPathExpression expr = xpath.compile("TaskResources/TaskResource");
			nodes = (NodeList) expr.evaluate(inputParams,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		taskElements = new Bitmap[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); ++i) {
			int id = resources.getIdentifier(nodes.item(i).getNodeValue(),
					"drawable", context.getPackageName());
			taskElements[i] = BitmapFactory.decodeResource(resources, id);
		}
	}

}
