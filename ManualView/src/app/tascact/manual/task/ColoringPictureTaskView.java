package app.tascact.manual.task;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.MotionEvent;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.ColorKeyboard;
import app.tascact.manual.view.utils.ColorPicker;
import app.tascact.manual.view.utils.ColorPicker.ColorPickerListener;

public class ColoringPictureTaskView extends TaskView {

	private ColorPicker colorPicker;
	private Node inputParams;
	private float keyboardHeightKoef;
	private int width;
	private int height;
	private Bitmap picture;
	private Bitmap borders;
	private Bitmap zones;
	private Paint paint;
	private List<Zone> splittedZones;
	private AlertDialog alertDialog;
	private Markup markup;
	private int currentColor;
	private boolean currentModeFill;
	private Zone currentZonePainting=null;
	
	public ColoringPictureTaskView(Context context, Node theInputParams, Markup markup) {
		super(context);
		this.markup = markup;
		inputParams = theInputParams;
		paint = new Paint();
		setBackgroundColor(Color.WHITE);
		alertDialog = new AlertDialog.Builder(context).create();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		Context context = getContext();

		// Keyboard
		Node kbdNode = XMLUtils.evalXpathExprAsNode(inputParams, "./Keyboard");
		keyboardHeightKoef = Float.parseFloat(kbdNode.getAttributes()
				.getNamedItem("height").getTextContent());
		colorPicker = new ColorPicker(context, kbdNode);
		colorPicker.addListener(new ColorPickerListener() {
			@Override
			public void onColorChanged(int c) {
				currentColor = c;
			}

			@Override
			public void onModeChanged(boolean isCurrentModeFill) {
				currentModeFill = isCurrentModeFill;
			}
		});
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (height * keyboardHeightKoef));
		params.setMargins(0, (int) (height * (1.0f - keyboardHeightKoef)), 0, 0);

		// the image
		picture = loadBitmap("./Image");
		borders = loadBitmap("./Borders");
		zones = loadBitmap("./Zones");
		/*
		 * imageView = new ImageView(context);
		 * imageView.setBackgroundColor(Color.WHITE);
		 * imageView.setImageBitmap(picture); imageView.setLayoutParams(new
		 * LayoutParams(width, (int) (height * (1.0f - keyboardHeightKoef))));
		 * 
		 * imageView.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * Drawable dr = imageView.getDrawable(); Rect bounds = dr.getBounds();
		 * int scaledHeight = bounds.height(); int scaledWidth = bounds.width();
		 * float heightRatio = picture.getHeight() / scaledHeight; float
		 * widthRatio = picture.getWidth() / scaledWidth; float
		 * scaledImageOffsetX = event.getX() - bounds.left; float
		 * scaledImageOffsetY = event.getY() - bounds.top; int x = (int)
		 * (scaledImageOffsetX * widthRatio); int y = (int) (scaledImageOffsetY
		 * * heightRatio); if(x>=0 && y>=0 && x<picture.getWidth() &&
		 * y<picture.getHeight()){ //picture.setPixel(x, y,
		 * keyboard.getSelectedColor());
		 * 
		 * } return true; } });
		 */
		picture = fitBitmap(picture);
		borders = fitBitmap(borders);
		zones = fitBitmap(zones);
		splittedZones = splitZones(zones);
		// root layout
		// mainLayout = new RelativeLayout(context);
		// mainLayout.addView(imageView);
		// mainLayout.addView(keyboard, params);

