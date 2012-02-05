package app.tascact.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CResources {
	public int TotalPages = -1;
	public int[][] Answers = { { R.drawable.pg5_2_task_1 },
			{ R.drawable.pg5_2_task_2 } };

	
	public String jsonPageResources = "{" + "    \"page\": ["
			+ "                {" + "                    \"PageResources\": ["
			+ R.drawable.book2_59_header
			+ ","
			+ R.drawable.book2_59_1
			+ ","
			+ R.drawable.book2_59_footer
			+ "                                     ],"
			+ "                    \"TaskResources\": ["
			+ "                                        {"
			+ "                                        },"
			+ "                                        {"
			+ "                                            \"TaskResource\":"
			+ "                                                            ["
			+ "                                                            ],"
			+ "											 \"TaskAnswer\":["
			+ "                                                            ],"
			+ "                                            \"TaskType\": 2"
			+ "                                        },   "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.book2_60
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.book2_61
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg4
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg5_1
			+ ","
			+ R.drawable.pg5_2
			+ ","
			+ R.drawable.pg5_3
			+ ","
			+ R.drawable.pg5_4
			+ ","
			+ R.drawable.pg5_footer
			+ "                                     ],"
			+ "                    \"TaskResources\": ["
			+ "                                        {"
			+ "                                        },"
			+ "                                        {"
			+ "                                            \"TaskResource\":"
			+ "                                                            ["
			+ R.drawable.pg5_2_task_1
			+ ","
			+ R.drawable.pg5_2_task_2
			+ ","
			+ R.drawable.pg5_2_task_3
			+ ","
			+ R.drawable.pg5_2_task_4
			+ ","
			+ R.drawable.pg5_2_task_5
			+ ","
			+ R.drawable.pg5_2_task_6
			+ "                                                            ],"
			+ "											 \"TaskAnswer\":["
			+ "                                                            ["
			+ R.drawable.pg5_2_task_2
			+ ","
			+ R.drawable.pg5_2_task_5
			+ "                                                            ]],"
			+ "                                            \"TaskType\": 1"
			+ "                                        },   "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg6
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg7_1
			+ ","
			+ R.drawable.pg7_2
			+ ","
			+ R.drawable.pg7_3
			+ ","
			+ R.drawable.pg7_footer
			+ "                                     ],"
			+ "                    \"TaskResources\": ["
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg8
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg9_1
			+ ","
			+ R.drawable.pg9_2
			+ ","
			+ R.drawable.pg9_3
			+ ","
			+ R.drawable.pg9_footer
			+ "                                       ],"
			+ "                    \"TaskResources\": ["
			+ "                                       {"
			+ "                                            \"TaskResource\":"
			+ "                                                            ["
			+ R.drawable.pg9_2_task_1
			+ ","
			+ R.drawable.pg9_2_task_2
			+ ","
			+ R.drawable.pg9_2_task_3
			+ ","
			+ R.drawable.pg9_2_task_4
			+ ","
			+ R.drawable.pg9_2_task_5
			+ ","
			+ R.drawable.pg9_2_task_6
			+ ","
			+ R.drawable.pg9_2_task_7
			+ ","
			+ R.drawable.pg9_2_task_8
			+ ","
			+ R.drawable.pg9_2_task_9
			+ ","
			+ R.drawable.pg9_2_task_10
			+ "                                                            ],"
			+ "											 \"TaskAnswer\":["
			+ "                                                            ["
			+ R.drawable.pg9_2_task_1
			+ ","
			+ R.drawable.pg9_2_task_5
			+ "                                                            ],"
			+ "                                                            ["
			+ R.drawable.pg9_2_task_2
			+ ","
			+ R.drawable.pg9_2_task_10
			+ "                                                            ],"
			+ "                                                            ["
			+ R.drawable.pg9_2_task_3
			+ ","
			+ R.drawable.pg9_2_task_8
			+ "                                                            ],"
			+ "                                                            ["
			+ R.drawable.pg9_2_task_4
			+ ","
			+ R.drawable.pg9_2_task_6
			+ "                                                            ],"
			+ "                                                            ["
			+ R.drawable.pg9_2_task_7
			+ ","
			+ R.drawable.pg9_2_task_9
			+ "                                                            ]],"
			+ "                                            \"TaskType\": 1"
			+ "                                        },   "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg10
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg11
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg12
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                },"
			+ "                {"
			+ "                    \"PageResources\": ["
			+ R.drawable.pg13
			+ "                                     ],"
			+ "                    \"TaskResources\": [    "
			+ "                                     ]"
			+ "                }"
			+ "            ]" + "}";
	
	
	

	public CResources() {
		try {
			JSONObject resources = new JSONObject(jsonPageResources);
			TotalPages = resources.getJSONArray("page").length();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public int[] GetPageResources(int pageNum) {
		int reti[] = null;

		try {
			JSONObject resources = new JSONObject(jsonPageResources);
			JSONArray pages = resources.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray pageResources = page.getJSONArray("PageResources");

			reti = new int[pageResources.length()];

			for (int i = 0; i < pageResources.length(); ++i) {
				reti[i] = pageResources.getInt(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reti;
	}

	public int[] GetTaskResources(int pageNum, int taskNum) {
		int reti[] = null;
		try {
			JSONObject resources = new JSONObject(jsonPageResources);
			JSONArray pages = resources.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");

			if (taskSet.length() == 0)
				return null;

			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription
					.getJSONArray("TaskResource");
			reti = new int[taskResources.length()];

			for (int i = 0; i < taskResources.length(); ++i) {
				reti[i] = taskResources.getInt(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reti;
	}

	public int GetTaskType(int pageNum, int taskNum) {
		int reti = -1;
		try {
			JSONObject resources = new JSONObject(jsonPageResources);
			JSONArray pages = resources.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");

			if (taskSet.length() == 0)
				return -1;

			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			reti = taskDescription.getInt("TaskType");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reti;
	}

	public int[][] GetTaskAnswer(int pageNum, int taskNum) {
		int reti[][] = null;

		try {
			JSONObject resources = new JSONObject(jsonPageResources);
			JSONArray pages = resources.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");

			if (taskSet.length() == 0)
				return null;

			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription
					.getJSONArray("TaskAnswer");
			reti = new int[taskResources.length()][2];

			for (int i = 0; i < taskResources.length(); ++i) {
				for (int j = 0; j < 2; ++j)
					reti[i][j] = taskResources.getJSONArray(i).getInt(j);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reti;
	}
}