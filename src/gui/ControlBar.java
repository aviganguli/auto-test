package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import main.SequenceController;

public class ControlBar extends JFrame {
	/**
	 * BoilerPlate
	 */
	private static final long serialVersionUID = 1L;
	private final String PLAY_TOOLTIP = "Play recorded actions";
	private final String PAUSE_TOOLTIP = "Pause recorded actions";
	private final String STOP_TOOLTIP = "Stop recorded actions";
	private final String FASTFORWARD_TOOLTIP = "Fast Forward recorded actions";
	private final String REWIND_TOOLTIP = "Stop recorded actions";
	private final ImageIcon playIcon; 
	private final ImageIcon pauseIcon;
	private final ImageIcon stopIcon; 
	private final ImageIcon ffIcon;
	private final ImageIcon rewindIcon; 
	private SequenceController controller;
	private boolean isPaused;

	private static double MAX_WIDTH;
	private static double MAX_HEIGHT;
	public static final double BAR_WIDTH = MAX_WIDTH/12;
	public static final double BAR_HEIGHT = MAX_WIDTH/10;
	public static final double START_POSX = MAX_WIDTH/2-BAR_WIDTH/2;
	public static final double START_POSY = MAX_HEIGHT-MAX_HEIGHT/8;
	
	
	
	public ControlBar(SequenceController controller) {
		super();
		this.controller = controller;
		this.isPaused = false;
		try {
			this.playIcon = new ImageIcon(
					ImageIO.read(getClass().getResource("/Icons/play.png")));
			this.pauseIcon = new ImageIcon(
					ImageIO.read(getClass().getResource("/Icons/pause.png")));
			this.stopIcon = new ImageIcon(
					ImageIO.read(getClass().getResource("/Icons/stop.png")));
			this.ffIcon = new ImageIcon(
					ImageIO.read(getClass().getResource("/Icons/fastforward.png")));
			this.rewindIcon = new ImageIcon(
					ImageIO.read(getClass().getResource("/Icons/rewind.png")));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find icons to display on buttons!");
		}
		this.MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		this.MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		populateButtons();
	}
	private void populateButtons() {
		//setUndecorated(true);
		JPanel barPanel = new JPanel(new GridLayout(1, 0));
		final JButton pauseButton = new JButton();
		final JButton stopButton = new JButton();
		final JButton ffButton = new JButton();
		final JButton rewindButton = new JButton();
		pauseButton.setIcon(pauseIcon);
		stopButton.setIcon(stopIcon);
		ffButton.setIcon(ffIcon);
		rewindButton.setIcon(rewindIcon);
		pauseButton.setToolTipText(PAUSE_TOOLTIP);
		stopButton.setToolTipText(STOP_TOOLTIP);
		ffButton.setToolTipText(FASTFORWARD_TOOLTIP);
		rewindButton.setToolTipText(REWIND_TOOLTIP);
		
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isPaused) {
					controller.play();
					pauseButton.setIcon(pauseIcon);
					pauseButton.setToolTipText(PAUSE_TOOLTIP);
					isPaused = false;
					return;
				} 
				controller.pause();
				pauseButton.setIcon(playIcon);
				pauseButton.setToolTipText(PLAY_TOOLTIP);
				isPaused = true;
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.stop();
			}
		});
		
		ffButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.fastForward();
			}
		});
		
		rewindButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.rewind();
			}
		});
		barPanel.add(rewindButton);
		barPanel.add(pauseButton);
		barPanel.add(ffButton);
		barPanel.add(stopButton);
		barPanel.setMaximumSize(new Dimension(new Double(BAR_WIDTH).intValue(), 
				new Double(BAR_HEIGHT).intValue()));
		barPanel.validate();
		barPanel.setVisible(true);
		barPanel.setOpaque(true);
		add(barPanel);
		setLocation(new Double(START_POSX).intValue(), 
				new Double(START_POSY).intValue());
		setMaximumSize(new Dimension(new Double(BAR_WIDTH).intValue(), 
				new Double(BAR_HEIGHT).intValue()));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validate();
		revalidate();
		pack();
		setVisible(true);
	}
		
	public void fade() {
		FadeAnimation.fade(this);
	}
}
