package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class WindowManager {
	private static String MAC_SCRIPT_PATH = System.getProperty("user.dir") +  
			File.separator + "maximize.scpt";
	
	public static void execute() {
		String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("mac")) {
			Scripts.MAC.execute();
		}
		else if (osType.contains("windows")) {
			Scripts.WINDOWS.execute();
		}
		else if(osType.contains("linux")) {
			Scripts.LINUX.execute();
		}
	}
	
	private enum Scripts {
		LINUX(Arrays.asList("")) {
			@Override
			void execute() {
				try {
					Robot rob = new Robot() ;
					rob.keyPress(KeyEvent.VK_ALT);
					rob.keyPress(KeyEvent.VK_F10);
			        rob.keyRelease(KeyEvent.VK_ALT);
			        rob.keyRelease(KeyEvent.VK_F10);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 
		
		MAC(Arrays.asList("osascript " + MAC_SCRIPT_PATH)) {
			@Override
			void execute() {		
				try {
					System.out.println(SCRIPT.get(0));
					Runtime.getRuntime().exec(SCRIPT.get(0));
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalStateException("Scripts failed!");
				}
			}
		},
		
		WINDOWS(Arrays.asList("")) {

			@Override
			void execute() {
				try {
					Robot rob = new Robot() ;
					rob.keyPress(KeyEvent.VK_WINDOWS);
					rob.keyPress(KeyEvent.VK_UP);
			        rob.keyRelease(KeyEvent.VK_WINDOWS);
			        rob.keyRelease(KeyEvent.VK_UP);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
	
		final List<String> SCRIPT;
		Scripts(List<String> script) {
	        this.SCRIPT=script;
	    }
		
		abstract void execute();
		
    }

}

