package app.cardgame;

import java.util.Random;

public class CardSetGenerator
{
	private static int[] clubsnum = {R.drawable.two_clubs, R.drawable.three_clubs,
						R.drawable.four_clubs, R.drawable.five_clubs,
						R.drawable.six_clubs, R.drawable.seven_clubs,
						R.drawable.eight_clubs, R.drawable.nine_clubs, R.drawable.ten_clubs};
	
	private static int[] heartsnum = {R.drawable.two_hearts, R.drawable.three_hearts,
						R.drawable.four_hearts, R.drawable.five_hearts,
						R.drawable.six_hearts, R.drawable.seven_hearts,
						R.drawable.eight_hearts, R.drawable.nine_hearts, R.drawable.ten_hearts};
	
	private static int[] diamondsnum = {R.drawable.two_diamonds, R.drawable.three_diamonds,
						R.drawable.four_diamonds, R.drawable.five_diamonds,
						R.drawable.six_diamonds, R.drawable.seven_diamonds,
						R.drawable.eight_diamonds, R.drawable.nine_diamonds, R.drawable.ten_diamonds};
	
	private static int[] spadesnum = {R.drawable.two_spades, R.drawable.three_spades,
						R.drawable.four_spades, R.drawable.five_spades,
						R.drawable.six_spades, R.drawable.seven_spades,
						R.drawable.eight_spades, R.drawable.nine_spades, R.drawable.ten_spades};
	
	//private static int[] clubspic = {R.drawable.jclubs, R.drawable.qclubs, R.drawable.kclubs, R.drawable.aclubs};
	
	//private static int[] heartspic = {R.drawable.jhearts, R.drawable.qhearts, R.drawable.khearts, R.drawable.ahearts};
	
	//private static int[] diamondspic = {R.drawable.jdiamonds, R.drawable.qdiamonds, R.drawable.kdiamonds, R.drawable.adiamonds};
	
	//private static int[] spadespic = {R.drawable.jspades, R.drawable.qspades, R.drawable.kspades, R.drawable.aspades};
	
	private int[] startCardSet = new int[6];
	private int[] endCardSet = new int[6];
	
	private void generate()
	{
		Random random = new Random();
		for(int i = 0; i < 6; ++i)
		{
			int pos = random.nextInt((int) (System.currentTimeMillis() / 1000));
			
			boolean flag = true;
			int j = 0;
			
			while(flag)
			{
				for(j = 0; j < i; ++j)
				{
					if(startCardSet[j] == clubsnum[pos % 9] || startCardSet[j] == spadesnum[pos % 9] ||
					   startCardSet[j] == heartsnum[pos % 9] || startCardSet[j] == diamondsnum[pos % 9])
					{
						pos = random.nextInt((int) (System.currentTimeMillis() / 1000));
						break;
					}
				}
				if(i == j)
					flag = false;
				
			}
			
			switch(pos % 2)
			{
				// выбор черных
				case 0:
				{
					startCardSet[i] = pos % 9 % 2 == 0 ? clubsnum[pos % 9] : spadesnum[pos % 9];
					endCardSet[i] = pos % 9 % 2 == 0 ? spadesnum[pos % 9] : clubsnum[pos % 9];
					break;
				}
				// выбор красных
				case 1:
				{
					startCardSet[i] = pos% 9 % 2 == 0 ? heartsnum[pos % 9] : diamondsnum[pos % 9];
					endCardSet[i] = pos % 9 % 2 == 0 ? diamondsnum[pos % 9] : heartsnum[pos % 9];
					break;
				}
			}
		}
	}
	
	public int[][] getCardSets()
	{
		int[][] reti = new int[2][6];
		
		generate();
		
		reti[0] = startCardSet;
		reti[1] = endCardSet;		
		return reti;
	}
}