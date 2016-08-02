package main;


import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import gui.ControlBar;

public class SessionController {
	private SequenceController controller;
	private ControlBar bar;
	private BarListener listener;
	private static final Tuple<Double, Double> HITBOX_X = new Tuple<Double,Double>(ControlBar.START_POSX - ControlBar.BAR_WIDTH,
			ControlBar.START_POSX + ControlBar.BAR_WIDTH*2);
	private static final Tuple<Double, Double> HITBOX_Y = new Tuple<Double,Double>(ControlBar.START_POSY - ControlBar.BAR_HEIGHT/2,
			ControlBar.START_POSY + ControlBar.BAR_WIDTH*1.5);
	private boolean isVisible;
	
	public SessionController(SequenceController controller) {
		this.listener = new BarListener();
		this.isVisible = false;
		this.controller = controller;
		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.SEVERE);
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public void start() {
		addAll();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				bar=new ControlBar(controller);
			}
		});
	}
	
	public void end() {
		removeAll();
	}
	
	private void removeAll() {
		GlobalScreen.removeNativeMouseListener(listener);
		GlobalScreen.removeNativeMouseMotionListener(listener);
	}

	private void addAll() {
		GlobalScreen.addNativeMouseListener(listener);
		GlobalScreen.addNativeMouseMotionListener(listener);
	}
	
	class BarListener implements NativeMouseInputListener {

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
			if (isInTriggerZone(e.getX(), e.getY()) && !isVisible) {
				isVisible = true;
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						bar=new ControlBar(controller);
					}
				});
			}
			if (!isInTriggerZone(e.getX(), e.getY()) && isVisible) {
				bar.fade();
				isVisible = false;
			}
		}
		
		
		private boolean isInTriggerZone(int x, int y) {
			System.out.println("here");
			return	( x > (HITBOX_X.getFirst()) && 
					x < (HITBOX_X.getSecond()) &&
					y < (HITBOX_Y.getFirst()) && 
					y > (HITBOX_Y.getSecond()));
		}
	}
	
	
}
