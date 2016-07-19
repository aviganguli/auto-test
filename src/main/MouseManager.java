package main;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class MouseManager {

	public MouseManager() {
		return;
	}

	public static void trackMouse() {
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
		ImitatorListener listener = new ImitatorListener();
		GlobalScreen.addNativeMouseListener(listener);
		GlobalScreen.addNativeMouseMotionListener(listener);
	}

	static class ImitatorListener implements NativeMouseInputListener {
		public void nativeMouseClicked(NativeMouseEvent e) {
			System.out.println("Mouse Clicked: " + e.getClickCount());
		}

		public void nativeMousePressed(NativeMouseEvent e) {
			System.out.println("Mouse Pressed: " + e.getButton());
		}

		public void nativeMouseReleased(NativeMouseEvent e) {
			System.out.println("Mouse Released: " + e.getButton());
		}

		public void nativeMouseMoved(NativeMouseEvent e) {
			System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
		}

		public void nativeMouseDragged(NativeMouseEvent e) {
			System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
		}

	}

}
