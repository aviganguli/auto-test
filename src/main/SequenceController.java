package main;

import java.util.List;

public interface SequenceController {
	
	public void play() ;
	
	public void pause() ;
	
	public void stop() ;
	
	public void rewind() ;
	
	public void fastForward() ;
	
	public List<Tuple<?,?>> getRecorded();
	
}
