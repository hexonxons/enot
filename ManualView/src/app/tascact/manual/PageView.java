package app.tascact.manual;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageView extends LinearLayout {
	
	public PageView(Context context, int[] pageResources, OnClickListener clickListener) {
		super(context);
		
		setOrientation(VERTICAL);
		
		for(int i = 0; i < pageResources.length; ++i) {
			ImageView pageElem = new ImageView(this.getContext());
			pageElem.setId(i);
			pageElem.setBackgroundResource(pageResources[i]);
			pageElem.setOnClickListener(clickListener);
			this.addView(pageElem);
		}
	}	
}
