package com.hexonxons.enote.utils;
/*
 * Class for history of touches and analyzing it
 * @author Alex Taran
 */

public class TouchMoment {
	public float x;
	public float y;
	public float t;
	
	public TouchMoment(float nx,float ny,float nt){
		x = nx;
		y = ny;
		t = nt;
	}
}
