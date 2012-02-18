package app.tascact.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * TaskType :
 * 1	-	connectelem
 * 2	-	completetable
 * 3	-	setoperators
 * .... 
 */
public class CResources
{	
	public int TotalPages = -1;
	public String jsonPageResources = "{\"manual\":[{\"textbook\":\"manual_1_1\",\"page\":[{\"PageResources\":[" + R.drawable.manual_1_1_pg1 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg2 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg3 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg4 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg5_1 + "," + R.drawable.manual_1_1_pg5_2 + "," + R.drawable.manual_1_1_pg5_3 + "," + R.drawable.manual_1_1_pg5_4 + "," + R.drawable.manual_1_1_pg5_5 + "],\"TaskResources\":[[],{\"TaskResource\":[" + R.drawable.manual_1_1_pg5_2_task_1 + "," + R.drawable.manual_1_1_pg5_2_task_2 + "," + R.drawable.manual_1_1_pg5_2_task_3 + "," + R.drawable.manual_1_1_pg5_2_task_4 + "," + R.drawable.manual_1_1_pg5_2_task_5 + "," + R.drawable.manual_1_1_pg5_2_task_6 + "],\"TaskAnswer\":[[" + R.drawable.manual_1_1_pg5_2_task_2 + "," + R.drawable.manual_1_1_pg5_2_task_5 + "]],\"TaskType\":1},{},{},{}]},{\"PageResources\":[" + R.drawable.manual_1_1_pg6 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg7 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg8 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg9_1 + "," + R.drawable.manual_1_1_pg9_2 + "," + R.drawable.manual_1_1_pg9_3 + "," + R.drawable.manual_1_1_pg9_4 + "],\"TaskResources\":[{\"TaskResource\":[" + R.drawable.manual_1_1_pg9_1_task_1 + "," + R.drawable.manual_1_1_pg9_1_task_2 + "," + R.drawable.manual_1_1_pg9_1_task_3 + "," + R.drawable.manual_1_1_pg9_1_task_4 + "," + R.drawable.manual_1_1_pg9_1_task_5 + "," + R.drawable.manual_1_1_pg9_1_task_6 + "," + R.drawable.manual_1_1_pg9_1_task_7 + "," + R.drawable.manual_1_1_pg9_1_task_8 + "," + R.drawable.manual_1_1_pg9_1_task_9 + "," + R.drawable.manual_1_1_pg9_1_task_10 + "],\"TaskAnswer\":[[" + R.drawable.manual_1_1_pg9_1_task_2 + "," + R.drawable.manual_1_1_pg9_1_task_10 + "],[" + R.drawable.manual_1_1_pg9_1_task_1 + "," + R.drawable.manual_1_1_pg9_1_task_5 + "],[" + R.drawable.manual_1_1_pg9_1_task_3 + "," + R.drawable.manual_1_1_pg9_1_task_8 + "],[" + R.drawable.manual_1_1_pg9_1_task_4 + "," + R.drawable.manual_1_1_pg9_1_task_6 + "],[" + R.drawable.manual_1_1_pg9_1_task_7 + "," + R.drawable.manual_1_1_pg9_1_task_9 + "]],\"TaskType\":1},[],[],[]]},{\"PageResources\":[" + R.drawable.manual_1_1_pg10 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg11 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg12 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg13 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg14 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg15 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg16 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg17 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg18 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg19 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg20 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg21 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg22 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg23 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg24 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg25 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg26 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg27 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg28 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg29 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg30 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg31 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg32 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg33 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg34 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg35 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg36 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg37 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg38 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg39 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg40 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg41 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg42 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg43 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg44 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg45 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg46 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg47 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg48 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg49 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg50 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg51 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg52 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg53 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg54 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg55 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg56 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg57 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg58 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg59 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg60 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg61 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg62 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg63 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg64 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg65 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg66 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg67 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg68 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg69 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg70 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg71 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg72 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg73 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg74 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg75 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg76 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg77 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg78 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg79 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg80 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg81 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg82 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg83 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg84 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg85 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg86 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg87 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg88 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg89 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg90 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg91 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg92 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg93 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg94 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg95 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg96 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg97 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg98 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg99 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg100 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg101 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg102 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg103 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg104 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg105 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg106 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg107 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg108 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg109 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg110 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg111 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg112 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg113 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg114 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg115 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg116 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg117 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg118 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg119 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg120 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg121 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg122 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg123 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg124 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg125 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg126 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg127 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg128 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg129 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg130 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg131 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg132 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg133 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg134 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg135 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_1_pg136 + "],\"TaskResources\":[]}]}, {\"textbook\":\"manual_1_2\",\"page\":[{\"PageResources\":[" + R.drawable.manual_1_2_pg1 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg2 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg3 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg4 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg5 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg6 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg7 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg8 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg9 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg10 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg11 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg12 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg13 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg14 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg15 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg16 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg17 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg18 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg19 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg20 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg21 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg22 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg23 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg24 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg25 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg26 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg27 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg28 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg29 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg30 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg31 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg32 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg33 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg34 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg35 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg36 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg37 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg38 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg39 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg40 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg41 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg42 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg43 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg44 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg45 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg46 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg47 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg48 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg49 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg50 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg51 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg52 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg53 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg54 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg55 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg56 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg57 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg58 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg59_1 + "," + R.drawable.manual_1_2_pg59_2 + "," + R.drawable.manual_1_2_pg59_3 + "," + R.drawable.manual_1_2_pg59_4 + "],\"TaskResources\":[{\"TaskResource\":[],\"TaskAnswer\":[],\"TaskType\":2},{},{},{}]},{\"PageResources\":[" + R.drawable.manual_1_2_pg60_1 + "," + R.drawable.manual_1_2_pg60_2 + "," + R.drawable.manual_1_2_pg60_3 + "," + R.drawable.manual_1_2_pg60_4 + "," + R.drawable.manual_1_2_pg60_5 + "," + R.drawable.manual_1_2_pg60_6 + "],\"TaskResources\":[{},{\"TaskResource\":[\"2+7=?\", \"8-4=?\", \"3+6=?\", \"10-6=?\", \"9+1=?\", \"6-3=?\", \"2+8=?\", \"7-5=?\", \"3+3=?\", \"10-7=?\", \"4+5=?\", \"6-2=?\"],\"TaskDescription\":\"Выполни действия:\",\"TaskAnswer\":[\"2+7=9\", \"8-4=4\", \"3+6=9\", \"10-6=4\", \"9+1=10\", \"6-3=3\", \"2+8=10\", \"7-5=2\", \"3+3=6\", \"10-7=3\", \"4+5=9\", \"6-2=4\"],\"TaskType\":3},{\"TaskResource\":[\"?????\"],\"TaskDescription\":\"У Оли 7 кукол, а у Маши на 3 куклы больше.\nСколько кукол у Маши?\",\"TaskAnswer\":[\"7+3=10&3+7=10\"],\"TaskType\":3},{\"TaskResource\":[\"5?4?3?2?1=1\"],\"TaskDescription\":\"Вставь пропущенные знаки действий:\",\"TaskAnswer\":[\"5-4+3-2-1=1\"],\"TaskType\":3},{},{\"TaskResource\":[\"5+4?4+5\", \"7+3?3+7\", \"4+2?4+3\", \"3+5?2+5\", \"7-3?7-2\", \"9-4?8-4\"],\"TaskDescription\":\"Сравни выражения, не вычисляя их значений:\",\"TaskAnswer\":[\"5+4=4+5\", \"7+3=3+7\", \"4+2<4+3\", \"3+5>2+5\", \"7-3<7-2\", \"9-4>8-4\"],\"TaskType\":3}]},{\"PageResources\":[" + R.drawable.manual_1_2_pg61_1 + "," + R.drawable.manual_1_2_pg61_2 + "," + R.drawable.manual_1_2_pg61_3 + "],\"TaskResources\":[{\"TaskResource\":[\"3+2-5=?\", \"7+3-4=?\", \"9-8+9=?\", \"6+3-5=?\", \"7-7+7=?\", \"3+3-3=?\"],\"TaskDescription\":\"Найди значения выражений:\",\"TaskAnswer\":[\"3+2-5=0\", \"7+3-4=6\", \"9-8+9=10\", \"6+3-5=4\", \"7-7+7=7\", \"3+3-3=3\"],\"TaskType\":3},{},{}]},{\"PageResources\":[" + R.drawable.manual_1_2_pg62_1 + "," + R.drawable.manual_1_2_pg62_2 + "," + R.drawable.manual_1_2_pg62_3 + "," + R.drawable.manual_1_2_pg62_4 + "," + "],\"TaskResources\":[{\"TaskResource\":[\"?????\"],\"TaskDescription\":\"В коробке лежало 10 игрушек. Митя взял 4 из них.\nСколько игрушек осталось в коробке?\",\"TaskAnswer\":[\"10-4=6\"],\"TaskType\":3},{\"TaskResource\":[\"?????\"],\"TaskDescription\":\"Петя и Митя играли в шашки. У пети осталось на доске 5 шашек, а у Мити - 3.\nСколько всего шашек осталось на доске?\",\"TaskAnswer\":[\"5+3=8&3+5=8\"],\"TaskType\":3},{\"TaskResource\":[\"?\", \"?\", \"?\", \"?\", \"?\", \"?\"], \"TaskDescription\":\"Вставь в пустые четырехугольники такие числа, чтобы сумма числе вдоль каждой стороны треугольника равнялась 8.\",\"TaskAnswer\":[\"5\", \"3\", \"2\", \"2\", \"0\", \"4\"],\"TaskType\":3},{\"TaskResource\":[" + R.drawable.manual_1_2_pg62_4_task_1 + "," + R.drawable.manual_1_2_pg62_4_task_2 + "," + R.drawable.manual_1_2_pg62_4_task_3 + "," + R.drawable.manual_1_2_pg62_4_task_4 + "," + R.drawable.manual_1_2_pg62_4_task_5 + "," + R.drawable.manual_1_2_pg62_4_task_6 + "," + R.drawable.manual_1_2_pg62_4_task_7 + "],\"TaskAnswer\":[[]],\"TaskType\":99}]},{\"PageResources\":[" + R.drawable.manual_1_2_pg63_1 + "," + R.drawable.manual_1_2_pg63_2 + "," + R.drawable.manual_1_2_pg63_3 + "," + R.drawable.manual_1_2_pg63_4 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg64_1 + "," + R.drawable.manual_1_2_pg64_2 + "," + R.drawable.manual_1_2_pg64_3 + "," + R.drawable.manual_1_2_pg64_4 + "," + R.drawable.manual_1_2_pg64_5 + "," + R.drawable.manual_1_2_pg64_6 + "," + R.drawable.manual_1_2_pg64_7 +"],\"TaskResources\":[{},{\"TaskResource\":[\"10-6-4=?\", \"8+2-5=?\", \"7-6+5=?\", \"2+4+3=?\", \"3+6+1=?\", \"9-2-6=?\"],\"TaskDescription\":\"Найди значения выражений:\",\"TaskAnswer\":[\"10-6-4=0\", \"8+2-5=5\", \"7-6+5=6\", \"2+4+3=9\", \"3+6+1=10\", \"9-2-6=1\"],\"TaskType\":3},{\"TaskResource\":[\"?????\"],\"TaskDescription\":\"У кролика было 10 пакетиков с семенами моркови. Для посадки он использовал 6 пакетиков семян.\nСколько пакетиков с семенами осталось у кролика?\",\"TaskAnswer\":[\"10-6=4\"],\"TaskType\":3},{},{},{},{}]},{\"PageResources\":[" + R.drawable.manual_1_2_pg65 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg66 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg67 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg68 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg69 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg70 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg71 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg72 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg73 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg74 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg75 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg76 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg77 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg78 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg79 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg80 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg81 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg82 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg83 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg84 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg85 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg86 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg87 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg88 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg89 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg90 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg91 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg92 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg93 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg94 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg95 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg96 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg97 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg98 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg99 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg100 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg101 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg102 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg103 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg104 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg105 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg106 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg107 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg108 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg109 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg110 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg111 + "],\"TaskResources\":[]},{\"PageResources\":[" + R.drawable.manual_1_2_pg112 + "],\"TaskResources\":[]}]}]}";
	private JSONObject mManual = null;
	private JSONObject mResources = null;
	
