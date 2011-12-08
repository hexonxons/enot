package app.cardgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class CardShowActivity extends Activity
{
	Animation cardMoving = null;
	Button btn = null;
	Bundle extras = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answerview); 
        
        btn = (Button)findViewById(R.id.btnrepeat);
        btn.setVisibility(View.INVISIBLE);
        
        extras = getIntent().getExtras();
    }
    
    public void OnCardClick(View V)
    {
		ImageView card = (ImageView)findViewById(V.getId());
		
		cardMoving = AnimationUtils.loadAnimation(this, R.anim.answeranimation);
		card.startAnimation(cardMoving);
		card.setVisibility(View.INVISIBLE);
		int[] cardSet = null;

		if(extras != null)
		{
			cardSet = extras.getIntArray("card");
		}
		
		ImageView[] cards = new ImageView[6];
		cards[0] = (ImageView)findViewById(R.id.card1);
		cards[1] = (ImageView)findViewById(R.id.card2);
		cards[2] = (ImageView)findViewById(R.id.card3);
		cards[3] = (ImageView)findViewById(R.id.card4);
		cards[4] = (ImageView)findViewById(R.id.card5);
		cards[5] = (ImageView)findViewById(R.id.card6);
		
		int j = 0;
		
		for(int i = 0; i < 6; ++i)
		{
			if(cards[i] != card)
			{
				cards[i].setImageResource(cardSet[j]);
				cards[i].setOnClickListener(null);
				++j;
			}
		}
		
		TextView txt = (TextView)findViewById(R.id.header);
		txt.setText("Мы убрали загаданную вами карту!");
		
		btn.setVisibility(View.VISIBLE);
    }
    
    public void OnRepeatClick(View V)
    {
    	Intent intent = new Intent(this, CardGameActivity.class);
		startActivity(intent);
		finish();
    }
}