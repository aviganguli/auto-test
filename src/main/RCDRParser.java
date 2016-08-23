package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class RCDRParser {
	
	public static File parseToFile(List<Tuple<?, ?>> recorded, String filePath, String jarPath) {
		
		File result = new File(filePath);
		if(result.exists()) { 
			throw new IllegalArgumentException("Error: Overwriting existing files!");
		}
		try (PrintWriter pw = new PrintWriter(result)) {
			pw.println(jarPath);
			for (Tuple<?, ?> tuple : recorded) {
				Integer event = (Integer) tuple.getFirst();
				if (event == MouseEvent.MOUSE_MOVED) {
					// second element must be tuple as it is stored in that way
					@SuppressWarnings("unchecked")
					Tuple<Integer, Integer> info = (Tuple<Integer, Integer>) tuple.getSecond();
					
					pw.println(MouseEvent.MOUSE_MOVED + " " + info.getFirst() + " " + info.getSecond());
					continue;
				}
				
				Integer info = (Integer) tuple.getSecond();
				switch (event) {
					case MouseEvent.MOUSE_PRESSED:
						pw.println(event + " " + info);
						break;
					case MouseEvent.MOUSE_RELEASED:
						pw.println(event + " " + info);
						break;
					case MouseEvent.MOUSE_WHEEL:
						pw.println(event + " " + info);
						break;
					case KeyEvent.KEY_PRESSED:
						pw.println(event + " " + info);
						break;
					case KeyEvent.KEY_RELEASED:
						pw.println(event + " " + info);
						break;
					default:
						throw new IllegalStateException("Event code : " + event + " with info : " + info + " not found!");
					}
				}
			return result;
		}
		catch(Exception e) {
			throw new IllegalStateException("Cannot find file!");
		}
	}
	
	public static Tuple<String,List<Tuple<?, ?>>> parseFromFile(String filePath) {
		File file = new File(filePath) ;
		List<Tuple <?,?>> resultList = new BlockingArrayList<>() ;
		if(! file.exists()) {
			throw new IllegalStateException("Error: Filepath not valid!") ;
		}
		try (Scanner scanner = new Scanner(file)) {
			String resultFile = scanner.nextLine();
			while(scanner.hasNextLine()) {
				String[] lineArr = scanner.nextLine().split("\\s+") ;
				int event = Integer.parseInt(lineArr[0]) ;
				Tuple<?, ?> tuple ;
				switch (event) {
				case MouseEvent.MOUSE_MOVED:
					Tuple<Integer, Integer> coords = new Tuple<Integer, Integer>(Integer.parseInt(lineArr[1]), 
															Integer.parseInt(lineArr[2])) ;	
					tuple =  new Tuple<Integer, Tuple<Integer, Integer>>(event, coords) ;
					break ;
				case MouseEvent.MOUSE_PRESSED:
					tuple =  new Tuple<Integer, Integer>(event, Integer.parseInt(lineArr[1])) ;
					break;
				case MouseEvent.MOUSE_RELEASED:
					tuple =  new Tuple<Integer, Integer>(event, Integer.parseInt(lineArr[1])) ;
					break;
				case MouseEvent.MOUSE_WHEEL:
					tuple =  new Tuple<Integer, Integer>(event, Integer.parseInt(lineArr[1])) ;
					break;
				case KeyEvent.KEY_PRESSED:
					tuple =  new Tuple<Integer, Integer>(event, Integer.parseInt(lineArr[1])) ;
					break;
				case KeyEvent.KEY_RELEASED:
					tuple =  new Tuple<Integer, Integer>(event, Integer.parseInt(lineArr[1])) ;
					break;
				default:
					throw new IllegalStateException("Invalid Event code : " + event);
				}			
				resultList.add(tuple) ;
			}
			return new Tuple<String, List<Tuple<?,?>>>(resultFile, resultList) ;
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Scanner Initialization Failed!") ;
		}		
	}
}

