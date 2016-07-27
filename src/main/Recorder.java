package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Desktop.Action;
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
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				System.out.println("Release Key Raw: " + e.getRawCode() + "CODE IS: " + e.getKeyCode());
				if (isOnlyRelease(e.getRawCode())) {
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, keyCodeMatcher(e.getKeyCode())));
				}
				//TODO: change following for key code
				if (!e.isActionKey() ) {
					if(e.getKeyCode() == 0) {
						//handles F3 and F4
						recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, 
								specialKeyMatcher(e.getRawCode())));
						return ;
					}
					String charTyped = NativeKeyEvent.getKeyText(e.getKeyCode());
					System.out.println("Char typed: " + charTyped);
					if (charTyped.matches("[A-Za-z]+")) {
						String keyName = "VK_" + charTyped.toUpperCase();
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
				}
				recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, keyCodeMatcher(e.getKeyCode())));
				return;
			}

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
				System.out.println("Key pressed: " + e.getRawCode() + " " + e.getKeyCode());
				if (!e.isActionKey() && (!NativeKeyEvent.getKeyText(e.getKeyCode()).matches("[A-Za-z]+") || (NativeKeyEvent.getKeyText(e.getKeyCode()).length() > 1)) ) {
					//if not action key and is not an alphabet
					recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, keyCodeMatcher(e.getKeyCode())));
				}
				//TODO: change following for key code
				if (e.isActionKey() || (e.getRawCode() == 160 || e.getRawCode() == 131))  {
					//or if F3 and F4
					if (checkUndefined(e.getKeyCode())) {
						throw new IllegalStateException("Could not recognize key with key code: " 
								+ e.getKeyCode());
					}
					//if e is F3 or F4 use specialKeyMatcher
					int vk_code = (e.getKeyCode() == 0) ? specialKeyMatcher(e.getRawCode()) : keyCodeMatcher(e.getKeyCode());
					recorded.add(new Tuple<Integer, Integer>
					(KeyEvent.KEY_PRESSED, vk_code));
				}
			}

		}
	
		//Uses raw code to match keys 
		static public int specialKeyMatcher(int rawCode) {
			switch(rawCode) {
				case 160 : return KeyEvent.VK_F3 ;
				case 131 : return KeyEvent.VK_F4 ;
				default : throw new IllegalStateException("Shouldn't happen raw code: " + rawCode) ;
			}
		}
		static public int keyCodeMatcher(int key) {
			switch (key) {
			//ignores F3 and F4 as they have same undefined keyCode
			case 59 : return KeyEvent.VK_F1 ; //fn + F1 on mac
			case 60 : return KeyEvent.VK_F2 ; 
			case 61 : return KeyEvent.VK_F3 ;
			case 62 : return KeyEvent.VK_F4 ;
			case 63 : return KeyEvent.VK_F5 ;
			case 64 : return KeyEvent.VK_F6 ;
			case 65 : return KeyEvent.VK_F7 ;
			case 66 : return KeyEvent.VK_F8 ;
			case 67 : return KeyEvent.VK_F9 ;
			case 68 : return KeyEvent.VK_F10 ;
			case 87 : return KeyEvent.VK_F11 ;
			case 88 : return KeyEvent.VK_F12 ;
			
			// secondary functions 
			/*case 57360 : return KeyEvent.VK_F7 ;
			case 57378 : return KeyEvent.VK_F8 ;
			case 57369 : return KeyEvent.VK_F9 ;
			case 57376 : return 74 ;//KeyEvent.VK_F10 Mute ; 181
			case 57390 : return 72 ; //KeyEvent.VK_F11 vol down; 182
			case 57392 : return 73 ;//KeyEvent.VK_F12 vol up; 183
*/			
			case 57421 : return KeyEvent.VK_RIGHT ;
			case 57424 : return KeyEvent.VK_DOWN ;
			case 57419 : return KeyEvent.VK_LEFT ;
			case 57416 : return KeyEvent.VK_UP ;
			//case 58 : return KeyEvent.VK_CAPS_LOCK ;
			case 3675 : return KeyEvent.VK_META ;
			case 3676 : return KeyEvent.VK_META ; 
			case 56 : return KeyEvent.VK_ALT ;
			case 3640 : return KeyEvent.VK_ALT ; 
			case 29 : return KeyEvent.VK_CONTROL ;
			case 42 : return KeyEvent.VK_SHIFT ;
			case 54 : return KeyEvent.VK_SHIFT ;
			case 2 : return KeyEvent.VK_1 ;
			case 3 : return KeyEvent.VK_2 ;
			case 4 : return KeyEvent.VK_3 ;
			case 5 : return KeyEvent.VK_4 ;
			case 6 : return KeyEvent.VK_5 ;
			case 7 : return KeyEvent.VK_6 ;
			case 8 : return KeyEvent.VK_7 ;
			case 9 : return KeyEvent.VK_8 ;
			case 10 : return KeyEvent.VK_9 ;
			case 11 : return KeyEvent.VK_0 ;
			case 12 : return KeyEvent.VK_MINUS ; 
			case 13 : return KeyEvent.VK_EQUALS ;
			case 39 : return KeyEvent.VK_SEMICOLON ;
			case 40 : return KeyEvent.VK_QUOTE ;
			case 51 : return KeyEvent.VK_COMMA ;
			case 52 : return KeyEvent.VK_PERIOD ;
			case 53 : return KeyEvent.VK_SLASH ;
			case 41 : return KeyEvent.VK_BACK_QUOTE ;
			case 26 : return KeyEvent.VK_OPEN_BRACKET ;
			case 27 : return KeyEvent.VK_CLOSE_BRACKET ;
			case 43 : return KeyEvent.VK_BACK_SLASH ;
			case 57 : return KeyEvent.VK_SPACE ;
			case 1 : return KeyEvent.VK_ESCAPE ;
			case 15 : return KeyEvent.VK_TAB ;
			case 14 : return KeyEvent.VK_BACK_SPACE ;
			case 28 : return KeyEvent.VK_ENTER ;
			default : return KeyEvent.CHAR_UNDEFINED;
			}
		}
		
		static boolean isOnlyRelease(int key) {
			switch (key) {
				case 57360 : return true ;
				case 57378 : return true ;
				case 57369 : return true ;
				case 57376 : return true ;
				case 57390 : return true ;
				case 57392 : return true ;
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
		
		
		static boolean checkUndefined(int keyCode) {
			return (keyCodeMatcher(keyCode) == KeyEvent.CHAR_UNDEFINED) && (keyCode != 0) ;
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
						System.out.println(tuple);
						robot.mousePress(info);
						robot.delay(100);
						break;
					case MouseEvent.MOUSE_RELEASED :
						System.out.println(tuple);
						robot.mouseRelease(info);
						robot.delay(100);
						break;
					case MouseEvent.MOUSE_WHEEL : 
						System.out.println(tuple);
						robot.mouseWheel(info);
						robot.delay(100);
						break;
					case KeyEvent.KEY_PRESSED :
						System.out.println(tuple);
						robot.keyPress(info);
						robot.delay(100);
						break;
					case KeyEvent.KEY_RELEASED :
						System.out.println(tuple);
						robot.keyRelease(info);
						robot.delay(100);
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
		
	
	
