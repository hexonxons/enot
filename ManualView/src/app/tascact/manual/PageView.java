package app.tascact.manual;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import app.tascact.manual.activity.TaskActivity;

/**
 * @author Losev Vladimir (myselflosik@gmail.com)
 */
public class PageView extends LinearLayout {
	/**
	 * @param context
	 * @param markup document's markup to display.
	 * @param pageNumber 1-based index.
	 */
	public PageView(Context context, XMLResources markup, int pageNumber) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setClickable(true);
		setOrientation(VERTICAL);
		
		this.markup = markup;
		this.pageNumber = pageNumber;
		pageResources = markup.getPageResources(pageNumber); 
		
		for(int i = 0; i < pageResources.length; ++i) {
			ImageView pageElem = new ImageView(this.getContext());
			// Tasks are enumerated 1-based 
			pageElem.setId(i + 1);
			pageElem.setBackgroundResource(pageResources[i]);
			pageElem.setOnClickListener(taskLauncher);
			this.addView(pageElem);
		}
	}	
	
	private int pageNumber;
	private int[] pageResources;
	private XMLResources markup;
	private OnClickListener taskLauncher = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Indexes are 1-based
			if(markup.getTaskType(pageNumber, v.getId()) != null) {
   				Intent intent = new Intent(v.getContext(), TaskActivity.class);
   				intent.putExtra("ManualName", markup.getManualName());
	   			intent.putExtra("PageNumber", pageNumber);
	   			intent.putExtra("TaskNumber", v.getId());
	   			intent.putExtra("TaskType", markup.getTaskType(pageNumber, v.getId()));
	   			getContext().startActivity(intent);
   			}
		}
	};

}
