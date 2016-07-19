package gui;


import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Main class running my start screen
 * @author StrawHatJedi
 *
 */
public class Main {
	private static final String TITLE = "Auto-Test";
	private static final String MAC_SCRIPT_PATH = System.getProperty("user.dir") +
			"/enableAssistiveApp.scpt" ;
	
	/**
	 * main runner
	 * @param args to be run with (nothing)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Runtime.getRuntime().exec("osascript " + MAC_SCRIPT_PATH);
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalStateException("Scripts failed!");
				}
				JFrame app = new JFrame(TITLE);
				final StartScreen start= new StartScreen(app);
				start.setOpaque(true);
				app.add(start);
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				app.validate();
				app.pack();
				app.setVisible(true);
				app.setResizable(true);	
			}
		});
	}

}


