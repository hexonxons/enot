/**
 * Page loader class
 * 
 * Load manual page content in non-ui thread
 * 
 * Copyright 2012 hexonxons
 * 
 * :mailto killgamesh666@gmail.com
 * 
 */

package com.hexonxons.enote.loaders;

import com.hexonxons.enote.view.PageView;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class PageLoaderTask extends AsyncTask<Void, Integer, Void>
{
	private final PageView mPage;
	private ProgressDialog mProgressDialog;
	private int mPageNumber = 0;
	
	public PageLoaderTask(PageView page, ProgressDialog dialog, int pageNumber)
	{
		mPage = page;
		mProgressDialog = dialog;
		mPageNumber = pageNumber;
	}
	
	@Override
	protected void onPreExecute() 
	{
		if(mProgressDialog != null && !mProgressDialog.isShowing())
			mProgressDialog.show();
	}
	@Override
	protected Void doInBackground(Void... params)
	{
		mPage.createPageContent(mPageNumber);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		if (mProgressDialog != null && mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
		
		mPage.loadPageContent();
	}

}
