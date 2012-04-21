package app.tascact.manual.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class LogWriter
{
	private static final String ENOTE_MANUALS_PATH = "/sdcard/eNote";
	private static final String LOG_FOLDER_NAME = "log";
	private static final String LOG_FILE_NAME = "log.xml";
	private static final String LAST_LOG_FILE_NAME = "last_log.xml";
	
	private static final String XML_ROOT_NODE_NAME = "Log";
	private static final String XML_ACTION_NODE_NAME = "Action";
	private static final String XML_OUTPUT_ENCODING = "UTF-8";
	
	private File mLogFile = null;
	private File mLastLogFile = null;
	private Uri mLogFolderUri = null;
	
	private Element mLogRootNode = null;
	private Element mLogAct = null;
	
	private Element mLastLogRootNode = null;
	private Element mLastLogAct = null;
	
	private Document mLogDom = null;
	private Document mLastLogDom = null;
	
	public LogWriter(String manualName, int pageNumber, int taskNumber)
	{
		mLogFolderUri = Uri.parse(ENOTE_MANUALS_PATH + File.separator + 
								  manualName + File.separator + 
								  LOG_FOLDER_NAME + File.separator + 
								  pageNumber + File.separator + 
								  taskNumber);
		InitTaskLogStructure();
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
		
		mLogFile = new File(LogFolder, LOG_FILE_NAME);
		mLastLogFile = new File(LogFolder, LAST_LOG_FILE_NAME);
		
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
		
		Element TimeStamp = null;
		Element Event = null;
		Element LocalTime = null;
		Element Time = null;
		Element EventNode = null;
		
		TimeStamp = mLogDom.createElement("TimeStamp");
		Event = mLogDom.createElement("Event");
		LocalTime = mLogDom.createElement("LocalTime");
		Time = mLogDom.createElement("Time");
		EventNode = mLogDom.createElement(node);
		
		LocalTime.appendChild(mLogDom.createTextNode(DateFormat.getDateTimeInstance().format(new Date())));
		Time.appendChild(mLogDom.createTextNode(Long.toString(System.currentTimeMillis())));
		EventNode.appendChild(mLogDom.createTextNode(event));
		
		Event.appendChild(LocalTime);
		Event.appendChild(Time);
		Event.appendChild(EventNode);
		
		TimeStamp.appendChild(Event);
		
		mLogAct.appendChild(TimeStamp);
		
		TimeStamp = mLastLogDom.createElement("TimeStamp");
		Event = mLastLogDom.createElement("Event");
		LocalTime = mLastLogDom.createElement("LocalTime");
		Time = mLastLogDom.createElement("Time");
		EventNode = mLastLogDom.createElement(node);
		
		LocalTime.appendChild(mLastLogDom.createTextNode(DateFormat.getDateTimeInstance().format(new Date())));
		Time.appendChild(mLastLogDom.createTextNode(Long.toString(System.currentTimeMillis())));
		EventNode.appendChild(mLastLogDom.createTextNode(event));
		
		Event.appendChild(LocalTime);
		Event.appendChild(Time);
		Event.appendChild(EventNode);
		
		TimeStamp.appendChild(Event);
		
		if(node.compareTo("TaskRestart") == 0)
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
}	