package app.tascact.manual.task;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.tascact.manual.Markup;
import app.tascact.manual.utils.LogWriter;
import app.tascact.manual.utils.XMLUtils;
import app.tascact.manual.view.TaskView;
import app.tascact.manual.view.utils.FieldView;

public class WriteExpressionTaskView extends TaskView
{
	private String mTaskResources = null;
	private String[] mTaskAnswers = null;
	private RelativeLayout mExpressionLayout = null;
	private ExpressionView mExpression = null;
	private boolean mAnswer = true;
	private AlertDialog mAlertDialog = null;
	
	private LogWriter mWriter = null;
	
	public WriteExpressionTaskView(Context context, Node res, Markup markup, LogWriter writer)
	{
		super(context);
		
		mWriter = writer;
		
		// Getting description of this task
		Node taskDescr = (Node) XMLUtils.evalXpathExpr(res, "./TaskDescription", XPathConstants.NODE);
		String descr = null;
		if (taskDescr != null) 
		{
			descr = taskDescr.getTextContent();
		}
		
		// Getting resources of this task
		NodeList taskRes = (NodeList) XMLUtils.evalXpathExpr(res, "./TaskResources/TaskResource", XPathConstants.NODESET);
		mTaskResources = taskRes.item(0).getTextContent();
		
		// Getting answer resources
		NodeList taskAns = (NodeList) XMLUtils.evalXpathExpr(res, "./TaskAnswer/Answer", XPathConstants.NODESET);
		mTaskAnswers = new String[taskAns.getLength()]; 
		for (int i = 0; i < taskAns.getLength(); ++i) 
		{
			mTaskAnswers[i] = taskAns.item(i).getTextContent();
		}
		
		mExpressionLayout = new RelativeLayout(context);
		// задаем белый фон
		mExpressionLayout.setBackgroundColor(Color.WHITE);
		mAlertDialog = new AlertDialog.Builder(context).create();

		if(descr != null)
		{
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			TextView description = new TextView(context);
			description.setText(descr);
			description.setGravity(Gravity.CENTER_HORIZONTAL);
			description.setTextSize(30);
			description.setTextColor(Color.BLACK);
			mExpressionLayout.addView(description, params);
		}
		mExpression = new ExpressionView(context, mTaskResources);	
		mExpression.setCursor();

		// добавляем выражение
		mExpressionLayout.addView(mExpression);
		this.addView(mExpressionLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	public void processKeyEvent(String label)
	{
		mWriter.WriteEvent("KeyPress", label);
		if(label == "Стереть")
			mExpression.delKeyLabel();
		else
			if(label != "Ввод")
				mExpression.setKeyLabel(label);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		int maxHeight = 70;
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, maxHeight);
		params.setMargins(0, 180, 0, 0);
		mExpression.setLayoutParams(params);	
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	public void RestartTask()
    {
		mWriter.WriteEvent("TaskRestart", "");
		mExpression.clearValues();
    	mAnswer = true;
		invalidate();
    }
    
    public void CheckTask()
    {
    	mWriter.WriteEvent("TaskCheck", "");
    	mAnswer = true;
    	
    	for(int j = 0; j < mTaskAnswers.length; ++j)
    	{
    		if(!mExpression.checkValues(mTaskAnswers[j]))
    			mAnswer = false;
    	}
    	String ans = mAnswer ? "Правильно" : "Неправильно";
		mAlertDialog.setMessage(ans);
		mAlertDialog.show();
    }
	
	private class ExpressionView extends LinearLayout
	{
		private FieldView mField = null;
		
		public ExpressionView(Context context, String expression)
		{
			super(context);			
			mField = new FieldView(context, "", 10);
			mField.setSelected(false);
			this.addView(mField, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		
		// функция задания значения нажатого поля
		// переделать
		public void setKeyLabel(String label)
		{
			mField.addSymb(label);
		}
		
		public void delKeyLabel()
		{
			mField.delSymb();
		}
		
		public void clearValues()
		{
			mField.setFieldContent("");
		}
		
		public void setCursor()
		{
			mField.setSelected(true);
		}
		
		public boolean checkValues(String answer)
		{
			String data = mField.getFieldContent();
			
			if(answer.indexOf(data) < 0 || data.length() == 0 ||
			   ((data.length() < answer.length() && answer.charAt(data.length()) != '&') &&
			   (answer.indexOf(data) + data.length() < answer.length())))
				return false;

			return true;
		}
	}	
}
