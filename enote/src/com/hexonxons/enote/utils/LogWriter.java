package com.hexonxons.enote.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class LogWriter
{
	private static final String ENOTE_MANUALS_PATH = "/sdcard/eNote";
	private static final String LOG_FOLDER_NAME = "log";
	private static final String LOG_FILENAME = "log.xml";
	private static final String LAST_LOG_FILENAME = "last_log.xml";
	
	private static final String XML_ROOT_NODE_NAME = "Log";
	private static final String XML_ACTION_NODE_NAME = "Action";
	private static final String XML_OUTPUT_ENCODING = "UTF-8";
	private static final String SERVER_URL = "http://bs.btty.su/~killgamesh/android/logger.php";
	private static final String SAVE_LOG_FILENAME = "saved.log";
	
	private String mManualName = null;
	private int mPageNumber;
	private int mTaskNumber;
	
	private File mLogFile = null;
	private File mLastLogFile = null;
	private File mSavedLogFile = null;

	private Uri mLogFolderUri = null;
	
	private Element mLogRootNode = null;
	private Element mLogAct = null;
	
	private Element mLastLogRootNode = null;
	private Element mLastLogAct = null;
	
	private Document mLogDom = null;
	private Document mLastLogDom = null;
	
	private ArrayDeque<String> mLogStack = null;
	
	private Context mContext = null;
	private RequestTask mSender = null;
	
	public LogWriter(Context context, String manualName, int pageNumber, int taskNumber)
	{
		mLogFolderUri = Uri.parse(ENOTE_MANUALS_PATH + File.separator + 
								  manualName + File.separator + 
								  LOG_FOLDER_NAME + File.separator + 
								  pageNumber + File.separator + 
								  taskNumber);
		mContext = context;
		
		mManualName = manualName;
		mPageNumber = pageNumber;
		mTaskNumber = taskNumber;
		
		mSender = new RequestTask();
		mSender.start();
		mSender.setRunning(true);
		
		mLogStack = new ArrayDeque<String>();
		
		InitTaskLogStructure();
		
		WriteEvent("ManualName", mManualName);
		WriteEvent("PageNumber", Integer.toString(mPageNumber));
		WriteEvent("TaskNumber", Integer.toString(mTaskNumber));
	}
	
	/**
	 * 
	 * Create needed files and forders for task or load data from that files.<br>
	 * Tree structure:<br>
	 * /eNote/#manualName#/logs/#mPageNumber#/#mTaskNumber#/all_log.xml<br>		   
	 * /eNote/#manualName#/logs/#mPageNumber#/#mTaskNumber#/last_log.xml<br>
	 * 
	 * @return true, if all files and forders exists or been created<br>
	 * false otherwise
	 * 
	 */
	private boolean InitTaskLogStructure()
	{
		File LogFolder = new File(mLogFolderUri.getPath());
		
		if(!LogFolder.exists())
		{
			if(!LogFolder.mkdirs())
				return false;
		}
		
		mLogFile = new File(LogFolder, LOG_FILENAME);
		mLastLogFile = new File(LogFolder, LAST_LOG_FILENAME);
		mSavedLogFile = new File(ENOTE_MANUALS_PATH + File.separator + SAVE_LOG_FILENAME);
		
		if(!mSavedLogFile.exists())
		{
			try
			{
				mSavedLogFile.createNewFile();
			}
			catch (IOException e)
			{
				Log.e("LogWriter", "IOException while creating mSavedLogFile.");
				Log.e("InitTaskLogStructure", e.getMessage());
				e.printStackTrace();
				return false;
			}
			
		}
		
		if(!mLogFile.exists())
		{
			try
			{
				mLogFile.createNewFile();
				FileOutputStream fos =  new FileOutputStream(mLogFile);
				XmlSerializer serializer = Xml.newSerializer();
				serializer.setOutput(fos, XML_OUTPUT_ENCODING);
				serializer.startDocument(null, null);
				serializer.startTag(null, XML_ROOT_NODE_NAME);
				serializer.endTag(null, XML_ROOT_NODE_NAME);
				serializer.endDocument();
                serializer.flush();
                fos.close();
			} 
			catch (IOException e)
			{
				Log.e("LogWriter", "IOException while creating mLogFile.");
				Log.e("InitTaskLogStructure", e.getMessage());
				e.printStackTrace();
				return false;
			}
				
		}
		
		if(!mLastLogFile.exists())
		{
			try 
			{
				mLastLogFile.createNewFile();
				FileOutputStream fos =  new FileOutputStream(mLastLogFile);
				XmlSerializer serializer = Xml.newSerializer();
				serializer.setOutput(fos, XML_OUTPUT_ENCODING);
				serializer.startDocument(null, null);
				serializer.startTag(null, XML_ROOT_NODE_NAME);
				serializer.startTag(null, XML_ACTION_NODE_NAME);
				serializer.endTag(null, XML_ACTION_NODE_NAME);
				serializer.endTag(null, XML_ROOT_NODE_NAME);
				serializer.endDocument();
                serializer.flush();
                fos.close();
			} 
			catch (IOException e)
			{
				Log.e("LogWriter", "IOException while creating mLastLogFile.");
				Log.e("InitTaskLogStructure", e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		
		try 
		{
			// file streams for logs
			FileInputStream LogFin = new FileInputStream(mLogFile);
			FileInputStream LastLogFin = new FileInputStream(mLastLogFile);
			FileInputStream SavedLogFin = new FileInputStream(mSavedLogFile);
			
			// reading saved logs
			BufferedReader reader = new BufferedReader(new InputStreamReader(SavedLogFin));
			if (SavedLogFin != null)
			{							
				String logString = null;
				while ((logString = reader.readLine()) != null)
				{	
					mLogStack.addLast(logString);
				}				
			}		
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// create new action for all log
			mLogDom = builder.parse(LogFin);
			mLogRootNode = mLogDom.getDocumentElement();
			mLogAct = mLogDom.createElement(XML_ACTION_NODE_NAME);
			
			builder = factory.newDocumentBuilder();
			mLastLogDom = builder.parse(LastLogFin);
			mLastLogRootNode = mLastLogDom.getDocumentElement();
			mLastLogAct = ((Element)mLastLogRootNode.getElementsByTagName(XML_ACTION_NODE_NAME).item(0));
		} 
		catch (FileNotFoundException e) 
		{
			Log.wtf("LogWriter", "mLastLogFile not found.");
			Log.wtf("InitTaskLogStructure", e.getMessage());
			e.printStackTrace();
			return false;
		} 
		catch (ParserConfigurationException e)
		{
			Log.e("LogWriter", "Parser configuration broken.");
			Log.e("InitTaskLogStructure", e.getMessage());
			e.printStackTrace();
			return false;
		} 
		catch (SAXException e)
		{
			Log.e("LogWriter", "Sax broken.");
			Log.e("InitTaskLogStructure", e.getMessage());
			e.printStackTrace();
			return false;
		} 
		catch (IOException e)
		{
			Log.e("LogWriter", "IO ex on dom.");
			Log.e("InitTaskLogStructure", e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean CloseLog()
	{	
		// write log data
		try 
		{
			mLogRootNode.appendChild(mLogAct);
			mLogDom.normalizeDocument();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(mLogDom);
			StreamResult result = new StreamResult(mLogFile);
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			Log.e("LogWriter", "Transformer configuration broken while operation with mLogFile.");
			Log.e("CloseLog", e.getMessage());
			e.printStackTrace();
			return false;
		} 
		catch (TransformerException e)
		{
			Log.e("LogWriter", "Transformer broken while operation with mLogFile.");
			Log.e("CloseLog", e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		try 
		{
			mLastLogDom.normalizeDocument();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(mLastLogDom);
			StreamResult result = new StreamResult(mLastLogFile);
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			Log.e("LogWriter", "Transformer configuration broken while operation with mLastLogFile.");
			Log.e("CloseLog", e.getMessage());
			e.printStackTrace();
			return false;
		} 
		catch (TransformerException e)
		{
			Log.e("LogWriter", "Transformer broken while operation with mLastLogFile.");
			Log.e("CloseLog", e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		try
		{
			mSender.setRunning(false);
			mSavedLogFile.createNewFile();
			
			FileWriter fw = new FileWriter(mSavedLogFile);
            BufferedWriter bout = new BufferedWriter(fw);
            
            String ev = null;
            while(mLogStack.size() != 0)
            {
            	ev = mLogStack.pop();
            	bout.write(ev + "\n");
            }
            bout.close();
			
		}
		catch (IOException e)
		{
			Log.e("LogWriter", "IOException somewhere while creating saved log");
			Log.e("CloseLog", e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Write event to log.
	 * 
	 * Struct of action xml node:
	 * 
	 * <TimeStamp>
	 *     <Event>
	 *         <LocalTime></LocalTime>
	 *         <Time>System.currentTimeMillis()</Time>
	 *         <node>event</node>
	 *     </Event>
	 * </TimeStamp>
	 * 
	 * @param node String to be name of event node
	 * @param event	Value of event node
	 */
	public void WriteEvent(String node, String event)
	{
		EventStruct EventToWrite = new EventStruct(node, event);
		
		Element TimeStamp = null;
		Element Event = null;
		Element LocalTime = null;
		Element Time = null;
		Element EventNode = null;
		
		TimeStamp = mLogDom.createElement("TimeStamp");
		Event = mLogDom.createElement("Event");
		LocalTime = mLogDom.createElement(EventToWrite.LocalTime.getName());
		Time = mLogDom.createElement(EventToWrite.Time.getName());
		EventNode = mLogDom.createElement(EventToWrite.Event.getName());
		
		LocalTime.appendChild(mLogDom.createTextNode(EventToWrite.LocalTime.getValue()));
		Time.appendChild(mLogDom.createTextNode(EventToWrite.Time.getValue()));
		EventNode.appendChild(mLogDom.createTextNode(EventToWrite.Event.getValue()));
		
		Event.appendChild(LocalTime);
		Event.appendChild(Time);
		Event.appendChild(EventNode);
		
		TimeStamp.appendChild(Event);
		
		mLogAct.appendChild(TimeStamp);
		
		TimeStamp = mLastLogDom.createElement("TimeStamp");
		Event = mLastLogDom.createElement("Event");
		LocalTime = mLastLogDom.createElement(EventToWrite.LocalTime.getName());
		Time = mLastLogDom.createElement(EventToWrite.Time.getName());
		EventNode = mLastLogDom.createElement(EventToWrite.Event.getName());
		
		LocalTime.appendChild(mLastLogDom.createTextNode(EventToWrite.LocalTime.getValue()));
		Time.appendChild(mLastLogDom.createTextNode(EventToWrite.Time.getValue()));
		EventNode.appendChild(mLastLogDom.createTextNode(EventToWrite.Event.getValue()));
		
		Event.appendChild(LocalTime);
		Event.appendChild(Time);
		Event.appendChild(EventNode);
		
		TimeStamp.appendChild(Event);
		
		if(EventToWrite.Event.getValue().compareTo("TaskRestart") == 0)
		{
			NodeList child = mLastLogAct.getChildNodes();
			for(int i = 0; i < child.getLength(); ++i)
			{
				mLastLogAct.removeChild(child.item(i));
			}
		}
		else
			mLastLogAct.appendChild(TimeStamp);
		
	}
	
	public Node GetLastAct()
	{
		NodeList actList = mLastLogDom.getElementsByTagName(XML_ACTION_NODE_NAME);
		return actList.item(0);
	}
	
	/**
	 * Event structure
	 */
	private class EventStruct extends Object
	{
		public NameValuePair Event = null;
		public NameValuePair LocalTime = null;
		public NameValuePair Time = null;
		
		EventStruct(String eventName, String EventValue)
		{
			Event = new BasicNameValuePair(eventName, EventValue);
			LocalTime = new BasicNameValuePair("TimeStamp", DateFormat.getDateTimeInstance().format(new Date()));
			Time = new BasicNameValuePair("Time", Long.toString(System.currentTimeMillis()));
			mLogStack.addLast(eventName + ";" + EventValue);
			mLogStack.addLast("TimeStamp" + ";" + DateFormat.getDateTimeInstance().format(new Date()));
			mLogStack.addLast("Time" + ";" + Long.toString(System.currentTimeMillis()));
		}
	}
	
	/**
	 * 
	 * Class for sending logs to the server
	 *
	 */
	class RequestTask extends Thread
	{
		private List<NameValuePair> mPostParams = new ArrayList<NameValuePair>();
		private boolean mRun = true;
		
		// response
		private String mResponseString = null;
    	// client for connection
        private final HttpClient mHttpClient = new DefaultHttpClient();
        
		public RequestTask()
		{
		}
		
		public void setRunning(boolean run)
		{
			mRun = run;
		}
		
		
		@Override
		public void run() 
		{
			while(mRun)
			{
				// if network avaliable
		        if(((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null)
		        {
		        	try 
		        	{
		        		// send event string
		        		String event = null;
		        		if(mLogStack.size() != 0)
		        		{
		        			event = mLogStack.pop();
		        			
			        		// post data
				    		HttpPost post = new HttpPost(SERVER_URL);			    		
				    		// post params
				    		BasicNameValuePair postEvent = new BasicNameValuePair(event.split(";")[0], event.split(";")[1]);
				    		mPostParams.clear();
				    		mPostParams.add(postEvent);
				    		// setting data
				    		post.setEntity(new UrlEncodedFormEntity(mPostParams));
				    		HttpResponse response = mHttpClient.execute(post);
				    		StatusLine statusLine = response.getStatusLine();
				    		if(statusLine.getStatusCode() == HttpStatus.SC_OK)
				            {
				                ByteArrayOutputStream out = new ByteArrayOutputStream();
				                response.getEntity().writeTo(out);
				                out.close();
				                mResponseString = out.toString();
				            }
				    		else
				            {
				                //Closes the connection.
				                response.getEntity().getContent().close();
				                throw new IOException(statusLine.getReasonPhrase());
				            }
		        		}
					} 
			        catch (ClientProtocolException e)
			        {
			        	Log.e("RequestTask", "ClientProtocolException while sending log");
						Log.e("run", e.getMessage());
						e.printStackTrace();
			        } 
		        	catch (UnsupportedEncodingException e)
		        	{
		        		Log.e("RequestTask", "UnsupportedEncodingException while sending log");
						Log.e("run", e.getMessage());
						e.printStackTrace();
					} 
		        	catch (IllegalStateException e)
		        	{
		        		Log.e("RequestTask", "IllegalStateException while sending log");
						Log.e("run", e.getMessage());
						e.printStackTrace();
					}
		        	catch (IOException e)
		        	{
		        		Log.e("RequestTask", "IOException while sending log");
						Log.e("run", e.getMessage());
						e.printStackTrace();
					} 
		        }
			}
		}
	}
}	