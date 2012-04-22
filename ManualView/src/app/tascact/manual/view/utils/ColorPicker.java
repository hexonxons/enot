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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import app.tascact.manual.R;
import app.tascact.manual.utils.LanguageUtils;
import app.tascact.manual.utils.XMLUtils;

public class ColorPicker extends LinearLayout {
	private List<ColorPickerListener> listeners = new LinkedList<ColorPickerListener>();
	private Node inputParams;
	private int width;
	private int height;
	private int rows;
	private int columns;
	private Bitmap colorCircle;
	private Bitmap colorCircleBig;
	private int[][] colors;
	private int currColor;
	private Paint paint;
	private ImageButton colorPickerButton;
	private ImageButton paletteButton;
	private ImageButton modeSwitcher;
	private Button currColorButton;
	private TableLayout colorTable;
	private TableLayout controlTable;
	private TableLayout colorPalette;
	private Bitmap modeFillBitmap;
	private Bitmap modeBrushBitmap;
	private Bitmap paletteBitmap;
	private boolean currentModeFill;
	private boolean isLayoutRequested = false;
	private final PopupWindow popupColorPicker = new PopupWindow(this,0,0);
	private final PopupWindow popupColorPalette = new PopupWindow(this,0,0);
	private final ColorPicker selfReference = this;
	private float brushSize;

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
		int sz = Math.min(w, h);
		colorCircle = ColorCircleGenerator.generateColorPalette(sz/2);
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
		
