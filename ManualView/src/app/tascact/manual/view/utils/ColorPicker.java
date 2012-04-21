package app.tascact.manual.view.utils;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import app.tascact.manual.utils.LanguageUtils;
import app.tascact.manual.utils.MathUtils;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.R;

public class ColorPicker extends LinearLayout {
	private List<ColorPickerListener> listeners = new LinkedList<ColorPickerListener>();
	private Node inputParams;
	private int width;
	private int height;
	private int rows;
	private int columns;
	private Bitmap colorPalette;
	private int[][] colors;
	private int currColor;
	private Paint paint;
	private ImageButton colorPickerButton;
	private ImageButton paletteButton;
	private ImageButton modeSwitcher;
	private Button currColorButton;
	private TableLayout colorTable;
	private TableLayout controlTable;
	private Bitmap modeFillBitmap;
	private Bitmap modeBrushBitmap;
	private Bitmap paletteBitmap;
	private boolean currentModeFill;
	private boolean isLayoutRequested = false;

	public ColorPicker(Context context, Node theInputParams) {
		super(context);
		inputParams = theInputParams;
		rows = XMLUtils.getIntegerProperty(inputParams, "./Rows", 2);
		columns = XMLUtils.getIntegerProperty(inputParams, "./Columns", 4);
		colors = LanguageUtils.createArray2D(rows, columns);
		NodeList nodes = XMLUtils.evalXpathExprAsNodeList(inputParams,
				"./Color");
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node nd = nodes.item(i);
			NamedNodeMap attrs = nd.getAttributes();
			int r = Integer
					.parseInt(attrs.getNamedItem("row").getTextContent());
			int c = Integer.parseInt(attrs.getNamedItem("column")
					.getTextContent());
			if (r >= 0 && r < rows && c >= 0 && c < columns) {
				colors[r][c] = readColorFromNode(nd);
			}
		}
		paint = new Paint();
		setBackgroundColor(Color.WHITE);
		
		Resources res = getContext().getResources();
		modeFillBitmap = BitmapFactory.decodeResource(res,R.drawable.colorpicker_modefill);
		modeBrushBitmap = BitmapFactory.decodeResource(res,R.drawable.colorpicker_modebrush);
		paletteBitmap = BitmapFactory.decodeResource(res,R.drawable.colorpicker_palette);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		Log.e("FUCK",w+" "+h);
		int sz = Math.min(w, h);
		colorPalette = generateColorPalette(sz/2);
		modeFillBitmap = Bitmap.createScaledBitmap(modeFillBitmap, sz/2, sz/2, false);
		modeBrushBitmap = Bitmap.createScaledBitmap(modeBrushBitmap, sz/2, sz/2, false);
		paletteBitmap = Bitmap.createScaledBitmap(paletteBitmap, sz/2, sz/2, false);
		
		modeSwitcher = new ImageButton(getContext());
		modeSwitcher.setPadding(0, 0, 0, 0);
		modeSwitcher.setImageBitmap(modeFillBitmap);
		currentModeFill = true;
		modeSwitcher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentModeFill = !currentModeFill;
				if(currentModeFill){
					modeSwitcher.setImageBitmap(modeFillBitmap);
				}else{
					modeSwitcher.setImageBitmap(modeBrushBitmap);
				}
				notifyListenersModeChanged();
			}
		});
		
		colorPickerButton = new ImageButton(this.getContext());
		colorPickerButton.setPadding(0, 0, 0, 0);
		colorPickerButton.setImageBitmap(colorPalette);
		paletteButton = new ImageButton(this.getContext());
		paletteButton.setPadding(0, 0, 0, 0);
		paletteButton.setImageBitmap(paletteBitmap);
		currColorButton = new Button(this.getContext());
		currColorButton.setBackgroundColor(Color.WHITE);
		currColor = Color.WHITE;
		currColorButton.setWidth(sz/2);
		currColorButton.setHeight(sz/2);
		controlTable = new TableLayout(this.getContext());
		TableRow rw1 = new TableRow(this.getContext());
		TableRow rw2 = new TableRow(this.getContext());
		rw1.addView(paletteButton);
		rw1.addView(colorPickerButton);
		rw2.addView(currColorButton);
		rw2.addView(modeSwitcher);
		controlTable.addView(rw1);
		controlTable.addView(rw2);
		this.addView(controlTable);
		
		
		this.setGravity(Gravity.BOTTOM);
		
		colorTable = new TableLayout(this.getContext());
		for(int i=0;i<rows;++i){
			TableRow rw = new TableRow(this.getContext());
			for(int j=0;j<columns;++j){
				Button bt = new Button(this.getContext());
				bt.setBackgroundColor(colors[i][j]);
				bt.setHeight(sz/2);
				bt.setWidth((w-sz)/columns);
				bt.setId((i<<16)+j);
				bt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						int col = colors[id>>>16][id&0xFFFF];
						currColorButton.setBackgroundColor(col);
						currColor = col;
						notifyListenersColorChanged();
					}
				});
				rw.addView(bt);
			}
			colorTable.addView(rw);
		}
		this.addView(colorTable);
		notifyListenersColorChanged();
		notifyListenersModeChanged();
		// kostyls
		isLayoutRequested = true;
		invalidate();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas){
		if(isLayoutRequested){
			isLayoutRequested = false;
			requestLayout();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		return true;
	}

	// Working with listeners:

	public void addListener(ColorPickerListener listener) {
		for (ColorPickerListener l : listeners) {
			if (l == listener) {
				return;
			}
		}
		listeners.add(listener);
	}

	public void removeListener(ColorPickerListener listener) {
		for (ColorPickerListener l : listeners) {
			if (l == listener) {
				listeners.remove(l);
				return;
			}
		}
	}
	
	private void notifyListenersColorChanged(){
		for (ColorPickerListener l : listeners) {
			l.onColorChanged(currColor);
		}
	}
	
	private void notifyListenersModeChanged(){
		for (ColorPickerListener l : listeners) {
			l.onModeChanged(currentModeFill);
		}
	}

	public interface ColorPickerListener {
		public void onColorChanged(int c);
		public void onModeChanged(boolean isCurrentModeFill);
	}

	// Helper functions
	public static Bitmap generateColorPalette(int size) {
		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		float r = size / 2;
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				float dist = (float) Math.sqrt((i - r) * (i - r) + (j - r)
						* (j - r));
				if (dist > r) {
					bitmap.setPixel(i, j, Color.GRAY);
				} else {
					float hsv[] = new float[3];
					hsv[0] = (float) ((Math.atan2(j - r, i - r) + Math.PI)*180.0f/Math.PI);
					hsv[1] = dist / r;
					hsv[2] = 1.0f;
					bitmap.setPixel(i, j, Color.HSVToColor(255, hsv));
				}
			}
		}
		return bitmap;
	}

	private static int readColorFromNode(Node nd) {
		NamedNodeMap attrs = nd.getAttributes();
		int R = Integer.parseInt(attrs.getNamedItem("R").getTextContent());
		int G = Integer.parseInt(attrs.getNamedItem("G").getTextContent());
		int B = Integer.parseInt(attrs.getNamedItem("B").getTextContent());
		return Color.argb(255, R, G, B);
	}
}
