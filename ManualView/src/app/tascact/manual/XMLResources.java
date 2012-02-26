/**
 * @author Losev Vladimir (myselflosik@gmail.com)
 */
package app.tascact.manual;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.res.Resources;
import app.tascact.manual.utils.XMLUtils;

public class XMLResources {
	private static final String BOOK_TAG = "book";
	private static final String PAGE_TAG = "page";
	private static final String TASK_LIST_TAG = "Tasks";
	private static final String TASK_TAG = "Task";
	private static final String RES_LIST_TAG = "PageResources";
	private static final String RES_TAG = "PageResource";
	private static final String TYPE_ATTR = "tasktype";

	private Integer pageNumber;
	
	private Context context;
	private Document resources;
	private String manualName;
	
	private int[][] pageResourcesCache;

	/**
	 * Parses given XML file.
	 * @param context
	 * @param XMLMarkupFile XML-markup containing file.
	 * @throws Throwable is thrown on any possible error.
	 */
	// TODO should if throw different exceptions?
	public XMLResources(Context context, int XMLMarkupResourceId)
			throws Throwable {
		this.context = context;

		// Prepare source to read from		
		InputStream in = context.getResources().openRawResource(
				XMLMarkupResourceId);
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer XMLMarkupBuffer = new StringBuffer();

		// Reading XML file to string
		String text;
		while ((text = br.readLine()) != null) {
			XMLMarkupBuffer.append(text);
			XMLMarkupBuffer.append("\n");
		}
		String XMLMarkup = XMLMarkupBuffer.toString();
		in.close();

		// Parsing XML
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(XMLMarkup));
		resources = db.parse(is);
		
		// Creating cache
		pageResourcesCache = new int[getPageNumber()][];
	}

	public XMLResources(Context context, String XMLMarkupFile) throws Throwable {
		this(context, context.getResources().getIdentifier(XMLMarkupFile,
				"raw", context.getPackageName()));
		manualName = XMLMarkupFile;
	}

	/** @return null if constructed not from file.*/
	public String getManualName() {
		return manualName;
	}
	
	/** @return null on failure.*/
	public Integer getPageNumber() {
		if (pageNumber == null) {
			String expr = "/" + BOOK_TAG + 
						  "/" + PAGE_TAG;
			
			NodeList nl = XMLUtils.evalXpathExprAsNodeList(resources, expr);
			if (nl != null) {
				pageNumber = nl.getLength();
			}
		}
		return pageNumber;
	}

	/**
	 * Returns drawable resources required by specified page.
	 * @param pageId page of document to refer. Index 1-based.
	 * @return returns null on failure.
	 */
	public int[] getPageResources(int pageId) {
		// If resources were not cached evaluating
		// and caching them in pageResourcesCache. 
		if (pageResourcesCache[pageId - 1] == null) {
			String expr = "/" + BOOK_TAG + 
						  "/" + PAGE_TAG + "[" + Integer.toString(pageId) + "]" + 
						  "/" + RES_LIST_TAG + 
						  "/" + RES_TAG;
			
			NodeList nl = XMLUtils.evalXpathExprAsNodeList(resources, expr);
			if (nl == null) {
				return null;
			}
	
			pageResourcesCache[pageId - 1] = new int[nl.getLength()];
	
			for (int i = 0; i < nl.getLength(); ++i) {
				String name = nl.item(i).getTextContent();
				name = name.trim();
				Resources res = context.getResources();
				pageResourcesCache[pageId - 1][i] = res.getIdentifier(name,
						"drawable", context.getPackageName());
			}
		}
		
		// Returning cached value.
		return pageResourcesCache[pageId - 1];
	}

	/**
	 * Returns Node referencing to specified task in XML document.
	 * @param pageId page of document to refer. Index 1-based.
	 * @param taskId task id to refer. Index 1-based.
	 * @return returns null on failure.
	 */
	public Node getTaskResources(int pageId, int taskId) {
		String expr = "/" + BOOK_TAG + 
					  "/" + PAGE_TAG + "[" + Integer.toString(pageId) + "]" + 
					  "/" + TASK_LIST_TAG + 
					  "/" + TASK_TAG + 
					  "[@number='" + Integer.toString(taskId) + "']";
		return XMLUtils.evalXpathExprAsNode(resources, expr);
	}

	// TODO why task type should be an integer type?
	/**
	 * @param pageId
	 * @param taskId
	 * @return null on failure.
	 */
	public Integer getTaskType(int pageId, int taskId) {
		String expr = "/" + BOOK_TAG + 
					  "/" + PAGE_TAG + "[" + Integer.toString(pageId) + "]" +
					  "/" + TASK_LIST_TAG + 
					  "/" + TASK_TAG + 
					  "[@number='" + Integer.toString(taskId) + "']";
		
		Node task = XMLUtils.evalXpathExprAsNode(resources, expr);
		if (task == null) {
			return null;
		}

		Node attribute = task.getAttributes().getNamedItem(TYPE_ATTR);
		String s = ((Attr) attribute).getValue();
		return Integer.parseInt(s);
	}
}
