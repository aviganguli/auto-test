package main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import sun.font.Script;

public class WindowManager {

	private final String OS_TYPE ;
	
	public WindowManager() {
		OS_TYPE = System.getProperty("os.name").toLowerCase();
		return ;
		
	}
	
	public void getOpenWindowsTitles() throws ScriptException{
		if(OS_TYPE.contains("mac") ) getAppleOpenWindowsTitles();
		
		return ;
		
		
	}
	
	
	private void getAppleOpenWindowsTitles() throws ScriptException {
		List<String> titles = new ArrayList<String>() ;
		String script = "tell application \"System Events\" to get " + 
		        "the title of every window of every process";
		ScriptEngineManager sem = new ScriptEngineManager() ;
		ScriptEngine engine = sem.getEngineByName("AppleScriptEngine");
		if(engine == null) {
			System.out.println("NO ENGINE");
			return ;
		}
		ArrayList<Object> res = (ArrayList<Object>)engine.eval(script) ;
		if(res == null) {
			System.out.println("Script Failed");
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
	private List<Object> flatten(Collection<Object> obj){
		ArrayList<Object> res = new ArrayList<Object>() ;
		for(Object o : obj){
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
