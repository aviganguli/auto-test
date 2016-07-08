package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;



public class WindowManager {
	private static String MAC_SCRIPT_PATH = System.getProperty("user.dir") + "/maximize.scpt";
	
	public static void execute() {
		String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("mac")) {
			Scripts.MAC.execute();
		}
		else if (osType.contains("windows")) {
			Scripts.WINDOWS.execute();
		}
	}
	
	private enum Scripts {
		LINUX(Arrays.asList()) {
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
		
		WINDOWS(Arrays.asList()) {

			@Override
			void execute() {
				try {
					Runtime.getRuntime().exec("python -m pip install pypiwin32");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalStateException("Shouldn't happen!");
				}
				ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");
				try {
					engine.eval("import win32gui, win32con");
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					engine.eval("hwnd = win32gui.GetForegroundWindow()");
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					engine.eval("win32gui.ShowWindow(hwnd, win32con.SW_MAXIMIZE)");
				} catch (ScriptException e) {
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