		// this.addView(mainLayout, new
		// LayoutParams(LayoutParams.MATCH_PARENT,height));
		this.addView(colorPicker, params);
	}

	private List<Zone> splitZones(Bitmap bmp) {
		List<Zone> res = new LinkedList<Zone>();
		int visited[][];
		visited = new int[bmp.getWidth()][];
		for (int i = 0; i < bmp.getWidth(); ++i) {
			visited[i] = new int[bmp.getHeight()];
			for (int j = 0; j < bmp.getHeight(); ++j) {
				visited[i][j] = 0;
			}
		}
		int currLabel = 1;
		for (int i = 0; i < bmp.getWidth(); ++i) {
			for (int j = 0; j < bmp.getHeight(); ++j) {
				if (Color.alpha(bmp.getPixel(i, j)) == 0 || visited[i][j] != 0) {
					continue;
				}
				Queue<Point> q = new LinkedList<Point>();
				visited[i][j] = currLabel;
				q.add(new Point(i, j));
				int[] dx = new int[] { 1, 0, -1, 0 };
				int[] dy = new int[] { 0, 1, 0, -1 };
				int minX = i, maxX = i;
				int minY = j, maxY = j;
				while (!q.isEmpty()) {
					Point p = q.poll();
					for (int k = 0; k < 4; ++k) {
						Point pt = new Point(p.x + dx[k], p.y + dy[k]);
						if (pt.x >= 0 && pt.y >= 0 && pt.x < bmp.getWidth()
								&& pt.y < bmp.getHeight()
								&& visited[pt.x][pt.y] == 0
								&& Color.alpha(bmp.getPixel(pt.x, pt.y)) != 0) {
							q.add(pt);
							if (pt.x < minX)
								minX = pt.x;
							if (pt.y < minY)
								minY = pt.y;
							if (pt.x > maxX)
								maxX = pt.x;
							if (pt.y > maxY)
								maxY = pt.y;
							visited[pt.x][pt.y] = currLabel;
						}
					}
				}

				Zone zn = new Zone();
				zn.offsetX = minX;
				zn.offsetY = minY;
				zn.bitmap = createTransparentBitmap(maxX - minX + 1, maxY
						- minY + 1);
				zn.painted = false;
				for (int ii = minX; ii < maxX; ++ii) {
					for (int jj = minY; jj < maxY; ++jj) {
						if (visited[ii][jj] == currLabel) {
							zn.bitmap.setPixel(ii - minX, jj - minY,
									Color.WHITE);
						}
					}
				}
				zn.color = Color.WHITE;
				zn.canvas = new Canvas(zn.bitmap);
				zn.backup = zn.bitmap.copy(zn.bitmap.getConfig(), true);
				res.add(zn);
				currLabel++;
			}
		}
		Log.i("ManualView", "Number of zones = " + res.size());
		return res;
	}

	private Bitmap createTransparentBitmap(int w, int h) {
		Bitmap bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint pnt = new Paint();
		pnt.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, w, h, pnt);
		return bmp;
	}

	private Bitmap loadBitmap(String path) {
		String imageName = XMLUtils.evalXpathExprAsNode(inputParams, path)
				.getTextContent();
		String filePath = markup.getMarkupFileDirectory()
				+ File.separator + "img" + File.separator + imageName + ".png";
		Bitmap res = BitmapFactory.decodeFile(filePath);
		//decodeResource(resources, resources
		//		.getIdentifier(imageName, "drawable", getContext()
		//				.getPackageName()));
		return res;
	}

	private Bitmap fitBitmap(Bitmap b) {
		float w = width;
		float h = height * (1.0f - keyboardHeightKoef);
		Rect src = new Rect(0, 0, b.getWidth(), b.getHeight());
		float scaleH = h / b.getHeight();
		float scaleW = w / b.getWidth();
		Bitmap bmp = Bitmap.createBitmap((int) w, (int) h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, w, h, paint);
		RectF dst = new RectF();
		if (scaleH > scaleW) { // fit to left-right borders
			dst.left = 0.0f;
			dst.right = w;
			dst.top = (h - scaleW * b.getHeight()) * 0.5f;
			dst.bottom = h - (h - scaleW * b.getHeight()) * 0.5f;
		} else { // fit to top-bottom borders
			dst.top = 0.0f;
			dst.bottom = h;
			dst.left = (w - scaleH * b.getWidth()) * 0.5f;
			dst.right = w - (w - scaleH * b.getWidth()) * 0.5f;
		}
		paint.setColor(Color.WHITE);
		canvas.drawBitmap(b, src, dst, paint);
		return bmp;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float w = width;
		float h = height * (1.0f - keyboardHeightKoef);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, w, h, paint);
		//paint.setColor(Color.GREEN);
		// canvas.drawBitmap(zones, 0, 0, paint);
		for (Zone z : splittedZones) {
			//float[] mat = new float[] { Color.red(z.color) / 255.0f, 0, 0, 0,
			//		0, 0, Color.green(z.color) / 255.0f, 0, 0, 0, 0, 0,
			//		Color.blue(z.color) / 255.0f, 0, 0, 0, 0, 0, 1.0f, 0 };
			//ColorMatrixColorFilter filter = new ColorMatrixColorFilter(mat);
			//paint.setColorFilter(filter);
			canvas.drawBitmap(z.bitmap, z.offsetX, z.offsetY, paint);
			//paint.setColorFilter(null);
		}
		canvas.drawBitmap(borders, 0, 0, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		int x = (int) m.getX();
		int y = (int) m.getY();
		int relx=0;
		int rely=0;
		Zone zn = null;
		for (Zone z : splittedZones) {
			if (x - z.offsetX >= 0 && y - z.offsetY >= 0
					&& x - z.offsetX < z.backup.getWidth()
					&& y - z.offsetY < z.backup.getHeight()) {
				int px = z.backup.getPixel(x- z.offsetX, y- z.offsetY);
				//Log.d("COLORPICKER","CurrPix: "+Integer.toHexString(px));
				if (Color.alpha(px)>=127) {
					relx = x- z.offsetX;
					rely = y- z.offsetY;
					zn = z;
					break;
				}
			}
		}
		if(zn==null){
			return true;
		}
		//PorterDuff.Mode mode = Mode.MULTIPLY;
		Xfermode xferMode = new PorterDuffXfermode(Mode.SRC_ATOP);
		//currentColor &=0x00FFFFFF;
		
		int currCol = currentColor & Color.argb((int)(m.getPressure()*255.0f), 255,255,255);
		//int col = currentColor& Color.argb((int)(m.getPressure()*255.0f), 255,255,255);
		//Log.d("COLORPICKER", "Pressure: "+m.getPressure());
		
		//col |= 0xFF000000;
		//col &= 0x00FFFFFF;
		
		if (m.getActionMasked() == MotionEvent.ACTION_DOWN) {
			if(currentModeFill){
				zn.color = currentColor;
				zn.painted = true;
				float[] mat = new float[] { Color.red(zn.color) / 255.0f, 0, 0, 0,
							0, 0, Color.green(zn.color) / 255.0f, 0, 0, 0, 0, 0,
						Color.blue(zn.color) / 255.0f, 0, 0, 0, 0, 0, 1.0f, 0 };
				ColorMatrixColorFilter filter = new ColorMatrixColorFilter(mat);
				paint.setColorFilter(filter);
				zn.canvas.drawBitmap(zn.backup, 0.0f, 0.0f, paint);
				paint.setColorFilter(null);
			}else{
				paint.setColor(currCol);
				//PorterDuffColorFilter filter = new PorterDuffColorFilter(col,mode);
				//paint.setColorFilter(filter);
				paint.setXfermode(xferMode);
				zn.canvas.drawCircle(relx, rely, 35.0f, paint);
				paint.setXfermode(null);
				//paint.setColorFilter(null);
				currentZonePainting = zn;
			}
			invalidate();
		}
		if (m.getActionMasked() == MotionEvent.ACTION_MOVE) {
			if(!currentModeFill && currentZonePainting==zn){
				paint.setColor(currCol);
				//PorterDuffColorFilter filter = new PorterDuffColorFilter(col,mode);
				//paint.setColorFilter(filter);
				paint.setXfermode(xferMode);
				zn.canvas.drawCircle(relx, rely, 35.0f, paint);
				//paint.setColorFilter(null);
				paint.setXfermode(null);
				invalidate();
			}
		}
		if (m.getActionMasked() == MotionEvent.ACTION_UP) {
			if(!currentModeFill && currentZonePainting==zn){
				paint.setColor(currCol);
				//PorterDuffColorFilter filter = new PorterDuffColorFilter(col,mode);
				//paint.setColorFilter(filter);
				paint.setXfermode(xferMode);
				zn.canvas.drawCircle(relx, rely, 35.0f, paint);
				//paint.setColorFilter(null);
				paint.setXfermode(null);
				currentZonePainting = null;
				invalidate();
			}
		}
		
		return true;
	}

	@Override
	public void RestartTask() {
		for(Zone z: splittedZones){
			z.color = Color.WHITE;
			z.painted = false;
		}
		invalidate();
	}

	@Override
	public void CheckTask() {
		boolean flag = true;
		for(Zone z: splittedZones){
			flag &= z.painted;
		}
		String message;
		if(flag){
			message = "Молодѣцъ! Всё раскрасилъ!";
		}else{
			message = "Нѣ всё раскрасилъ :(";
		}
		alertDialog.setMessage(message);
		alertDialog.show();
		invalidate();
	}

	private class Zone {
		public Bitmap bitmap;
		public Bitmap backup;
		public Canvas canvas;
		public int offsetX;
		public int offsetY;
		public int color;
		public boolean painted;
	}
}
