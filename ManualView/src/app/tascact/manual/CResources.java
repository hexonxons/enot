package app.tascact.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CResources
{
	public int[][] PageResources = {{R.drawable.pg1},
									{R.drawable.pg2},
									{R.drawable.pg3},
									{R.drawable.pg4},
									{R.drawable.pg5_1, R.drawable.pg5_2,  R.drawable.pg5_3, R.drawable.pg5_4, R.drawable.pg5_footer},
									{R.drawable.pg6},
									{R.drawable.pg7_1, R.drawable.pg7_2, R.drawable.pg7_3, R.drawable.pg7_footer},
									{R.drawable.pg8},
									{R.drawable.pg9_1, R.drawable.pg9_2, R.drawable.pg9_3, R.drawable.pg9_footer},
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
									  {{R.drawable.pg9_2_task_1, R.drawable.pg9_2_task_2, R.drawable.pg9_2_task_3, R.drawable.pg9_2_task_4, R.drawable.pg9_2_task_5, R.drawable.pg9_2_task_6, R.drawable.pg9_2_task_7, R.drawable.pg9_2_task_8, R.drawable.pg9_2_task_9, R.drawable.pg9_2_task_10}, {0},{0}},
									  {{0}},
									  {{0}},
									  {{0}},
									  {{0}}
									  };
	
	public int[][] Answers = {{R.drawable.pg5_2_task_1}, {R.drawable.pg5_2_task_2}};
	
	public static String jsonPageResources = "{" +
			"    \"page\": [" +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg1  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg2  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg3  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg4  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg5_1 + ","  +
			                                         R.drawable.pg5_2 + ","  +
			                                         R.drawable.pg5_3 + ","  +
			                                         R.drawable.pg5_4 + ","  +
			                                         R.drawable.pg5_footer  +
			"                                     ]," +
			"                    \"TaskResources\": [" +
			"                                        {" +
			"                                        }," +
			"                                        {" +
			"                                            \"TaskResource\":" +
			"                                                            [" +
			                                                                 R.drawable.pg5_2_task_1 + ","  +
			                                                                 R.drawable.pg5_2_task_1 + ","  +
			                                                                 R.drawable.pg5_2_task_2 + ","  +
			                                                                 R.drawable.pg5_2_task_3 + ","  +
			                                                                 R.drawable.pg5_2_task_4 + ","  +
			                                                                 R.drawable.pg5_2_task_5 + ","  +
			                                                                 R.drawable.pg5_2_task_6 + ","  +
			"                                                            ]," +
			"                                            \"TaskType\": 0" +
			"                                        },   " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg6  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg7_1 + ","  +
		                                             R.drawable.pg7_2 + ","  +
			                                         R.drawable.pg7_3 + ","  +
			                                         R.drawable.pg7_footer  +
			"                                     ]," +
			"                    \"TaskResources\": [" +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg8  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg9_1 + ","  +
			                                         R.drawable.pg9_2 + ","  +
			                                         R.drawable.pg9_3 + ","  +
			                                         R.drawable.pg9_footer  +
			"                                     ]," +
			"                    \"TaskResources\": [" +
			"                                        {" +
			"                                            \"TaskResource\":" +
			"                                                            [" +
			                                                                 R.drawable.pg9_2_task_1 + ","  +
			                                                                 R.drawable.pg9_2_task_2 + ","  +
			                                                                 R.drawable.pg9_2_task_3 + ","  +
			                                                                 R.drawable.pg9_2_task_4 + ","  +
			                                                                 R.drawable.pg9_2_task_5 + ","  +
			                                                                 R.drawable.pg9_2_task_6 + ","  +
			                                                                 R.drawable.pg9_2_task_7 + ","  +
			                                                                 R.drawable.pg9_2_task_8 + ","  +
			                                                                 R.drawable.pg9_2_task_9 + ","  +
			                                                                 R.drawable.pg9_2_task_10 +
			"                                                            ]," +
			"                                            \"TaskType\": 0" +
			"                                        },   " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg10  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg11  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg12  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"                {" +
			"                    \"PageResources\": [" +
			                                         R.drawable.pg13  +
			"                                     ]," +
			"                    \"TaskResources\": [    " +
			"                                     ]" +
			"                }," +
			"            ]" +
			"}";
	
	public static int[] GetPageResources(int pageNum) throws JSONException
	{
		JSONObject resources = new JSONObject(jsonPageResources);
		JSONArray pages = resources.getJSONArray("page");
		JSONObject page = pages.getJSONObject(pageNum);
		JSONArray pageResources = page.getJSONArray("PageResources");
		int reti[] = new int[pageResources.length()];
		for(int i = 0; i < pageResources.length(); ++i)
		{
			reti[i] = pageResources.getInt(i);
		}
		return reti;
	}
}