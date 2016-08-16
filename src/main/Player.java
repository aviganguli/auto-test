package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class Player implements SequenceController {
	private Robot robot;
	private List<Tuple<?, ?>> recorded;
	private boolean paused;
	private Tuple<?,?> currentElem;
	
	public Player(List<Tuple<?, ?>> recorded) {
		this.recorded = recorded;
		this.paused = false;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot make robot to reproduce actions!");
		}
	}
	
	@Override
	public void play() {
		boolean wasPaused = false;
		if (paused) {
			wasPaused = true;
		}
		paused = false;
		for (Tuple<?, ?> tuple : recorded) {
			if (paused) {
				currentElem = tuple;
				return;
			}
			if (wasPaused) {
				if (currentElem!=null && currentElem == tuple) {
					wasPaused = false;
				}
				else {
					continue;
				}
			}
			Integer event = (Integer) tuple.getFirst();
			if (event == MouseEvent.MOUSE_MOVED) {
				// second element must be tuple as it is stored in that way
				@SuppressWarnings("unchecked")
				Tuple<Integer, Integer> info = (Tuple<Integer, Integer>) tuple.getSecond();
				robot.mouseMove(info.getFirst(), info.getSecond());
				continue;
			}
			Integer info = (Integer) tuple.getSecond();
			switch (event) {
				case MouseEvent.MOUSE_PRESSED:
					System.out.println(tuple);
					robot.mousePress(info);
					robot.delay(100);
					break;
				case MouseEvent.MOUSE_RELEASED:
					System.out.println(tuple);
					robot.mouseRelease(info);
					robot.delay(100);
					break;
				case MouseEvent.MOUSE_WHEEL:
					System.out.println(tuple);
					robot.mouseWheel(info);
					robot.delay(100);
					break;
				case KeyEvent.KEY_PRESSED:
					System.out.println(tuple);
					robot.keyPress(info);
					robot.delay(100);
					break;
				case KeyEvent.KEY_RELEASED:
					System.out.println(tuple);
					robot.keyRelease(info);
					robot.delay(100);
					break;
				default:
					throw new IllegalStateException("Event code : " + event + " with info : " + info + " not found!");
				}
			}
		}


	@Override
	public void pause() {
		paused = true;
	}


	@Override
	public void stop() {
		paused = true;
		currentElem = null;
	}


	@Override
	public void rewind() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fastForward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Tuple<?, ?>> getRecorded() {
		return recorded;
	}

}
