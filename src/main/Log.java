package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.net.URLClassLoader;


/**
 * 
 * @author samuellee & StrawhatJedi
 * Create a new file log that contains 10 most recently used
 * file paths.  - Singleton Class
 * 
 */
public enum Log {
	RECENT;
	private final String FILE_NAME = "recentLog.txt" ;
	//private final String FILE_PATH = "resources/config/";
	private File rLog;
	private final int LIMIT = 10;
	private InputStream rLog_jar = null;
	private Boolean read_jar = false ;
	
	/**
	 * Singleton constructor 
	 */
	Log() {
		this.rLog = new File(FILE_NAME);
		// TODO: Refactor this code to remove check for file existence and actual path checks
		if(!rLog.exists()) { 
			try {
				rLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Shouldn't happen!");
			}
			
		}
		ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        	}
	
		rLog_jar = getClass().getResourceAsStream( 
				"/" + 
				FILE_NAME);
		System.out.println(rLog_jar);
	}

	/*public void addToLog(String path) {
		try (Scanner scanner = new Scanner(rLog)) {
			List<String> paths = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				paths.add(scanner.nextLine());
			}
			Set<String> uniquePaths = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>(){
				*//**
				 * boiler plate
				 *//*
				private static final long serialVersionUID = 1L;

				protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
			        return size() > LIMIT;
			    }
			});
			uniquePaths.addAll(paths);
			if (uniquePaths.contains(path)) {
				uniquePaths.remove(path);
			}
			uniquePaths.add(path);
			rLog.delete();
			rLog.createNewFile();
			PrintWriter fw = new PrintWriter(rLog);
			System.out.println(uniquePaths);
			for (String newPath : uniquePaths) {
				fw.println(newPath);
			}
			fw.close();
			return;
		} catch (IOException e) {
			throw new IllegalStateException("Log file i/o should not "
					+ "cause errors.") ;
		}
			
	}
	*/
	public void addToLog(String path) { 
		//reads from jar during first run else read from rLog
		try (Scanner scanner = (read_jar) ? new Scanner(rLog) : new Scanner(rLog_jar)) {
			if(!read_jar) read_jar = true ;
			//sets to true after first read
			List<String> paths = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				paths.add(scanner.nextLine());
			}
			Set<String> uniquePaths = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>(){
			/**
				 boiler plate
			**/
				private static final long serialVersionUID = 1L;

				protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
			        return size() > LIMIT;
			    }
			});
			uniquePaths.addAll(paths);
			if (uniquePaths.contains(path)) {
				uniquePaths.remove(path);
			}
			uniquePaths.add(path);
			rLog.delete();
			rLog.createNewFile();
			PrintWriter fw = new PrintWriter(rLog);
			System.out.println(uniquePaths);
			for (String newPath : uniquePaths) {
				fw.println(newPath);
			}
			fw.close();
			return;
		} catch (IOException e) {
			throw new IllegalStateException("Log file i/o should not "
					+ "cause errors.") ;
		}
			
	}
	
	public void updateJar() {
		try {
			Process proc = Runtime.getRuntime().exec("jar uf " + 
					System.getProperty("user.dir") 
					+ File.separator + "exec.jar recentLog.txt" );
			  StreamRedirector in = new StreamRedirector(proc.getInputStream(), System.out);
		        StreamRedirector err = new StreamRedirector(proc.getErrorStream(), System.err);
		        in.start();
		        err.start();
			proc.waitFor(1, TimeUnit.SECONDS);
			rLog.delete();
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*public List<String> readFromLog() {
		
		List<String> result = new ArrayList<String>();
		
		try (Scanner scanner = new Scanner(rLog)) {
			while (scanner.hasNextLine()) {
				result.add(scanner.nextLine());
			}
			
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Log file should already exist.");
		}
	
		return result;
	}*/
	
	
	public List<String> readFromLog() {
		
		List<String> result = new ArrayList<String>();
		
		try (Scanner scanner = (read_jar) ? new Scanner(rLog) : new Scanner(rLog_jar)) {
			System.out.println(read_jar);
			while (scanner.hasNextLine()) {
				result.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		rLog_jar = getClass().getResourceAsStream(File.separator + 
				FILE_NAME);
		return result;
	}
	
}
	 

