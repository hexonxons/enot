package app.tascact.manual.task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import app.tascact.manual.Markup;
import app.tascact.manual.R;
import app.tascact.manual.view.TaskView;

public class CompleteTableTaskView extends TaskView {

	private int mTableHeight = 0;
	private int mTableWidth = 0;
	private int mColumnWidth = 0;
	private int mTableMargin = 0;
	private int mLegendWidth = 0;
	private int mRowHeight = 0;
	private int mColNum = 0;

	private int selectedX;
	private int selectedY;
	private Rect selRect = new Rect();
	Rect[] toInvAfterCheck;

	private Rect[] mKeypad = null;
	private int selectedKey = 0;
	private int KEYPAD_COUNT = 0;

	private String[][] mFilledCells = null;
	private boolean[][] canBeEdited = null;
	private boolean[] Checked;

	private boolean EDIT_MODE = true;

	private String[][] InitialResources = { { "7", "2", "" }, { "", "6", "9" },
			{ "5", "", "10" }, { "", "0", "10" }, { "2", "3", "" },
			{ "Слагаемое", "Слагаемое", "Сумма" } };
	private String[] InitialKeypadResources = { "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "0" };
	private String[] Answers = { "9", "3", "5", "10", "5" };

	public CompleteTableTaskView(Context context, Markup markup,
			int PageNumber, int TaskNumber) {
		super(context);

		this.setBackgroundColor(Color.WHITE);

		mColNum = InitialResources.length - 1;
		mFilledCells = new String[mColNum][3];
		canBeEdited = new boolean[mColNum][3];
		for (int i = 0; i < mColNum; i++) {
			for (int j = 0; j < 3; j++) {
				if (InitialResources[i][j] == "") {
					mFilledCells[i][j] = "";
					canBeEdited[i][j] = true;
				} else {
					mFilledCells[i][j] = InitialResources[i][j];
					canBeEdited[i][j] = false;
				}
			}
		}
		KEYPAD_COUNT = InitialKeypadResources.length;
		toInvAfterCheck = new Rect[mColNum];
		Checked = new boolean[mColNum];

		setFocusable(true);
		setFocusableInTouchMode(true);
		setWillNotDraw(false);
	}

	private final Rect selectedRect = new Rect();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mTableMargin = (int) (w / 20);
		mTableWidth = w - 2 * mTableMargin;
		mLegendWidth = w / 5;
		mTableHeight = (int) (h / 6);
		mColumnWidth = (mTableWidth - mLegendWidth) / (mColNum);
		mRowHeight = mTableHeight / 3;
		getRect(selectedX, selectedY, selectedRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * mColumnWidth) + mTableMargin + mLegendWidth,
				(int) (y * mRowHeight), (int) (x * mColumnWidth + mColumnWidth)
						+ mTableMargin + mLegendWidth,
				(int) (y * mRowHeight + mRowHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.table_background));
		canvas.drawRect(mTableMargin, 0, mTableWidth + mTableMargin,
				mTableHeight, background);

		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.table_dark));
		Paint highlight = new Paint();
		highlight.setColor(getResources().getColor(R.color.table_highlight));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.table_light));

		for (int i = 0; i < 3; i++) {
			canvas.drawLine(mTableMargin, i * mRowHeight, mTableWidth
					+ mTableMargin, i * mRowHeight, dark);
			canvas.drawLine(mTableMargin, i * mRowHeight + 1, mTableWidth
					+ mTableMargin, i * mRowHeight + 1, highlight);

		}

		canvas.drawLine(mLegendWidth + mTableMargin, 0, mLegendWidth
				+ mTableMargin, mTableHeight, light);
		canvas.drawLine(mLegendWidth + mTableMargin + 1, 0, mLegendWidth
				+ mTableMargin + 1, mTableHeight, highlight);

		for (int i = 0; i < mColNum + 1; i++) {
			canvas.drawLine(i * mColumnWidth + mLegendWidth + mTableMargin, 0,
					i * mColumnWidth + mLegendWidth + mTableMargin,
					mTableHeight, light);
			canvas.drawLine(i * mColumnWidth + mLegendWidth + mTableMargin + 1,
					0, i * mColumnWidth + mLegendWidth + mTableMargin + 1,
					mTableHeight, highlight);
		}

		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.table_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize((float) (mRowHeight * 0.75));
		foreground.setTextAlign(Paint.Align.CENTER);

		FontMetrics fm = foreground.getFontMetrics();

		float x = mColumnWidth / 2;
		float y = mRowHeight / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < mColNum; i++) {
			for (int j = 0; j < 3; j++) {

				if (mFilledCells[i][j] != "" && canBeEdited[i][j] == true) {
					foreground.setColor(getResources().getColor(
							R.color.table_edit));
					canvas.drawText(mFilledCells[i][j], mTableMargin
							+ mLegendWidth + i * mColumnWidth + x, j
							* mRowHeight + y, foreground);
				} else {
					foreground.setColor(getResources().getColor(
							R.color.table_foreground));
					canvas.drawText(mFilledCells[i][j], mTableMargin
							+ mLegendWidth + i * mColumnWidth + x, j
							* mRowHeight + y, foreground);
				}

			}
		}

		foreground.setTextSize((float) (mRowHeight * 0.5));
		x = mLegendWidth / 2;

		foreground.setColor(getResources().getColor(R.color.table_foreground));
		for (int i = 0; i < 3; i++) {
			canvas.drawText(InitialResources[InitialResources.length - 1][i],
					mTableMargin + x, i * mRowHeight + y, foreground);
		}

		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.table_selected));
		if (EDIT_MODE)
			canvas.drawRect(selRect, selected);

		foreground.setTextSize((float) (mRowHeight * 0.75));
		mKeypad = new Rect[KEYPAD_COUNT + 2];
		int keyWidth = (int) ((getWidth() - 2 * mTableMargin - (KEYPAD_COUNT - 1) * 20) / KEYPAD_COUNT);
		for (int i = 0; i < KEYPAD_COUNT; i++) {
			x = keyWidth / 2;
			int keyX = (int) (mTableMargin + i * keyWidth + i * 20);
			int keyY = (int) (getHeight() / 2);
			Rect r = new Rect(keyX, keyY, (int) (keyX + keyWidth),
					(int) (keyY + mRowHeight));
			canvas.drawRect(r, background);
			canvas.drawText(Integer.toString(i), keyX + x, keyY + y, foreground);
			mKeypad[i] = r;
		}

		foreground.setTextSize((float) (mRowHeight * 0.5));
		x = mLegendWidth / 2;
		int keyX = (int) (mTableMargin + mLegendWidth + 20);
		int keyY = (int) (2 * getHeight() / 3);
		Rect r = new Rect(keyX, keyY, (int) (keyX + mLegendWidth),
				(int) (keyY + mRowHeight));
		canvas.drawRect(r, background);
		canvas.drawText("Ввод", keyX + x, keyY + y, foreground);
		mKeypad[KEYPAD_COUNT] = r;

		keyX = (int) (mTableMargin + 2 * mLegendWidth + 60);
		keyY = (int) (2 * getHeight() / 3);
		r = new Rect(keyX, keyY, (int) (keyX + mLegendWidth),
				(int) (keyY + mRowHeight));
		canvas.drawRect(r, background);
		canvas.drawText("Стереть", keyX + x, keyY + y, foreground);
		mKeypad[KEYPAD_COUNT + 1] = r;

		for (int i = 0; i < mColNum; i++) {
			if (toInvAfterCheck[i] != null && !Checked[i]) {
				selected.setColor(getResources().getColor(
						R.color.table_incorrect));
				canvas.drawRect(toInvAfterCheck[i], selected);
				toInvAfterCheck[i] = null;
			} else if (toInvAfterCheck[i] != null && Checked[i]) {
				selected.setColor(getResources()
						.getColor(R.color.table_correct));
				canvas.drawRect(toInvAfterCheck[i], selected);
				toInvAfterCheck[i] = null;
			}
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getAction();
		float X = event.getX();
		float Y = event.getY();
		if (eventAction != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		if (IsInsideTable(X, Y)) {
			EDIT_MODE = true;
			int selX = (int) ((X - mTableMargin - mLegendWidth) / mColumnWidth);
			int selY = (int) (Y / mRowHeight);
			if (canBeEdited[selX][selY] == true) {
				Select(selX, selY);
			}

		} else {
			// ������������ ���� ������������ ���� ��������������������
			for (int i = 0; i < KEYPAD_COUNT; i++)
				if (mKeypad[i].contains((int) X, (int) Y)) {
					EDIT_MODE = true;
					selectedKey = i;
					mFilledCells[selectedX][selectedY] += Integer
							.toString(selectedKey);
					Rect toInv = new Rect(0, selRect.top, getWidth(),
							selRect.bottom);
					invalidate(toInv);
				}
			//
			if (mKeypad[KEYPAD_COUNT].contains((int) X, (int) Y)) {
				EDIT_MODE = false;
				Rect toInv = new Rect(0, selRect.top, getWidth(),
						selRect.bottom);
				invalidate(toInv);
			}

			if (mKeypad[KEYPAD_COUNT + 1].contains((int) X, (int) Y)) {
				EDIT_MODE = true;
				if (mFilledCells[selectedX][selectedY].length() != 0) {
					mFilledCells[selectedX][selectedY] = mFilledCells[selectedX][selectedY]
							.substring(
									0,
									mFilledCells[selectedX][selectedY].length() - 1);
				}
				Rect toInv = new Rect(0, selRect.top, getWidth(),
						selRect.bottom);
				invalidate(toInv);
			}

		}

		return true;
	}

	private void Select(int x, int y) {
		invalidate(selRect);
		selectedX = Math.min(Math.max(x, 0), mColNum);
		selectedY = Math.min(Math.max(y, 0), 2);
		getRect(selectedX, selectedY, selRect);
		invalidate(selRect);
	}

	private boolean IsInsideTable(float x, float y) {
		return (y <= mTableHeight && x >= mTableMargin + mLegendWidth && x <= getWidth()
				- mTableMargin);
	}

	@Override
	public void RestartTask() {
		Checked = new boolean[mColNum];
		toInvAfterCheck = new Rect[mColNum];
		selRect = new Rect();
		for (int i = 0; i < mColNum; i++) {
			for (int j = 0; j < 3; j++) {
				if (InitialResources[i][j] == "") {
					mFilledCells[i][j] = "";
					canBeEdited[i][j] = true;
				} else {
					mFilledCells[i][j] = InitialResources[i][j];
					canBeEdited[i][j] = false;
				}
			}
		}
		invalidate();
	}

	@Override
	public void CheckTask() {
		for (int i = 0; i < mColNum; i++) {
			for (int j = 0; j < 3; j++) {
				if (canBeEdited[i][j]) {
					Checked[i] = (mFilledCells[i][j].equals(Answers[i]));
					int checkX = Math.min(Math.max(i, 0), mColNum);
					int checkY = Math.min(Math.max(j, 0), 2);
					toInvAfterCheck[i] = new Rect();
					getRect(checkX, checkY, toInvAfterCheck[i]);
					selRect = new Rect();
					invalidate();
				}
			}
		}
	}
}
