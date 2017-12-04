package swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import core.WebHandler;

public class Window {
	private static final String WINDOW_TITLE = "ShopGoodWill Notifier";
	private WebHandler webHandler;
	private Timer auctionTimer;
	
	
	static {
		// JFrame.setDefaultLookAndFeelDecorated(true);
	}
	
	/**
	 * Constructor
	 */
	public Window(WebHandler webHandler) {
		this.webHandler = webHandler;
		
		JFrame mainFrame = new JFrame(WINDOW_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create and set the content pane to the gui panel
		mainFrame.setContentPane(createContentPane());
		
		// let the layout managers do their work and size everything
		mainFrame.pack();
		mainFrame.setMinimumSize(mainFrame.getSize());
		
		// center and show the window
		mainFrame.setLocationRelativeTo(null);	
		mainFrame.setVisible(true);
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel createContentPane() {
		final JPanel gui = new JPanel(new BorderLayout(5, 5));
		
		// create and add the diagnostics panel to gui north
		gui.add(createDiagnosticsPanel(), BorderLayout.NORTH);
		
		// create and add the auctions panel to gui center
		gui.add(createAuctionsPanel(), BorderLayout.CENTER);
		
		// create and add the buttons panel to gui south
		gui.add(createButtonsPanel(), BorderLayout.SOUTH);
		
		return gui;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel createDiagnosticsPanel() {
		final JPanel diags = new JPanel(new GridLayout(2, 0, 3, 3));
		diags.setBorder(new TitledBorder("Diagnostics"));

		String[] labels = {"Status:", "Watchlist Owner:", "Last Update:", "Next Update:", "Number of Auctions:", "Volume:"};
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
	
	/**
	 * 
	 * @return
	 */
	private JPanel createAuctionsPanel() {
		final int DUMMY_NUM_AUCTIONS = 100;
		
		final JPanel aucts = new JPanel(new CardLayout(4, 4));
		aucts.setBorder(BorderFactory.createTitledBorder("Auctions"));
		
		String[] headers = {"ID", "Title", "My Max", "Cur Price", "Num Bids", "Time till Auction", "End Date", "URL"};
		String[][] data = new String[DUMMY_NUM_AUCTIONS][headers.length];
		
		for(int i = 0; i < DUMMY_NUM_AUCTIONS; i++) {
			for(int j = 0; j < headers.length; j++) {
				data[i][j] = Integer.toString((i * headers.length) + j);
			}
		}
		
		DefaultTableModel model = new DefaultTableModel(data, headers);
        JTable table = new JTable(model) {
        	@Override
        	public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }
        };
        
        try {
            // 1.6+
            table.setAutoCreateRowSorter(true);
        } catch(Exception continuewithNoSort) { /* swallow exception */ }
        
        JScrollPane tableScroll = new JScrollPane(table);
        Dimension tablePreferred = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(new Dimension(tablePreferred.width, tablePreferred.height));
		
        aucts.add(tableScroll);
        
		return aucts;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel createButtonsPanel() {
		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		btns.setBorder(BorderFactory.createEmptyBorder());
		
		JButton update = new JButton("Update");
		
		btns.add(update);
		
		return btns;
	}
	
	
}
