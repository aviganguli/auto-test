package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Main class running my start screen
 * 
 * @author AvishekGanguli
 *
 */
public class Main {
	private static final String TITLE = "Auto-Test";
	private static final String MAC_SCRIPT_PATH = System.getProperty("user.dir") + "/enableAssistiveApp.scpt";

	/**
	 * main runner
	 * 
	 * @param args
	 *            to be run with (nothing)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String osType = System.getProperty("os.name").toLowerCase();
				if (osType.contains("mac")) {
					try {
						Runtime.getRuntime().exec("osascript " + MAC_SCRIPT_PATH);
					} catch (IOException e) {
						e.printStackTrace();
						throw new IllegalStateException("Scripts failed!");
					}
				}
				JFrame app = new JFrame(TITLE);
				Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
				app.setMinimumSize(new Dimension((int)Math.round(dimension.getWidth()*0.5), (int)Math.round(dimension.getHeight()*.5)));
				final StartScreen start = new StartScreen(app);
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
