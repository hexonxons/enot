package app.taskact.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;


public class DragNMoveView extends View
{
	// картинка шара
	private Bitmap ball;
	// картинка конечной позиции шара
	private Bitmap ballpos;
	// координаты перемещаемого шара
	private int coordX;
	private int coordY;
	
	// начальные координаты перемещаемого шара
	private int startX;
	private int startY;
	
	// выбран ли шар, или мы ткнули в другую позицию экрана
	private int isBallSet = 0;
	// достиг ли шар конечной позиции
	boolean isEndPos = false;
	
	boolean endTouch = false;
	
	public DragNMoveView(Context context)
	{
	    super(context);
	    // включаем обработку прикосновений
	    setFocusable(true);
        
	    // создаем картинку
	    ball = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
	    ballpos = BitmapFactory.decodeResource(context.getResources(), R.drawable.ballpos);
	    
	    // начальные координаты перемещаемого шара
	    coordX = 50;
	    coordY = 50;
	    
	    startX = coordX;
	    startY = coordY;
	}
	// метод отрисовки канвы
	@Override protected void onDraw(Canvas canvas)
	{
		// цвет канвы
	    canvas.drawColor(0xFFCCCCCC);    
	    
	    // отрисовыываем шары
  		canvas.drawBitmap(ballpos, 500, 500, null);
  		
  		
  		// если шар не достиг конечной позиции, то рисуем его заново в начальной позиции
  		if(endTouch)
  		{
		    if(!isEndPos)
		    {
		    	canvas.drawBitmap(ball, startX, startY, null);
		    }
		    // иначе рисуем его в конечной позиции
		    else
		    {
		    	canvas.drawBitmap(ball, 500, 500, null);
		    }
  		}
  		else
  		{
  			canvas.drawBitmap(ball, coordX, coordY, null);
  		}
	}

	// event прикасания к экрану
	public boolean onTouchEvent(MotionEvent event)
	{
		// получаем action
	    int eventAction = event.getAction(); 
	    
	    // получаем координаты прикасания
	    int X = (int)event.getX(); 
	    int Y = (int)event.getY(); 
	
	    switch (eventAction)
	    { 
	    	// action - нажали на экран
		    case MotionEvent.ACTION_DOWN:
		    {
		    	if(isEndPos)
		    		break;
		    	endTouch = false;
	    		// получаем координаты центра перемещаемого шара
	    		int centerX = coordX + 65;
	    		int centerY = coordY + 65;

	    		double radCircle  = Math.sqrt( (double) (((centerX - X)*(centerX - X)) + (centerY - Y)*(centerY - Y)));
	    		
	    		// если радиус < 65, то шары накладываем
	    		if (radCircle < 65)
	    		{
	    			isBallSet = 1;
	                break;
	    		}
		        
	    		break; 
		    }
		    // action - ведем палец по экрану
		    case MotionEvent.ACTION_MOVE:
		    {
		    	if(isEndPos)
		    		break;
		    	
		    	endTouch = false;
		        if (isBallSet != 0)
		        {
		        	coordX = X - 65;
		        	coordY = Y - 65;
		        }
		    	
		        break; 
		    }
		    // action - убрали палец
		    case MotionEvent.ACTION_UP:
		    {
		    	if(isEndPos)
		    		break;
		    	
		    	if(Math.sqrt( (double) (Math.pow(coordX - 500, 2) + Math.pow(coordY - 500, 2))) > 130)
		    		isEndPos = false;
		    	else
		    		isEndPos = true;
		    	
		    	endTouch = true;
		    	break;
		    }
	    } 
	    
	    invalidate(); 
	    return true; 
	}
}
