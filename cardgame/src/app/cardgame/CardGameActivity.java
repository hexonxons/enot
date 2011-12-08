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

public class CardGameActivity extends Activity
{
	Animation cardMoving = null;
	int[][] cardSet = null;
	ImageView[] cards = new ImageView[6];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        
        CardSetGenerator gen = new CardSetGenerator();
        cardSet = gen.getCardSets();
        
		cards[0] = (ImageView)findViewById(R.id.card1);
		cards[1] = (ImageView)findViewById(R.id.card2);
		cards[2] = (ImageView)findViewById(R.id.card3);
		cards[3] = (ImageView)findViewById(R.id.card4);
		cards[4] = (ImageView)findViewById(R.id.card5);
		cards[5] = (ImageView)findViewById(R.id.card6);
		
		for(int i = 0; i < 6; ++i)
		{
			cards[i].setImageResource(cardSet[0][i]);
		}
    }
    
    public void onBtnClick(View V)
    {

		Button btn = (Button)findViewById(R.id.btnsetcard);
		TextView header = (TextView)findViewById(R.id.header);
		cardMoving = AnimationUtils.loadAnimation(this, R.anim.cardhide);
		
		for(int i = 0; i < 6; ++i)
		{
			cards[i].startAnimation(cardMoving);
			cards[i].setVisibility(View.INVISIBLE);
		}
		
		btn.setVisibility(View.INVISIBLE);
		header.setVisibility(View.INVISIBLE);
		
		Intent intent = new Intent(this, CardShowActivity.class);
		intent.putExtra("card", cardSet[1]);
		startActivity(intent);
		finish();
    }
}