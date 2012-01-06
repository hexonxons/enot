package app.tascact.manual;

import java.util.ArrayList;

public class TaskMatch extends CTaskType
{
	public int[] rightStarts;
	public int[] rightEnds;
	
	public ArrayList<Integer> currentStarts;
	public ArrayList<Integer> currentEnds;
	TaskMatch(int[] _rightStarts, int[] _rightEnds)
	{
		id = 1;
		rightStarts = _rightStarts.clone();
		rightEnds = _rightEnds.clone();
		currentStarts = new ArrayList<Integer>();
		currentEnds = new ArrayList<Integer>();
	}
	
	//проверка - сравнение имеющихся массивов "начал" и "концов" с заданными правильными
	public boolean Check()
	{
		int indexStart = 0;
		//if ((currentStarts.size() != rightStarts.length) || (currentEnds.size() != rightEnds.length))
			//return false;
		for (int i = 0; i < currentStarts.size(); ++i)
		{
			indexStart = currentStarts.indexOf(rightStarts[i]);
			if (indexStart != -1)
			{
				if (rightEnds[indexStart] == currentEnds.get(i))
					return true;
				else return false;
			}
			else return false;
		}
		return false;
	}
}
