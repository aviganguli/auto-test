package main;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import gui.ControlBar;

public class SessionController implements NativeMouseInputListener {
	private SequenceController controller;
	private ControlBar bar;
	private static final Tuple<Double, Double> HITBOX_X = new Tuple<Double,Double>(ControlBar.MAX_WIDTH/10,
			ControlBar.MAX_WIDTH*.9);
	private static final Tuple<Double, Double> HITBOX_Y = new Tuple<Double,Double>(ControlBar.START_POSY - ControlBar.BAR_HEIGHT,
			ControlBar.MAX_HEIGHT);
	private boolean isVisible;
	private boolean isFirstRun;
	
	public SessionController(SequenceController controller) {
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
		this.isVisible = false;
		this.controller = controller;
		System.out.println("CONS: " + Thread.currentThread());
	}
	
	public void start() {
		isFirstRun = true;
		addAll();
		bar = new ControlBar(controller);	
	}
	
	public void end() {
		removeAll();
	}
	
	private void removeAll() {
		GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addAll() {
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
	}
	

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		return;
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		return;
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		return;		
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		return;	
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
			
			
				System.out.println("MOUSE MOVED: " + Thread.currentThread());
				if (isInTriggerZone(e.getX(), e.getY()) && !isVisible) {
					isVisible = true;
					
					if (isFirstRun) {
						bar.dispose();
						isFirstRun = false;
					}
					
					bar = new ControlBar(controller);
				}
					
				if (!isInTriggerZone(e.getX(), e.getY()) && isVisible) {
					bar.fade();
					isVisible = false;
				}
			}
		
	
	
	private boolean isInTriggerZone(int x, int y) {
		return	(x > (HITBOX_X.getFirst()) && 
				x < (HITBOX_X.getSecond()) &&
				y > (HITBOX_Y.getFirst()) && 
				y < (HITBOX_Y.getSecond()));
	}


}
