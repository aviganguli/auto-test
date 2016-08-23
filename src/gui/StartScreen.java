package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.apple.laf.AquaTabbedPaneUI;

import main.JTextAreaOutputStream;
import main.Log;
import main.Player;
import main.RCDRParser;
import main.Recorder;
import main.SequenceController;
import main.SessionController;
import main.StreamRedirector;
import main.Tuple;
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
	private final String FILE_MENU_TITLE = "File";
	private final String ADD_APP_ITEM = "New";
	private final String RECENT_APP_ITEM = "Recent";
	private final String RECORD_TOOLTIP = "Begin recording the selected file";
	private final String PLAY_TOOLTIP = "Begin playing the selected recording";
	private final String CLEAR_TOOLTIP = "Clear the console";
	private final String NEWTAB_TOOLTIP = "Create new tab" ;
	private final String RECORD_MENU_ITEM = "Recording";
	private final String PLAY_MENU_ITEM = "Playback";
	private static Log recentJARLog;
	private static Log recentRCDRLog;
	private boolean isRecording;
	private String selectedJARFile;
	private String  selectedRCDRFile;
	private ExecutorService executorService;
	private boolean isSelectedJARFile;
	private boolean isSelectedRCDRFile;
	private JTextArea errorBox;
	private JTextPane playText;
	private JTextPane recordText;
	private int numTabs;
	private int totalNumTabs;
	private JTabbedPane tabbedPane;
	private boolean isFirstRun;
	private ImageIcon closeIcon;
	private String OS_TYPE ;
	
	
	/**
	 * Constructor for all components
	 * @param appFrame frame app runs in
	 */
	StartScreen(JFrame appFrame) {

		/*BlockingArrayList<Tuple<?, ?>> arr = RCDRParser.parseFromFile("trial.rcdr") ;
		System.out.println(arr);
		RCDRParser.parseToFile(arr, "/Users/samuellee/auto-test/run/trial2.rcdr") ;*/


		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		recentJARLog = Log.JAR;
		recentRCDRLog = Log.RCDR;
		this.OS_TYPE = System.getProperty("os.name").toLowerCase() ;
		this.startFrame = appFrame;
		this.isFirstRun = true;
		this.isSelectedJARFile = false;
		this.isSelectedRCDRFile = false;
		this.numTabs = 0;
		this.totalNumTabs = 0;
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tab = (JTabbedPane) e.getSource() ;
				ErrorPanel errPanel = (ErrorPanel) tab.getSelectedComponent() ;
				StartScreen.this.errorBox = errPanel.getTextArea() ;
				System.out.println("State Changed " + tab.getSelectedIndex());
			}
		});
		
		if(OS_TYPE.contains("mac")){ 
			// for Mac
			tabbedPane.setUI(new AquaTabbedPaneUI()  {
				protected void paintContentBorder(Graphics g,int tabPlacement,int selectedIndex){}
			});
		}
		else { 
			// For Windows or Linux 
			tabbedPane.setUI(new BasicTabbedPaneUI() {
				  protected void paintContentBorder(Graphics g,int tabPlacement,int selectedIndex){}
			});
		}
		
		try {
			closeIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Icons/close.png")));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find icons to display on buttons!") ;
		}
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
		JMenu recordingMenu = new JMenu(RECORD_MENU_ITEM);
		recordingMenu.setMnemonic(KeyEvent.VK_R);
		JMenu playerMenu = new JMenu(PLAY_MENU_ITEM);
		playerMenu.setMnemonic(KeyEvent.VK_P);
		//Build the first menu.
		JMenu fileMenu = new JMenu(FILE_MENU_TITLE);
		fileMenu.setMnemonic(KeyEvent.VK_A);
		fileMenu.getAccessibleContext().setAccessibleDescription(
		        "Adds applications to test");
		final JMenuItem addRecordItem = new JMenuItem(ADD_APP_ITEM,KeyEvent.VK_N);
		addRecordItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		addRecordItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JARFileSelect fileSelect = new JARFileSelect(addRecordItem);
				fileSelect.listen(e);
			}
		});
		JMenu recentRecordItem = new JMenu(RECENT_APP_ITEM);
		recentRecordItem.setMnemonic(KeyEvent.VK_R);
		recentRecordItem.addMenuListener(new RecentJARMenuListener(recentRecordItem));
		recordingMenu.add(addRecordItem);
		recordingMenu.add(recentRecordItem);
		final JMenuItem addPlayItem = new JMenuItem(ADD_APP_ITEM,KeyEvent.VK_N);
		addPlayItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		addPlayItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RCDRFileSelect fileSelect = new RCDRFileSelect(addPlayItem);
				fileSelect.open(e);		
			}
		});
		JMenu recentPlayItem = new JMenu(RECENT_APP_ITEM);
		recentPlayItem.setMnemonic(KeyEvent.VK_R);
		recentPlayItem.addMenuListener(new RecentRCDRMenuListener(recentPlayItem));
		playerMenu.add(addPlayItem);
		playerMenu.add(recentPlayItem);
		fileMenu.add(recordingMenu);
		fileMenu.add(playerMenu);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		startFrame.setJMenuBar(menuBar);
		createErrorDisplay(); //creates error display box
		add(tabbedPane, BorderLayout.CENTER);
		createModeButtons() ; //creates play and record buttons in startScreen panel
		validate();
		setVisible(true);
	}
	
	
	class ErrorPanel extends JPanel{
		private JTextArea textBox ;
		
		public ErrorPanel(LayoutManager layout) {
			this.setLayout(layout);
		}
		
		public void setTextArea(JTextArea textArea) {
			
			this.textBox = textArea ;
		}
		
		public JTextArea getTextArea() {
			
			return this.textBox ;
		}
	}
	
	private void createErrorDisplay() {
		numTabs++;
		totalNumTabs++;
		System.out.println(totalNumTabs); //DEBUG
		errorBox = new JTextArea();
		errorBox.setEditable(false);
		errorBox.setLineWrap (false);
		JScrollPane scrollBox = new JScrollPane(errorBox);
		ErrorPanel errorPanel = new ErrorPanel(new GridBagLayout());
		errorPanel.setTextArea(errorBox);
		
		GridBagConstraints constraints = new GridBagConstraints();
		ImageIcon clearIcon;
		try {
			clearIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Icons/clear.gif")));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find icons to display on buttons!") ;
		}
		JPanel tabPanel = new JPanel(new GridLayout(2, 1)) ;
		JButton clearButton = new JButton(clearIcon);
		clearButton.addActionListener((e) -> errorBox.setText(""));
		clearButton.setOpaque(false);
		clearButton.setContentAreaFilled(false);
		clearButton.setBorderPainted(false);
		clearButton.setToolTipText(CLEAR_TOOLTIP);
		tabPanel.add(clearButton);
		
		ImageIcon newTabIcon ;
		try {
			newTabIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Icons/newTab.png")));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find icons to display on buttons!") ;
		}
		JButton newTabButton = new JButton(newTabIcon) ;
		newTabButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createErrorDisplay();
			}
		});
		newTabButton.setOpaque(false);
		newTabButton.setContentAreaFilled(false);
		newTabButton.setBorderPainted(false);
		newTabButton.setToolTipText(NEWTAB_TOOLTIP);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.NORTH;
		tabPanel.add(newTabButton) ;
		errorPanel.add(tabPanel, constraints);
		
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		errorPanel.add(scrollBox, constraints);
		tabbedPane.addTab("Tab " + totalNumTabs, errorPanel);
		tabbedPane.setTabComponentAt(numTabs-1, getTitlePanel(tabbedPane, errorPanel, "Tab " + totalNumTabs));
		tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Tab " + totalNumTabs) ) ;
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
		JButton playerButton = new JButton(playIcon) ;
		JButton recordButton = new JButton(recordIcon) ;
		playerButton.setToolTipText(PLAY_TOOLTIP);
		recordButton.setToolTipText(RECORD_TOOLTIP);
		playerButton.setOpaque(false);
		playerButton.setContentAreaFilled(false);
		playerButton.setBorderPainted(false);
		recordButton.setOpaque(false);
		recordButton.setContentAreaFilled(false);
		recordButton.setBorderPainted(false);
		playerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isSelectedRCDRFile) {
					JOptionPane.showMessageDialog(startFrame,
						    "Please select a recording.",
						    "Auto-Test: Warning",
						    JOptionPane.WARNING_MESSAGE);
					RCDRFileSelect fileSelect = new RCDRFileSelect(recordButton);
					fileSelect.open(e);
					return;
				}
				isRecording = false;
				beginSession();	
			}
		});
		recordButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isSelectedJARFile) {
					JOptionPane.showMessageDialog(startFrame,
						    "Please select a file.",
						    "Auto-Test: Warning",
						    JOptionPane.WARNING_MESSAGE);
					JARFileSelect fileSelect = new JARFileSelect(recordButton);
					fileSelect.listen(e);
					return;
				}
				isRecording = true;
				beginSession(); 
			}
		});
		constraints.gridx = 0;
		constraints.gridy = 0;
		buttonPanel.add(playerButton, constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		buttonPanel.add(recordButton, constraints);
		playText.setText("No File selected!");
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
	
	public JPanel getTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title)
	 {
	  JPanel titlePanel = new JPanel(new GridBagLayout());
	  GridBagConstraints constraints = new GridBagConstraints();
	  titlePanel.setOpaque(false);
	  JLabel titleLabel = new JLabel(title);
	  constraints.anchor = GridBagConstraints.CENTER;
	  constraints.gridx = 0;
	  constraints.gridy = 0;
	  
	  titlePanel.add(titleLabel, constraints);
	  JButton closeButton = new JButton(closeIcon);
	  closeButton.setOpaque(false);
	  closeButton.setContentAreaFilled(false);
	  closeButton.setBorderPainted(false);
	  closeButton.addActionListener((e) ->  {
		  if (numTabs == 1) return; 
		  else  {
			  tabbedPane.remove(panel);
			  numTabs--;
		  }
	  });
	  constraints.anchor = GridBagConstraints.NORTHEAST;
	  constraints.gridx = 1;
	  constraints.gridy = 0;
	  titlePanel.add(closeButton, constraints);
	  return titlePanel;
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
	
	private void beginRecording() {
		Recorder recorder = new Recorder();
		SessionController sessionController = new SessionController(recorder);
		Process proc = startProgram(selectedJARFile);
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
				List<Tuple<?, ?>> recorded = sessionController.end();
				startFrame.setState(Frame.NORMAL);
				isRecording = false;
				RCDRFileSelect fileSelect = new RCDRFileSelect(StartScreen.this);
				fileSelect.save(recorded);
				System.out.println("ended");
				return;
			}
		});
	}
	
	private void beginPlaying() {
		Tuple<String, List<Tuple<?, ?>>> tuple = RCDRParser.parseFromFile(selectedRCDRFile);
		Player player = new Player(tuple.getSecond());
		startProgram(tuple.getFirst());
		player.play();
	}
	
	private void beginSession() {
		if (!isFirstRun) {
			//createErrorDisplay();
		}
		isFirstRun = false;
		if (isRecording) {
			beginRecording();
		}
		else {
			beginPlaying();
		}
	}
	
		
	/** 
	 Handles file choosing and parsing
	 */	
	class JARFileSelect extends JFileChooser {
		Component parent;
		
		public JARFileSelect(Component parent) {
			this.parent = parent;
		}
		public void listen(ActionEvent e) {
			JFileChooser fileChooser = this;
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return ".jar";
				}
				
				@Override
				public boolean accept(File file) {
					return isJAR(file.getName()) || file.isDirectory();
				}
			});
		    int returnVal = fileChooser.showOpenDialog(parent);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		           recentJARLog.addToLog(file.getAbsolutePath());
		           selectedJARFile = file.getAbsolutePath();
		           recordText.setText(selectedJARFile);
		           isSelectedJARFile = true;
		    } else if (returnVal==JFileChooser.CANCEL_OPTION) {
		    	JOptionPane.showMessageDialog(startFrame, "Please select JAR file.");
		   }
		}
		
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
	}
	
	class RecentJARMenuListener implements MenuListener {
		JMenu menu;
		
		public RecentJARMenuListener(JMenu menu) {
			this.menu = menu;
		}
		/*
		 * Handles starting previously opened applications 
		 */
		@Override
		public void menuSelected(MenuEvent e) {
			List <String> recentPaths = recentJARLog.readFromLog();
			for (String path : recentPaths) {
				final JMenuItem recent = new JMenuItem(path);
				recent.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						selectedJARFile=recent.getText();
						recentJARLog.addToLog(recent.getText());
						isSelectedJARFile = true;
						recordText.setText(selectedJARFile);
					}
				});
				menu.add(recent);
			}
			
		}
		
		@Override
		public void menuDeselected(MenuEvent e) {
			menu.removeAll();
			
		}
		
		@Override
		public void menuCanceled(MenuEvent e) {
			// do nothing
			
		}
	}
	
	/** 
	 Handles file choosing and parsing
	 */	
	class RCDRFileSelect extends JFileChooser{
		Component parent;
		
		public RCDRFileSelect(Component parent) {
			this.parent = parent;
		}
		
		public void open(ActionEvent e) {
			JFileChooser fileChooser = this;
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileFilter() {
		
				@Override
				public String getDescription() {
					return "." + RCDR_EXT;
				}
				
				@Override
				public boolean accept(File file) {
					return ((isRCDR(file.getName()) == null) || (isRCDR(file.getName()).booleanValue()) ||   
							file.isDirectory());
				}
			});
		    int returnVal = this.showOpenDialog(parent);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = this.getSelectedFile();
		           recentRCDRLog.addToLog(file.getAbsolutePath());
		           selectedRCDRFile = file.getAbsolutePath();
		           playText.setText(selectedRCDRFile);
		           isSelectedRCDRFile = true;
		    } else if (returnVal==JFileChooser.CANCEL_OPTION) {
		    	JOptionPane.showMessageDialog(startFrame, "Please select RCDR file.");
		   }
		}
		
		public void save(List<Tuple<?, ?>> recorded) {

			JFileChooser fileChooser = this;
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fileChooser.showSaveDialog(fileChooser.getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				
				selectedRCDRFile = this.getSelectedFile().getAbsolutePath();
				
				if (isRCDR(selectedRCDRFile) == null) {
					selectedRCDRFile = this.getSelectedFile().getAbsolutePath().concat("." + RCDR_EXT);
				}
				
				else if (isRCDR(selectedRCDRFile) != null && isRCDR(selectedRCDRFile) == false) {
					JOptionPane.showMessageDialog(startFrame, "Please select RCDR file.");
					//save(recorded);
					return;
				}
				
				playText.setText(selectedRCDRFile);
				isSelectedRCDRFile = true;
				RCDRParser.parseToFile(recorded, selectedRCDRFile, selectedJARFile);
			}
			else if (returnVal == JFileChooser.CANCEL_OPTION) {
		    	JOptionPane.showMessageDialog(startFrame, "Please select RCDR file.");
		   }
			removeAll();
		}
		
		final String RCDR_EXT="rcdr";
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
	    
		private Boolean isRCDR(String fileName) {
			String ext = getExtension(fileName);
			if (ext == null) {
				return null;
			}
			else {
				return (ext.equals(RCDR_EXT));
			}
		}
	}
	
	class RecentRCDRMenuListener implements MenuListener {
		JMenu menu;
		
		public RecentRCDRMenuListener(JMenu menu) {
			this.menu = menu;
		}
		/*
		 * Handles starting previously opened applications 
		 */
		@Override
		public void menuSelected(MenuEvent e) {
			List <String> recentPaths = recentRCDRLog.readFromLog();
			for (String path : recentPaths) {
				final JMenuItem recent = new JMenuItem(path);
				recent.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						selectedRCDRFile=recent.getText();
						recentRCDRLog.addToLog(recent.getText());
						isSelectedRCDRFile = true;
						playText.setText(selectedRCDRFile);
					}
				});
				menu.add(recent);
			}
			
		}
		
		@Override
		public void menuDeselected(MenuEvent e) {
			menu.removeAll();
			
		}
		
		@Override
		public void menuCanceled(MenuEvent e) {
			// do nothing
			
		}
	}
}

