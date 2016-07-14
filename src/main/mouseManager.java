package main;


import java.awt.MouseInfo;

public class mouseManager {
	
	public mouseManager() {
		return ;
	}
	
	public static void trackMouse() {
		
	while(true) {
		System.out.println(MouseInfo.getPointerInfo().getLocation() );
		
	}
	
	}
}
