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
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.view.MotionEvent;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;

public class LabyrinthTaskView extends TaskView {
	private Node inputParams;
	private Bitmap picture;
	private Bitmap mask;
	private int width;
	private int height;
	private PointF mazePos;
	private PointF fingerPos;
	private float mazeScale;
	private Paint emptyPaint;
	private boolean isTouched = false;
	private AlertDialog alertDialog;
	private float fingerDelta = 0.0f;
	
	public LabyrinthTaskView(Context context, Node theInputParams, Markup markup) {
		super(context);
		inputParams = theInputParams;
		String pictureName = XMLUtils.getStringProperty(inputParams, "./Picture", "");
		String maskName = XMLUtils.getStringProperty(inputParams, "./Mask", "");
		String filePath = markup.getMarkupFileDirectory()
				+ File.separator + "img" + File.separator + pictureName + ".png";
		picture = BitmapFactory.decodeFile(filePath);
		filePath = markup.getMarkupFileDirectory()
				+ File.separator + "img" + File.separator + maskName + ".png";
		mask = BitmapFactory.decodeFile(filePath);
		
		emptyPaint = new Paint(Paint.DITHER_FLAG);
		emptyPaint.setAntiAlias(true);
		
		setBackgroundColor(Color.WHITE);
		alertDialog = new AlertDialog.Builder(context).create();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		// recount pos and scale
		mazePos = new PointF();
		float scaleW = (float)width/picture.getWidth();
		float scaleH = (float)height/picture.getHeight();
		if (scaleH > scaleW) { // fit to left-right borders
			mazeScale = scaleW;
			mazePos.x = 0.0f;
			mazePos.y = (height - scaleW * picture.getHeight()) * 0.5f;
		} else { // fit to top-bottom borders
			mazeScale = scaleH;
			mazePos.x = (w - scaleH * picture.getWidth()) * 0.5f;
			mazePos.y = 0.0f;
		}
		fingerDelta = Math.min(width, height)*0.14f;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Rect src = new Rect(0,0,picture.getWidth(),picture.getHeight());
		RectF dst = new RectF(mazePos.x,mazePos.y,mazePos.x+picture.getWidth()*mazeScale,mazePos.y+picture.getHeight()*mazeScale);
		canvas.drawBitmap(picture, src, dst, emptyPaint);
		if(isTouched){
			emptyPaint.setARGB(255,255,64,64);
			canvas.drawCircle(fingerPos.x, fingerPos.y-fingerDelta, Math.min(width, height)*0.015f, emptyPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int imgX = (int) ((x-mazePos.x)/mazeScale);
		int imgY = (int) ((y-fingerDelta-mazePos.y)/mazeScale);
		int color = 0;
		if(imgX>=0 && imgY>=0 && imgX<mask.getWidth() && imgY<mask.getHeight()){
			color = mask.getPixel(imgX, imgY);
		}
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			if(isFuckingGreen(color)){
				isTouched = true;
			}else{
				isTouched = false;
			}
			fingerPos = new PointF(x,y);
			invalidate();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (!isTouched) {
				invalidate();
				break;
			}
			fingerPos.x = x;
			fingerPos.y = y;
			if(! (isFuckingGreen(color) || isFuckingBlue(color) || isFuckingRed(color))){
				alertDialog.setMessage("Попробуй ещё раз!");
				alertDialog.show();
				isTouched = false;
			}
			invalidate();
			break;
		}

		case MotionEvent.ACTION_UP: {
			if (!isTouched) {
				invalidate();
				break;
			}
			if(isFuckingRed(color)){
				alertDialog.setMessage("Молодец!");
				alertDialog.show();
			}else if(!isFuckingGreen(color)){
				alertDialog.setMessage("Попробуй ещё раз!");
				alertDialog.show();
			}
			isTouched = false;
			invalidate();
			break;
		}
		}
		return true;
	}
	
	boolean isFuckingRed(int color){
		return Color.red(color)>200 && Color.blue(color)<50 && Color.green(color)<50;
	}
	
	boolean isFuckingGreen(int color){
		return Color.green(color)>200 && Color.red(color)<50 && Color.blue(color)<50;
	}
	
	boolean isFuckingBlue(int color){
		return Color.blue(color)>200 && Color.red(color)<50 && Color.green(color)<50;
	}

	@Override
	public void RestartTask() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CheckTask() {
		// TODO Auto-generated method stub
		
	}

}
