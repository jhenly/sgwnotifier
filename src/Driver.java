import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.*;

/**
 * 
 * 
 * @author Jonathan Henly
 * @since NOV-10-2017
 * @version 0.1
 * 
 */
public class Driver {
	
	static {
		System.setProperty("http.keepAlive", "true");
		
		// turn off HtmlUnit's logging
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	}
	
	/*
	 * Private Constants
	 */
	private static final String CLASS_NAME = "Driver";
	private static final int JAVASCRIPT_WAIT_TIME = 1000;
	private static final String SGW_URL = "https://www.shopgoodwill.com";
	private static final String WATCHLIST_URL = "https://www.shopgoodwill.com/MyShopgoodwill/WatchList";
	private static final String USERNAME = "jhenly";
	private static final String PASSWORD = "Corbu133";
	
	// Private Members
	private static HashMap<Integer, Auction> auctions;
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		WebClient webClient = null;
		HtmlPage sgwPage = null;
		List<Auction> parsedAuctions = null;
		
		// start log
		Logger.startLog();
		
		log("Creating the WebClient.");
		webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		log("Logging in to ShopGoodWill.com");
		sgwPage = loginToSGW(webClient);
		
		log("Redirecting to ShopGoodWill watchlist page.");
		sgwPage = redirectToWatchlistPage(sgwPage);
		webClient.waitForBackgroundJavaScript(JAVASCRIPT_WAIT_TIME);
		
		log("Parsing the watchlist.");
		parsedAuctions = parseWatchListTable(sgwPage);
		
		// log(sgwPage.asXml());
		
		log("Closing the WebClient.");
		webClient.close();
		
