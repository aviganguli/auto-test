package main;


import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.concurrent.TimeUnit;

public class MouseManager {
	
	public MouseManager() {
		return ;
	}
	
	public static void trackMouse() {
		MouseInfo.getPointerInfo().getLocation() ;
		try {
			TimeUnit.SECONDS.sleep(8);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Robot bot = null;
		  try {
		   bot = new Robot();
		  } catch (Exception failed) {
		   System.err.println("Failed instantiating Robot: " + failed);
		  }
		  int mask = InputEvent.BUTTON1_DOWN_MASK;
		  bot.mouseMove(14, 34);
		  bot.mousePress(mask);
		  bot.mouseRelease(mask);
		 }
		
}

