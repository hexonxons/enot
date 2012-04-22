package app.tascact.manual.task;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import app.tascact.manual.Markup;
import app.tascact.manual.task.NoteLayout.TableActiveArea;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;


public class NewCompleteTableTaskView extends TaskView
{
	private String mTaskDescription = "";
	private NoteLayout mMainLayout = null;

	public NewCompleteTableTaskView(Context context, Node resource, Markup markup, LogWriter writer)
	{
		super(context);
		mMainLayout = new NoteLayout(context, writer);
		
		// Getting description of this task
		Node TaskDescription = (Node) XMLUtils.evalXpathExpr(resource, "./TaskDescription", XPathConstants.NODE);
		if (TaskDescription != null) 
		{
			mTaskDescription = TaskDescription.getTextContent();
		}
		
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
				
				
				TableActiveArea expr = mMainLayout.new TableActiveArea(context);
				expr.setParameters(columnsNum, rowsNum);
				
				for(int j = 0; j < rowsNum; ++j)
				{
					expr.setRow(Rows.item(j).getTextContent(), j);
				}
				
				for(int j = 0; j < rowsNum; ++j)
				{
					expr.setRowAnswer(Answers.item(j).getTextContent(), j);
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
				
				TableActiveArea expr = mMainLayout.new TableActiveArea(context);
				expr.setParameters(columnsNum, rowsNum);
				expr.setRow(Rows.item(i).getTextContent(), 0);
				expr.setRowAnswer(Answers.item(i).getTextContent(), 0);
								
				mMainLayout.addView(expr);
			}
		}
		
		
		
		this.addView(mMainLayout);
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
}
