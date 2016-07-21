package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class Recorder {
	private static List<Tuple<Integer,Integer>> recorded;
	private static Robot robot;
	
	public Recorder() {
		recorded = new ArrayList<>();
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new IllegalArgumentException("Shouldn't happen! Robot gone wrong!");
		}
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
		GlobalScreen.addNativeKeyListener(listener);
	}
	

		static class ImitatorListener implements NativeMouseInputListener, NativeKeyListener {
			List<Tuple<Integer,Integer>> recorded =  Recorder.recorded;
			Robot robot = Recorder.robot;
			
			@Override
			public void nativeMouseClicked(NativeMouseEvent e) {
				System.out.println("Mouse Clicked: " + e.getClickCount());
			} 
			
			@Override
			public void nativeMousePressed(NativeMouseEvent e) {
				System.out.println("Mouse Pressed: " + e.getButton());
			}
			
			@Override
			public void nativeMouseReleased(NativeMouseEvent e) {
				
			}
			
			@Override
			public void nativeMouseMoved(NativeMouseEvent e) {
				System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
			}
			
			@Override
			public void nativeMouseDragged(NativeMouseEvent e) {
				System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
			}

			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {
				boolean upperCase = false ;
				if (!e.isActionKey())  {
					String charTyped = new Character(e.getKeyChar()).toString();
					if (Character.isUpperCase(charTyped.codePointAt(0))) {
						upperCase = true ;
					}
					System.out.println("Key Typed: " + charTyped);
					if (charTyped.matches("[A-Za-z0-9]+")) {
						String keyName = "VK_" + charTyped.toUpperCase();
			            try {
							int keyCode = KeyEvent.class.getField(keyName).getInt(null);
							if (upperCase) {
								recorded.add(new Tuple<Integer, Integer>
								(KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT));
							}
							recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, keyCode));
							if (upperCase) {
								recorded.add(new Tuple<Integer, Integer>
								(KeyEvent.KEY_RELEASED, KeyEvent.VK_SHIFT));
							}
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e1) {
							throw new IllegalStateException("Could not parse key event!");
						}
					}
					else {
						recorded.add(new Tuple<Integer, Integer>
						(KeyEvent.KEY_PRESSED, keyMatch(e.getRawCode())));
					}
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				System.out.println("Release Key Raw: " + e.getRawCode());
				if (isOnlyRelease(e.getRawCode())) {
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, keyMatch(e.getRawCode())));
				}
				if (checkUndefined(e.getRawCode())) {
					String keyName = "VK_" + NativeKeyEvent.getKeyText(e.getKeyCode()).toUpperCase();
					int keyCode;
					try {
						keyCode = KeyEvent.class.getField(keyName).getInt(null);
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException e1) {
						throw new IllegalStateException("Could not parse key event!");
					}
					recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, keyCode));
					return;
				}
					recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, keyMatch(e.getRawCode())));
					return;
				}

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
				System.out.println("Key pressed: " + e.getRawCode());
				if (e.isActionKey() || (e.getRawCode() == 160 || e.getRawCode() == 131))  {
					if (checkUndefined(e.getRawCode())) {
						System.err.println("Could not recognize key");
						return;
					}
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, keyMatch(e.getRawCode())));
				}
			}
		}
	
		
		static public int keyMatch(int key) {
			switch (key) {
			case 160 : return KeyEvent.VK_F3 ;
			case 131 : return KeyEvent.VK_F4 ;
			case 242 : return KeyEvent.VK_F7 ;
			case 240 : return KeyEvent.VK_F8 ;
			case 241 : return KeyEvent.VK_F9 ;
			case 74 : return KeyEvent.VK_F10 ;
			case 73 : return KeyEvent.VK_F11;
			case 72 : return KeyEvent.VK_F12 ;
			case 36 : return KeyEvent.VK_ENTER ;
			case 48 : return KeyEvent.VK_TAB ;
			case 124 : return KeyEvent.VK_RIGHT ;
			case 125 : return KeyEvent.VK_DOWN ;
			case 123 : return KeyEvent.VK_LEFT ;
			case 126 : return KeyEvent.VK_UP ;
			//case 57 : return KeyEvent.VK_CAPS_LOCK ;
			case 53 : return KeyEvent.VK_ESCAPE ;
			case 51 : return KeyEvent.VK_BACK_SPACE ;
			case 55 : return KeyEvent.VK_META ;
			case 54 : return KeyEvent.VK_META ;
			case 61 : return KeyEvent.VK_ALT ;
			case 58 : return KeyEvent.VK_ALT ;
			case 59 : return KeyEvent.VK_CONTROL ;
			case 56 : return KeyEvent.VK_SHIFT ;
			case 60 : return KeyEvent.VK_SHIFT ;
			case 49 : return KeyEvent.VK_SPACE ;
			case 43 : return KeyEvent.VK_COMMA ;
			case 47 : return KeyEvent.VK_PERIOD ;
			case 44 : return KeyEvent.VK_SLASH ;
			case 39 : return KeyEvent.VK_QUOTE ;
			case 41 : return KeyEvent.VK_SEMICOLON ;
			case 42 : return KeyEvent.VK_BACK_SLASH ;
			case 30 : return KeyEvent.VK_CLOSE_BRACKET ;
			case 33 : return KeyEvent.VK_OPEN_BRACKET ;
			case 24 : return KeyEvent.VK_EQUALS ;
			case 27 : return KeyEvent.VK_MINUS ;
			case 50 : return KeyEvent.VK_BACK_QUOTE;
			default : return KeyEvent.CHAR_UNDEFINED;
			}
		}
	
		
		static boolean isOnlyRelease(int key) {
			switch (key) {
				case 242 : return true ;
				case 240 : return true ;
				case 241 : return true ;
				case 74 : return true ;
				case 73 : return true ;
				case 72 : return true ;
				default : return false ;
			}
		}
		
		
		static boolean checkUndefined(int rawCode) {
			return (keyMatch(rawCode) == KeyEvent.CHAR_UNDEFINED) ;
		}
}
		
	
	
