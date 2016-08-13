package gui;


import java.awt.Graphics;

import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {

	private BasicTabbedPaneUI ui;
	
	public CustomTabbedPaneUI (TabbedPaneUI ui) {
		this.ui = (BasicTabbedPaneUI) ui;
	}
	
	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
		
	}
}
