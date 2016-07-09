package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

import main.Log;
import main.StreamRedirector;
import main.WindowManager;

/**
 * 
 * @author samuellee & Strawhat Jedi
 *
 */
public class StartScreen extends JPanel {
	/**
	 * Boilerplate
	 */
	private static final long serialVersionUID = 1L;
	private JFrame startFrame;
	private final String ADD_MENU_TITLE = "Add";
	private final String ADD_APP_ITEM = "New Application";
	private final String RECENT_APP_ITEM = "Recent Applications";
	private final Log recentLog;
	
	/**
	 * Constructor for all components
	 * @param gameFrame frame game runs in
	 */
	StartScreen(JFrame gameFrame) {
		this.recentLog = Log.RECENT;
		this.startFrame = gameFrame;
		JPanel startPanel = new JPanel();
		//Build the first menu.
		JMenu addMenu = new JMenu(ADD_MENU_TITLE);
		addMenu.setMnemonic(KeyEvent.VK_A);
		addMenu.getAccessibleContext().setAccessibleDescription(
		        "Adds applications to test");
		final JMenuItem addApp = new JMenuItem(ADD_APP_ITEM,KeyEvent.VK_N);
		addApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		addApp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileFilter() {
					private final String JAR_EXT="jar";
					/**
					 * 
					 * @param fileName name of file to be filtered
					 * @return string representing extension
					 */
				    private String getExtension(String fileName) {
				        String ext = null;
				        int i = fileName.lastIndexOf('.');

				        if (i > 0 &&  i < fileName.length() - 1) {
				            ext = fileName.substring(i+1).toLowerCase();
				        }
				        return ext;
				    }
				    
					private boolean isJAR(String fileName) {
						String ext = getExtension(fileName);
						return (ext==null) ? false : ext.equals(JAR_EXT);
					}
					
					@Override
					public String getDescription() {
						return "Please select a JAR file.";
					}
					
					@Override
					public boolean accept(File file) {
						return isJAR(file.getName());
					}
				});
			    int returnVal = fileChooser.showOpenDialog(addApp);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fileChooser.getSelectedFile();
			            recentLog.addToLog(file.getAbsolutePath());
			            startProgram(file.getPath());
			            
			    } else if (returnVal==JFileChooser.CANCEL_OPTION) {
			    	JOptionPane.showMessageDialog(startFrame, "Please select JAR file.");
			   }
			}
		});
		final JMenu recentApps = new JMenu(RECENT_APP_ITEM);
		recentApps.setMnemonic(KeyEvent.VK_R);
		recentApps.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				List <String> recentPaths = recentLog.readFromLog();
				for (String path : recentPaths) {
					final JMenuItem recent = new JMenuItem(path);
					recent.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							startProgram(recent.getText());
						}
					});
					recentApps.add(recent);
				}
				
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				recentApps.removeAll();
				
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				// do nothing
				
			}
		});
		addMenu.add(addApp);
		addMenu.add(recentApps);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(addMenu);
		startFrame.setJMenuBar(menuBar);
		startPanel.validate();
		startPanel.setVisible(true);
	}
	
	/**
	 * function that takes in the name of a jar file and runs it while 
	 * redirecting its standard out and error
	 * @param executableName the name of the file to be run
	 */
	private void startProgram(String execeutableName) {
		// Run a java app in a separate system process
        Process proc;
		try {
			proc = Runtime.getRuntime().exec("java -jar " + execeutableName);
			try {
				proc.waitFor(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new IllegalStateException("Script setup error!");
			}
		}	catch (IOException e1) {
				e1.printStackTrace();
				throw new IllegalStateException("Application has the above error");
		}
		WindowManager.execute();
        // Then retrieve the process output
        StreamRedirector in = new StreamRedirector(proc.getInputStream(), System.out);
        StreamRedirector err = new StreamRedirector(proc.getErrorStream(), System.err);
        in.start();
        err.start();
	}
}
