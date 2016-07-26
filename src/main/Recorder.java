package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

public class Recorder {
	private static List<Tuple<?,?>> recorded;
	private static Robot robot;
	private static ImitatorListener listener;
	
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
		this.listener = new ImitatorListener();
		addAll();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		removeAll();
		System.out.println("Play started:" + recorded);
		playback();
		System.out.println("Play ended" + recorded);
	}

	
		static class ImitatorListener implements NativeMouseInputListener, NativeKeyListener, NativeMouseWheelListener {
			List<Tuple<?,?>> recorded =  Recorder.recorded;
			Robot robot = Recorder.robot;
			
			@Override
			public void nativeMouseClicked(NativeMouseEvent e) {
				System.out.println("Mouse Clicked: " + e.getClickCount());
			} 
			
			@Override
			public void nativeMousePressed(NativeMouseEvent e) {
				System.out.println("Mouse Pressed: " + e.getButton());
				int button = mouseButtonMatcher(e.getButton()) ;
				recorded.add(new Tuple<Integer, Integer>(MouseEvent.MOUSE_PRESSED, button)) ;
			}
			
			@Override
			public void nativeMouseReleased(NativeMouseEvent e) {
				System.out.println("Mouse Released: " + e.getButton());
				int button = mouseButtonMatcher(e.getButton()) ;
				recorded.add(new Tuple<Integer, Integer>(MouseEvent.MOUSE_RELEASED, button)) ;			
			}
			
			//stores event type and tuple of (x,y) coordinates i.e (event, (x,y)) 
			@Override
			public void nativeMouseMoved(NativeMouseEvent e) {
				System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
				recorded.add(new Tuple<Integer, Tuple<Integer, Integer>>(MouseEvent.MOUSE_MOVED, 
						new Tuple<Integer, Integer>(e.getX(), e.getY())));
			}
			
			@Override
			public void nativeMouseDragged(NativeMouseEvent e) {
				//System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
				//recorded.add(new Tuple<Integer, Integer>(MouseEvent.MOUSE_DRAGGED, e.))
			}
			
			@Override
			public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
				System.out.println("Mouse Wheel: " + e.getWheelRotation() + " " + e.getScrollType() + " " + e.getScrollAmount());
				recorded.add(new Tuple<Integer, Integer>(MouseWheelEvent.MOUSE_WHEEL, 
						(e.getWheelRotation() * e.getScrollAmount()))) ;
				
			}
			
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {
				boolean upperCase = false ;
				if (!e.isActionKey())  {
					String charTyped = new Character(e.getKeyChar()).toString();
					if (Character.isUpperCase(charTyped.codePointAt(0))) {
						upperCase = true ;
					}
					System.out.println("Key Typed: " + new Integer(e.getKeyChar()) + e.getKeyChar());
					if (charTyped.matches("[A-Za-z]+")) {
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
						(KeyEvent.KEY_PRESSED, keyRawMatcher(e.getRawCode())));
					}
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				System.out.println("Release Key Raw: " + e.getRawCode());
				if (isOnlyRelease(e.getRawCode())) {
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, keyRawMatcher(e.getRawCode())));
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
					recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, keyRawMatcher(e.getRawCode())));
					return;
				}

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
				System.out.println("Key pressed: " + e.getRawCode());
				if (e.isActionKey() || (e.getRawCode() == 160 || e.getRawCode() == 131))  {
					//or if F3 and F4
					if (checkUndefined(e.getRawCode())) {
						throw new IllegalStateException("Could not recognize key with raw code: " 
								+ e.getRawCode());
					}
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, keyRawMatcher(e.getRawCode())));
				}
			}

		}
	
		
		static public int keyRawMatcher(int key) {
			switch (key) {
			case 160 : return KeyEvent.VK_F3 ;
			case 131 : return KeyEvent.VK_F4 ;
			case 242 : return KeyEvent.VK_F7 ;
			case 240 : return KeyEvent.VK_F8 ;
			case 241 : return KeyEvent.VK_F9 ;
			case 74 : return KeyEvent.VK_F10 ;
			case 73 : return KeyEvent.VK_F11;
			case 72 : return KeyEvent.VK_F12 ;
			case 124 : return KeyEvent.VK_RIGHT ;
			case 125 : return KeyEvent.VK_DOWN ;
			case 123 : return KeyEvent.VK_LEFT ;
			case 126 : return KeyEvent.VK_UP ;
			//case 57 : return KeyEvent.VK_CAPS_LOCK ;
			case 55 : return KeyEvent.VK_META ;
			case 54 : return KeyEvent.VK_META ;
			case 61 : return KeyEvent.VK_ALT ;
			case 58 : return KeyEvent.VK_ALT ;
			case 59 : return KeyEvent.VK_CONTROL ;
			case 56 : return KeyEvent.VK_SHIFT ;
			case 60 : return KeyEvent.VK_SHIFT ;
			case 18 : return KeyEvent.VK_1 ;
			case 19 : return KeyEvent.VK_2 ;
			case 20 : return KeyEvent.VK_3 ;
			case 21 : return KeyEvent.VK_4 ;
			case 23 : return KeyEvent.VK_5 ;
			case 22 : return KeyEvent.VK_6 ;
			case 26 : return KeyEvent.VK_7 ;
			case 28 : return KeyEvent.VK_8 ;
			case 25 : return KeyEvent.VK_9 ;
			case 29 : return KeyEvent.VK_0 ;
			case 27 : return KeyEvent.VK_MINUS ; 
			case 24 : return KeyEvent.VK_EQUALS ;
			case 41 : return KeyEvent.VK_SEMICOLON ;
			case 39 : return KeyEvent.VK_QUOTE ;
			case 43 : return KeyEvent.VK_COMMA ;
			case 47 : return KeyEvent.VK_PERIOD ;
			case 44 : return KeyEvent.VK_SLASH ;
			case 50 : return KeyEvent.VK_BACK_QUOTE ;
			case 33 : return KeyEvent.VK_OPEN_BRACKET ;
			case 30 : return KeyEvent.VK_CLOSE_BRACKET ;
			case 42 : return KeyEvent.VK_BACK_SLASH ;
			case 49 : return KeyEvent.VK_SPACE ;
			case 53 : return KeyEvent.VK_ESCAPE ;
			case 48 : return KeyEvent.VK_TAB ;
			case 51 : return KeyEvent.VK_BACK_SPACE ;
			case 36 : return KeyEvent.VK_ENTER ;
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
		
		public static int mouseButtonMatcher(int button) {
			switch (button) {
			case 1 : return MouseEvent.BUTTON1_DOWN_MASK ; 
			case 2 : return MouseEvent.BUTTON2_DOWN_MASK ;
			case 3 : return MouseEvent.BUTTON3_DOWN_MASK ;
			default : throw new IllegalStateException("Mouse Button: " + button + " is not valid") ;
				
			}
			
		}
		
		
		static boolean checkUndefined(int rawCode) {
			return (keyRawMatcher(rawCode) == KeyEvent.CHAR_UNDEFINED) ;
		}
		
		public void playback() {
			
			for(Tuple<?,?> tuple : recorded) {
				Integer event = (Integer) tuple.getFirst() ;
				if (event == MouseEvent.MOUSE_MOVED) {
					//second element must be tuple as it is stored in that way
					@SuppressWarnings("unchecked")
					Tuple<Integer,Integer> info = (Tuple<Integer, Integer>) tuple.getSecond();
					robot.mouseMove(info.getFirst(), info.getSecond());
					continue;
				}
				Integer info = (Integer) tuple.getSecond();
				switch (event) {
					case MouseEvent.MOUSE_PRESSED : 
						robot.mousePress(info);
						robot.delay(100);
						System.out.println(tuple);
						break;
					case MouseEvent.MOUSE_RELEASED : 
						robot.mouseRelease(info);
						robot.delay(100);
						System.out.println(tuple);
						break;
					case MouseEvent.MOUSE_WHEEL : 
						robot.mouseWheel(info);
						robot.delay(100);
						System.out.println(tuple);
						break;
					case KeyEvent.KEY_PRESSED : 
						robot.keyPress(info);
						robot.delay(100);
						System.out.println(tuple);
						break;
					case KeyEvent.KEY_RELEASED : 
						robot.keyRelease(info);
						robot.delay(100);
						System.out.println(tuple);
						break;
					default : throw new IllegalStateException("Event code : "  + event + " with info : " + info 
							+ " not found!");
				}
			}
		}
		
		public static void removeAll() {
			GlobalScreen.removeNativeMouseListener(listener);
			GlobalScreen.removeNativeMouseMotionListener(listener);
			GlobalScreen.removeNativeKeyListener(listener);
			GlobalScreen.removeNativeMouseWheelListener(listener);
		}
		
		public static void addAll() {
			GlobalScreen.addNativeMouseListener(listener);
			GlobalScreen.addNativeMouseMotionListener(listener);
			GlobalScreen.addNativeKeyListener(listener);
			GlobalScreen.addNativeMouseWheelListener(listener);
		}
}
		
	
	
