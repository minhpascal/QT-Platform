/**
 * 
 */
package com.qtplaf.library.trading.server;

import java.util.Currency;

/**
 * Access to account information.
 * 
 * @author Miquel Sas
 */
public interface Account {
	/**
	 * Returns account balance. The last account balance available is the balance for the previous end-of-day
	 * processing.
	 * 
	 * @return balance
	 */
	double getBalance();

	/**
	 * Returns the account currency.
	 * 
	 * @return The account currency.
	 */
	Currency getCurrency();

	/**
	 * Returns the account equity in the account currency. The equity is normally calculated every N time, so at a given
	 * moment can be not accurate.
	 * 
	 * @return The account equity in the account currency.
	 */
	double getEquity();

	/**
	 * Returns the current leverage, normally stated by contract with the broker.
	 * 
	 * @return The current leverage.
	 */
	double getLeverage();

	/**
	 * Returns the maximum use of leverage exceeding which will result in margin cut.
	 *
	 * @return The maximum use of leverage before margin cuts
	 */
	double getMarginCutLevel();

	/**
	 * Returns the maximum use of leverage exceeding which will result in margin cut. Value effective before weekends.
	 *
	 * @return The maximum use of leverage before margin cuts at weekends.
	 */
	double getOverWeekEndLeverage();

	/**
	 * Returns the used margin in account currency.
	 *
	 * @return The used margin.
	 */
	double getUsedMargin();

	/**
	 * Returns the current use of leverage. Value returned by this function is for information purposes and can be
	 * incorrect right after order changes, as it is updated about every N time.
	 * 
	 * @return The current use of leverage.
	 */
	double getUsedLeverage();

	/**
	 * Returns the user name.
	 *
	 * @return The user name.
	 */
	String getUserName();

	/**
	 * Returns a boolean indicating if account is connected to the server.
	 *
	 * @return A boolean indicating if account is connected to the server.
	 */
	boolean isConnected();
}
