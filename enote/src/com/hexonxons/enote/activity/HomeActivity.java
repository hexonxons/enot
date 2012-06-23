/*
 * HomeActivity класс
 * 
 * Запуск процесса главной страницы
 * 
 */

package com.hexonxons.enote.activity;

import com.hexonxons.enote.view.HomeView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

public class HomeActivity extends Activity
{	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Uri directoriesToSearchIn[] = {Uri.parse("/sdcard/eNote")};
		Uri explicitlySpecifiedBooks[] = {
				// Uncomment this to see how the view looks like
				// when there are many books.
				/*
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_1"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2"),
				Uri.parse("/sdcard/eNote/geydman_1_2") 
				*/
				};
        
		HomeView view = new HomeView(this, directoriesToSearchIn, explicitlySpecifiedBooks);
		setContentView(view);
    }  
}