		// Colorpickerbutton and popupcolorpicker
		popupColorPicker.setWidth(400);
		popupColorPicker.setHeight(400);
		ImageButton btn = new ImageButton(getContext());
		colorCircleBig = ColorCircleGenerator.generateAdvancedColorPalette(400);
		btn.setBackgroundColor(Color.TRANSPARENT);
		btn.setImageBitmap(colorCircleBig);
		btn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getActionMasked()==MotionEvent.ACTION_UP){
					int x = (int)event.getX();
					int y = (int)event.getY();
					if(x>=0 && y>=0 && x<colorCircleBig.getWidth() && y<colorCircleBig.getHeight()){
						int col = colorCircleBig.getPixel(x,y);
						if(col!=Color.TRANSPARENT){
							setCurrentColor(col);
						}
					}
				}
				return true;
			}
		});
		popupColorPicker.setContentView(btn);
		colorPickerButton = new ImageButton(this.getContext());
		colorPickerButton.setPadding(0, 0, 0, 0);
		colorPickerButton.setImageBitmap(colorCircle);
		colorPickerButton.setBackgroundColor(Color.WHITE);
		colorPickerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPicker picker = selfReference;
				if(popupColorPicker.isShowing()){
					popupColorPicker.dismiss();
				}else{
					dismissAllPopups();
					popupColorPicker.showAtLocation(picker, Gravity.CENTER | Gravity.BOTTOM, 0,picker.getHeight()+10);
				}
			}
		});
		
		// palettebutton
		
		colorPalette = generateColorPalette(560, 400);
		popupColorPalette.setWidth(560);
		popupColorPalette.setHeight(400);
		
		popupColorPalette.setContentView(colorPalette);
		
		paletteButton = new ImageButton(this.getContext());
		paletteButton.setPadding(0, 0, 0, 0);
		paletteButton.setImageBitmap(paletteBitmap);
		currColorButton = new Button(this.getContext());
		currColorButton.setBackgroundColor(Color.WHITE);
		
		paletteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPicker picker = selfReference;
				if(popupColorPalette.isShowing()){
					popupColorPalette.dismiss();
				}else{
					dismissAllPopups();
					popupColorPalette.showAtLocation(picker, Gravity.CENTER | Gravity.BOTTOM, 0,picker.getHeight()+10);
				}
			}
		});
		
		
		// final settings
		
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
		SeekBar brushSizeSeekBar = new SeekBar(getContext());
		brushSizeSeekBar.setMinimumWidth(w-sz);
		brushSizeSeekBar.setMinimumHeight(sz/4);
		brushSizeSeekBar.setPadding(10, 0, 10, 10);
		brushSizeSeekBar.setMax(200);
		brushSizeSeekBar.setProgress(100);
		brushSize = 0.5f;
		brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float mx = seekBar.getMax();
				brushSize = seekBar.getProgress()/mx;
				notifyListenersBrushSizeChanged();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do nothing
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Do nothing
			}
		});
		colorTable.addView(brushSizeSeekBar);
		for(int i=0;i<rows;++i){
			TableRow rw = new TableRow(this.getContext());
			for(int j=0;j<columns;++j){
				Button bt = new Button(this.getContext());
				bt.setBackgroundColor(colors[i][j]);
				bt.setHeight(sz*3/8);
				bt.setWidth((w-sz)/columns);
				bt.setId((i<<16)+j);
				bt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						int col = colors[id>>>16][id&0xFFFF];
						setCurrentColor(col);
					}
				});
				rw.addView(bt);
			}
			colorTable.addView(rw);
		}
		this.addView(colorTable);
		notifyListenersColorChanged();
		notifyListenersModeChanged();
		notifyListenersBrushSizeChanged();
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
	
	private void notifyListenersBrushSizeChanged(){
		for (ColorPickerListener l : listeners) {
			l.onBrushSizeChanged(brushSize);
		}
	}

	public interface ColorPickerListener {
		public void onColorChanged(int c);
		public void onModeChanged(boolean isCurrentModeFill);
		public void onBrushSizeChanged(float sz);
	}

	// Helper functions
	

	private static int readColorFromNode(Node nd) {
		NamedNodeMap attrs = nd.getAttributes();
		int R = Integer.parseInt(attrs.getNamedItem("R").getTextContent());
		int G = Integer.parseInt(attrs.getNamedItem("G").getTextContent());
		int B = Integer.parseInt(attrs.getNamedItem("B").getTextContent());
		return Color.argb(255, R, G, B);
	}
	
	private void setCurrentColor(int col){
		currColorButton.setBackgroundColor(col);
		currColor = col;
		notifyListenersColorChanged();
		dismissAllPopups();
	}
	
	private void dismissAllPopups(){
		popupColorPalette.dismiss();
		popupColorPicker.dismiss();
	}
	
	private final int[][] PALETTE_COLORS = 
		{
			{0xFF000022,0xFF000044,0xFF000066,0xFF000088,0xFF0000AA,0xFF0000CC,0xFF0000EE,0xFFFFFF22,0xFFFFFF44,0xFFFFFF66,0xFFFFFF88,0xFFFFFFAA,0xFFFFFFCC,0xFFFFFFEE},
			{0xFF002200,0xFF004400,0xFF006600,0xFF008800,0xFF00AA00,0xFF00CC00,0xFF00EE00,0xFFFF22FF,0xFFFF44FF,0xFFFF66FF,0xFFFF88FF,0xFFFFAAFF,0xFFFFCCFF,0xFFFFEEFF},
			{0xFF220000,0xFF440000,0xFF660000,0xFF880000,0xFFAA0000,0xFFCC0000,0xFFEE0000,0xFF22FFFF,0xFF44FFFF,0xFF66FFFF,0xFF88FFFF,0xFFAAFFFF,0xFFCCFFFF,0xFFEEFFFF},			
			{0xFF002222,0xFF004444,0xFF006666,0xFF008888,0xFF00AAAA,0xFF00CCCC,0xFF00EEEE,0xFFFF2222,0xFFFF4444,0xFFFF6666,0xFFFF8888,0xFFFFAAAA,0xFFFFCCCC,0xFFFFEEEE},
			{0xFF222200,0xFF444400,0xFF666600,0xFF888800,0xFFAAAA00,0xFFCCCC00,0xFFEEEE00,0xFF2222FF,0xFF4444FF,0xFF6666FF,0xFF8888FF,0xFFAAAAFF,0xFFCCCCFF,0xFFEEEEFF},
			{0xFF220022,0xFF440044,0xFF660066,0xFF880088,0xFFAA00AA,0xFFCC00CC,0xFFEE00EE,0xFF22FF22,0xFF44FF44,0xFF66FF66,0xFF88FF88,0xFFAAFFAA,0xFFCCFFCC,0xFFEEFFEE},
			{0xFF111111,0xFF222222,0xFF333333,0xFF444444,0xFF555555,0xFF666666,0xFF777777,0xFF888888,0xFF999999,0xFFAAAAAA,0xFFBBBBBB,0xFFCCCCCC,0xFFDDDDDD,0xFFEEEEEE}
		};
	
	private TableLayout generateColorPalette(float w,float h){
		TableLayout t= new TableLayout(getContext());
		for(int i=0;i<PALETTE_COLORS.length;++i){
			TableRow rw = new TableRow(getContext());
			for(int j=0;j<PALETTE_COLORS[i].length;++j){
				Button bt = new Button(getContext());
				bt.setBackgroundColor(PALETTE_COLORS[i][j]);
				bt.setWidth((int) (w/PALETTE_COLORS[i].length));
				bt.setHeight((int) (h/PALETTE_COLORS.length));
				bt.setId((i<<16)+j);
				bt.setPadding(0, 0, 0, 0);
				bt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						int col = PALETTE_COLORS[id>>>16][id&0xFFFF];
						//int col=0;
						setCurrentColor(col);
					}
				});
				rw.addView(bt);
			}
			t.addView(rw);
		}
		return t;
	}
}
