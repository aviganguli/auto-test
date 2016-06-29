package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;


public class StartScreen extends JPanel {
	/**
	 * Boilerplate
	 */
	private static final long serialVersionUID = 1L;
	private JFrame startFrame;
	private final String ADD_MENU_TITLE = "Add";
	private final String ADD_APP_ITEM = "New Application";
	

	/**
	 * Constructor for all components
	 * @param gameFrame frame game runs in
	 */
	StartScreen(JFrame gameFrame) {
		this.startFrame=gameFrame;
		JPanel startPanel= new JPanel();
		//Build the first menu.
		JMenu addMenu = new JMenu(ADD_MENU_TITLE);
		addMenu.setMnemonic(KeyEvent.VK_A);
		addMenu.getAccessibleContext().setAccessibleDescription(
		        "Adds applications to test");
		JMenuItem addApp = new JMenuItem(ADD_APP_ITEM,KeyEvent.VK_N);
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
			            System.out.println(file.getName());
			    } else if (returnVal==JFileChooser.CANCEL_OPTION) {
			    	JOptionPane.showMessageDialog(startFrame, "Please select JAR file.");
			   }
			}
		});
		addMenu.add(addApp);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(addMenu);
		startFrame.setJMenuBar(menuBar);
		startPanel.validate();
		startPanel.setVisible(true);
	}
		
}
