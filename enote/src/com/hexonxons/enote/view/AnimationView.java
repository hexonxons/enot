/*
 * AnimationView класс
 * 
 * View анимации ответа в задаче
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package com.hexonxons.enote.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

public class AnimationView extends SurfaceView implements SurfaceHolder.Callback 
{
	// количество кадров в секунду
	private static long mFPS = 60;
	// текущий сдвиг анимации от правого края
	private static int mOffset = 0;
	// Offset Per Frame
	private static int mOPF = -3;
	// Thread, управляющий анимацией
    private ViewThread mThread;
    // Объект анимации
    private AnimateObject mAnimateObject = null;
    // Прошедшее время
    private long mElapsed = 0;
    
    private int mWidth = 0;
    
    private boolean mIsLastFrame = false;
    
    /*
     * AnimationView 			-	Конструктор класса.
     * 
	 * Left						-	Координата смещения View по горизонтали.
	 * Top						-	Координата смещения View по вертикали.
	 * Width 					-	Ширина всего View. Анимация начинается от правого края.
	 * Height					-	Высота всего View.
	 * AnimationResourcesFolder	-	Имя директории с ресурсами анимации.
	 * FPS						-	Количество кадров анимации в секунду.
	 * OPF						-	Скорость смещения анимации(offset per frame).
	 * 								Сдвиг только по горизонтали.
	 * Repeat					-	Повторять ли анимацию в цикле.
	 * LastFrame				-	Установить ли последний кадр анимации первым кадром в наборе кадров.
	 */
	public AnimationView(Context context,
						 int Left,
						 int Top,
						 int Width,
						 int Height,
						 String AnimationResourcesFolder,
						 int FPS,
						 int OPF,
						 boolean Repeat,
						 boolean LastFrame)
	{
		super(context);
		// Создаем объект анимации
		mAnimateObject = new AnimateObject(context, AnimationResourcesFolder, Repeat);
		// Создаем thread, управляющий анимацией
        mThread = new ViewThread(this);
        
        mOPF = OPF;
        mWidth = Width;
        mOffset = Width;
        mIsLastFrame = LastFrame;
        // Ставим View на верхний уровень
        this.setZOrderOnTop(true); 
        getHolder().addCallback(this);
        // делаем View прозрачным
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        
        // Расположение View
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Width, Height);
		params.setMargins(Left, Top, 0, 0);
		this.setLayoutParams(params);
	}	

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!mThread.isAlive())
        {
            mThread = new ViewThread(this);
            mOffset = mWidth;
            mThread.setRunning(true);
            mThread.start();
        }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mThread.isAlive())
        {
            mThread.setRunning(false);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
    	this.setVisibility(INVISIBLE);
    	return super.onTouchEvent(event);
    }
    
    public void setTextToDisplay(String TextToDisplay)
    {
    	mAnimateObject.setTextToDisplay(TextToDisplay);
    }
    
    public void doDraw(long elapsed, Canvas canvas)
    {
    	mOffset += mOPF;
    	mAnimateObject.doDraw(canvas, mOffset);
    	if(mOffset < 0)
    	{
    		mThread.setRunning(false);
    		if(mIsLastFrame)
    			mAnimateObject.doDraw(canvas,0, 0);
    		mAnimateObject.doTextDraw(canvas);
    	}
    }
    
    public void animate(long elapsedTime)
    {
    	mElapsed += elapsedTime;
    	
    	if(mElapsed > 1000 / mFPS)
    	{
    		mAnimateObject.animate();
    		mElapsed = 0;
    	}
    }
    
	private class ViewThread extends Thread 
	{
	    private AnimationView mAnimation;
	    private SurfaceHolder mHolder;
	    private boolean mRun = false;
		private long mStartTime;
		private long mElapsed;
		
	    public ViewThread(AnimationView animation)
	    {
	    	mAnimation = animation;
	        mHolder = mAnimation.getHolder();
	    }
	    
	    public void setRunning(boolean run)
	    {
	        mRun = run;
	    }
	    
	    @Override
	    public void run() 
	    {
	        Canvas canvas = null;
	        
	        mStartTime = System.currentTimeMillis();
	        while (mRun) 
	        {
	            canvas = mHolder.lockCanvas();
	            if (canvas != null) 
	            {
	            	mAnimation.animate(mElapsed);
	            	mAnimation.doDraw(mElapsed, canvas);
	                mElapsed = System.currentTimeMillis() - mStartTime;
	                mHolder.unlockCanvasAndPost(canvas);
	            }
	            mStartTime = System.currentTimeMillis();
	        }
	    }
	}
	
	/*
	 *  Класс, хранящий в себе данные об анимации.
	 */
	private class AnimateObject
	{
		// Кадр анимации для отрисовки
		private Bitmap mBitmap;
		private AssetManager mAssetManager = null;
		
		// Имя директории с ресурсами анимации
		private String mAnimationResourcesFolder = null;
		// Текст для отображения
		private String mTextToDisplay = "";
		// Номер текущего кадра анимации
		private int mCurrFrame = 0;
		// Количество всех кадров анимации.
		private int mFramesCount = 0;
		// Имена файлов - кадров анимации в порядке воспроизведения.
		private String[] mFrames = null;
		private boolean mIsRepeat = false;
		
		
		/*
	     * AnimateObject 			-	Конструктор класса.
	     * 
		 * AnimationResourcesFolder	-	Имя директории с ресурсами анимации.
		 * Repeat					-	Повторять ли анимацию в цикле.
		 * LastFrame				-	Установить ли последний кадр анимации первым кадром в наборе кадров.
		 * Repeat					-	Повторять ли анимацию в цикле.
		 */
		public AnimateObject(Context context,
							 String AnimationResourcesFolder,
							 boolean Repeat)
		{
			// Получаем структуру доступа к assets
			mAssetManager = context.getAssets();
			// Начальный кадр - нулевой
			mCurrFrame = 0;
			// Задаем название папки с ресурсами анимации в assets
			mAnimationResourcesFolder = AnimationResourcesFolder;
			// Задаем текст для отображения
			mTextToDisplay = "";
			
			mIsRepeat = Repeat;
			
			try 
			{
				// Открываем .config файл анимации
				InputStream is = mAssetManager.open(mAnimationResourcesFolder + "/config");
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				// Считываем количество кадров
				mFramesCount = Integer.parseInt(reader.readLine());
				// Считываем имена файлов - кадров анимации
				mFrames = new String[mFramesCount];
				for(int i = 0; i < mFramesCount; ++i)
				{
					mFrames[i] = mAnimationResourcesFolder + "/" + reader.readLine();
				}
			} 
			catch (IOException e)
			{
				Log.e("Assets/Animation: ", e.getMessage());
				e.printStackTrace();
			}
			
			// Считываем ресурс начального кадра
			ReadFrame(mCurrFrame);
	    }
		
		/*
		 * ReadFrame	-	Функция загрузки нового кадра для анимации
		 * 
		 * frame		-	Номер кадра.
		 */
		private void ReadFrame(int frame)
		{
			try 
			{
				InputStream is = mAssetManager.open(mFrames[frame]);
				mBitmap = BitmapFactory.decodeStream(is);
			} 
			catch (IOException e)
			{
				Log.e("Assets/AnswerAnimationResources: ", e.getMessage());
				e.printStackTrace();
			}
		}
		
		/*
		 * doDraw	-	Функция отрисовки нового кадра
		 * 
		 * canvas	-	Канва, на которой рисуем.
		 * offset	-	Сдвиг нового кадра.
		 */
		public void doDraw(Canvas canvas, int offset) 
		{
			Paint paint = new Paint();
			
			// Стираем все, что было нарисовано до этого кадра
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			// Рисуем
			canvas.drawBitmap(mBitmap, offset, 0, paint);
		}
		
		/*
		 * doDraw	-	Функция отрисовки нового кадра
		 * 
		 * canvas	-	Канва, на которой рисуем.
		 * frameNum	-	Номер кадра.
		 * offset	-	Сдвиг нового кадра.
		 */
		public void doDraw(Canvas canvas, int frameNum, int offset) 
		{
			Paint paint = new Paint();
			
			// Стираем все, что было нарисовано до этого кадра
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			
			// Считываем frameNum-й кадр
			ReadFrame(frameNum);
			// Рисуем
			canvas.drawBitmap(mBitmap, offset, 0, paint);
		}
		
		/*
		 * doTextDraw	-	Функция отрисовки текста.
		 * 
		 * canvas	-	Канва, на которой рисуем.
		 */
		public void doTextDraw(Canvas canvas) 
		{
			// Цвет текста - Чорный
			// Размер - 20
			Paint paint = new Paint(); 
			paint.setColor(Color.BLACK); 
			paint.setTextSize(20); 
			// Рисуем текст
			canvas.drawText(mTextToDisplay, 80, 40, paint);
		}
		
	    public void setTextToDisplay(String TextToDisplay)
	    {
	    	mTextToDisplay = TextToDisplay;
	    }
	    
		public boolean animate() 
		{
			if(mCurrFrame < mFramesCount)
			{
				ReadFrame(mCurrFrame);
				mCurrFrame++;
				return true;
			}
			else
			{
				if(!mIsRepeat)
					return true;
				mCurrFrame = 0;
				ReadFrame(mCurrFrame);
				return true;
			}
	    }
	}
}
