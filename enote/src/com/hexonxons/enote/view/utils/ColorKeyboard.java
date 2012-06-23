package com.hexonxons.enote.view.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hexonxons.enote.utils.XMLUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ColorKeyboard extends View {

	private Node inputParams;
	private int rows;
	private int columns;
	private int selectionRow = 0;
	private int selectionColumn = 0;
	private int[][] colors;
	private int width, height;
	private Paint paint;
	private int backgroundColor;
	private int selectionColor;

	public ColorKeyboard(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		rows = XMLUtils.getIntegerProperty(inputParams, "./Rows", 2);
		columns = XMLUtils.getIntegerProperty(inputParams, "./Columns", 5);
		colors = new int[rows][];
		for (int i = 0; i < rows; ++i) {
			colors[i] = new int[columns];
			for (int j = 0; j < columns; ++j) {
				colors[i][j] = Color.WHITE;
			}
		}
		NodeList nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./Color");
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node nd = nodes.item(i);
			NamedNodeMap attrs = nd.getAttributes();
			int r = Integer
					.parseInt(attrs.getNamedItem("row").getTextContent());
			int c = Integer.parseInt(attrs.getNamedItem("column")
					.getTextContent());
			colors[r][c] = readColorFromNode(nd);
		}
		backgroundColor = readColorFromNode(XMLUtils.evalXpathExprAsNode(
				inputParams, "./BackgroundColor"));
		selectionColor = readColorFromNode(XMLUtils.evalXpathExprAsNode(
				inputParams, "./SelectionColor"));
		paint = new Paint();
	}

	private int readColorFromNode(Node nd) {
		NamedNodeMap attrs = nd.getAttributes();
		int R = Integer.parseInt(attrs.getNamedItem("R").getTextContent());
		int G = Integer.parseInt(attrs.getNamedItem("G").getTextContent());
		int B = Integer.parseInt(attrs.getNamedItem("B").getTextContent());
		return Color.argb(255, R, G, B);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(backgroundColor);
		canvas.drawRect(0, 0, width, height, paint);
		// Magic algorithm to count all margins :)
		float buttonWidth = width / columns;
		float buttonHeight = height / rows;
		float margin = 0.07f * Math.min(buttonWidth, buttonHeight);
		buttonWidth = (width - 2 * margin) / columns;
		buttonHeight = (height - 2 * margin) / rows;
		// Draw the selection
		paint.setColor(selectionColor);
		canvas.drawRect(buttonWidth * selectionColumn + margin, buttonHeight * selectionRow
				+ margin, buttonWidth * (selectionColumn + 1)+ margin, buttonHeight
				* (selectionRow + 1)+ margin, paint);
		// And draw 'em all!
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				paint.setColor(colors[i][j]);
				canvas.drawRect(buttonWidth * j + 2 * margin, buttonHeight * i
						+ 2 * margin, buttonWidth * (j + 1), buttonHeight
						* (i + 1), paint);
			}
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent m){
		float x = m.getX();
		float y = m.getY();
		float buttonWidth = width / columns;
		float buttonHeight = height / rows;
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				if(x>=buttonWidth*j && x<=buttonWidth*(j+1) &&
				   y>=buttonHeight*i && y<=buttonHeight*(i+1)){
					selectionRow = i;
					selectionColumn = j;
				}
			}
		}
		invalidate();
		return true;
	}
	
	public int getSelectedColor(){
		return colors[selectionRow][selectionColumn];
	}
}
