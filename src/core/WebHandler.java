package core;

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

import javax.swing.SwingUtilities;

import swing.Window;
import utils.Logger;

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
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import com.gargoylesoftware.htmlunit.*;

/**
 * 
 * 
 * @author Jonathan Henly
 * @version NOV-27-2017
 * 
 */
public final class WebHandler {

	static {
		System.setProperty("http.keepAlive", "true");
		
		// turn off HtmlUnit's logging
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);
	}
	
	/*
	 * Private Constants
	 */
	private static final String CLASS_NAME = "WebHandler";
	private static final int JAVASCRIPT_WAIT_TIME = 2000;
	private static final String SGW_URL = "https://www.shopgoodwill.com";
	private static final String SGW_SIGNIN_URL = "https://www.shopgoodwill.com/signin";
	private static final String SGW_WATCHLIST_URL = "https://shopgoodwill.com/shopgoodwill/inprogress-auctions";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	
	// Private Members
	private static HashMap<Integer, Auction> auctions;
	private static WebHandler instance;
	private WebClient webClient;
	
	/**
	 * Private Constructor
	 * 
	 * Don't want this class to be extended!
	 */
	private WebHandler() {
		log("Creating the WebClient.");
		webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}
	
	
	/**
	 * <p>
	 * Initialize the singleton {@code WebHandler} instance.
	 * </p>
	 * 
	 * <p>
	 * Note that this method creates a new instance of {@code WebHandler} if,
	 * and only if, one does not exist.
	 * </p>
	 * 
	 * @return a singleton instance of {@code WebHandler}.
	 */
	public static WebHandler init() {
		WebHandler wh = (instance == null) ? new WebHandler() : instance;
		
		return wh;
	}
	
	
	/**
	 * 
	 */
	public void close() {
		log("Closing the WebClient.");
		webClient.close();		
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public List<Auction> gatherWatchListAuctions() {
		HtmlPage watchListPage = null;
		HtmlPage signinPage = null;
		List<Auction> parsedAuctions = null;
		
		if(watchListPage == null) {
			log("Signing in to ShopGoodWill.com");			
			signinPage = signinToSGW();
			webClient.waitForBackgroundJavaScript(JAVASCRIPT_WAIT_TIME);
			checkForValidSignin(signinPage);
			
			log("Redirecting to ShopGoodWill watchlist page.");
			watchListPage = redirectToWatchlistPage(signinPage);
			webClient.waitForBackgroundJavaScript(JAVASCRIPT_WAIT_TIME);
		}
		
		log(watchListPage.asXml());
		System.exit(1000);
		log("Parsing the watchlist.");
		parsedAuctions = parseWatchListTable(watchListPage);
		
		return parsedAuctions;
	}
	
	/**
	 * 
	 * @param webClient
	 * @return
	 */
	private HtmlPage signinToSGW() {
		HtmlPage signinPage = null;
		HtmlPage signedInPage = null;
		HtmlForm signinForm = null;
		HtmlSubmitInput signinSubmit = null;
		HtmlTextInput username = null;
		HtmlPasswordInput password = null;
		
		// get the signin page
		try {
			signinPage = webClient.getPage(SGW_SIGNIN_URL);
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
		
		/*
		 * Get the HTML form that we are dealing with and within that form, find
		 * the submit button and the field that we want to change.
		 */
		signinForm = (HtmlForm) signinPage.querySelector("form#login-form");
		signinSubmit = signinForm.getInputByName("login-submit");
		
		// change the value of the username text field
		username = signinForm.getInputByName("Username");
		username.setValueAttribute(USERNAME);
		
		// change the value of the password text field
		password = signinForm.getInputByName("Password");
		password.setValueAttribute(PASSWORD);
		
		// submit the form and get back the SGW signed-in page
		try {
			signedInPage = signinSubmit.click();
		} catch (IOException e) {
			log(e.getMessage());
			e.printStackTrace();
		}
		
		return signedInPage;
	}
	
	
	private void checkForValidSignin(HtmlPage rerectPage) {
		HtmlAnchor welcomeAnchor = null;
		HtmlUnorderedList accountMenu = null;
		boolean welcomeFound = false;
		
		accountMenu = (HtmlUnorderedList) rerectPage.querySelector(".header-top .container .account-menu");
		
		int i = 0;
		int j = 0;
		for(DomNode child : accountMenu.getChildren()) {
			for (DomNode subChild : child.getChildren()) {
				if(subChild.getTextContent().trim().contains("Welcome")) {
					welcomeFound = true;
					System.out.println("Found 'Welcome' at (" + i + ", " + j + ")");
					break;
				}
				
				j += 1;
			}
			
			if(welcomeFound) {
				break;
			}
			
			j = 0;
			i += 1;
		}
		
		if(!welcomeFound) {
			System.out.println("Did not find 'Welcome'");
			System.exit(5000);
		}
	}
	
	/**
	 * 
	 * @param sgwPage
	 * @return
	 */
	private HtmlPage redirectToWatchlistPage(HtmlPage sgwPage) {
		DomNode dropdownMenu = null;
		HtmlAnchor wlAnchor = null;
		HtmlPage wlPage = null;
		
		dropdownMenu = sgwPage
				.querySelector(".header-top .container .account-menu .dropdown .dropdown-menu");
		
		for (DomNode child : dropdownMenu.getChildNodes()) {
			if (child.getTextContent().trim().equals("Watch List")) {
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
	private List<Auction> parseWatchListTable(HtmlPage wlist) {
		HtmlTable wlTable = null;
		HtmlTableBody wlBody = null;
		List<Auction> parsed = new LinkedList<Auction>();

		wlTable = (HtmlTable) wlist.querySelector("table#watch-list-table");
		
		log(wlist.asXml());
		Logger.closeLog();
		System.out.println(wlTable);
		System.exit(10000);
		
		if (!wlTable.getBodies().isEmpty()) {
			wlBody = wlTable.getBodies().get(0);
		} else {
			log("Error: table#watch-list-table does not have a table body.");
			System.exit(10);
		}

		for (HtmlTableRow row : wlBody.getRows()) {
			List<HtmlTableCell> cells = row.getCells();

			if (cells.size() < 10) {
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
	private Auction parseWatchListTableRow(List<HtmlTableCell> cells) {
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

		// piece together the auction's URL using the titleAnchor's href
		// attribute
		String url = SGW_URL + titleAnchor.getHrefAttribute();

		// Auction CURRENT PRICE
		cur = cells.get(CellType.CUR_PRICE);
		Double curPrice = Double.parseDouble(cur.getTextContent().trim()
				.substring(1));

		// Auction MAX BID
		cur = cells.get(CellType.MAX_BID);
		// remove leading and trailing whitespace from user's max bid
		String tmpMaxBid = cur.getTextContent().trim();
		// if no max bid then zero it
		Double maxBid = ("".equals(tmpMaxBid)) ? 0.0 : Double
				.parseDouble(tmpMaxBid.substring(1));

		// Auction NUM BIDS
		cur = cells.get(CellType.NUM_BIDS);
		String tmpNumBids = cur.getTextContent().trim();
		// zero numBids since SGW displays "No bids" instead of 0
		int numBids = 0;

		try {
			numBids = Integer.parseInt(tmpNumBids.split(" ")[0]);
		} catch (NumberFormatException nfe) { /* swallow exception */
		}

		// Auction ENDING DATE
		cur = cells.get(CellType.END_DATE);
		String tmpEndDate = cur.getTextContent().trim();
		SimpleDateFormat endDateFormat = new SimpleDateFormat(END_DATE_FORMAT,
				Locale.ENGLISH);

		Date endDate = null;
		try {
			endDate = endDateFormat.parse(tmpEndDate);
		} catch (ParseException e) {
			log(e.getMessage());
			endDate = Date.from(Instant.EPOCH);
		}

		// System.out.printf("id: %d\ntitle: %s\ncurPrice: $%3.2f\nmaxBid: $%3.2f\nnumBids: %3d\nendDate: %s\nurl: %s\n",
		// id, title, curPrice, maxBid, numBids, endDate, url);

		auction = new Auction(id, title, curPrice, maxBid, numBids, endDate,
				url);

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
