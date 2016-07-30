package gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import main.Recorder;
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
	private final String PLAY_TOOLTIP = "Play recorded actions";
	private final String PAUSE_TOOLTIP = "Pause recorded actions";
	private final String STOP_TOOLTIP = "Stop recorded actions";
	public final Log recentLog;
	private Recorder recorder ;
	
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
			
			/* 
			 Handles file choosing and parsing
			 */
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
			
			/*
			 * Handles starting previously opened applications 
			 */
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
	
	
	private void populateButtons() {
		JButton playButton = new JButton();
		JButton pauseButton = new JButton();
		JButton stopButton = new JButton();
		try {
			playButton.setIcon(new ImageIcon(
					ImageIO.read(getClass().getResource("Icons/play.png"))));
			pauseButton.setIcon(new ImageIcon(
					ImageIO.read(getClass().getResource("Icons/pause.png"))));
			stopButton.setIcon(new ImageIcon(
					ImageIO.read(getClass().getResource("Icons/stop.png"))));
		} catch (IOException e) {
			throw new IllegalStateException("Could not populate buttons with images!");
		}
		playButton.setToolTipText(PLAY_TOOLTIP);
		pauseButton.setToolTipText(PAUSE_TOOLTIP);
		stopButton.setToolTipText(STOP_TOOLTIP);
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
	}
	
	
	/**
	 * Takes in the name of a jar file and runs it while 
	 * redirecting its standard out and error
	 * @param executableName the name of the file to be run
	 */
	private void startProgram(String executableName) {
		// Run a java app in a separate system process
        Process proc;
        String os_type = System.getProperty("os.name").toLowerCase() ;
        if(os_type.contains("windows")) executableName = "\"" + executableName + "\"" ;
        
		try {
			proc = Runtime.getRuntime().exec("java -jar " + executableName);
			try {
				proc.waitFor(3, TimeUnit.SECONDS); 
				//should handle case where user switches window
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new IllegalStateException("Script setup error!");
			}
		}	catch (IOException e1) {
				e1.printStackTrace();
				throw new IllegalStateException("Application has the above error");
		}
		WindowManager.execute();
		recorder = new Recorder(); //NEED TO CHANGE
        // Then retrieve the process output
        StreamRedirector in = new StreamRedirector(proc.getInputStream(), System.out);
        StreamRedirector err = new StreamRedirector(proc.getErrorStream(), System.err);
        in.start();
        err.start();
	}
	
	/*public void updateOnClose() {
		recentLog.updateJar(); 	
	}*/
	
}
