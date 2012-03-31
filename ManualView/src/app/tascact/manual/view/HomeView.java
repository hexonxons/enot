package app.tascact.manual.view;

import java.io.File;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import app.tascact.manual.activity.PageReaderActivity;

public class HomeView extends ScrollView {
	Vector<Uri> documentDirectories;
	ImageView bookCovers[]; 
	FrameLayout innerWrapper;
	
	public HomeView(Context context, Uri[] searchDirectories, Uri[] paths) {
		super(context);
		innerWrapper = new FrameLayout(context);
		addView(innerWrapper);
		
		documentDirectories = new Vector<Uri>();
		
		
		//Adding all explicitly specified books.
		if (paths != null) {
			for (Uri path : paths) {
				documentDirectories.addElement(path);
			}
		}
			
		// Searching all books in given directories.
		if (searchDirectories != null) {
			for (Uri dir : searchDirectories) {
				File f = new File(dir.getPath());
				if (!f.isDirectory()) {
					continue;
				}
				
				File[] files = f.listFiles();
				if (files == null) {
					continue;
				}
				
				for (File file : files) {
					if (file.isDirectory()) {
						documentDirectories.addElement(Uri.fromFile(file));
					}
				}			
			}
		}
		
		// Creating covers and adding them to the scene.
		// Note that they will be positioned in onSizeChanged.
		bookCovers = new ImageView[documentDirectories.size()];
		for (int i = 0; i < bookCovers.length; ++i) {
			bookCovers[i] = new ImageView(context);
			bookCovers[i].setId(i);
			bookCovers[i].setBackgroundColor(Color.GRAY);
			bookCovers[i].setImageURI(Uri.parse(documentDirectories.get(i)
					.getPath() + File.separator + "cover.png"));
			bookCovers[i].setOnClickListener(openBook);
			innerWrapper.addView(bookCovers[i]);
		}
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// +5 is in order to have distance between neighbors at least 5 px.
		int rowSize = w / (150 + 5);
		int offset = (w - 150 * rowSize) / (rowSize + 1);
		
		// Creating proper layout.
		for (int i = 0; i < bookCovers.length; ++i) {
			LayoutParams params = new LayoutParams(150, 200);
			params.leftMargin = offset + (offset + 150) * (i % rowSize);
			params.topMargin  = offset + (offset + 200) * (i / rowSize);
			bookCovers[i].setLayoutParams(params);
		}
		
	}
	
	OnClickListener openBook = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			Intent intent = new Intent(v.getContext(), PageReaderActivity.class);
			intent.putExtra("bookName", documentDirectories.get(i).getLastPathSegment());
   			getContext().startActivity(intent);			
		}
		
	};
}
