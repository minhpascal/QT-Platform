/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates the different types of orders. Note that not all brokers may support all enumerated order commands. In
 * such cases the system will approximate the required order command to the available one.
 * 
 * @author Miquel Sas
 */
public enum OrderCommand {
	/**
	 * Buy, the command for a long order already filled.
	 */
	Buy,
	/**
	 * Buy at market price.
	 */
	BuyMarket,
	/**
	 * Buy when ask price &lt;= specified price.
	 */
	BuyLimitAsk,
	/**
	 * Buy when bid price &lt;= specified price.
	 */
	BuyLimitBid,
	/**
	 * Buy when ask price &gt;= specified price.
	 */
	BuyStopAsk,
	/**
	 * Buy when bid price &gt;= specified price.
	 */
	BuyStopBid,
	/**
	 * Sell, the command for a short order already filled.
	 */
	Sell,
	/**
	 * Sell at market price.
	 */
	SellMarket,
	/**
	 * Sell when ask price &gt;= specified price.
	 */
	SellLimitAsk,
	/**
	 * Sell when bid price &gt;= specified price.
	 */
	SellLimitBid,
	/**
	 * Sell when ask price &lt;= specified price.
	 */
	SellStopAsk,
	/**
	 * Sell when bid price &lt;= specified price.
	 */
	SellStopBid,
	/**
	 * Place a ask at the specified price.
	 */
	PlaceAsk,
	/**
	 * Place bid at specified price.
	 */
	PlaceBid;
}
