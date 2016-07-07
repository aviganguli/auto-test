package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class WindowManager {

	private final String OS_TYPE ;
	private final ScriptEngineManager manager ;
	private final ScriptEngine engine ;
	
	public WindowManager() {
		OS_TYPE = System.getProperty("os.name").toLowerCase();
		manager = new ScriptEngineManager() ;
		if(OS_TYPE.contains("mac")) engine = manager.getEngineByName("AppleScriptEngine") ; 
		else engine = null ;
		
		return ;
		
	}
	
	public void getOpenWindowsTitles() throws ScriptException{
		if(OS_TYPE.contains("mac") ) {
			getAppleOpenWindowsTitles();
		}
		else System.out.println("OS not supported");
		return ;
		
		
	}
	
	
	private void getAppleOpenWindowsTitles() throws ScriptException {
		List<String> titles = new ArrayList<String>() ;
		String script = "tell application \"System Events\" to get " + 
		        "the title of every window of every process";
		if(engine == null) {
			System.out.println("NO ENGINE");
			return ;
		}
		ArrayList<Object> res = (ArrayList<Object>)engine.eval(script) ;
		if(res == null) {
			System.out.println("No Windows opened");
			return ;
		}
		for(Object o : flatten(res)) {
			if(o != null) {
				String elem = o.toString() ;
				titles.add(elem) ;
				System.out.print(elem + "\n") ;
				
			}	
		}
		return ;
		
	}
	
	public void maximizeWindows() throws ScriptException {
		if(OS_TYPE.contains("mac")) appleMaximizeWindows();
		else System.out.println("OS not supported");
		return ;
		
		
	}
	
	
	private void appleMaximizeWindows() throws ScriptException {
		// posibly perform in another thread and do thread_join() 
				String script = "tell application \"System Events\" to tell (first process where frontmost is true)\n" +
								"click (button 1 where subrole is \"AXZoomButton\" of window 1 )\n" +
								 "end tell\n" + "tell application \"System Events\" to get name of (process 1 where frontmost is true)" ;
				String script2 = "tell application \"System Events\" to tell (process 1 whose name is \"java\")\n" +
						"click (button 2 of window 1)\n" +
						"end tell" ;
				String script3 = "tell application \"System Events\"" +
						"set front window's bounds to {0,0,1000,1000}\n" +
						"end tell" ;
				//System.out.print(script);
				//getOpenWindowsTitles(); for debugging
				//System.out.println(engine.eval(script) );
				String src_file = (System.getProperty("user.dir")) + "/maximize.scpt";
				try {
					System.out.println(src_file);
					Runtime.getRuntime().exec(new String[] { "osascript", src_file }) ;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("EXECUTED MAXIMIZE");
				return ;
			}
			
			private List<Object> flatten(Collection<Object> objs){
				ArrayList<Object> res = new ArrayList<Object>() ;
				for(Object o : objs){
					if(o instanceof Collection) {
						res.addAll( flatten((Collection)o) ) ;
					}
					else{
						res.add(o) ;
					}
				}
				return res ;
	}
}



