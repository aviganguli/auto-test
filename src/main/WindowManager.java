package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class WindowManager {
	private static String MAC_SCRIPT_PATH = System.getProperty("user.dir") + "/maximize.scpt";
	
	public static void execute() {
		String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("mac")) {
			Scripts.MAC.execute();
		}
	}
	
	private enum Scripts {
		LINUX(Arrays.asList("")) {
			@Override
			void execute() {
				// TODO Auto-generated method stub
				
			}
		}, 
		
		MAC(Arrays.asList("osascript " + MAC_SCRIPT_PATH)) {
			@Override
			void execute() {
				try {
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
				// TODO Auto-generated method stub
				
			}
			
		};
	
		final List<String> SCRIPT;
		Scripts(List<String> script) {
	        this.SCRIPT=script;
	    }
		
		abstract void execute();
		
    }

}

