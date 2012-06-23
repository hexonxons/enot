package com.hexonxons.enote.view;

import java.io.File;
import java.util.Vector;

import com.hexonxons.enote.R;
import com.hexonxons.enote.activity.PageReaderActivity;
import com.hexonxons.enote.utils.Markup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HomeView extends ScrollView
{
	Vector<Uri> documentDirectories;
	Cover bookCovers[]; 
	LinearLayout innerWrapper;
	
	public HomeView(Context context, Uri[] searchDirectories, Uri[] paths)
	{
		super(context);
		
		this.setBackgroundColor(Color.WHITE);
		
		innerWrapper = new LinearLayout(context);
		innerWrapper.setOrientation(LinearLayout.VERTICAL);
		addView(innerWrapper);
		
		documentDirectories = new Vector<Uri>();
		
		
		//Adding all explicitly specified books.
		if (paths != null) 
		{
			for (Uri path : paths) 
			{
				documentDirectories.addElement(path);
			}
		}
			
		// Searching all books in given directories.
		if (searchDirectories != null)
		{
			for (Uri dir : searchDirectories) 
			{
				File f = new File(dir.getPath());
				if (!f.isDirectory()) 
				{
					continue;
				}
				
				File[] files = f.listFiles();
				if (files == null)
				{
					continue;
				}
				
				for (File file : files) 
				{
					if (file.isDirectory()) {
						documentDirectories.addElement(Uri.fromFile(file));
					}
				}			
			}
		}
		
		// Creating covers and adding them to the scene.
		// Note that they will be positioned in onSizeChanged.
		bookCovers = new Cover[documentDirectories.size()];
		for (int i = 0; i < bookCovers.length; ++i)
		{
			bookCovers[i] = new Cover(context, documentDirectories.get(i).getPath(), "", i);
			innerWrapper.addView(bookCovers[i]);
		}
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// Creating proper layout.
		for (int i = 0; i < bookCovers.length; ++i)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, 200);
			bookCovers[i].setLayoutParams(params);
		}
		
	}
	
	OnClickListener OpenBookClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(v.getContext(), PageReaderActivity.class);
			intent.putExtra("bookName", documentDirectories.get(v.getId()).getLastPathSegment());
   			getContext().startActivity(intent);			
		}
	};
	
	private class Cover extends LinearLayout
	{
		private LinearLayout mContent = null;
		private LinearLayout mInfo = null;
		private Markup mMarkup = null;
		
		public Cover(Context context, String dirUri, String description, int id)
		{
			super(context);
			
			this.setOrientation(LinearLayout.VERTICAL);
			this.setOnClickListener(OpenBookClickListener);
			this.setId(id);
			
			ImageView coverImage = new ImageView(context);
			coverImage.setId(id);
			coverImage.setBackgroundColor(Color.WHITE);
			coverImage.setImageURI(Uri.parse(dirUri  + File.separator + "cover.png"));
			coverImage.setOnClickListener(OpenBookClickListener);
			
			try 
			{
				mMarkup = new Markup(context, Uri.parse(dirUri));
			} 
			catch (Throwable e)
			{
				Log.e("HomeView", "Exception while creating mMarkup.");
				e.printStackTrace();
				return;
			}
			
			mContent = new LinearLayout(context);
			mContent.setBackgroundColor(Color.WHITE);
			mContent.addView(coverImage, new LinearLayout.LayoutParams(200, 198));
			
			mInfo = new LinearLayout(context);
			mInfo.setOrientation(LinearLayout.VERTICAL);
			TextView name = new TextView(context);
			name.setTextColor(Color.DKGRAY);
			name.setTextSize(20);
			name.setText("Название: " + mMarkup.getName());
			mInfo.addView(name);
			
			TextView subname = new TextView(context);
			subname.setTextColor(Color.DKGRAY);
			subname.setTextSize(20);
			subname.setText("Тип: " + mMarkup.getSubname());
			mInfo.addView(subname);
			
			TextView authors = new TextView(context);
			authors.setTextColor(Color.DKGRAY);
			authors.setTextSize(20);
			authors.setText("Авторы: " + mMarkup.getAuthors());
			mInfo.addView(authors);
			
			ImageView separator = new ImageView(context);
			separator.setBackgroundResource(R.drawable.separator_contents);
			
			this.addView(mContent);
			mContent.addView(mInfo);
			this.addView(separator, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
}
