import java.util.Date;
import java.util.HashMap;


/**
 * 
 * @author Jonathan Henly
 * @since NOV 13, 2017
 */
public class Auction {
	// Constants
	private static String CLASS_NAME = "Auction";

	// Private Members
	private int id;
	private String title;
	private double curPrice;
	private double myMaxBid;
	private int numBids;
	private Date endDate;
	private String url;
	private boolean updated;
	
	
	/**
	 * 
	 * @param id
	 * @param title
	 * @param curPrice
	 * @param myMaxBid
	 * @param numBids
	 * @param endDate
	 */
	public Auction(int id, String title, double curPrice, double myMaxBid,
			int numBids, Date endDate, String url) {
		super();
		this.id = id;
		this.title = title;
		this.curPrice = curPrice;
		this.myMaxBid = myMaxBid;
		this.numBids = numBids;
		this.endDate = endDate;
		this.url = url;
		this.updated = false;
	}
	
	/**
	 * @return the current price of the auction
	 */
	public double getCurrentPrice() {
		return curPrice;
	}

	/**
	 * @param price
	 *            the current price of the auction
	 */
	protected void setCurrentPrice(double price) {
		this.curPrice = price;
	}

	/**
	 * @return your maximum bid on this auction
	 */
	public double getMaxBid() {
		return myMaxBid;
	}

	/**
	 * @param maxBid
	 *            the maximum you're willing to bid on this auction
	 */
	protected void setMaxBid(double maxBid) {
		this.myMaxBid = maxBid;
	}

	/**
	 * @return the number of current bids on this auction
	 */
	public int getBids() {
		return numBids;
	}

	/**
	 * @param bids
	 *            the number of current bids on this auction
	 */
	protected void setBids(int bids) {
		this.numBids = bids;
	}

	/**
	 * @return this auction's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the title of this auction
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the ending date of this auction
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * @return the URL of this auction
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * 
	 * @return true if this Auction was updated in the last update.
	 */
	protected boolean hasBeenUpdated() {
		return updated;
	}
	
	
	
	private static void log(String msg) {
		Logger.log(CLASS_NAME, msg);
	}
	
	
}
