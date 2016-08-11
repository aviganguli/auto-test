package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.JTextAreaOutputStream;
import main.Log;
import main.Recorder;
import main.SessionController;
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
	private final String RECORD_TOOLTIP = "Begin recording the selected file";
	private final String PLAY_TOOLTIP = "Begin playing the selected recording";
	private static Log recentLog;
	private boolean isRecording;
	private String selectedFile;
	private ExecutorService executorService;
	private boolean isSelected;
	private JTextArea errorBox;
	private JTextPane playText;
	private JTextPane recordText;
	
	
	/**
	 * Constructor for all components
	 * @param appFrame frame app runs in
	 */
	StartScreen(JFrame appFrame) {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.recentLog = Log.RECENT;
		this.startFrame = appFrame;
		this.isSelected = false;
		setLayout(new BorderLayout());
		this.playText = new JTextPane();
		this.recordText = new JTextPane();
		setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		playText.setBorder(BorderFactory.createEmptyBorder());
		recordText.setBorder(BorderFactory.createEmptyBorder());
		playText.setOpaque(false);
		recordText.setOpaque(false);
		playText.setEditable(false);
		recordText.setEditable(false);
		playText.setFocusable(false);
		recordText.setFocusable(false);
		StyledDocument doc = playText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		doc = recordText.getStyledDocument();
		center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
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
				FileSelect fileSelect = new FileSelect(addApp);
				fileSelect.listen(e);		
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
							selectedFile=recent.getText();
							recentLog.addToLog(recent.getText());
							isRecording = true;
							isSelected = true;
							recordText.setText(selectedFile);
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
		
		createErrorDisplay(); //creates error display box
		createModeButtons() ; //creates play and record buttons in startScreen panel
		addMenu.add(addApp);
		addMenu.add(recentApps);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(addMenu);
		startFrame.setJMenuBar(menuBar);
		validate();
		setVisible(true);
	}
	
	
	private void createErrorDisplay() {
		errorBox = new JTextArea();
		errorBox.setEditable(false);
		errorBox.setLineWrap (false);
		JScrollPane scrollBox = new JScrollPane(errorBox);
		add(scrollBox,BorderLayout.CENTER);
	}
	
	private void createModeButtons() {
		ImageIcon playIcon ;
		ImageIcon recordIcon ;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		try {
			playIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Icons/playMode.png")));
			recordIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Icons/recordMode.png"))) ;	
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find icons to display on buttons!") ;
		}	
		JButton playButton = new JButton(playIcon) ;
		JButton recordButton = new JButton(recordIcon) ;
		playButton.setToolTipText(PLAY_TOOLTIP);
		recordButton.setToolTipText(RECORD_TOOLTIP);
		playButton.setOpaque(false);
		playButton.setContentAreaFilled(false);
		playButton.setBorderPainted(false);
		recordButton.setOpaque(false);
		recordButton.setContentAreaFilled(false);
		recordButton.setBorderPainted(false);
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isSelected) {
					JOptionPane.showMessageDialog(startFrame,
						    "Please select a recording.",
						    "Auto-Test: Warning",
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				isRecording = false;
				beginSession();	
			}
		});
		recordButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isSelected) {
					JOptionPane.showMessageDialog(startFrame,
						    "Please select a file.",
						    "Auto-Test: Warning",
						    JOptionPane.WARNING_MESSAGE);
					FileSelect fileSelect = new FileSelect(recordButton);
					fileSelect.listen(e);
					return;
				}
				isRecording = true;
				beginSession(); 
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 0;
		buttonPanel.add(playButton, constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		buttonPanel.add(recordButton, constraints);
		playText.setText("TO-DO");
		recordText.setText("No File selected!");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(playText, constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(recordText, constraints);
		buttonPanel.validate();
		buttonPanel.setVisible(true);
		add(buttonPanel, BorderLayout.NORTH);
	}
	
	
	/**
	 * Takes in the name of a jar file and runs it while 
	 * redirecting its standard out and error
	 * @param executableName the name of the file to be run
	 * @return process being run
	 */
	private Process startProgram(String executableName) {
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
        // Then retrieve the process output
        StreamRedirector in = new StreamRedirector(proc.getInputStream(), new JTextAreaOutputStream(errorBox));
        StreamRedirector err = new StreamRedirector(proc.getErrorStream(),  new JTextAreaOutputStream(errorBox));
        in.start();
        err.start();
        return proc;
	}
	
	private void beginSession() {
		if (isRecording) {
			SessionController sessionController = new SessionController(new Recorder());
			Process proc = startProgram(selectedFile);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new IllegalStateException("Program cannot wait for gui elements to load!");
			}
			sessionController.start();
			startFrame.setState(Frame.ICONIFIED);
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {
					while (proc.isAlive()) {
						
					}
					System.out.println("end");
					sessionController.end();
					startFrame.setState(Frame.NORMAL);
					isRecording = false;
					System.out.println("ended");
				}
			});
		}
	}
		
	/** 
	 Handles file choosing and parsing
	 */	
	class FileSelect {
		Component parent;
		
		public FileSelect(Component parent) {
			this.parent = parent;
		}
		public void listen(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(new FileFilter() {
				final String JAR_EXT="jar";
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
		    int returnVal = fileChooser.showOpenDialog(parent);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		           recentLog.addToLog(file.getAbsolutePath());
		           selectedFile = file.getAbsolutePath();
		           recordText.setText(selectedFile);
		           isSelected = true;
		    } else if (returnVal==JFileChooser.CANCEL_OPTION) {
		    	JOptionPane.showMessageDialog(startFrame, "Please select JAR file.");
		   }
		}
	}
}
