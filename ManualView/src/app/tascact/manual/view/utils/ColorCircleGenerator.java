package app.tascact.manual.view.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;
import app.tascact.manual.utils.MathUtils;

public class ColorCircleGenerator {
	
	public static Bitmap generateBrush(int size){
		Bitmap br = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		for(int i=0;i<size;++i){
			for(int j=0;j<size;++j){
				int r2 = (i-size/2)*(i-size/2)+(j-size/2)*(j-size/2);
				float a = (float)(Math.sqrt(r2)/(size/2)); // a is from 0 to 1
				int alpha = 0;
				if(a>=1.0f){
					alpha = 0;
				}else if(a<=0.2f){
					alpha = 255;
				}else{
					alpha = (int)(Math.cos((a-0.2f)/0.8f*Math.PI/2.0f)*255.0f);
				}
				alpha = MathUtils.clamp(alpha, 0, 255);
				int col = Color.argb(alpha, 255, 255, 255);
				br.setPixel(i, j, col);
			}
		}
		return br;
	}
	
	public static Bitmap generateColorPalette(int size) {
		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		float r = size / 2;
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				float dist = (float) Math.sqrt((i - r) * (i - r) + (j - r)
						* (j - r));
				if (dist > r) {
					bitmap.setPixel(i, j, Color.TRANSPARENT);
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
	
	public static Bitmap generateAdvancedColorPalette(int size) {
		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		float r = size / 2;
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				float dist = (float) Math.sqrt((i - r) * (i - r) + (j - r)
						* (j - r));
				if (dist > r) {
					bitmap.setPixel(i, j, Color.TRANSPARENT);
				} else {
					float hsv[] = new float[3];
					hsv[0] = (float) ((Math.atan2(j - r, i - r) + Math.PI)*180.0f/Math.PI);
					final float k = 0.65f;
					if(dist/r<k){
						hsv[1] = dist /k/ r;
						hsv[2] = 1.0f;
					}else{
						hsv[1] = 1.0f;
						hsv[2] = 1.0f-(dist/r-k)/(1.0f-k);
					}
					bitmap.setPixel(i, j, Color.HSVToColor(255, hsv));
				}
			}
		}
		return bitmap;
	}
}
