/*
 * HomeActivity класс
 * 
 * Запуск процесса главной страницы
 * 
 */

package app.tascact.manual.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import app.tascact.manual.view.HomeView;

public class HomeActivity extends Activity
{	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
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
        
		HomeView view = new HomeView(this, directoriesToSearchIn,
				explicitlySpecifiedBooks);
		setContentView(view);
    }  
    
    @Override
    protected void onStop()
    {
    	WriteLogs();
    	super.onStop();
    }
    
    private void WriteLogs()
    {
		try
		{
			Process m_LogcatProcess = Runtime.getRuntime().exec("logcat -d *:W");
			BufferedReader m_LogcatReader = new BufferedReader(new InputStreamReader(m_LogcatProcess.getInputStream()), 8192);
			
			FileOutputStream LogFout = null;
			try 
			{
				File mLogFile = new File("/sdcard/eNote/", "logcat" + System.currentTimeMillis() + ".log");
				LogFout = new FileOutputStream(mLogFile);
				String line;
				
				while((line = m_LogcatReader.readLine()) != null)
				{
					line += "\n";
					LogFout.write(line.getBytes());
				}
				LogFout.close();
			}
			catch (FileNotFoundException e)
			{

				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
    }
}