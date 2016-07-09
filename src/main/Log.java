package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;




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
	private final String FILE_PATH = "Downloads/";
	private File rLog;
	private final int LIMIT = 10;
	
	/**
	 * Singleton constructor 
	 */
	Log() {
		this.rLog = new File(FILE_PATH + FILE_NAME);
		if(!rLog.exists()) { 
			try {
				rLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Shouldn't happen!");
			} 
		}
	}
	
	public void addToLog(String path) {
		try (Scanner scanner = new Scanner(rLog)) {
			List<String> paths = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				paths.add(scanner.nextLine());
			}
			Set<String> uniquePaths = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>(){
				/**
				 * boiler plate
				 */
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
	
	public List<String> readFromLog() {
		
		List<String> result = new ArrayList<String>();
		
		try (Scanner scanner = new Scanner(rLog)) {
			while (scanner.hasNextLine()) {
				result.add(scanner.nextLine());
			}
			
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Log file should already exist.");
		}
	
		return result;
	}
}
	 

