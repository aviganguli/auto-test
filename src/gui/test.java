package gui;

	import java.awt.FlowLayout;
	import java.awt.HeadlessException;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.awt.event.KeyEvent;

	import javax.swing.AbstractAction;
	import javax.swing.Action;
	import javax.swing.JButton;
	import javax.swing.JComponent;
	import javax.swing.JFrame;
	import javax.swing.JMenu;
	import javax.swing.JMenuBar;
	import javax.swing.JMenuItem;
	import javax.swing.KeyStroke;
	import javax.swing.SwingUtilities;

	/**
	 * This Swing program demonstrates setting shortcut key and hotkey for menu
	 * and button.
	 * @author www.codejava.net
	 *
	 */
	public class test extends JFrame {
		private JButton button = new JButton();
		
		public test() throws HeadlessException {
			super("Swing Shortcuts Demo");
			
			makeMenu();
			makeButton();
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(640, 480);
			setLocationRelativeTo(null);
			
		}
		
		private void makeMenu() {
			JMenuBar menuBar = new JMenuBar();
			JMenu menuFile = new JMenu("File");
			menuFile.setMnemonic(KeyEvent.VK_F);
			
			JMenuItem menuItemOpen = new JMenuItem("Open");
			menuItemOpen.setMnemonic(KeyEvent.VK_O);
			
			KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
			menuItemOpen.setAccelerator(keyStrokeToOpen);
			
			menuItemOpen.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent evt) {
					System.out.println("Opening File...");
				}
			});
			
			JMenuItem menuItemSave = new JMenuItem();
			
			Action saveAction = new AbstractAction("Save") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Saving...");
				}
			};
			saveAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			saveAction.putValue(Action.ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
			
			menuItemSave.setAction(saveAction);
			
			JMenuItem menuItemExit = new JMenuItem("Exit");
			menuItemExit.setMnemonic('X');
			
			menuFile.add(menuItemOpen);
			menuFile.add(menuItemSave);
			menuFile.add(menuItemExit);
			
			menuBar.add(menuFile);
			
			setJMenuBar(menuBar);
		}
		
		private void makeButton() {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(button);
			
			Action buttonAction = new AbstractAction("Refresh") {
				
				@Override
				public void actionPerformed(ActionEvent evt) {
					System.out.println("Refreshing...");
				}
			};
			
			String key = "Refresh";

			button.setAction(buttonAction);
			
			buttonAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
			
			button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), key);
			
			button.getActionMap().put(key, buttonAction);
			
		}

		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					new test().setVisible(true);
				}
			});
		}
}


