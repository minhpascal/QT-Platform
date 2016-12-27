/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates possible account states.
 * 
 * @author Miquel Sas
 */
public enum AccountState {
	/**
	 * Account OK, trading is allowed.
	 */
	Ok,
	/**
	 * Margin call triggered, closing positions by force. Entering new orders is prohibited.
	 */
	MarginClosing,
	/**
	 * Margin call status. Entering new orders is prohibited.
	 */
	MarginCall,
	/**
	 * Account acting as usually, but margin call doesn't close positions.
	 */
	OkNoMarginCall,
	/**
	 * Disabled. Entering new orders is prohibited.
	 */
	Disabled,
	/**
	 * Account is blocked. Entering new orders is prohibited.
	 */
	Blocked;
}
