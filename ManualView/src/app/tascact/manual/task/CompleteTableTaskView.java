package app.tascact.manual.task;

import java.io.File;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;
import app.tascact.manual.Markup;
import app.tascact.manual.task.NoteLayout.EditInput;
import app.tascact.manual.task.NoteLayout.TableInput;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;


public class CompleteTableTaskView extends TaskView
{
	private NoteLayout mMainLayout = null;

	public CompleteTableTaskView(Context context, Node resource, Markup markup, LogWriter writer)
	{
		super(context);
		mMainLayout = new NoteLayout(context, writer);
		this.setOrientation(LinearLayout.VERTICAL);

		Node TaskType = (Node) XMLUtils.evalXpathExpr(resource, "./Type", XPathConstants.NODE);
		if(TaskType.getTextContent().compareTo("Table") == 0)
		{
			// Getting resources of this task
			NodeList Tables = (NodeList) XMLUtils.evalXpathExpr(resource, "./Table", XPathConstants.NODESET);
			
			for(int i = 0; i < Tables.getLength(); ++i)
			{
				Node table = Tables.item(i);
				int rowsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./RowsNum", XPathConstants.NODE)).getTextContent());
				int columnsNum = Integer.parseInt(((Node) XMLUtils.evalXpathExpr(table, "./ColumnsNum", XPathConstants.NODE)).getTextContent());
				
				Node TableDescription = (Node)XMLUtils.evalXpathExpr(table, "./TableDescription", XPathConstants.NODE);
				NodeList VerticalHeaders = (NodeList) XMLUtils.evalXpathExpr(table, "./VerticalHeaders/Header", XPathConstants.NODESET);
				NodeList Rows = (NodeList) XMLUtils.evalXpathExpr(table, "./Rows/Row", XPathConstants.NODESET);
				NodeList Answers = (NodeList) XMLUtils.evalXpathExpr(table, "./Answers/Row", XPathConstants.NODESET);
				
				TableInput expr = mMainLayout.new TableInput(context);
				
				if(TableDescription != null)
				{
					expr.setTableHeader(TableDescription.getTextContent());
				}
				
				expr.setParameters(columnsNum, rowsNum);
				
				for(int j = 0; j < rowsNum; ++j)
				{
					expr.setRow(Rows.item(j).getTextContent(), Answers.item(j).getTextContent(), j);
				}

				Node ImageResource = (Node)XMLUtils.evalXpathExpr(resource, "./ImageResource", XPathConstants.NODE);
				if(ImageResource != null)
				{
					String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + ImageResource.getTextContent() + ".png";
					Bitmap TaskImage = BitmapFactory.decodeFile(filePath);
					mMainLayout.setTaskImage(TaskImage);
				}
				
				mMainLayout.addView(expr);
			}
		}
		
		if(TaskType.getTextContent().compareTo("Operators") == 0)
		{
			// Getting resources of this task
			NodeList Rows = (NodeList) XMLUtils.evalXpathExpr(resource, "./TaskResources/TaskResource", XPathConstants.NODESET);
			// Getting answer resources
			NodeList Answers = (NodeList) XMLUtils.evalXpathExpr(resource, "./TaskAnswer/Answer", XPathConstants.NODESET);
			
			for(int i = 0; i < Rows.getLength(); ++i)
			{
				int rowsNum = 1;
				int columnsNum = Rows.item(i).getTextContent().split("\\,").length;
				
				TableInput expr = mMainLayout.new TableInput(context);
				expr.setParameters(columnsNum, rowsNum);
				expr.setRow(Rows.item(i).getTextContent(), Answers.item(i).getTextContent(), 0);
							
				Node ImageResource = (Node)XMLUtils.evalXpathExpr(resource, "./ImageResource", XPathConstants.NODE);
				if(ImageResource != null)
				{
					String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + ImageResource.getTextContent() + ".png";
					Bitmap TaskImage = BitmapFactory.decodeFile(filePath);
					mMainLayout.setTaskImage(TaskImage);
				}
				
				mMainLayout.addView(expr);
			}
		}
		
		if(TaskType.getTextContent().compareTo("EditControl") == 0)
		{
			// Getting resources of this task
			Node Rows = (Node) XMLUtils.evalXpathExpr(resource, "./TaskResources/TaskResource", XPathConstants.NODE);
			// Getting answer resources
			Node Answers = (Node) XMLUtils.evalXpathExpr(resource, "./TaskAnswer/Answer", XPathConstants.NODE);

			int capacity = Rows.getTextContent().length() + 1;
			EditInput expr = mMainLayout.new EditInput(context, capacity, 1, capacity);
			expr.setRowAnswer(Answers.getTextContent());		
			
			Node ImageResource = (Node)XMLUtils.evalXpathExpr(resource, "./ImageResource", XPathConstants.NODE);
			if(ImageResource != null)
			{
				String filePath = markup.getMarkupFileDirectory() + File.separator + "img" + File.separator + ImageResource.getTextContent() + ".png";
				Bitmap TaskImage = BitmapFactory.decodeFile(filePath);
				mMainLayout.setTaskImage(TaskImage);
			}
			
			mMainLayout.addView(expr);
		}

		this.addView(mMainLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mMainLayout.setLayoutParams(new LayoutParams(w,h));
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void RestartTask()
	{
		mMainLayout.RestartTask();
	}

	@Override
	public void CheckTask()
	{
		mMainLayout.CheckTask();
	}	
	
	public void processKeyEvent(String label)
	{
		mMainLayout.processKeyEvent(label);
	}
	
	public void replay()
	{
		mMainLayout.replay();
	}
}
