package app.tascact.manual.utils;

import java.util.List;

import android.graphics.PointF;

/*
 * Class for math methods, i.e. touches analysis
 */

public class MathUtils {
	/*
	 * uses Minimal Square Method to find average speed of a sequence of touches
	 * see: http://multitest.semico.ru/mnk.htm
	 */
	public static PointF getAverageVelocity(List<TouchMoment> moments) {
		PointF result = new PointF(0.0f,0.0f);
		int N = moments.size();
		if(N<=1){
			return result;
		}
		float sx = 0.0f; // sum(xi)
		float sy = 0.0f; // sum(yi)
		float st = 0.0f; // sum(ti)
		float st2 = 0.0f; // sum(ti^2)
		float sxt = 0.0f; // sum(xi*ti)
		float syt = 0.0f; // sum(yi*ti)
		for (TouchMoment m : moments) {
			sx+=m.x;
			sy+=m.y;
			st+=m.t;
			st2+=m.t*m.t;
			sxt+=m.x*m.t;
			syt+=m.y*m.t;
		}
		float z = (st*st-N*st2); // 'z' means znamenatel
		result.x = (sx*st-N*sxt)/z;
		result.y = (sy*st-N*syt)/z;
		return result;
	}
}
