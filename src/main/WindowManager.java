package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import sun.font.Script;

public class WindowManager {

	private final String OS_TYPE ;
	private final ScriptEngineManager man ;
	private final ScriptEngine engine ;
	
	public WindowManager() {
		OS_TYPE = System.getProperty("os.name").toLowerCase();
		man = new ScriptEngineManager() ;
		if(OS_TYPE.contains("mac")) engine = man.getEngineByName("AppleScriptEngine") ; 
		else engine = null ;
		
		return ;
		
	}
	
	public void getOpenWindowsTitles() throws ScriptException{
		if(OS_TYPE.contains("mac") ) getAppleOpenWindowsTitles();
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
				String script = "tell application \"System Events\" to tell (process 1 where frontmost is true)\n" +
								"try\n" + "click (button 1 of window 1 whose subrole is \"AXZoomButton\")\n" +
								"end try\n" + "end tell" ;
				//System.out.print(script);
				//getOpenWindowsTitles(); for debugging
				engine.eval(script) ;
				//System.out.println("EXECUTED MAXIMIZE");
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



