package core;

import javax.swing.SwingUtilities;

import swing.Window;
import utils.Logger;

public class Driver {
	private static WebHandler webHandler;

	public static void main(String[] args) {
		Logger.startLog();
		webHandler = WebHandler.init();
		
		for(Auction a : webHandler.gatherWatchListAuctions()) {
			System.out.println(a);
		}
		
		webHandler.close();
		
		System.exit(10000);
		// SWING DEBUGGING
		Runnable r = new Runnable() {
			
			public void run() {
				Window win = new Window(webHandler);
			}
			
		};
		SwingUtilities.invokeLater(r);
		
		Logger.closeLog();
	}

}
