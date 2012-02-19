/**
 * UNSTABLE CODE!!!
 * THIS CODE IS KNOWN TO WORK POORLY
 * OR NOT TO WORK AT ALL. DO NOT RECOMMIT
 * THIS CODE TO STABLE BRANCHES.
 * 
 * @author Losev Vladimir (myselflosik@gmail.com)
 */
package app.tascact.manual;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import javax.xml.xpath.*;


public class XMLResources {
	private static final String BOOK_TAG = "book";
	private static final String PAGE_TAG = "page";
	private static final String TASK_TAG = "task";
	private static final String RES_LIST_TAG = "PageResources";
	private static final String RES_TAG = "PageResource";
	private static final String TYPE_ATTR = "type";
	
	
	private Context context;
	private Document resources;
	
	/**
	 * Parses given xml file.
	 * @param context 
	 * @param XMLMarkupFile XML-markup containing file.
	 * @throws Throwable is thrown on any possible error.
	 */
	//TODO should if throw different exceptions?
	public XMLResources(Context context, int XMLMarkupResourceId) throws Throwable {
		this.context = context;
	
		InputStream in = context.getResources().openRawResource(XMLMarkupResourceId); 
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer XMLMarkupBuffer = new StringBuffer();
		
		String text;
		while ((text = br.readLine()) != null) {
			XMLMarkupBuffer.append(text);
			XMLMarkupBuffer.append("\n");
		}
		String XMLMarkup = XMLMarkupBuffer.toString();
		in.close();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(XMLMarkup));
		resources = db.parse(is);
	}
	
	public XMLResources(Context context, String XMLMarkupFile) throws Throwable {
		// TODO Say "no" to hardcode
		this(context, context.getResources().getIdentifier(XMLMarkupFile, "raw", "app.tascact.manual"));
	}
	
	/**
	 * 
	 * @return null on failure.
	 */
	public Integer getPageNumber() {
		String expr = "/" + BOOK_TAG + "/" + PAGE_TAG;
		NodeList nl = (NodeList)evalXpathExpr(expr, XPathConstants.NODESET);
		if (nl == null)
			return null;
		
		return nl.getLength();
	}
	
	/**
	 * Returns drawable resources required by specified page.
	 * @param pageId page of document to refer. Index 1-based.
	 * @return returns null on failure.
	 */
	public int[] getPageResources(int pageId) {				
		String expr = "/" + BOOK_TAG + "/" + PAGE_TAG + "["
				+ Integer.toString(pageId) + "]/" + RES_LIST_TAG + "/"
				+ RES_TAG;
		NodeList nl = (NodeList) evalXpathExpr(expr, XPathConstants.NODESET);
		Log.d("XML", expr);
		if (nl == null) {
			return null;
		}

		int[] pageResources = new int[nl.getLength()];

		for (int i = 0; i < nl.getLength(); ++i) {
			String name = nl.item(i).getTextContent();
			name = name.trim();
			Resources res = context.getResources();
			//TODO /manifest/@package
			pageResources[i] = res.getIdentifier(name, "drawable", "app.tascact.manual");
		}

		return pageResources;
	}

	/**
	 * Returns Node referencing to specified task in XML document.
	 * @param pageId page of document to refer. Index 1-based.
	 * @param taskId task id to refer. Index 1-based.
	 * @return returns null on failure.
	 */
	public Node GetTaskResources(int pageId, int taskId) {
		String expr = "/" + BOOK_TAG+ "/" + PAGE_TAG + "[" + Integer.toString(pageId) + "]/"
				+ TASK_TAG + "[" + Integer.toString(taskId) + "]";
		return (Node) evalXpathExpr(expr, XPathConstants.NODE);
	}

	// TODO why task type should be an integer type?
	/**
	 * 
	 * @param pageId
	 * @param taskId
	 * @return null on failure.
	 */
	public Integer getTaskType(int pageId, int taskId) {
		String expr = "/" + BOOK_TAG+ "/" + PAGE_TAG + "[" + Integer.toString(pageId) + "]/"
				+ TASK_TAG + "[" + Integer.toString(taskId) + "]";
		Node task = (Node)evalXpathExpr(expr, XPathConstants.NODE);
		String s = ((Attr)task.getAttributes().getNamedItem(TYPE_ATTR)).getValue();
		return Integer.parseInt(s);
	}
	
	/**
	 * Applies xpath expression to the resources. 
	 * @param expr xpath expression to apply.
	 * @param rType specifies return type.
	 * @return returns null on failure.
	 */
	protected Object evalXpathExpr(String expr, QName rType) {
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();		
			XPathExpression pathexp = xpath.compile(expr);
			return pathexp.evaluate(resources, rType);
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			Log.e("XML", e.getMessage());
			return null;
		}
	}
	
//	protected List<Node> extractNodesWithTag(Node src, String tag) {
//		List<Node> result = new ArrayList<Node>();
//		NodeList documentList = resources.getChildNodes();
//		
//		for (int i = 0; i != documentList.getLength(); ++i) {
//			Node node = documentList.item(i);
//			if (node.getNodeName().equals(tag)) {
//				result.add(node);
//			}
//		}
//		
//		return result;
//	}
}
