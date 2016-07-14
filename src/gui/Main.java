package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
				final StartScreen start= new StartScreen(app);
				start.setOpaque(true);
				app.add(start);
				app.addWindowListener(new WindowListener() {
					
					@Override
					public void windowOpened(WindowEvent e) {
						
					}
					
					@Override
					public void windowIconified(WindowEvent e) {

					}
					
					@Override
					public void windowDeiconified(WindowEvent e) {
						
					}
					
					@Override
					public void windowDeactivated(WindowEvent e) {
						
					}
					
					@Override
					public void windowClosing(WindowEvent e) {
						start.updateOnClose(); //update jar and delete rLog on close
						//does not work using ALT F4, must used key listener
						return ;
						
					}
					
					@Override
					public void windowClosed(WindowEvent e) {
						
					}
					
					@Override
					public void windowActivated(WindowEvent e) {
						
					}
				});
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				app.validate();
				app.pack();
				app.setVisible(true);
				app.setResizable(true);			
			}
		});
	}

}


