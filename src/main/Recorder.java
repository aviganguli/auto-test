package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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

/**
 * 
 * Class which enables/disables recording and playback abilities
 * 
 * @author samuellee and AvishekGanguli
 *
 */
public class Recorder implements SequenceController {
	private Robot robot;
	private ImitatorListener listener;
	private boolean isRecording = false;
	

	/**
	 * Removes logging from JNativeHook and instantiates a new recorder 
	 * with no actions present
	 */
	public Recorder() {
		this.listener = new ImitatorListener();
		try {
			this.robot = new Robot();
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
		//debug_recorder();
	}
	
	/**
	 * 
	 * @param list to be played
	 * 
	 * Constructor to initialize class with a recording from previous runs
	 */
	public Recorder(BlockingArrayList<Tuple<?, ?>> recording) {
		
		this.listener = new ImitatorListener() ;
		this.listener.setRecorded(recording);
		
		try {
			this.robot = new Robot();
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
		//debug_recorder();
	}

	/*private void debug_recorder() {

		addAll();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		removeAll();
		List<Tuple<?, ?>> recorded = listener.getRecorded();
		System.out.println("Play started:" + recorded);
		playback(recorded);
		System.out.println("Play ended" + recorded);
	}*/

	public void start() {
		listener.getRecorded().setBlock(true);
		isRecording = false;
		addAll();
	}
	
	public void play() {
		listener.getRecorded().setBlock(false);
		isRecording = true;
	}
	
	public void stop() {
		isRecording = false;
		removeAll();
	}
	
	public void pause() {
		isRecording = false;
		listener.getRecorded().setBlock(true);
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
		return listener.getRecorded();
	}
	
	private void removeAll() {
		GlobalScreen.removeNativeMouseListener(listener);
		GlobalScreen.removeNativeMouseMotionListener(listener);
		GlobalScreen.removeNativeKeyListener(listener);
		GlobalScreen.removeNativeMouseWheelListener(listener);
	}

	private void addAll() {
		GlobalScreen.addNativeMouseListener(listener);
		GlobalScreen.addNativeMouseMotionListener(listener);
		GlobalScreen.addNativeKeyListener(listener);
		GlobalScreen.addNativeMouseWheelListener(listener);
	}
	

	/**
	 * 
	 * @throws IllegalStateException
	 *             when unsupported keys such as, secondary F3 or secondary F4,
	 *             are typed
	 * 
	 *             Listens for key and mouse actions and records them for
	 *             playback later. Keys such as the secondary functions of the
	 *             function keys (F3, F4, F10...) are not supported and will
	 *             raise an exception when pressed.
	 *
	 */
	class ImitatorListener implements NativeMouseInputListener, NativeKeyListener, NativeMouseWheelListener {
		private BlockingArrayList<Tuple<?, ?>> recorded;

		public ImitatorListener() {
			this.recorded = new BlockingArrayList<>();
		}
	
		@Override
		public void nativeMouseClicked(NativeMouseEvent e) {
			System.out.println("Mouse Clicked: " + e.getClickCount());
		}

		@Override
		public void nativeMousePressed(NativeMouseEvent e) {
			System.out.println("Mouse Pressed: " + e.getButton());
			int button = mouseButtonMatcher(e.getButton());
			recorded.add(new Tuple<Integer, Integer>(MouseEvent.MOUSE_PRESSED, button));
		}

		@Override
		public void nativeMouseReleased(NativeMouseEvent e) {
			System.out.println("Mouse Released: " + e.getButton());
			int button = mouseButtonMatcher(e.getButton());
			recorded.add(new Tuple<Integer, Integer>(MouseEvent.MOUSE_RELEASED, button));
		}

		// stores event type and tuple of (x,y) coordinates i.e (event, (x,y))
		@Override
		public void nativeMouseMoved(NativeMouseEvent e) {
			System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
			recorded.add(new Tuple<Integer, Tuple<Integer, Integer>>(MouseEvent.MOUSE_MOVED,
					new Tuple<Integer, Integer>(e.getX(), e.getY())));
		}

		@Override
		public void nativeMouseDragged(NativeMouseEvent e) {
			// System.out.println("Mouse Dragged: " + e.getX() + ", " +
			// e.getY());
			// recorded.add(new Tuple<Integer,
			// Integer>(MouseEvent.MOUSE_DRAGGED, e.))
		}

		@Override
		public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
			System.out.println(
					"Mouse Wheel: " + e.getWheelRotation() + " " + e.getScrollType() + " " + e.getScrollAmount());
			recorded.add(new Tuple<Integer, Integer>(MouseWheelEvent.MOUSE_WHEEL,
					(e.getWheelRotation() * e.getScrollAmount())));

		}

		/**
		 * 
		 * @throws IllegalStateException
		 *             if key detected is unsupported
		 * 
		 *             Listens for and records typing of capitalized and
		 *             uncapitalized alphabetical characters
		 * 
		 */
		@Override
		public void nativeKeyTyped(NativeKeyEvent e) {
			boolean upperCase = false;
			if (!e.isActionKey()) {
				String charTyped = new Character(e.getKeyChar()).toString();
				if (Character.isUpperCase(charTyped.codePointAt(0))) {
					upperCase = true;
				}
				System.out.println("Key Typed: " + new Integer(e.getKeyChar()) + e.getKeyChar());
				if (charTyped.matches("[A-Za-z]+")) {
					String keyName = "VK_" + charTyped.toUpperCase();
					try {
						int keyCode = KeyEvent.class.getField(keyName).getInt(null);
						if (upperCase) {
							recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT));
						}
						recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, keyCode));
						if (upperCase) {
							recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_RELEASED, KeyEvent.VK_SHIFT));
						}
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException e1) {
						throw new IllegalStateException("Could not parse key event!");
					}
				}
			}
		}

		/**
		 * 
		 * @throws IllegalStateEception
		 *             if key detected is unsupported
		 * 
		 *             Handles all key release events. Certain events are only
		 *             detected in key release.
		 * 
		 */
		@Override
		public void nativeKeyReleased(NativeKeyEvent e) {
			System.out.println("Release Key Raw: " + e.getRawCode() + "CODE IS: " + e.getKeyCode());

			if (!e.isActionKey()) {

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

		/**
		 * 
		 * @throws IllegalStateEception
		 *             if key detected is unsupported i.e Secondary F3 and
		 *             Secondary F4
		 * 
		 *             Handles all key press events.
		 * 
		 */
		@Override
		public void nativeKeyPressed(NativeKeyEvent e) {
			System.out.println("Key pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
			System.out.println("Key pressed: " + e.getRawCode() + " " + e.getKeyCode());
			if (!e.isActionKey() && (!NativeKeyEvent.getKeyText(e.getKeyCode()).matches("[A-Za-z]+")
					|| (NativeKeyEvent.getKeyText(e.getKeyCode()).length() > 1))) {
				// if not action key and is not an alphabet
				recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, keyCodeMatcher(e.getKeyCode())));
			}

			if (e.isActionKey()) {
				recorded.add(new Tuple<Integer, Integer>(KeyEvent.KEY_PRESSED, keyCodeMatcher(e.getKeyCode())));
			}
		}

		/**
		 * 
		 * @param key
		 *            code of key event
		 * @return the respective VK code used by Robot
		 * @throws IllegalArgumentException
		 *             if key code is not supported
		 * 
		 *             Parses key code into robot key code
		 * 
		 */
		private int keyCodeMatcher(int key) {
			switch (key) {
			case 59:
				return KeyEvent.VK_F1;
			case 60:
				return KeyEvent.VK_F2;
			case 61:
				return KeyEvent.VK_F3;
			case 62:
				return KeyEvent.VK_F4;
			case 63:
				return KeyEvent.VK_F5;
			case 64:
				return KeyEvent.VK_F6;
			case 65:
				return KeyEvent.VK_F7;
			case 66:
				return KeyEvent.VK_F8;
			case 67:
				return KeyEvent.VK_F9;
			case 68:
				return KeyEvent.VK_F10;
			case 87:
				return KeyEvent.VK_F11;
			case 88:
				return KeyEvent.VK_F12;

			// secondary functions
			/*
			 * case 57360 : return KeyEvent.VK_F7 ; case 57378 : return
			 * KeyEvent.VK_F8 ; case 57369 : return KeyEvent.VK_F9 ; case 57376
			 * : return 74 ;//KeyEvent.VK_F10 Mute ; 181 case 57390 : return 72
			 * ; //KeyEvent.VK_F11 vol down; 182 case 57392 : return 73
			 * ;//KeyEvent.VK_F12 vol up; 183
			 */
			case 57421:
				return KeyEvent.VK_RIGHT;
			case 57424:
				return KeyEvent.VK_DOWN;
			case 57419:
				return KeyEvent.VK_LEFT;
			case 57416:
				return KeyEvent.VK_UP;
			// case 58 : return KeyEvent.VK_CAPS_LOCK ;
			case 3675:
				return KeyEvent.VK_META;
			case 3676:
				return KeyEvent.VK_META;
			case 56:
				return KeyEvent.VK_ALT;
			case 3640:
				return KeyEvent.VK_ALT;
			case 29:
				return KeyEvent.VK_CONTROL;
			case 42:
				return KeyEvent.VK_SHIFT;
			case 54:
				return KeyEvent.VK_SHIFT;
			case 2:
				return KeyEvent.VK_1;
			case 3:
				return KeyEvent.VK_2;
			case 4:
				return KeyEvent.VK_3;
			case 5:
				return KeyEvent.VK_4;
			case 6:
				return KeyEvent.VK_5;
			case 7:
				return KeyEvent.VK_6;
			case 8:
				return KeyEvent.VK_7;
			case 9:
				return KeyEvent.VK_8;
			case 10:
				return KeyEvent.VK_9;
			case 11:
				return KeyEvent.VK_0;
			case 12:
				return KeyEvent.VK_MINUS;
			case 13:
				return KeyEvent.VK_EQUALS;
			case 39:
				return KeyEvent.VK_SEMICOLON;
			case 40:
				return KeyEvent.VK_QUOTE;
			case 51:
				return KeyEvent.VK_COMMA;
			case 52:
				return KeyEvent.VK_PERIOD;
			case 53:
				return KeyEvent.VK_SLASH;
			case 41:
				return KeyEvent.VK_BACK_QUOTE;
			case 26:
				return KeyEvent.VK_OPEN_BRACKET;
			case 27:
				return KeyEvent.VK_CLOSE_BRACKET;
			case 43:
				return KeyEvent.VK_BACK_SLASH;
			case 57:
				return KeyEvent.VK_SPACE;
			case 1:
				return KeyEvent.VK_ESCAPE;
			case 15:
				return KeyEvent.VK_TAB;
			case 14:
				return KeyEvent.VK_BACK_SPACE;
			case 28:
				return KeyEvent.VK_ENTER;
			default:
				throw new IllegalStateException("Could not recognize key with key code: " + key);
			}
		}

		/**
		 * 
		 * @param mouse
		 *            code of mouse event
		 * @return the respective VK code used by Robot
		 * @throws IllegalArgumentException
		 *             if mouse code is not supported
		 * 
		 *             Parses mouse code into robot mouse code
		 * 
		 */
		private int mouseButtonMatcher(int button) {
			switch (button) {
			case 1:
				return MouseEvent.BUTTON1_DOWN_MASK;
			case 2:
				return MouseEvent.BUTTON2_DOWN_MASK;
			case 3:
				return MouseEvent.BUTTON3_DOWN_MASK;
			default:
				throw new IllegalStateException("Mouse Button: " + button + " is not valid");
			}

		}
		
		public BlockingArrayList<Tuple<?, ?>> getRecorded() {
			return recorded;
		}
		
		public void setRecorded(BlockingArrayList <Tuple<?, ?>> recording) {
			if(this.recorded != null) throw new IllegalStateException("Recorded should be null") ;
			this.recorded = recording ;
			return ;
		}

	}

}