	public CResources(int manualNum)
	{
		try
		{
			mResources = new JSONObject(jsonPageResources);
			JSONArray mManuals = mResources.getJSONArray("manual");
			mManual = mManuals.getJSONObject(manualNum - 1);
			TotalPages = mManual.getJSONArray("page").length();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
	}
	public int[] GetPageResources(int pageNum)
	{
		int reti[] = null;
		
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray pageResources = page.getJSONArray("PageResources");
			
			reti = new int[pageResources.length()];
			
			for(int i = 0; i < pageResources.length(); ++i)
			{
				reti[i] = pageResources.getInt(i);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public int[] GetTaskResources(int pageNum, int taskNum)
	{
		int reti[] = null;
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return null;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription.getJSONArray("TaskResource");
			reti = new int[taskResources.length()];
			
			for(int i = 0; i < taskResources.length(); ++i)
			{
				reti[i] = taskResources.getInt(i);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public boolean isTaskSet(int pageNum, int taskNum)
	{
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return false;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			if(taskDescription.length() == 0)
				return false;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	public String[] GetStringTaskResources(int pageNum, int taskNum)
	{
		String reti[] = null;
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return null;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription.getJSONArray("TaskResource");
			reti = new String[taskResources.length()];
			
			for(int i = 0; i < taskResources.length(); ++i)
			{
				reti[i] = taskResources.getString(i);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public int GetTaskType(int pageNum, int taskNum)
	{
		int reti = -1;
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return -1;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			reti = taskDescription.getInt("TaskType");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public int[][] GetTaskAnswer(int pageNum, int taskNum)
	{
		int reti[][] = null;
		
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return null;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription.getJSONArray("TaskAnswer");
			reti = new int[taskResources.length()][2];
			
			for(int i = 0; i < taskResources.length(); ++i)
			{
				for(int j = 0; j < 2; ++j)
					reti[i][j] = taskResources.getJSONArray(i).getInt(j);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public String[] GetTaskAnswerArray(int pageNum, int taskNum)
	{
		String reti[] = null;
		
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return null;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			JSONArray taskResources = taskDescription.getJSONArray("TaskAnswer");
			reti = new String[taskResources.length()];
			
			for(int i = 0; i < taskResources.length(); ++i)
			{
					reti[i] = taskResources.getString(i);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
	
	public String GetTaskDescription(int pageNum, int taskNum)
	{
		String reti = null;
		
		try
		{
			JSONArray pages = mManual.getJSONArray("page");
			JSONObject page = pages.getJSONObject(pageNum);
			JSONArray taskSet = page.getJSONArray("TaskResources");
			
			if(taskSet.length() == 0)
				return null;
			
			JSONObject taskDescription = taskSet.getJSONObject(taskNum);
			reti = taskDescription.getString("TaskDescription");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return reti;
	}
}