package gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main class running my start screen
 * @author StrawHatJedi
 *
 */
public class Main {
	private static final String TITLE = "Auto-Test";
	public static final String PATH =  System.getProperty("user.dir") +
			"/resources/" ;
	
	/**
	 * main runner
	 * @param args to be run with (nothing)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame app = new JFrame(TITLE);
				StartScreen start= new StartScreen(app);
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


