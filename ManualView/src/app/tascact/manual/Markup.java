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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import app.tascact.manual.activity.TaskActivity;
import app.tascact.manual.utils.XMLUtils;

public class Markup {
	private static final String BOOK_TAG = "book";
	private static final String PAGE_TAG = "page";
	private static final String TASK_LIST_TAG = "Tasks";
	private static final String TASK_TAG = "Task";
	private static final String RES_LIST_TAG = "PageResources";
	private static final String RES_TAG = "PageResource";
	private static final String TYPE_ATTR = "tasktype";
	private static final String HEIGTH_ATTR = "height";
	private static final String WIDTH_ATTR = "width";

	private Integer pageNumber;

	private Context context;
	private Document resources;
	private String manualName;
	private int[][] pageResourcesCache;
	private int height;
	private int width;

	/**
	 * Parses given XML file.
	 * @param context
	 * @param XMLMarkupFile XML-markup containing file.
	 * @throws Throwable is thrown on any possible error.
	 */
	// TODO should if throw different exceptions?
	public Markup(Context context, int XMLMarkupResourceId)
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

		Node bookRoot = XMLUtils.evalXpathExprAsNode(resources, "/" + BOOK_TAG + "[1]");

		Node heightAttribute = bookRoot.getAttributes().getNamedItem(HEIGTH_ATTR);
		String h = ((Attr) heightAttribute).getValue();
		height =  Integer.parseInt(h);

		Node widthAttribute = bookRoot.getAttributes().getNamedItem(WIDTH_ATTR);
		String w = ((Attr) widthAttribute).getValue();
		width =  Integer.parseInt(w);

		if (height == 0 || width == 0) {
			throw new Throwable("Invalid page size.");
		}

	}

	public Markup(Context context, String XMLMarkupFile) throws Throwable {
		this(context, context.getResources().getIdentifier(XMLMarkupFile,
				"raw", context.getPackageName()));
		manualName = XMLMarkupFile;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
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

	/**Node attribute = task.getAttributes().getNamedItem(TYPE_ATTR);
		String s = ((Attr) attribute).getValue();
		
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

	public PageView getPageView(int pageNumber) {
		return new PageView(pageNumber);
	}

	/**
	 * This class represents a page of the document.
	 * @author losik
	 */
	private class PageView extends LinearLayout {
		public PageView(int pageNumber) {
			super(context);
			setBackgroundColor(Color.WHITE);
			setOrientation(LinearLayout.VERTICAL);

			this.pageNumber = pageNumber;
			int[] resources = getPageResources(pageNumber);

			for(int i = 0; i < resources.length; ++i) {
				ImageView pageElem = new ImageView(this.getContext());
				// Tasks are enumerated 1-based 
				pageElem.setId(i + 1);
				// Makes it keep the ratio when size changed
				pageElem.setImageResource(getPageResources(pageNumber)[i]);
				pageElem.setOnClickListener(taskLauncher);
				pageElem.setAdjustViewBounds(true);
				
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				this.addView(pageElem, params);
			}
		}	

		private int pageNumber;
		private OnClickListener taskLauncher = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Indexes are 1-based
				if(getTaskType(pageNumber, v.getId()) != null) {
	   				Intent intent = new Intent(v.getContext(), TaskActivity.class);
	   				intent.putExtra("ManualName", getManualName());
		   			intent.putExtra("PageNumber", pageNumber);
		   			intent.putExtra("TaskNumber", v.getId());
		   			intent.putExtra("TaskType", getTaskType(pageNumber, v.getId()));
		   			context.startActivity(intent);
	   			}
			}
		};
	}
}