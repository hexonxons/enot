package app.tascact.manual.utils;

import android.graphics.*;

public final class GraphicsUtils {
	private static Paint emptyPaint = new Paint();
	public static void drawBitmapOnCanvas(Canvas canvas,Bitmap bitmap,PointF position) {
		Rect src = new Rect();
		src.left = 0;
		src.right = bitmap.getWidth();
		src.top = 0;
		src.bottom = bitmap.getHeight();
		RectF dst = new RectF();
		dst.left = position.x;
		dst.right = position.x + bitmap.getWidth();
		dst.top = position.y;
		dst.bottom = position.y + bitmap.getHeight();
		canvas.drawBitmap(bitmap, src, dst, emptyPaint);
	}
}
