/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates the possible messages received upon order changes.
 * 
 * @author Miquel Sas
 */
public enum OrderMessage {
	/**
	 * Amount changed.
	 */
	AmountChanged,
	/**
	 * Expiration time changed.
	 */
	ExpirationTimeChanged,
	/**
	 * Label changed.
	 */
	LabelChanged,
	/**
	 * Price changed.
	 */
	PriceChanged,
	/**
	 * Stop loss price changed.
	 */
	StopLossChanged,
	/**
	 * Take price changed.
	 */
	TakeProfitChanged,
	/**
	 * Order command changed.
	 */
	OrderCommandChanged,
	/**
	 * Order closed by merge.
	 */
	ClosedByMerge,
	/**
	 * Order closed by stop loss.
	 */
	ClosedByStopLoss,
	/**
	 * Order closed by take profit.
	 */
	ClosedByTakeProfit,
	/**
	 * Order closed at market.
	 */
	ClosedAtMarket,
	/**
	 * Order fully filled.
	 */
	Filled;
}
