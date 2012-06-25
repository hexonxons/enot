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

import android.app.AlertDialog;
import android.os.AsyncTask;

import com.hexonxons.enote.view.PageView;

public class PageLoaderTask extends AsyncTask<Void, Integer, Void>
{
	private final PageView mPage;
	private AlertDialog mProgressDialog;
	private int mPageNumber = 0;
	
	public PageLoaderTask(PageView page, AlertDialog mDialog, int pageNumber)
	{
		mPage = page;
		mProgressDialog = mDialog;
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