		// end log
		Logger.closeLog();
	}
	
	/**
	 * 
	 * @param webClient
	 * @return
	 */
	private static HtmlPage loginToSGW(WebClient webClient) {
	        HtmlPage loginPage = null;
	        HtmlPage watchlistPage = null;
	        HtmlForm loginForm = null;
	        HtmlSubmitInput loginSubmit = null;
	        HtmlTextInput username = null;
	        HtmlPasswordInput password = null;
	        
	        // get the login page
			try {
				loginPage = webClient.getPage(WATCHLIST_URL);
			} catch (FailingHttpStatusCodeException e) {
				log(e.getMessage());
				e.printStackTrace();
			} catch (MalformedURLException e) {
				log(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log(e.getMessage());
				e.printStackTrace();
			}
			
	        /* Get the HTML form that we are dealing with and within that form,
	         * find the submit button and the field that we want to change.
	         */
	        loginForm = (HtmlForm) loginPage.querySelector("form#login-form");
	        loginSubmit = loginForm.getInputByName("login-submit");

	        // change the value of the username text field
	        username = loginForm.getInputByName("Username");
	        username.setValueAttribute(USERNAME);
	        
	        // change the value of the password text field
	        password = loginForm.getInputByName("Password");
	        password.setValueAttribute(PASSWORD);

	        // submit the form and get back the watchlist
	        try {
				watchlistPage = loginSubmit.click();
			} catch (IOException e) {
				log(e.getMessage());
				e.printStackTrace();
			}
			
	        return watchlistPage;
	}
	
	/**
	 * 
	 * @param sgwPage
	 * @return
	 */
	private static HtmlPage redirectToWatchlistPage(HtmlPage sgwPage) {
		DomNode dropdownMenu = null;
		HtmlAnchor wlAnchor = null;
		HtmlPage wlPage = null;
		
		
        dropdownMenu = sgwPage.querySelector(".header-top .container .account-menu .dropdown .dropdown-menu");
        
        for(DomNode child : dropdownMenu.getChildNodes()) {
        	if(child.getTextContent().trim().equals("Watch List")) {
        		wlAnchor = (HtmlAnchor) child.getFirstChild();
        		break;
        	}
        }
        
        try {
			wlPage = wlAnchor.click();
		} catch (IOException e) {
			log(e.getMessage());
			e.printStackTrace();
		}

        return wlPage;
	}
	
	/**
	 * 
	 * @param wlist
	 */
	private static List<Auction> parseWatchListTable(HtmlPage wlist) {
		HtmlTable wlTable = null;
		HtmlTableBody wlBody = null;
		List<Auction> parsed = new LinkedList<Auction>();
		
		wlTable = (HtmlTable) wlist.querySelector("table#watch-list-table");
		if (!wlTable.getBodies().isEmpty()) {
			wlBody = wlTable.getBodies().get(0);
		} else {
			log("Error: table#watch-list-table does not have a table body.");
			System.exit(10);
		}
		
		for (HtmlTableRow row : wlBody.getRows()) {
			List<HtmlTableCell> cells = row.getCells();
			
			if(cells.size() < 10) {
				log("Error: Encountered table row with less than 10 cells.");
				continue;
			}
			
			parsed.add(parseWatchListTableRow(cells));
		}
		
		return parsed;
	}
	
	/*
	 * 
	 */
	private static class CellType {
		static int ID = 2;
		static int TITLE = 3;
		static int CUR_PRICE = 4;
		static int MAX_BID = 5;
		static int NUM_BIDS = 6;
		static int END_DATE = 7;
	}
	
	/**
	 * 
	 * @param row
	 * @return
	 */
	private static Auction parseWatchListTableRow(List<HtmlTableCell> cells) {
		final String END_DATE_FORMAT = "M/d/yyyy h:mm:ss aa zzz";
		Auction auction = null;
		HtmlTableCell cur = null;
		
		// skip the first two <td> tags and get the auction id
		
		// Auction ID
		cur = cells.get(CellType.ID);
		int id = Integer.parseInt(cur.getTextContent().trim());
		
		// Auction TITLE and URL
		cur = cells.get(CellType.TITLE);
		HtmlAnchor titleAnchor = (HtmlAnchor) cur.getChildNodes().get(1);
		String title = titleAnchor.getTextContent().trim();
		
		// piece together the auction's URL using the titleAnchor's href attribute
		String url = SGW_URL + titleAnchor.getHrefAttribute();
		
		// Auction CURRENT PRICE
		cur = cells.get(CellType.CUR_PRICE);
		Double curPrice = Double.parseDouble(cur.getTextContent().trim().substring(1));
		
		// Auction MAX BID
		cur = cells.get(CellType.MAX_BID);
		// remove leading and trailing whitespace from user's max bid 
		String tmpMaxBid = cur.getTextContent().trim();
		// if no max bid then zero it
		Double maxBid = ("".equals(tmpMaxBid)) ? 0.0 : Double.parseDouble(tmpMaxBid.substring(1));
		
		// Auction NUM BIDS
		cur = cells.get(CellType.NUM_BIDS);
		String tmpNumBids = cur.getTextContent().trim();
		// zero numBids since SGW displays "No bids" instead of 0
		int numBids = 0;
		
		try {
			numBids = Integer.parseInt(tmpNumBids.split(" ")[0]);
		} catch(NumberFormatException nfe) { /* swallow exception */ }
		
		// Auction ENDING DATE
		cur = cells.get(CellType.END_DATE);
		String tmpEndDate = cur.getTextContent().trim();
		SimpleDateFormat endDateFormat = new SimpleDateFormat(END_DATE_FORMAT, Locale.ENGLISH);
		
		Date endDate = null;
		try {
			endDate = endDateFormat.parse(tmpEndDate);
		} catch (ParseException e) {
			log(e.getMessage());
			endDate = Date.from(Instant.EPOCH);
		}
		
		// System.out.printf("id: %d\ntitle: %s\ncurPrice: $%3.2f\nmaxBid: $%3.2f\nnumBids: %3d\nendDate: %s\nurl: %s\n", id, title, curPrice, maxBid, numBids, endDate, url);
		
		auction = new Auction(id, title, curPrice, maxBid, numBids, endDate, url);
		
		return auction;
	}
	
	/**
	 * 
	 * @param msg
	 */
	private static void log(String msg) {
		Logger.log(CLASS_NAME, msg);
	}
	
}