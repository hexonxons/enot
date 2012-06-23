package com.hexonxons.enote.view.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.hexonxons.enote.R;;

public class AnswerCheckDialog extends AlertDialog
{
	public AnswerCheckDialog(Context context)
	{
		super(context);
	}
	
	public void setAnswer(boolean answer)
	{
		LinearLayout answerLayout = new LinearLayout(getContext());
		answerLayout.setBackgroundColor(Color.WHITE);
		answerLayout.setOrientation(LinearLayout.VERTICAL);
		answerLayout.setGravity(Gravity.CENTER);
		
		ImageView answerImage = new ImageView(getContext());
		answerImage.setBackgroundResource(answer ? R.drawable.right : R.drawable.wrong);
		
		TextView answerText = new TextView(getContext());
		answerText.setTextColor(Color.BLACK);
		answerText.setTextSize(20);
		answerText.setText(answer ? "Правильно" : "Неправильно");
		
		answerLayout.addView(answerImage, new LayoutParams(200, 200));
		answerLayout.addView(answerText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setView(answerLayout, 0, 0, 0, 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		dismiss();
		return true;
	}	
}
