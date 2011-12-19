package app.tascact.manual;

public class CResources
{
	public int[][] PageResources = {{R.drawable.pg1},
									{R.drawable.pg2},
									{R.drawable.pg3},
									{R.drawable.pg4},
									{R.drawable.pg5_1, R.drawable.pg5_2,  R.drawable.pg5_3, R.drawable.pg5_4, R.drawable.pg5_footer},
									{R.drawable.pg6},
									{R.drawable.pg7},
									{R.drawable.pg8},
									{R.drawable.pg9},
									{R.drawable.pg10},
									{R.drawable.pg11},
									{R.drawable.pg12},
									{R.drawable.pg13}};
	// 1 индекс - номер страницы
	// 2 индекс - номер задачи на странице
	// 3 индекс - массив элементов задачи
	public int[][][] TaskResources = {{{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0},{R.drawable.pg5_2_task_1, R.drawable.pg5_2_task_2, R.drawable.pg5_2_task_3, R.drawable.pg5_2_task_4, R.drawable.pg5_2_task_5, R.drawable.pg5_2_task_6},{0}, {0}, {0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}}
									  };
}