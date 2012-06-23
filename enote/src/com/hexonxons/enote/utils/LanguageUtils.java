package com.hexonxons.enote.utils;

public class LanguageUtils {
	public static int[][] createArray2D(int szx,int szy){
		int[][] result = new int[szx][];
		for(int i=0;i<szx;++i){
			result[i] = new int[szy];
		}
		return result;
	}
}
