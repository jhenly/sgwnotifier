package swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class Window {
	private static final String WINDOW_TITLE = "ShopGoodWill Notifier";
	
	public Window() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		JFrame mainFrame = new JFrame(WINDOW_TITLE);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(createContentPane());
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
	}
	
	private JPanel createContentPane() {
		JPanel gui = new JPanel(new BorderLayout(5, 5));
		
		JPanel diagnostics = createDiagnosticsPanel();
		gui.add(diagnostics, BorderLayout.NORTH);
		
		JPanel auctions = createAuctionsPanel();
		gui.add(auctions, BorderLayout.CENTER);
		
		JPanel buttons = createButtonsPanel();
		gui.add(buttons, BorderLayout.SOUTH);
		
		return gui;
	}
	
	private JPanel createDiagnosticsPanel() {
		JPanel diags = new JPanel(new GridLayout(2, 0, 3, 3));
		diags.setBorder(new TitledBorder("Diagnostics"));

		String[] labels = {"Status:", "Watchlist Owner", "Last Update:", "Next Update:", "Number of Auctions:", "Volume"};
		String[] placeHolders = {"connected", "jhenly", "10 mins", "20 mins", "20", "56%"};
		
		JLabel[] jlabelNames = new JLabel[labels.length];
		JLabel[] jlabelData = new JLabel[labels.length];
		
		for(int i = 0; i < labels.length; i++) {
			jlabelNames[i] = new JLabel(labels[i]);
			jlabelData[i] = new JLabel(placeHolders[i]);
			
			diags.add(jlabelNames[i]);
			diags.add(jlabelData[i]);
		}
		
		return diags;
	}
	
	private JPanel createAuctionsPanel() {
		FlowLayout auctsLayout = new FlowLayout();
		JPanel aucts = new JPanel(auctsLayout);
		
		
		
		return aucts;
	}

	private JPanel createButtonsPanel() {
		FlowLayout btnsLayout = new FlowLayout(FlowLayout.RIGHT, 3, 3);
		JPanel btns = new JPanel(btnsLayout);
		btns.setBorder(BorderFactory.createEmptyBorder());
		
		
		
		return btns;
	}
	
	
}
