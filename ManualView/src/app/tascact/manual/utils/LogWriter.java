package app.tascact.manual.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.net.Uri;
import android.util.Xml;

public class LogWriter
{
	private String mManualname = null;
	private int mPageNumber = 0;
	private int mTaskNumber = 0;
	private Uri mLogUri = null;
	private File mLogFile = null;
	private Element mRootNode = null;
	private Element mAct = null;
	Document dom = null;
	
	public LogWriter(String manualName, int pageNumber, int taskNumber)
	{
		mManualname = manualName;
		mPageNumber = pageNumber;
		mTaskNumber = taskNumber;
		
		mLogUri = Uri.parse("/sdcard/eNote" + File.separator + mManualname + File.separator);
		InitLogFile(mLogUri.getPath(), Integer.toString(mPageNumber) + Integer.toString(mTaskNumber));
		
		try 
		{
			mLogFile = new File(mLogUri.getPath(), Integer.toString(mPageNumber) + Integer.toString(mTaskNumber));
			FileInputStream fin = new FileInputStream(mLogFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(fin);
            mRootNode = dom.getDocumentElement();
            mAct = dom.createElement("Act");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void InitLogFile(String path, String name)
	{
		File logfile = new File(path, name);
		if(!logfile.exists())
		{
			try 
			{
				logfile.createNewFile();
				FileOutputStream fos =  new FileOutputStream(logfile);
				XmlSerializer serializer = Xml.newSerializer();
				serializer.setOutput(fos, "UTF-8");
				serializer.startDocument(null, null);
				serializer.startTag(null, "Log");
				serializer.endTag(null, "Log");
				serializer.endDocument();
                serializer.flush();
                fos.close();
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void CloseLog()
	{	
		try
		{
			mRootNode.appendChild(mAct);
			dom.normalizeDocument();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(dom);
			StreamResult result = new StreamResult(mLogFile);
			transformer.transform(source, result);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void WriteEvent(String event)
	{
		Element TimeStamp = dom.createElement("TimeStamp");
		Element Time = dom.createElement("Time");
		Element Event = dom.createElement("Event");
		Time.appendChild(dom.createTextNode(Long.toString(System.currentTimeMillis())));
		Event.appendChild(dom.createTextNode(event));
		
		TimeStamp.appendChild(Time);
		TimeStamp.appendChild(Event);
		mAct.appendChild(TimeStamp);
	}
}	